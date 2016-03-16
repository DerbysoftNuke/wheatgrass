package com.derby.nuke.wheatgrass.wechat.controller;


import javax.servlet.http.HttpSession
import javax.transaction.Transactional

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

import com.derby.nuke.wheatgrass.entity.PointHistory
import com.derby.nuke.wheatgrass.entity.UserSkill
import com.derby.nuke.wheatgrass.repository.PointHistoryRepository
import com.derby.nuke.wheatgrass.repository.SkillRepository
import com.derby.nuke.wheatgrass.repository.UserMedalRepository
import com.derby.nuke.wheatgrass.repository.UserRepository
import com.derby.nuke.wheatgrass.repository.UserSkillRepository
import com.derby.nuke.wheatgrass.wechat.Consts
import com.google.common.collect.Lists
import com.google.common.collect.Sets

@RestController("UserProfileController")
class ProfileController extends WechatController{

	@Autowired
	def SkillRepository skillRepository;

	@Autowired
	def UserMedalRepository userMedalRepository;

	@Autowired
	def UserSkillRepository userSkillRepository;

	@Autowired
	def PointHistoryRepository pointHistoryRepository;

	@RequestMapping(value="/profiles", method = RequestMethod.GET)
	def listUsers(){
		def users = userRepository.findAll();
		return new ModelAndView("wechat/users", ["users": users]);
	}

	@RequestMapping(value="/profile", method = RequestMethod.GET)
	def getProfile(HttpSession session, @RequestParam(value="userId", required=false) id){
		def userId = session.getAttribute(Consts.USER_ID);
		if(userId == null){
			throw new IllegalArgumentException("UserId not found");
		}

		def user;
		if(id != null){
			user = userRepository.getByUserId(id);
		}else{
			user = userRepository.getByUserId(userId);
		}

		def allSkills = Lists.newArrayList(skillRepository.findAll());
		def skills = [];
		allSkills.each {skill->
			def a = user.skills.find{userSkill->
				userSkill.skill.equals(skill);
			}

			if(a == null){
				skills.add(skill);
			}
		}
		def pointedUserSkillIds=Sets.newHashSet();
		user.skills.each {userSkill->
			if(pointHistoryRepository.getByOperatorAndUserSkill(userId, userSkill.id)!=null){
				pointedUserSkillIds.add(userSkill.id);
			}
		}
		def userMedals = userMedalRepository.findByUserId(user.id);
		def medals = [];
		userMedals.each{
			medals.add(it.medal);
		}
		return new ModelAndView("wechat/profile", ["user": user, "skills": skills, medals: medals,"sessionUserId":userId,"pointedUserSkillIds":pointedUserSkillIds]);
	}

	@RequestMapping(value="/profile", method = RequestMethod.POST)
	def updateProfile(HttpSession session, @RequestParam(value="skills", required=false) ids){
		def userId = session.getAttribute(Consts.USER_ID);
		if(userId == null){
			throw new IllegalArgumentException("UserId not found");
		}

		def skillIds = Sets.newHashSet();
		if(ids != null){
			skillIds = Sets.newHashSet(ids);
		}
		def user = userRepository.getByUserId(userId);
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
		def deletedUserSkillIds=Sets.newHashSet();
		deletedUserSkills.each{
			deletedUserSkill->
			deletedUserSkillIds.add(deletedUserSkill.id);
		}
		if(deletedUserSkillIds.size()>0){
			pointHistoryRepository.deleteByUserSkill(deletedUserSkillIds);
		}
		userRepository.saveAndFlush(user);
		return getProfile(session, null);
	}

	@RequestMapping(value="/profile/addPoint", method = RequestMethod.POST)
	@Transactional
	def addPoint(HttpSession session, @RequestParam(value="targetUserId", required=true)String targetUserId,@RequestParam(value="skillId", required=true)String skillId){
		def userId = session.getAttribute(Consts.USER_ID);
		if(userId == null){
			throw new IllegalArgumentException("UserId not found");
		}
		UserSkill userSkill=userSkillRepository.getByUserAndSkill(targetUserId, skillId);
		if(userSkill==null){
			throw new IllegalArgumentException("userSkill not found");
		}
		PointHistory  pointHistory=pointHistoryRepository.getByOperatorAndUserSkill(userId, userSkill.id);
		if(pointHistory!=null){
			throw new IllegalArgumentException("you have already added this skill's point");
		}
		userSkill.point++;
		userSkillRepository.saveAndFlush(userSkill);
		pointHistoryRepository.saveAndFlush(new PointHistory(operator:userRepository.getByUserId(userId),userSkill:userSkill,operationTime:new Date()));
		return userSkill.point;
	}

	@RequestMapping(value="/profile/cancelPoint", method = RequestMethod.POST)
	@Transactional
	def cancelPoint(HttpSession session, @RequestParam(value="targetUserId", required=true)String targetUserId,@RequestParam(value="skillId", required=true)String skillId){
		def userId = session.getAttribute(Consts.USER_ID);
		if(userId == null){
			throw new IllegalArgumentException("UserId not found");
		}
		UserSkill userSkill=userSkillRepository.getByUserAndSkill(targetUserId, skillId);
		if(userSkill==null){
			throw new IllegalArgumentException("userSkill not found");
		}
		PointHistory  pointHistory=pointHistoryRepository.getByOperatorAndUserSkill(userId, userSkill.id);
		if(PointHistory==null){
			throw new IllegalArgumentException("you have't added this skill's point");
		}
		userSkill.point--;
		userSkillRepository.saveAndFlush(userSkill);
		pointHistoryRepository.delete(pointHistory);
		return userSkill.point;
	}
}
