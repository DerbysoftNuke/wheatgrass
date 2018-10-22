package com.derby.nuke.wheatgrass.entity;

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where
import org.joda.time.Days

import java.time.LocalDate

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany

import org.hibernate.annotations.GenericGenerator

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer

@Entity
@EqualsAndHashCode(excludes=["skills","medals"])
@ToString(excludes=["skills","medals"])
//@SQLDelete(sql = "update user set IS_DEL = 1 where id = ?")
//@Where(clause = "IS_DEL = 0")
class User{

	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(columnDefinition = "CHAR(32)")
	@Id
	String id;
	@Column(unique=true)
	String userId;
	@Column
	String email;

	@JsonIgnore
	String password;
	String name;
	String englishName;
	String nickName;
	String department;
	String position;
	String imageUrl;
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	LocalDate birthday;
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	LocalDate entryday;
	String birthplace;
	@Enumerated(EnumType.STRING)
	Sex sex;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval=true)
	Set<UserSkill> skills = new HashSet<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval=true)
	Set<UserMedal> medals = new HashSet<>();

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Column(name = "IS_DEL")
	boolean isDel = false;

	def getEntryDays(){
		return entryday?Days.daysBetween(org.joda.time.LocalDate.parse(entryday.toString()),org.joda.time.LocalDate.now()).getDays():0
	}
}