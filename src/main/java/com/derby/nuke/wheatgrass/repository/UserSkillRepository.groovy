package com.derby.nuke.wheatgrass.repository;

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource

import com.derby.nuke.wheatgrass.entity.User
import com.derby.nuke.wheatgrass.entity.UserSkill

@RepositoryRestResource(path = "userskill")
interface UserSkillRepository extends PagingAndSortingRepository<UserSkill, String> {

}