package com.derby.nuke.wheatgrass.repository;

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource

import com.derby.nuke.wheatgrass.entity.Skill
import com.derby.nuke.wheatgrass.entity.User
import com.derby.nuke.wheatgrass.entity.UserSkill

@RepositoryRestResource(path = "userSkill")
interface UserSkillRepository extends JpaRepository<UserSkill, String> {
	@Query("from UserSkill where user.userId=? and skill.id=?")
	UserSkill getByUserAndSkill(String userId, String skillId);

	@Query("select distinct user.userId from UserSkill where skill.id in(:skillIds)")
	Collection<String> getUserIdsBySkills(@Param("skillIds") Collection<String> skillIds);
	
	@Query("select us.user from UserSkill us inner join us.skill where us.skill.id=? order by us.user.name")
	Collection<User> findUsersBySkill(String skillId);
	
	@Query("select new map(s, count(us)) from UserSkill us right join us.skill s group by s.id order by s.category desc, s.name")
	List<Map<Skill,Integer>> findSkillsWithExpertCount();
	
}