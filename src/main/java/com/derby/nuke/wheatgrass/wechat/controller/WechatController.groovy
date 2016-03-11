package com.derby.nuke.wheatgrass.wechat.controller;

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping

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
	
}