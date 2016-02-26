package com.derby.nuke.wheatgrass.repository;

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource

import com.derby.nuke.wheatgrass.entity.User

@RepositoryRestResource(path = "user")
interface UserRepository extends PagingAndSortingRepository<User, String> {

	User getByEmail(@Param("email") String email);
	
	User getByOpenId(@Param("openId") String openId);
	
}