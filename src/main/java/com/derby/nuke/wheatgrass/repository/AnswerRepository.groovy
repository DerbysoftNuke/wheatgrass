package com.derby.nuke.wheatgrass.repository;

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

import com.derby.nuke.wheatgrass.entity.Answer

@RepositoryRestResource(path = "answer")
interface AnswerRepository extends JpaRepository<Answer, String> {
}