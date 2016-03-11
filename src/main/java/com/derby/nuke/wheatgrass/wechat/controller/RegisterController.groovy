package com.derby.nuke.wheatgrass.wechat.controller;

import javax.mail.internet.MimeUtility
import javax.servlet.http.HttpSession

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

import com.derby.nuke.wheatgrass.entity.Sex
import com.derby.nuke.wheatgrass.entity.User
import com.derby.nuke.wheatgrass.repository.UserRepository
import com.derby.nuke.wheatgrass.wechat.Consts

@RestController
class RegisterController extends WechatController{
	
	@Value('${web.external.url}')
	def externalUrl;

	@RequestMapping(value="/register", method = RequestMethod.GET)
	def register(HttpSession session, @RequestParam String code){
		def userId = session.getAttribute(Consts.USER_ID);
		if(userId == null){
			throw new IllegalArgumentException("UserId not found");
		}

		def user = userRepository.getByUserId(userId);
		if(user != null){
			return new ModelAndView("wechat/successful", "message", "加入成功!");
		}
		
		//TODO
		user = new User();
		def userInfo = wechatService.getUserInfo(code);
		user.nickName = userInfo.nickname;
		if(userInfo.sex == "1"){
			user.sex = Sex.Male; 
		} else if(userInfo.sex == "2"){
			user.sex = Sex.Female; 
		}
		user.imageUrl = userInfo.headimgurl;
		
		wechatService.register(userId);
		userRepository.save(user);
		return new ModelAndView("wechat/successful", "message", "加入成功!");
	}

}