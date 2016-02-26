package com.derby.nuke.wheatgrass.entity;

import java.time.LocalDate

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

import lombok.Data
import lombok.NoArgsConstructor
import lombok.RequiredArgsConstructor

import org.hibernate.annotations.GenericGenerator

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
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
	LocalDate birthday;
	String birthplace;
}