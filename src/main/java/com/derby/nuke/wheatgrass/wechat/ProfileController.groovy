package com.derby.nuke.wheatgrass.wechat;

import javax.servlet.http.HttpSession

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

import com.derby.nuke.wheatgrass.repository.UserRepository

@RestController("UserProfileController")
class ProfileController extends WechatController{

	@RequestMapping(value="/profile", method = RequestMethod.GET)
	def profile(HttpSession session, @RequestParam(value="id", required=false) id){
		def openId = session.getAttribute("wechat.openId");
		if(openId == null){
			throw new IllegalArgumentException("OpenId not found");
		}

		def user;
		if(id != null){
			user = userRepository.findOne(id);
		}else{
			user = userRepository.getByOpenId(openId);
		}

		return new ModelAndView("wechat/profile", "user", user);
	}
}