package com.derby.nuke.wheatgrass.entity

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint

import org.hibernate.annotations.GenericGenerator

@Entity
@EqualsAndHashCode
@ToString
@Table(uniqueConstraints=[@UniqueConstraint(columnNames=["operator_id", "user_skill_id"])])
class PointHistory {
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(columnDefinition = "CHAR(32)")
	@Id
	String id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "operator_id")
	User operator;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_skill_id")
	UserSkill userSkill;
	Date operationTime;
}
