package com.derby.nuke.wheatgrass.entity;

import groovy.transform.ToString

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table
import javax.persistence.UniqueConstraint

import org.hibernate.annotations.GenericGenerator

@Entity
@Table(uniqueConstraints=[@UniqueConstraint(columnNames=["category", "name"])])
@ToString
class Skill {

	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(columnDefinition = "CHAR(32)")
	@Id
	String id;
	String category;
	String name;
	String description;
	int star;
}