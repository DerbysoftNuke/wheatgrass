package com.derby.nuke.wheatgrass.wechat.controller.expert

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

import com.derby.nuke.wheatgrass.repository.SkillRepository

@RestController
class SkillController extends ExpertController {

	@Autowired
	def SkillRepository skillRepository;

	@RequestMapping(value="/skills", method = RequestMethod.GET)
	def list(){
		def skills = skillRepository.findAll();
		return new ModelAndView("wechat/expert/skills", ["skills": skills]);
	}
}
