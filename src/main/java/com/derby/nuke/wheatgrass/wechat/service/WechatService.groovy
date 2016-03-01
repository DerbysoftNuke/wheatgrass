package com.derby.nuke.wheatgrass.wechat.service

import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.HttpClientBuilder
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
	@Value('${wechat.app.secret}')
	def appSecret;

	private def client = HttpClients.custom().setDefaultCookieStore(new BasicCookieStore()).build();
	private final def cache = CacheBuilder.from("expireAfterWrite=5m").build();
	
	def getOpenId(code){
		return cache.get(code, {key -> doCall(code)}).openid;
	}
	
	def getAccessToken(code){
		return cache.get(code, {key -> doCall(code)}).access_token;
	}
	
	def getAccessToken(){
		def client = HttpClientBuilder.create().build();
		try{
			def response = client.execute(new HttpGet(url+"/cgi-bin/token?grant_type=client_credential&appid=${appId}&secret=${appSecret}"));
			def obj = new ObjectMapper().readValue(response.getEntity().getContent(), Map.class);
			def token = obj.access_token;
			def expires = obj.expires_in;
			return token;
		}finally{
			client.close();
		}
	}
	
	private def doCall(code){
		def response = client.execute(RequestBuilder.get().setUri("${url}/sns/oauth2/access_token?appid=${appId}&secret=SECRET&code=${code}&grant_type=authorization_code").build());
		return new ObjectMapper().readValue(response.getEntity().getContent(), Map.class);
	}

}