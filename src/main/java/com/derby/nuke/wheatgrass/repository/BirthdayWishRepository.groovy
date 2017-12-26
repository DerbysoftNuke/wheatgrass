package com.derby.nuke.wheatgrass.repository

import com.derby.nuke.wheatgrass.entity.BirthdayWish
import com.derby.nuke.wheatgrass.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.rest.core.annotation.RepositoryRestResource

import java.time.LocalDate

@RepositoryRestResource(path = "birthdayWish")
interface BirthdayWishRepository extends JpaRepository<BirthdayWish, String> {

    List<BirthdayWish> findByBirthdayBetween(LocalDate startDate, LocalDate endDate);

    @Query("select distinct user from BirthdayWish where birthday=?")
    Collection<User> findUsersByBirthday(LocalDate birthday);

    @Query("from BirthdayWish where birthday=? and user.userId=?")
    BirthdayWish findByBirthdayAndUser(LocalDate birthday, String userId);
}