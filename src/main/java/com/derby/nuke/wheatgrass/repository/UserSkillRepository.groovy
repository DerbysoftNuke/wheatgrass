package com.derby.nuke.wheatgrass.repository;

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.rest.core.annotation.RepositoryRestResource

import com.derby.nuke.wheatgrass.entity.UserSkill

@RepositoryRestResource(path = "userSkill")
interface UserSkillRepository extends JpaRepository<UserSkill, String> {
	@Query("from UserSkill where user.userId=? and skill.id=?")
	UserSkill getByUserAndSkill(String userId, String skillId);
}