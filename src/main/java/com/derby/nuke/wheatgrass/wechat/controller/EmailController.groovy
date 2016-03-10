package com.derby.nuke.wheatgrass.wechat.controller;

import javax.mail.internet.MimeUtility
import javax.servlet.http.HttpSession

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

import com.derby.nuke.wheatgrass.entity.Sex;
import com.derby.nuke.wheatgrass.entity.User
import com.derby.nuke.wheatgrass.repository.UserRepository
import com.derby.nuke.wheatgrass.wechat.service.WechatService

@RestController
class EmailController extends WechatController{
	
	@Value('${web.external.url}')
	def externalUrl;
	@Autowired
	def JavaMailSender mailSender;

	@RequestMapping(value="/email/bind", method = RequestMethod.GET)
	def bindEmail(HttpSession session){
		def openId = session.getAttribute("wechat.openId");
		if(openId == null){
			throw new IllegalArgumentException("OpenId not found");
		}

		def userInfo = session.getAttribute("wechat.userInfo");
		def user = userRepository.getByOpenId(openId);
		if(user != null){
			if(user.validation){
				return new ModelAndView("wechat/warning", "message", "邮箱已激活: ${user.email}");
			}
		}else{
			user = new User();
		}
		
		if(userInfo != null){
			user.nickName = userInfo.nickname;
			if(userInfo.sex == "1"){
				user.sex = Sex.Male; 
			} else if(userInfo.sex == "2"){
				user.sex = Sex.Female; 
			}
			user.imageUrl = userInfo.headimgurl;
			userRepository.save(user);
		}

		return new ModelAndView("wechat/bind_email");
	}

	@RequestMapping(value="/email/bind", method = RequestMethod.POST)
	def bindEmail(HttpSession session, @RequestParam(value="emailPrefix") emailPrefix, @RequestParam(value="emailSufix") emailSufix){
		def openId = session.getAttribute("wechat.openId");
		if(openId == null){
			throw new IllegalArgumentException("OpenId not found");
		}

		def user = userRepository.getByOpenId(openId);
		if(user != null){
			if(user.validation){
				return new ModelAndView("wechat/warning", "message", "邮箱已经绑定: ${user.email}");
			}
		}

		def email = emailPrefix+emailSufix;
		def token = UUID.randomUUID().toString();

		def emailUser = userRepository.getByEmail(email);
		if(emailUser != null && emailUser.validation){
			return new ModelAndView("wechat/warning", "message", "邮箱地址已经被使用!");
		}

		if(user == null){
			user = new User(email:email, token:token, openId:openId);
		}else{
			user.email = email;
			user.token = token;
			user.openId = openId;
		}

		def message = new SimpleMailMessage();
		message.from = "nuke.wiki@derbygroupmail.com";
		message.to = new String[1];
		message.to[0] = email;
		message.subject = MimeUtility.encodeText("激活邮箱", "UTF-8", "B");
		message.text= "${externalUrl}/wechat/email/verify?token=${token}&email=${email}";
		mailSender.send(message);
		userRepository.save(user);
		return new ModelAndView("wechat/successful", "message", "请前往邮箱激活!");
	}

	@RequestMapping(value="/email/verify")
	def verifyEmail(@RequestParam(value="token") token, @RequestParam(value="email") email){
		def user = userRepository.getByEmail(email);
		if(user == null){
			throw new IllegalArgumentException("不存在的邮箱地址, 请重新绑定邮箱");
		}

		if(user.validation){
			return new ModelAndView("wechat/warning", "message", "邮箱已经激活");
		}

		if(user.token != token){
			throw new IllegalArgumentException("无效请求, 请重新绑定邮箱");
		}

		user.validation = true;
		userRepository.save(user);
		return new ModelAndView("wechat/successful", "message", "激活成功!");
	}
}