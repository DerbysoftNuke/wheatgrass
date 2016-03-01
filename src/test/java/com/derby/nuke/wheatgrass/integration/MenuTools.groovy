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
	def WechatService wechatService = new WechatService(url:"https://api.weixin.qq.com", appId: "wxfa334e1b73137720", appSecret:"d4624c36b6795d1d99dcf0547af5443d");

	@Test
	void createMenu(){
		def token = wechatService.getAccessToken();
		println "token: ${token}";
		def menuData = IOUtils.toString(MenuTools.class.getResourceAsStream("menu.json"), "UTF-8");
		def response = client.execute(RequestBuilder.post().setUri("https://api.weixin.qq.com/cgi-bin/menu/create?access_token=${token}").addHeader("Content-Type", "applicatoin/json;charset=UTF-8").setEntity(new StringEntity(menuData, "UTF-8")).build());
		println IOUtils.toString(response.getEntity().getContent());
	}
	
	static void main(String[] args) {
		println URLEncoder.encode("https://54.248.83.126/wheatgrass/wechat/mine_skill", "UTF-8");
	}
	
}