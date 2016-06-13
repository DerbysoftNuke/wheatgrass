package com.derby.nuke.wheatgrass.repository;

import java.time.LocalDate

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.rest.core.annotation.RepositoryRestResource

import com.derby.nuke.wheatgrass.entity.BirthdayWish

@RepositoryRestResource(path = "birthdayWish")
interface BirthdayWishRepository extends JpaRepository<BirthdayWish, String> {

	List<BirthdayWish> findByBirthdayBetween(LocalDate startDate, LocalDate endDate);

	@Query("select distinct user.userId from BirthdayWish where birthday=?")
	Collection<String> findUserIdsByBirthday(LocalDate birthday);
	
	@Query("from BirthdayWish where birthday=? and user.userId=?")
	BirthdayWish findByBirthdayAndUser(LocalDate birthday,String userId);
}