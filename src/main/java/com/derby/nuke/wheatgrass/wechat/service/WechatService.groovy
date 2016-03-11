package com.derby.nuke.wheatgrass.wechat.service

import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.HttpClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import com.derby.nuke.wheatgrass.wechat.WechatException
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.cache.CacheBuilder

@Service
class WechatService{

	@Value('${wechat.url}')
	def url = "wechat.url";
	@Value('${wechat.app.id}')
	def appId;
	@Value('${wechat.app.secret}')
	def appSecret;
	@Value('${wechat.agent.id}')
	def agentId;

	private def client = HttpClients.custom().setDefaultCookieStore(new BasicCookieStore()).build();
	private final def cache = CacheBuilder.from("expireAfterWrite=1h").build();
	
	def getAccessToken(){
		def result = cache.get(appId, {key -> get("/cgi-bin/gettoken?corpid=${appId}&corpsecret=${appSecret}")});
		return result.access_token;
	}
	
	def getOpenId(userId){
		def accessToken = getAccessToken();
		def result = post("/cgi-bin/user/convert_to_openid?access_token=${accessToken}", [userid: userId]);
		return result.openid;
	}
	
	def getUserInfo(code){
		def accessToken = getAccessToken();
		return get("/cgi-bin/user/getuserinfo?access_token=${accessToken}&code=${code}&agentid=${agentId}");
	}
	
	def register(userId){
		def accessToken = getAccessToken();
		def result = get("/cgi-bin/user/authsucc?access_token=${accessToken}&userid=${userId}");
		if(result.errcode != "0"){
			throw new WechatException(result.errcode, result.errmsg);
		}
		return true;
	}
	
	def getDepartments(){
		def accessToken = getAccessToken();
		def result = get("/cgi-bin/department/list?access_token=${accessToken}");
		return result.department;
	}

	def getUrlForCode(redirectUri){
		return "https://open.weixin.qq.com/connect/oauth2/authorize?appid=${appId}&redirect_uri=${redirectUri}&response_type=code&scope=snsapi_userinfo&state=${agentId}";
	}

	private def get(uri){
		def response = client.execute(new HttpGet(url+uri));
		return new ObjectMapper().readValue(response.getEntity().getContent(), Map.class);
	}

	private def post(uri, json){
		StringWriter out = new StringWriter();
		new ObjectMapper().writeValue(out, json);
		def request = RequestBuilder.post().setUri(url+uri).addHeader("Content-Type", "applicatoin/json;charset=UTF-8").setEntity(new StringEntity(out.toString(), "UTF-8")).build();
		def response = client.execute(request);
		return new ObjectMapper().readValue(response.getEntity().getContent(), Map.class);
	}
}