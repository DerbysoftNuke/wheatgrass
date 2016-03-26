package com.derby.nuke.wheatgrass.wechat.controller.expert

import javax.servlet.http.HttpSession
import javax.transaction.Transactional

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

import com.derby.nuke.wheatgrass.entity.Answer;
import com.derby.nuke.wheatgrass.entity.Question
import com.derby.nuke.wheatgrass.repository.AnswerRepository
import com.derby.nuke.wheatgrass.repository.QuestionRepository
import com.derby.nuke.wheatgrass.repository.SkillRepository
import com.derby.nuke.wheatgrass.repository.UserSkillRepository
import com.derby.nuke.wheatgrass.wechat.Consts
import com.google.common.collect.Sets

@RestController("QuestionController")
class QuestionController extends ExpertController{

	@Autowired
	def SkillRepository skillRepository;

	@Autowired
	def QuestionRepository questionRepository;
	
	@Autowired
	def AnswerRepository answerRepository;

	@Autowired
	def UserSkillRepository userSkillRepository;

	@Value('${web.external.url}')
	def externalUrl;

	def pageSize=7;
	
	@RequestMapping(value="/questions", method = RequestMethod.GET)
	def list(){
		PageRequest pageRequest=new PageRequest(0, pageSize, Direction.DESC,"createTime");
		Page<Question> page=questionRepository.findAll(pageRequest);
		return view("questions",["questions": page.content,"totalCount":page.totalElements]);
	}

	@RequestMapping(value="/questions", method = RequestMethod.POST)
	def list(@RequestParam(value="currentPage")Integer currentPage){
		PageRequest pageRequest=new PageRequest(currentPage, pageSize, Direction.DESC,"createTime");
		Page<Question> page=questionRepository.findAll(pageRequest);
		return page.content;
	}

	@RequestMapping(value="/question", method = RequestMethod.GET)
	def viewQuestion(@RequestParam(value="questionId") questionId){
		def question = questionRepository.findOne(questionId);
		if(question == null){
			throw new IllegalArgumentException("Question not found");
		}

		return view("question", ["question": question]);
	}
	
	@RequestMapping(value="/question/answer", method = RequestMethod.POST)
	@Transactional
	def answerQuestion(HttpSession session, @RequestParam(value="questionId") questionId, @RequestParam(value="content") content){
		def userId = session.getAttribute(Consts.USER_ID);
		if(userId == null){
			throw new IllegalArgumentException("UserId not found");
		}

		def user = userRepository.getByUserId(userId);
		
		Question question = questionRepository.findOne(questionId);
		Answer answer = answerRepository.saveAndFlush(new Answer("question":["id":questionId],"createTime":new Date(),"content":content, answerer:user));
		def messageType = "news";
		def message = [
			news: [
				articles: [
					[
						title: question.title,
						description: "哇, "+user.name+"回答了你的问题, 快来看看吧!",
						url: externalUrl+"/wechat/expert/question?questionId="+questionId
					]
				]
			]
		]
		wechatService.sendMessage([question.getProposer().getUserId()], messageType, message);
		return redirectTo("/question", [questionId: question.getId()]);
	}
	
	@RequestMapping(value="/question/answer/best", method = RequestMethod.POST)
	@Transactional
	def bestAnswerQuestion(HttpSession session, @RequestParam(value="questionId") questionId, @RequestParam(value="answerId") answerId){
		def userId = session.getAttribute(Consts.USER_ID);
		if(userId == null){
			throw new IllegalArgumentException("UserId not found");
		}

		def user = userRepository.getByUserId(userId);
		
		Question question = questionRepository.findOne(questionId);
		question.setRecognizedAnswerId(answerId);
		questionRepository.saveAndFlush(question);
		Answer answer = answerRepository.findOne(answerId);
		def messageType = "news";
		def message = [
			news: [
				articles: [
					[
						title: question.title,
						description: "哇, 恭喜，你的回答已经被采纳为最佳答案！",
						url: externalUrl+"/wechat/expert/question?questionId="+questionId
					]
				]
			]
		]
		//TODO 被采纳者获得积分或勋章
		wechatService.sendMessage([answer.getAnswerer().getUserId()], messageType, message);
		return redirectTo("/question", [questionId: question.getId()]);
	}

	@RequestMapping(value="/question/ask", method = RequestMethod.GET)
	def askQuestion(){
		return view("ask_question",["skills":skillRepository.findAll()]);
	}

	@RequestMapping(value="/question/ask", method = RequestMethod.POST)
	@Transactional
	def askQuestion(HttpSession session, @RequestParam(value="title") title, @RequestParam(value="content") content,@RequestParam(value="skills", required=false) skillIds, @RequestParam(value="all", required=false) all){
		def userId = session.getAttribute(Consts.USER_ID);
		if(userId == null){
			throw new IllegalArgumentException("UserId not found");
		}

		def user = userRepository.getByUserId(userId);
		Question question=questionRepository.saveAndFlush(new Question("title":title, "content":content, "proposer":user, "createTime":new Date()));
		def messageType = "news";
		def message = [
			news: [
				articles: [
					[
						title: title,
						description: "哇, 你被"+user.name+"@了, 快来回答问题赢取积分吧!",
						url: externalUrl+"/wechat/expert/question?questionId="+question.id
					]
				]
			]
		]
		
		if("true".equals(all) || "on".equals(all)){
			wechatService.sendMessage(null, messageType, message);
		}else if(skillIds != null){
			def userIds = userSkillRepository.getUserIdsBySkills(Sets.newHashSet(skillIds));
			if(!userIds.isEmpty()){
				wechatService.sendMessage(userIds, messageType, message);
			}
		}
		return redirectTo("/question", [questionId: question.getId()]);
	}
	
}
