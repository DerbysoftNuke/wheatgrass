package com.derby.nuke.wheatgrass.repository;

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

import com.derby.nuke.wheatgrass.entity.UserMedal

@RepositoryRestResource(path = "userMedal")
interface UserMedalRepository extends JpaRepository<UserMedal, String> {

	List<UserMedal> findByUserId(String userId);
}