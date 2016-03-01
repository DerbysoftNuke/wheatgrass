package com.derby.nuke.wheatgrass.wechat.service

import org.apache.http.client.methods.RequestBuilder
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.HttpClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.cache.CacheBuilder

@Service
class WechatService{

	@Value('${wechat.url}')
	def url = "wechat.url";
	@Value('${wechat.app.id}')
	def appId;

	private def client = HttpClients.custom().setDefaultCookieStore(new BasicCookieStore()).build();
	private final def cache = CacheBuilder.from("expireAfterWrite=5m").build();
	
	def getOpenId(code){
		return cache.get(code, {key -> doCall(code)}).openid;
	}
	
	def getAccessToken(code){
		return cache.get(code, {key -> doCall(code)}).access_token;
	}
	
	private def doCall(code){
		def response = client.execute(RequestBuilder.get().setUri("${url}/sns/oauth2/access_token?appid=${appId}&secret=SECRET&code=${code}&grant_type=authorization_code").build());
		return new ObjectMapper().readValue(response.getEntity().getContent(), Map.class);
	}

}