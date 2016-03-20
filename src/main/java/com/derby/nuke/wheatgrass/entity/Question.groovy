package com.derby.nuke.wheatgrass.entity

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
import javax.persistence.OneToOne

import org.hibernate.annotations.GenericGenerator

@Entity
@EqualsAndHashCode(excludes=["recognizedAnswer","answers"])
@ToString(excludes=["recognizedAnswer","answers"])
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

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recognizedAnswer_id")
	Answer recognizedAnswer;

	@OneToMany(mappedBy = "question", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval=true)
	List<Answer> answers=new ArrayList<>();
}
