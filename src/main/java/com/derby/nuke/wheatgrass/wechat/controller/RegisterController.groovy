package com.derby.nuke.wheatgrass.wechat.controller;

import java.time.LocalDate

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

import com.derby.nuke.wheatgrass.ScheduleConfiguration
import com.derby.nuke.wheatgrass.entity.Sex
import com.derby.nuke.wheatgrass.entity.User
import com.derby.nuke.wheatgrass.repository.UserRepository
import com.derby.nuke.wheatgrass.wechat.OAuthRequired

@RestController
@OAuthRequired(false)
class RegisterController extends WechatController{
	
	@Autowired
	def ScheduleConfiguration scheduleConfiguration;

	@RequestMapping(value="/register", method = RequestMethod.GET)
	def register(@RequestParam String code){
		def userId = wechatService.getUserId(code);
		wechatService.register(userId);
		scheduleConfiguration.downloadUser(userId);
		return new ModelAndView("wechat/successful", "message", "加入成功!");
	}

}