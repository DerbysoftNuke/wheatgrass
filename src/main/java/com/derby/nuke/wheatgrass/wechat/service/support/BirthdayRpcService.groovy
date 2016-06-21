package com.derby.nuke.wheatgrass.wechat.service.support;

import java.time.LocalDate;

import com.googlecode.jsonrpc4j.JsonRpcService;

@JsonRpcService("/birthday.ci")
interface BirthdayRpcService {
	
	def sendReminder(LocalDate today);
	
	def birthdayWishRecord(LocalDate today);

}
