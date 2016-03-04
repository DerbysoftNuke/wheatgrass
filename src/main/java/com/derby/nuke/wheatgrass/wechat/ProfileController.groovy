package com.derby.nuke.wheatgrass.wechat;

import javax.servlet.http.HttpSession

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

import com.derby.nuke.wheatgrass.entity.UserSkill
import com.derby.nuke.wheatgrass.repository.SkillRepository
import com.derby.nuke.wheatgrass.repository.UserRepository
import com.google.common.collect.Lists
import com.google.common.collect.Sets

@RestController("UserProfileController")
class ProfileController extends WechatController{
	
	@Autowired
	def SkillRepository skillRepository;
	
	@RequestMapping(value="/profile", method = RequestMethod.GET)
	def getProfile(HttpSession session, @RequestParam(value="id", required=false) id){
		def openId = session.getAttribute("wechat.openId");
		if(openId == null){
			throw new IllegalArgumentException("OpenId not found");
		}

		def user;
		if(id != null){
			user = userRepository.findOne(id);
		}else{
			user = userRepository.getByOpenId(openId);
		}
		
		def allSkills = Lists.newArrayList(skillRepository.findAll());
		def skills = [];
		allSkills.each {skill->
			def a = user.skills.find{userSkill->
				userSkill.skill.equals(skill)
			}
			
			if(a == null){
				skills.add(skill);
			}
		}
		return new ModelAndView("wechat/profile", ["user": user, "skills": skills]);
	}
	
	@RequestMapping(value="/profile", method = RequestMethod.POST)
	def updateProfile(HttpSession session, @RequestParam(value="skills", required=false) ids){
		def openId = session.getAttribute("wechat.openId");
		if(openId == null){
			throw new IllegalArgumentException("OpenId not found");
		}

		def skillIds = Sets.newHashSet();
		if(ids != null){
			skillIds = Sets.newHashSet(ids);
		}
		def user = userRepository.getByOpenId(openId);
		def allSkills = skillRepository.findAll(skillIds);
		
		def deletedUserSkills = Sets.newHashSet();
		user.skills.each {userSkill->
			if(!skillIds.contains(userSkill.skill.id)){
				deletedUserSkills.add(userSkill);
			}else{
				skillIds.remove(userSkill.skill.id);
			}
		}
		
		user.skills.removeAll(deletedUserSkills);
		skillIds.each {skillId->
			user.skills.add(new UserSkill(user: user, skill: skillRepository.getOne(skillId)));
		}
		
		userRepository.saveAndFlush(user);
		return getProfile(session, null);
	}
}