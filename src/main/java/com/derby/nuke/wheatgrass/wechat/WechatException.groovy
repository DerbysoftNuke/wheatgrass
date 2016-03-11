package com.derby.nuke.wheatgrass.wechat;

class WechatException extends RuntimeException {

	def errorCode;
	def errorMessage;

	WechatException(String errorCode, String errorMessage){
		super("${errorMessage} by error code ${errorCode}");
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
}
