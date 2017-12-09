package com.derby.nuke.wheatgrass.integration

import static org.junit.Assert.*;

import org.junit.Test;

import com.derby.nuke.wheatgrass.wechat.service.WechatService;

class WechatServiceTools {
	
	def WechatService wechatService = new WechatService(url:"https://qyapi.weixin.qq.com", appId: "wx2ee6e261c5669f29", appSecret:"xmo7ibgGQng1Yy_9xSEtrfMGrKyesjuAZkA5mKGjbj0");
	
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
		println wechatService.getUserInfo("kck");
	}
	
	@Test
	void getUsersByDepartment(){
		println wechatService.getUsersByDepartment("1");
	}

}
