package com.derby.nuke.wheatgrass.wechat.controller

import javax.servlet.http.HttpSession
import javax.transaction.Transactional

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

import com.derby.nuke.wheatgrass.entity.Question
import com.derby.nuke.wheatgrass.repository.QuestionRepository
import com.derby.nuke.wheatgrass.repository.SkillRepository
import com.derby.nuke.wheatgrass.repository.UserSkillRepository
import com.derby.nuke.wheatgrass.wechat.Consts
import com.google.common.collect.Sets

@RestController("QuestionController")
class QuestionController extends WechatController{

	@Autowired
	def SkillRepository skillRepository;

	@Autowired
	def QuestionRepository questionRepository;

	@Autowired
	def UserSkillRepository userSkillRepository;

	@RequestMapping(value="/question", method = RequestMethod.GET)
	def getProfile(HttpSession session, @RequestParam(value="questionId", required=true) questionId){
		def question = questionRepository.getOne(questionId);
		return new ModelAndView("wechat/question", ["question": question]);
	}
	
	@RequestMapping(value="/askQuestion", method = RequestMethod.GET)
	def getProfile(HttpSession session){
		return new ModelAndView("wechat/ask_question");
	}

	@RequestMapping(value="/askQuestion", method = RequestMethod.POST)
	@Transactional
	def askQuestion(HttpSession session, @RequestParam(value="title", required=true) title, @RequestParam(value="content", required=true) content,@RequestParam(value="skills", required=false) skills){
		def userId = session.getAttribute(Consts.USER_ID);
		if(userId == null){
			throw new IllegalArgumentException("UserId not found");
		}
		questionRepository.saveAndFlush(new Question("title":title,"content":content,"proposer":userRepository.getByUserId(userId),"createTime":new Date()));
		def skillIds = Sets.newHashSet();
		if(skills != null){
			skillIds = Sets.newHashSet(skills);
			wechatService.questionNotify(userSkillRepository.getUserIdsBySkills(skillIds),"you have one question to answer");
		}
	}
}
