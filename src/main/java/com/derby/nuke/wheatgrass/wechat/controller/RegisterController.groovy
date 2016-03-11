package com.derby.nuke.wheatgrass.wechat.controller;

import java.time.LocalDate

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

import com.derby.nuke.wheatgrass.entity.Sex
import com.derby.nuke.wheatgrass.entity.User
import com.derby.nuke.wheatgrass.repository.UserRepository
import com.derby.nuke.wheatgrass.wechat.OAuthRequired

@RestController
@OAuthRequired(false)
class RegisterController extends WechatController{
	
	@Value('${web.external.url}')
	def externalUrl;

	@RequestMapping(value="/register", method = RequestMethod.GET)
	def register(@RequestParam String code){
		def userId = wechatService.getUserId(code);
		def user = userRepository.getByUserId(userId);
		if(user == null){
			user = new User();
		}
		
		def userInfo = wechatService.getUserInfo(userId);
		user.userId = userInfo.userid;
		user.name = userInfo.name;
		user.email = userInfo.email;
		user.nickName = userInfo.weixinid;
		user.position = userInfo.position;
		if(userInfo.gender == "1"){
			user.sex = Sex.Male; 
		} else if(userInfo.gender == "2"){
			user.sex = Sex.Female; 
		}
		if(user.imageUrl == null){
			user.imageUrl = userInfo.avatar;
		}
		userInfo.extattr.attrs.each{attr->
			if(attr.name == "生日" && attr.value != null){
				user.birthday = LocalDate.parse(attr.value);
			}else if(attr.name == "籍贯"){
				user.birthplace = attr.value;
			}
		}
		if(userInfo.department!=null && userInfo.department.size() > 0){
			def department = wechatService.getDepartment(userInfo.department[0]);
			user.department = department.name;
		}
		
		wechatService.register(userId);
		userRepository.save(user);
		return new ModelAndView("wechat/successful", "message", "加入成功!");
	}

}