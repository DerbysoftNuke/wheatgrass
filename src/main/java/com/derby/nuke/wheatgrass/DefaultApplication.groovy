package com.derby.nuke.wheatgrass

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.bind.annotation.RequestMapping
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
