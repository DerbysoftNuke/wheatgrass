package com.derby.nuke.wheatgrass.wechat.service.support

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.deser.std.CollectionDeserializer
import com.fasterxml.jackson.databind.deser.std.MapDeserializer
import com.fasterxml.jackson.databind.deser.std.StringDeserializer
import com.fasterxml.jackson.databind.ser.std.CollectionSerializer
import com.fasterxml.jackson.databind.ser.std.MapSerializer
import com.fasterxml.jackson.databind.ser.std.StringSerializer
import com.googlecode.jsonrpc4j.JsonRpcMethod
import com.googlecode.jsonrpc4j.JsonRpcService

/**
 * Created by Passyt on 2018/1/16.
 */
@JsonRpcService("wechat.ci")
interface WechatRpcService {

    @JsonRpcMethod("sendMessage")
    def boolean sendMessage(
            @JsonSerialize(using = CollectionSerializer) @JsonDeserialize(using = CollectionDeserializer) userIds,
            @JsonSerialize(using = StringSerializer) @JsonDeserialize(using = StringDeserializer) type,
            @JsonSerialize(using = MapSerializer) @JsonDeserialize(using = MapDeserializer) message,
            @JsonSerialize(using = StringSerializer) @JsonDeserialize(using = StringDeserializer) agentId)

}
