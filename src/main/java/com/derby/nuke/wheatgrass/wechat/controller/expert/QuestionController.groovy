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

import com.derby.nuke.wheatgrass.entity.Question
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
	def UserSkillRepository userSkillRepository;

	@Value('${web.external.url}')
	def externalUrl;

	def pageSize=7;
	
	@RequestMapping(value="/questions", method = RequestMethod.GET)
	def list(){
		return new ModelAndView("wechat/expert/questions",["questions": list(0)]);
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

		return new ModelAndView("wechat/expert/question", ["question": question]);
	}

	@RequestMapping(value="/question/ask", method = RequestMethod.GET)
	def askQuestion(){
		return new ModelAndView("wechat/expert/ask_question",["skills":skillRepository.findAll()]);
	}

	@RequestMapping(value="/question/ask", method = RequestMethod.POST)
	@Transactional
	def askQuestion(HttpSession session, @RequestParam(value="title") title, @RequestParam(value="content") content,@RequestParam(value="skills", required=false) skillIds, @RequestParam(value="all", required=false) all){
		def userId = session.getAttribute(Consts.USER_ID);
		if(userId == null){
			throw new IllegalArgumentException("UserId not found");
		}

		Question question=questionRepository.saveAndFlush(new Question("title":title,"content":content,"proposer":userRepository.getByUserId(userId),"createTime":new Date()));
		String notifyContent="<a href=${externalUrl}/wechat/question?questionId="+question.getId()+"'>you have one question to answer</a>";
		if("true".equals(all)){
			wechatService.questionNotifyAll(notifyContent);
		}else{
			if(skillIds != null){
				wechatService.questionNotify(userSkillRepository.getUserIdsBySkills(Sets.newHashSet(skillIds)),notifyContent);
			}
		}
		viewQuestion(question.getId());
	}
	
}
