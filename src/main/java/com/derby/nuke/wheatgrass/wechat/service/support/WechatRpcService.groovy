package com.derby.nuke.wheatgrass.wechat.service.support

import com.googlecode.jsonrpc4j.JsonRpcMethod
import com.googlecode.jsonrpc4j.JsonRpcService

/**
 * Created by Passyt on 2018/1/16.
 */
@JsonRpcService("/wechat.ci")
interface WechatRpcService {

    @JsonRpcMethod("sendMessage")
    def sendMessage(userIds, type, message, agentId)

}
