package com.derby.nuke.wheatgrass.entity;

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import java.time.LocalDate

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

import org.hibernate.annotations.GenericGenerator

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer

@Entity
@EqualsAndHashCode
@ToString
class User {

	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(columnDefinition = "CHAR(32)")
	@Id
	String id;
	String openId;
	String email;
	String password;
	String name;
	String department;
	String imageUrl;
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	LocalDate birthday;
	String birthplace;
	String token;
	boolean validation;
}