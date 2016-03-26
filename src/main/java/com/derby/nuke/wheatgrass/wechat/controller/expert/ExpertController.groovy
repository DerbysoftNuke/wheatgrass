package com.derby.nuke.wheatgrass.wechat.controller.expert;

import org.springframework.web.bind.annotation.RequestMapping

import com.derby.nuke.wheatgrass.wechat.OAuthRequired
import com.derby.nuke.wheatgrass.wechat.controller.WechatController

@RequestMapping("/wechat/expert")
@OAuthRequired
class ExpertController extends WechatController {

	def getViewPrefix(){
		return "/wechat/expert";
	}

	def getRedirectPrefix(){
		return "/wechat/expert";
	}
}
