package com.derby.nuke.wheatgrass.repository;

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource

import com.derby.nuke.wheatgrass.entity.Skill
import com.derby.nuke.wheatgrass.entity.User

@RepositoryRestResource(path = "skill")
interface SkillRepository extends PagingAndSortingRepository<Skill, String> {

	User getByName(@Param("name") String name);
	
}