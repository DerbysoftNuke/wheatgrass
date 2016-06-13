package com.derby.nuke.wheatgrass.repository;

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.transaction.annotation.Transactional

import com.derby.nuke.wheatgrass.entity.User

@Transactional
@RepositoryRestResource(path = "user")
interface UserRepository extends JpaRepository<User, String>,JpaSpecificationExecutor<User> {

	User getByEmail(@Param("email") String email);

	User getByUserId(@Param("userId") String userId);

	@Query("from User order by name")
	List<User> findAll();
	
	@Query("from User where birthday like ?")
	List<User> findByBirthdayLike(String pattern);
}