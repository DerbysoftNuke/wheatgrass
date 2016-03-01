package com.derby.nuke.wheatgrass.wechat.model;

class MessageContext{
	
	private def static ThreadLocal<MessageContext> CONTEXT = new ThreadLocal<MessageContext>();
	
	def userId;
	def inputMessage;
	
	def static MessageContext get(){
		CONTEXT.get();
	}
	
	def static MessageContext put(MessageContext context){
		CONTEXT.set(context);
	}
	
	def static void remove(){
		CONTEXT.remove();
	}
	
}