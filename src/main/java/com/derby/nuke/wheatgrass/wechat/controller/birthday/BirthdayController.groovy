package com.derby.nuke.wheatgrass.wechat.controller.birthday

import com.derby.nuke.wheatgrass.web.AjaxResponse
import com.google.common.base.Splitter
import com.google.common.collect.Sets

import java.time.LocalDate

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

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    def list(HttpSession session, @RequestParam(value = "date") String date) {
        def userId = session.getAttribute(Consts.USER_ID);
        if (userId == null) {
            throw new IllegalArgumentException("UserId not found");
        }
        LocalDate localDate = LocalDate.parse(date);
        List<BirthdayWish> birthdayWishes = birthdayService.findBirthdayWishes(localDate);
        Set<String> birthdayWishIds = birthdayService.findSendingOutWishIds(userId, localDate);
        return view("list", ["month": localDate.getMonthValue(), "userId": userId, "birthdayWishes": birthdayWishes, "birthdayWishIds": birthdayWishIds]);
    }

	@RequestMapping(value="/wish/send", method = RequestMethod.POST)
	@Transactional
	def sendBirthdayWish(HttpSession session, @RequestParam(value="birthdayWishId") String birthdayWishId, @RequestParam(value="gifts", required=false) String gifts, @RequestParam(value="word", required=false)String word){
		def userId = session.getAttribute(Consts.USER_ID);
		if (userId == null) {
			throw new IllegalArgumentException("UserId not found");
		}
        Set<String> giftSet = Sets.newHashSet(Splitter.on(",").split(gifts).iterator());
		BirthdayWish birthdayWish = birthdayService.findOne(birthdayWishId);
		BirthdayWishWord birthdayWishWord = birthdayService.findBirthdayWishWord(birthdayWishId, userId)
		if (birthdayWishWord == null) {
			birthdayWishWord = new BirthdayWishWord()
			birthdayWishWord.setContent(word);
			birthdayWishWord.setCreateTime(new Date());
			User user = userRepository.getByUserId(userId);
			birthdayWishWord.setWisher(user);
			birthdayWishWord.setBirthdayWish(birthdayWish);
            if(giftSet!=null){
                birthdayWishWord.setGifts(giftSet)
            }
			birthdayService.saveOrUpdate(birthdayWishWord);

			if (giftSet != null) {
				birthdayWish.countGifts(giftSet);
				birthdayService.saveOrUpdate(birthdayWish);
			}
			return AjaxResponse.successWithData(["giftCounts": birthdayWish.giftCounts])
		} else {
			return AjaxResponse.fail("AlreadySendWish");
		}
	}

	@RequestMapping(value = "/showWish", method = RequestMethod.GET)
	@Transactional
	def showWish(HttpSession session, @RequestParam(value = "birthdayWishId") String birthdayWishId) {
		def userId = session.getAttribute(Consts.USER_ID)
		if (userId == null) {
			throw new IllegalArgumentException("UserId not found")
		}
		BirthdayWish birthdayWish = birthdayService.findOne(birthdayWishId)
		List<BirthdayWishWord> birthdayWishWords = birthdayService.findBirthdayWishWords(birthdayWishId)

		return view("showWish", ["userId": userId, "birthdayWish": birthdayWish, "birthdayWishWords": birthdayWishWords]);
	}

	@RequestMapping(value = "/wish", method = RequestMethod.GET)
	@Transactional
	def wish(HttpSession session, @RequestParam(value = "birthday") String birthday) {
        def userId = session.getAttribute(Consts.USER_ID)
        if (userId == null) {
            throw new IllegalArgumentException("UserId not found")
        }
        BirthdayWish birthdayWish = birthdayWishRepository.findByBirthdayAndUser(LocalDate.parse(birthday), userId)
        return view("wish", ["userId": userId, "birthdayWish": birthdayWish])
	}
}
