package com.derby.nuke.wheatgrass.wechat.controller

import javax.servlet.http.HttpSession
import javax.transaction.Transactional

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
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

	def pageSize=7;

	@RequestMapping(value="/question", method = RequestMethod.GET)
	def getProfile(HttpSession session, @RequestParam(value="questionId", required=true) questionId){
		def userId = session.getAttribute(Consts.USER_ID);
		if(userId == null){
			throw new IllegalArgumentException("UserId not found");
		}
		def question = questionRepository.getOne(questionId);
		return new ModelAndView("wechat/question", ["question": question]);
	}

	@RequestMapping(value="/askQuestion", method = RequestMethod.GET)
	def getProfile(HttpSession session){
		def userId = session.getAttribute(Consts.USER_ID);
		if(userId == null){
			throw new IllegalArgumentException("UserId not found");
		}
		return new ModelAndView("wechat/ask_question",["skills":skillRepository.findAll()]);
	}

	@RequestMapping(value="/askQuestion", method = RequestMethod.POST)
	@Transactional
	def askQuestion(HttpSession session, @RequestParam(value="title", required=true) title, @RequestParam(value="content", required=true) content,@RequestParam(value="skills", required=false) skills,@RequestParam(value="all", required=false) all){
		def userId = session.getAttribute(Consts.USER_ID);
		if(userId == null){
			throw new IllegalArgumentException("UserId not found");
		}
		Question question=questionRepository.saveAndFlush(new Question("title":title,"content":content,"proposer":userRepository.getByUserId(userId),"createTime":new Date()));
		def skillIds = Sets.newHashSet();
		String notifyContent="you have one question to answer";
		if(all!=null && "true".equals(all)){
			wechatService.questionNotifyAll(notifyContent);
		}else{
			if(skills != null){
				skillIds = Sets.newHashSet(skills);
				wechatService.questionNotify(userSkillRepository.getUserIdsBySkills(skillIds),notifyContent);
			}
		}
		getProfile(session,question.getId());
	}
	@RequestMapping(value="/question/list", method = RequestMethod.GET)
	def list(HttpSession session){
		def userId = session.getAttribute(Consts.USER_ID);
		if(userId == null){
			throw new IllegalArgumentException("UserId not found");
		}
		PageRequest pageRequest=new PageRequest(0, pageSize, Direction.DESC,"createTime");
		Page<Question> page=questionRepository.findAll(pageRequest);
		return new ModelAndView("wechat/question_list",["questions":page.content]);
	}

	@RequestMapping(value="/question/loadQuestions", method = RequestMethod.POST)
	def loadQuestions(HttpSession session,@RequestParam(value="currentPage", required=true)Integer currentPage){
		def userId = session.getAttribute(Consts.USER_ID);
		if(userId == null){
			throw new IllegalArgumentException("UserId not found");
		}
		PageRequest pageRequest=new PageRequest(currentPage, pageSize, Direction.DESC,"createTime");
		Page<Question> page=questionRepository.findAll(pageRequest);
		return page.content;
	}
}
