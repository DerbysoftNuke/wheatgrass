package com.derby.nuke.wheatgrass.wechat.service

import com.derby.nuke.wheatgrass.entity.BirthdayWishWord
import com.derby.nuke.wheatgrass.repository.BirthdayWishWordRepository

import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import com.derby.nuke.wheatgrass.entity.BirthdayWish
import com.derby.nuke.wheatgrass.entity.User
import com.derby.nuke.wheatgrass.repository.BirthdayWishRepository
import com.derby.nuke.wheatgrass.repository.UserRepository
import com.derby.nuke.wheatgrass.repository.UserSpecifications
import com.derby.nuke.wheatgrass.wechat.service.support.BirthdayRpcService
import com.google.common.collect.Lists

@Service
class BirthdayService implements BirthdayRpcService{

	@Autowired
	UserRepository userRepository;
	@Autowired
	BirthdayWishRepository birthdayWishRepository;
	@Autowired
	BirthdayWishWordRepository birthdayWishWordRepository;
	@Autowired
	WechatService wechatService;

	@Value('${web.external.url}')
	def externalUrl;
	@Value('${wechat.birthday.agent.id ?: "0"}')
	def assistantAgentId;

	def sendReminder(LocalDate today){
		Collection<String> birthdayWishs=birthdayWishRepository.findUserIdsByBirthday(today);

		def messageType = "news";
		def message = [
			news: [
				articles: [
					[
						title: "在这个特殊的日子里......",
						url: externalUrl+"/wechat/birthday/showWish?birthday="+today,
						picurl: externalUrl+"/img/birthday/cover1.jpg"
					],
					[
						title: "快来，快来！收礼物！收礼物咯！！",
						url: externalUrl+"/wechat/birthday/gift?date="+today,
						picurl: externalUrl+"/img/birthday/cover2.png"
					]
				]
			]
		]
		wechatService.sendMessage(birthdayWishs, messageType, message, assistantAgentId);
	}

	List<BirthdayWish> findBirthdayWishes(LocalDate today){
		LocalDate firstDate = today.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate lastDate = today.with(TemporalAdjusters.lastDayOfMonth());
		return birthdayWishRepository.findByBirthdayBetween(firstDate, lastDate);
	}

	BirthdayWish findOne(String birthdayWishId){
		return birthdayWishRepository.findOne(birthdayWishId);
	}

	void saveOrUpdate(BirthdayWish birthdayWish){
		birthdayWishRepository.save(birthdayWish);
	}

	BirthdayWishWord findBirthdayWishWord(String birthdayWishId, String userId){
		return birthdayWishWordRepository.findByBirthdayWishIdAndWisherId(birthdayWishId,userRepository.getByUserId(userId).getId())
	}

	List<BirthdayWishWord> findBirthdayWishWords(String birthdayWishId){
		return birthdayWishWordRepository.findByBirthdayWishId(birthdayWishId)
	}

	def birthdayWishRecord(LocalDate nextMonth){
 		List<User> users=userRepository.findAll(UserSpecifications.birthdayPattern("%-"+nextMonth.toString().split("-")[1]+"-%"));
		for(User user:users){
			def day=user.birthday.day;
			if (!nextMonth.isLeapYear() && nextMonth.getMonthValue() == 2 && day == 29) {
				day=28;
			}

			LocalDate birthday=LocalDate.of(nextMonth.year,nextMonth.monthValue,day);
			try{
				birthdayWishRepository.save(new BirthdayWish("createTime":new Date(),"user":user,"birthday":birthday));
			}catch(e){}
		}

		def messageType = "news";
		def message = [
			news: [
				articles: [
					[
						title: nextMonth.getMonthValue()+"月寿星",
						description: "点击图片进入",
						url: externalUrl+"/wechat/birthday/listWish?date="+nextMonth,
						picurl: externalUrl+"/img/birthday/birthday_cover_"+nextMonth.getMonthValue()+".jpg"
					]
				]
			]
		]
		if(users.size() > 0){
			wechatService.sendMessage(["@all"], messageType, message, assistantAgentId);
		}
	}
}
