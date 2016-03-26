package com.derby.nuke.wheatgrass.wechat.controller;

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView

import com.derby.nuke.wheatgrass.repository.UserRepository
import com.derby.nuke.wheatgrass.wechat.OAuthRequired
import com.derby.nuke.wheatgrass.wechat.service.WechatService

@RequestMapping("/wechat")
@OAuthRequired
class WechatController{
	
	protected final def log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	def UserRepository userRepository;
	@Autowired
	def WechatService wechatService;
	
	def redirectTo(String view, Map params){
		if(params==null||params.isEmpty()){
			return new RedirectView(getRedirectPrefix()+view,true)
		}
		def paramString = "";
		params.each{k,v->
			paramString+="&"+k+"="+v
		}
		return new RedirectView(getRedirectPrefix()+view+"?"+paramString.substring(1),true)
	}
	
	def view(String view, Map params){
		return new ModelAndView(getViewPrefix() + "/" + view, params);
	}
	
	def getViewPrefix(){
		return "/";
	}
	
	def getRedirectPrefix(){
		return "/";
	}
	
}