package com.derby.nuke.wheatgrass

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@RestController
@EnableAutoConfiguration
class DefaultApplication {

	@RequestMapping("/")
	def home(){
		"Welcome to wheatgrass";
	}

	static void main(String[] args) {
		SpringApplication.run DefaultApplication, args
	}
}
