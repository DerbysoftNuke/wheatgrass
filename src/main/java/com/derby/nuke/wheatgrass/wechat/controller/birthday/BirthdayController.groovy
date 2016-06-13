package com.derby.nuke.wheatgrass.wechat.controller.birthday

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import javax.servlet.http.HttpSession
import javax.transaction.Transactional

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import com.derby.nuke.wheatgrass.entity.BirthdayWish
import com.derby.nuke.wheatgrass.entity.BirthdayWishWord
import com.derby.nuke.wheatgrass.entity.User
import com.derby.nuke.wheatgrass.repository.BirthdayWishRepository
import com.derby.nuke.wheatgrass.wechat.Consts
import com.derby.nuke.wheatgrass.wechat.OAuthRequired
import com.derby.nuke.wheatgrass.wechat.controller.WechatController
import com.derby.nuke.wheatgrass.wechat.service.BirthdayService
import com.google.common.collect.Lists

@RequestMapping("/wechat/birthday")
@OAuthRequired
@RestController
class BirthdayController extends WechatController{

	@Autowired
	BirthdayService birthdayService;
	
	@Autowired
	BirthdayWishRepository birthdayWishRepository;

	def getViewPrefix(){
		return "/wechat/birthday";
	}

	def getRedirectPrefix(){
		return "/wechat/birthday";
	}

	@RequestMapping(value="/showWish", method = RequestMethod.GET)
	def showWish(HttpSession session,@RequestParam(value="birthday") birthday){
		def userId = session.getAttribute(Consts.USER_ID);
		if(userId == null){
			throw new IllegalArgumentException("UserId not found");
		}
		birthday=LocalDate.parse(birthday);
		BirthdayWish birthdayWish=birthdayWishRepository.findByBirthdayAndUser(birthday,userId);
		def age=birthday.year-birthdayWish.user.birthday.year;
		List<BirthdayWishWord> birthdayWishWords= birthdayWish.birthdayWishWords;
		List<List<BirthdayWishWord>> birthdayWishWordList=Lists.newArrayList();
		for(int index=0;index<birthdayWishWords.size();index++){
			if(index%9==0){
				birthdayWishWordList.add(Lists.newArrayList());
			}
			birthdayWishWordList.get(birthdayWishWordList.size()-1).add(birthdayWishWords.get(index));
		}

		return view("show_wish",["birthdayWish":birthdayWish,"birthdayWishWordList":birthdayWishWordList,"age":age,"birthday":birthday.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))]);
	}

	@RequestMapping(value="/listWish", method = RequestMethod.GET)
	def listWish(HttpSession session){
		def userId = session.getAttribute(Consts.USER_ID);
		if(userId == null){
			throw new IllegalArgumentException("UserId not found");
		}
		LocalDate nextMonth = LocalDate.now().plusMonths(1);
		List<BirthdayWish> birthdayWishes = birthdayService.findBirthdayWishes(nextMonth);
		return view("list_wish",["userId":userId, "birthdayWishes": birthdayWishes, "month": nextMonth.getMonthValue()]);
	}

	@RequestMapping(value="/sendWish", method = RequestMethod.POST)
	@Transactional
	def sendWish(HttpSession session, @RequestParam(value="birthdayWishId") birthdayWishId, @RequestParam(value="type", required=false) type, @RequestParam(value="word", required=false) word){
		def userId = session.getAttribute(Consts.USER_ID);
		if(userId == null){
			throw new IllegalArgumentException("UserId not found");
		}
		BirthdayWish birthdayWish = birthdayService.findOne(birthdayWishId);

		if(type!=null){
			birthdayWish.sendFlowerUserIds.remove(userId);
			birthdayWish.sendCakeUserIds.remove(userId);
			birthdayWish.sendFireworkUserIds.remove(userId);
			if("flower".equals(type)){
				birthdayWish.sendFlowerUserIds.add(userId);
			} else if("cake".equals(type)){
				birthdayWish.sendCakeUserIds.add(userId);
			} else if("firework".equals(type)){
				birthdayWish.sendFireworkUserIds.add(userId);
			}
			birthdayService.saveOrUpdate(birthdayWish);
			return [
				"flowerCounts": birthdayWish.sendFlowerUserIds.size(),
				"cakeCounts": birthdayWish.sendCakeUserIds.size(), 
				
				"fireworkCounts": birthdayWish.sendFireworkUserIds.size()
			];
		} else if(word!=null){
			Set<String> wisherIds = birthdayWish.getSendWishWordUserIds();
			BirthdayWishWord birthdayWishWord = new BirthdayWishWord();
			birthdayWishWord.setContent(word);
			birthdayWishWord.setCreateTime(new Date());
			User user = userRepository.getByUserId(userId);
			birthdayWishWord.setWisher(user);
			birthdayWishWord.setBirthdayWish(birthdayWish);
			birthdayWish.getBirthdayWishWords().add(birthdayWishWord);
			birthdayService.saveOrUpdate(birthdayWish);
			return ["wisherCounts":birthdayWish.getSendWishWordUserIds().size()];
		}
	}
}