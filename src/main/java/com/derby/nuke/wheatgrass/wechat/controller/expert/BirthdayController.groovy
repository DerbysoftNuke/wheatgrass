package com.derby.nuke.wheatgrass.wechat.controller.expert

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import com.derby.nuke.wheatgrass.wechat.OAuthRequired;
import com.derby.nuke.wheatgrass.wechat.controller.WechatController;

@RequestMapping("/wechat/birthday")
@OAuthRequired
class BirthdayController extends WechatController{
	def getViewPrefix(){
		return "/wechat/birthday";
	}

	def getRedirectPrefix(){
		return "/wechat/birthday";
	}
	
	@RequestMapping(value="/showWish", method = RequestMethod.GET)
	def showWish(){
		return view("show_wish");
	}
	
}
