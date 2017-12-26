package com.derby.nuke.wheatgrass.wechat.service.support

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.time.LocalDate;

import com.googlecode.jsonrpc4j.JsonRpcService;

@JsonRpcService("/birthday.ci")
interface BirthdayRpcService {

    def happyBirthday(
            @JsonSerialize(using = LocalDateSerializer) @JsonDeserialize(using = LocalDateDeserializer) LocalDate today);

    def announceBirthdayPersons(
            @JsonSerialize(using = LocalDateSerializer) @JsonDeserialize(using = LocalDateDeserializer) LocalDate today);

}
