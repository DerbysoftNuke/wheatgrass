package com.derby.nuke.wheatgrass.repository

import com.derby.nuke.wheatgrass.entity.BirthdayWishWord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

import java.time.LocalDate

@RepositoryRestResource(path = "birthdayWish")
interface BirthdayWishWordRepository extends JpaRepository<BirthdayWishWord, String> {

	List<BirthdayWishWord> findByWisherIdAndBirthdayWishBirthdayBetween(String wisherId, LocalDate startDate, LocalDate endDate);
	List<BirthdayWishWord> findByBirthdayWishId(String birthdayWishId);
	BirthdayWishWord findByBirthdayWishIdAndWisherId(String birthdayWishId, String wisherId);

}