package com.derby.nuke.wheatgrass.integration

import static org.junit.Assert.*;

import org.junit.Test;

import com.derby.nuke.wheatgrass.wechat.service.WechatService;

class WechatServiceTools {
	
	def WechatService wechatService = new WechatService(url:"https://qyapi.weixin.qq.com", appId: "wx6cf7c599aa792ce2", appSecret:"Hf6ktqrHU4FyI95NucAjTsxMgSEv4xMQ_3r9X2ogzlMVALcFZEDka0twc3ZmGZ8H");
	
	@Test
	void getDepartments(){
		println wechatService.getDepartments();
	}
	
	@Test
	void getOpenId(){
		println wechatService.getOpenId("eric.kuang");
	}
	
	@Test
	void getUserInfo(){
		println wechatService.getUserInfo("parry.zhang");
	}
	
	@Test
	void getUsersByDepartment(){
		println wechatService.getUsersByDepartment("1");
	}

}
