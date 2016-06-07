package com.derby.nuke.wheatgrass.wechat.service

import java.time.LocalDate

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import com.derby.nuke.wheatgrass.entity.User
import com.derby.nuke.wheatgrass.repository.UserRepository
import com.derby.nuke.wheatgrass.repository.UserSpecifications

@Service
class BirthdayService {

	@Autowired
	UserRepository userRepository;
	@Autowired
	WechatService wechatService;

	@Value('${web.external.url}')
	def externalUrl;

	def sendReminder(LocalDate today){
		List<User> users=userRepository.findAll(UserSpecifications.userHasBirthday(today));
		def userIds=[];
		users.each{ user->
			userIds.add(user.userId);
		}
		def messageType = "news";
		def message = [
			news: [
				articles: [
					[
						title: "在这个特殊的日子里......",
						description: "在这个特殊的日子里......",
						url: externalUrl+"/wechat/expert/question?questionId=",
						picurl: "PIC_URL"
					],
					[
						title: "快来，快来！收礼物！收礼物咯！！",
						description: "快来，快来！收礼物！收礼物咯！！",
						url: externalUrl+"/wechat/expert/question?questionId=",
						picurl: "PIC_URL"
					]
				]
			]
		]
		//userIds=["caochengkai"];
		wechatService.sendMessage(userIds, messageType, message);
	}
}
