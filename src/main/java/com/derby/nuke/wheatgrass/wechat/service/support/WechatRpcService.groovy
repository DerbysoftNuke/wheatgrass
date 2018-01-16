package com.derby.nuke.wheatgrass.wechat.service.support

import com.googlecode.jsonrpc4j.JsonRpcMethod
import com.googlecode.jsonrpc4j.JsonRpcService
import org.springframework.web.bind.annotation.ResponseBody

/**
 * Created by Passyt on 2018/1/16.
 */
@JsonRpcService("/**/wechat.ci")
interface WechatRpcService {

    @JsonRpcMethod("sendMessage")
    def Map sendMessage4rpc(List<String> userIds, String type, Map message, String agentId);

}
