package com.derby.nuke.wheatgrass.entity;

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

import org.hibernate.annotations.GenericGenerator

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
@Entity
@EqualsAndHashCode(excludes=["answers"])
@ToString(excludes=["answers"])
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
class Question {

	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(columnDefinition = "CHAR(32)")
	@Id
	String id;

	String title;

	String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "proposer_id")
	User proposer;

	Date createTime;

	String recognizedAnswerId;

	@OneToMany(mappedBy = "question", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval=true)
	List<Answer> answers=new ArrayList<>();
}

