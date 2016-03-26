package com.derby.nuke.wheatgrass.wechat.controller.expert

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

import com.derby.nuke.wheatgrass.repository.UserSkillRepository

@RestController
class SkillController extends ExpertController {

	@Autowired
	def UserSkillRepository userSkillRepository;

	@RequestMapping(value="/skills", method = RequestMethod.GET)
	def list(){
		def skills = userSkillRepository.findSkillsWithExpertCount();
		return new ModelAndView("wechat/expert/skills", ["skills": skills]);
	}
}
