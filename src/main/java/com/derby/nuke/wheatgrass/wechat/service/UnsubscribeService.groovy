package com.derby.nuke.wheatgrass.wechat.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import com.derby.nuke.wheatgrass.repository.UserRepository
import com.derby.nuke.wheatgrass.wechat.model.Article
import com.derby.nuke.wheatgrass.wechat.model.MessageContext
import com.derby.nuke.wheatgrass.wechat.model.Message.MessageType

@Service
class UnsubscribeService {

	@Autowired
	def UserRepository userRepository;

	def invoke(params){
		def user = userRepository.getByOpenId(MessageContext.get().userId);
		if(user != null){
			userRepository.delete(user);
		}
		
		return [
			type: MessageType.text,
			content: ""
		];
	}
	
}