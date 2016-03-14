package com.derby.nuke.wheatgrass.repository;

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource

import com.derby.nuke.wheatgrass.entity.UserSkill

@RepositoryRestResource(path = "userskill")
interface UserSkillRepository extends JpaRepository<UserSkill, String> {
	UserSkill getByUserId(@Param("user.userId") String userId);
	
}