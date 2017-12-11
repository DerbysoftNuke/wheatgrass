package com.derby.nuke.wheatgrass.wechat.service

import com.derby.nuke.wheatgrass.wechat.WechatException
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Joiner
import com.google.common.cache.CacheBuilder
import org.apache.commons.collections.CollectionUtils
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.HttpClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class WechatService {

    @Value('${wechat.url}')
    def url = "wechat.url";
    @Value('${wechat.app.id}')
    def appId;
    @Value('${wechat.app.secret}')
    def appSecret;
    @Value('${wechat.agent.id}')
    def agentId;

    private final def cache = CacheBuilder.from("expireAfterWrite=1h").build();

    def getAccessToken() {
        def result = cache.get(appId, { key -> get("/cgi-bin/gettoken?corpid=${appId}&corpsecret=${appSecret}") });
        return result.access_token;
    }

    def register(userId) {
        def accessToken = getAccessToken();
        def result = get("/cgi-bin/user/authsucc?access_token=${accessToken}&userid=${userId}");
        if (result.errcode != 0) {
            throw new WechatException(String.valueOf(result.errcode), result.errmsg);
        }
        return true;
    }

    def getUserId(code) {
        def accessToken = getAccessToken();
        def result = get("/cgi-bin/user/getuserinfo?access_token=${accessToken}&code=${code}&agentid=${agentId}");
        return result.UserId;
    }

    def getOpenId(userId) {
        def accessToken = getAccessToken();
        def result = post("/cgi-bin/user/convert_to_openid?access_token=${accessToken}", [userid: userId]);
        return result.openid;
    }

    def getUserInfo(userId) {
        def accessToken = getAccessToken();
        return get("/cgi-bin/user/get?access_token=${accessToken}&userid=${userId}");
    }

    def getDepartment(departmentId) {
        def accessToken = getAccessToken();
        def result = get("/cgi-bin/department/list?access_token=${accessToken}&id=${departmentId}");
        return result.department[0];
    }

    def getDepartments() {
        def accessToken = getAccessToken();
        def result = get("/cgi-bin/department/list?access_token=${accessToken}");
        return result.department;
    }

    def getUsersByDepartment(departmentId) {
        def accessToken = getAccessToken();
        def result = get("/cgi-bin/user/simplelist?access_token=${accessToken}&department_id=${departmentId}&fetch_child=1&status=1");
        return result.userlist;
    }

    def sendMessage(userIds, type, message) {
        sendMessage(userIds, type, message, agentId);
    }

    def sendMessage(userIds, type, message, agentId) {
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        def accessToken = getAccessToken();
        def touser = Joiner.on("|").skipNulls().join(userIds);

        def request = ["touser": touser, "msgtype": type, "agentid": agentId, "safe": "0"];
        message.each { key, value ->
            request[key] = value;
        }
        post("/cgi-bin/message/send?access_token=${accessToken}", request);
    }

    def getUrlForCode(redirectUri) {
        return "https://open.weixin.qq.com/connect/oauth2/authorize?appid=${appId}&redirect_uri=${redirectUri}&response_type=code&scope=snsapi_userinfo&state=${agentId}";
    }

    private def get(uri) {
        def client = HttpClients.custom().setDefaultCookieStore(new BasicCookieStore()).build();
        try {
            def response = client.execute(new HttpGet(url + uri));
            return new ObjectMapper().readValue(response.getEntity().getContent(), Map.class);
        } finally {
            client.close()
        }
    }

    private def post(uri, json) {
        StringWriter out = new StringWriter();
        new ObjectMapper().writeValue(out, json);
        def request = RequestBuilder.post().setUri(url + uri).addHeader("Content-Type", "applicatoin/json;charset=UTF-8").setEntity(new StringEntity(out.toString(), "UTF-8")).build();
        def client = HttpClients.custom().setDefaultCookieStore(new BasicCookieStore()).build();
        try {
            def response = client.execute(request);
            return new ObjectMapper().readValue(response.getEntity().getContent(), Map.class);
        } finally {
            client.close()
        }
    }
}