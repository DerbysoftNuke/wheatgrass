package com.derby.nuke.wheatgrass.repository;

import java.time.LocalDate

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

import com.derby.nuke.wheatgrass.entity.BirthdayWish

@RepositoryRestResource(path = "birthdayWish")
interface BirthdayWishRepository extends JpaRepository<BirthdayWish, String> {
	
	List<BirthdayWish> findByBirthdayBetween(LocalDate startDate, LocalDate endDate);
}