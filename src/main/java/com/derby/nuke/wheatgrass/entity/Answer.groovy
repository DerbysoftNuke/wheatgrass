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
import org.hibernate.annotations.Parameter
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs

import com.derby.nuke.wheatgrass.hibernate.SimpleCollectionType
import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@TypeDefs([
	@TypeDef(name = "setType", typeClass = SimpleCollectionType.class, parameters = [
		@Parameter(name = SimpleCollectionType.SPLIT, value = ","),
		@Parameter(name = SimpleCollectionType.ITEM_TYPE, value = "java.lang.String"),
		@Parameter(name = SimpleCollectionType.COLLECTION_TYPE, value = "java.util.HashSet")
	])
])
@EqualsAndHashCode(excludes=[])
@ToString(excludes=[])
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
class Answer{

	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(columnDefinition = "CHAR(32)")
	@Id
	String id;
	@Column(length = 16777215)
	String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "answerer_id")
	User answerer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "question_id")
	Question question;

	Date createTime;

	@Type(type = "setType")
	@Column(length = 16777215)
	Set<String> markUsefulUserIds = new HashSet();

	@Type(type = "setType")
	@Column(length = 16777215)
	Set<String> markUnusefulUserIds = new HashSet();
}