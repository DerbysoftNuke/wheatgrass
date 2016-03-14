package com.derby.nuke.wheatgrass.integration

import org.apache.commons.io.IOUtils
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.HttpClients
import org.junit.Test

import com.derby.nuke.wheatgrass.wechat.service.WechatService

class MenuTools{

	def client = HttpClients.custom().setDefaultCookieStore(new BasicCookieStore()).build();
	def WechatService wechatService = new WechatService(url:"https://qyapi.weixin.qq.com", appId: "wx6cf7c599aa792ce2", appSecret:"Hf6ktqrHU4FyI95NucAjTsxMgSEv4xMQ_3r9X2ogzlMVALcFZEDka0twc3ZmGZ8H");

	@Test
	void createMenu(){
		def token = wechatService.getAccessToken();
		println "token: ${token}";
		def menuData = IOUtils.toString(MenuTools.class.getResourceAsStream("menu.json"), "UTF-8");
		def response = client.execute(RequestBuilder.post().setUri("https://qyapi.weixin.qq.com/cgi-bin/menu/create?access_token=${token}&agentid=5").addHeader("Content-Type", "applicatoin/json;charset=UTF-8").setEntity(new StringEntity(menuData, "UTF-8")).build());
		println IOUtils.toString(response.getEntity().getContent());
	}
	
	@Test
	void getMenu(){
		def token = wechatService.getAccessToken();
		println "token: ${token}";
		def response = client.execute(RequestBuilder.get().setUri("https://api.weixin.qq.com/cgi-bin/menu/get?access_token=${token}").addHeader("Content-Type", "applicatoin/json;charset=UTF-8").build());
		println IOUtils.toString(response.getEntity().getContent());
	}
	
	@Test
	void getOpenId(){
		println wechatService.getOpenId("0319e473f9560df234932f61e202cf1H");
	}
	
	static void main(String[] args) {
		println URLEncoder.encode("http://54.248.83.126/wheatgrass/wechat/register", "UTF-8");
		println URLDecoder.decode("https%3A%2F%2F54.248.83.126%2Fwheatgrass%2Fwechat%2Fbind_email", "UTF-8");
		println URLDecoder.decode("http%3A%2F%2F127.0.0.1%3A8080%2Fwechat%2Femail%2Fbind%3Ffetch_userinfo%3Dtrue", "UTF-8");
	}
	
}