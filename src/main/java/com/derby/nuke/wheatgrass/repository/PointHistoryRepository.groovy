package com.derby.nuke.wheatgrass.repository;

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.transaction.annotation.Transactional

import com.derby.nuke.wheatgrass.entity.PointHistory

@RepositoryRestResource(path = "pointHistory")
interface PointHistoryRepository extends JpaRepository<PointHistory, String> {
	@Query("from PointHistory where operator.userId=? and userSkill.id=?")
	PointHistory getByOperatorAndUserSkill(String operatorId,String userSkillId);

	@Transactional
	@Modifying
	@Query("delete from PointHistory where userSkill.id in(:userSkillIds)")
	void deleteByUserSkill(@Param("userSkillIds") Collection<String> userSkillIds);
}