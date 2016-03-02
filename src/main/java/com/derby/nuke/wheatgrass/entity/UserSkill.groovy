package com.derby.nuke.wheatgrass.entity;

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

import org.hibernate.annotations.GenericGenerator
import org.springframework.data.rest.core.annotation.RestResource

@Entity
@EqualsAndHashCode
@ToString
class UserSkill {

	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(columnDefinition = "CHAR(32)")
	@Id
	String id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	User user;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "skill_id")
	Skill skill;
	int point;
}