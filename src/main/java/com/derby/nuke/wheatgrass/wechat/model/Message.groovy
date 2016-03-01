package com.derby.nuke.wheatgrass.wechat.model;

import groovy.transform.AutoClone

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlElementWrapper
import javax.xml.bind.annotation.XmlRootElement

@AutoClone
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="xml")
class Message{
	@XmlElement(name="ToUserName") def String to;
	@XmlElement(name="FromUserName") def String from;
	@XmlElement(name="CreateTime") def long createTime;
	/**
	 * text, image, voice, video, location, link
	 */
	@XmlElement(name="MsgType") def MessageType type;

	/**
	 * for normal message
	 */
	@XmlElement(name="Content") def String content;

	/**
	 * for image message: picUrl & mediaId
	 */
	@XmlElement(name="PicUrl") def String picUrl;
	@XmlElement(name="MediaId") def String mediaId;

	/**
	 * for voice message: mediaId, format & recognition
	 */
	@XmlElement(name="Format") def String format;
	@XmlElement(name="Recognition") def String recognition;

	/**
	 * for video message: mediaId & thumbMediaId
	 */
	@XmlElement(name="ThumbMediaId") def String thumbMediaId;

	/**
	 * for location message: x, y, scale & label
	 */
	@XmlElement(name="Location_X") def Double x;
	@XmlElement(name="Location_Y") def Double y;
	@XmlElement(name="Scale") def Integer scale;
	@XmlElement(name="Label") def String label;

	/**
	 * for link message: mediaId & thumbMediaId
	 */
	@XmlElement(name="Title") def String title;
	@XmlElement(name="Description") def String description;
	@XmlElement(name="Url") def String url;
	
	/**
	 * for news message: articleCount & articles
	 */
	@XmlElement(name="ArticleCount") def String articleCount;
	@XmlElementWrapper(name="Articles") @XmlElement(name="item") def List<Article> articles;
	
	@XmlElement(name="MsgId") def String msgId;
	
	enum MessageType{
		text, image, voice, video, location, link, music, news
	}
}

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="item")
class Article{
	
	@XmlElement(name="Title") def String title;
	@XmlElement(name="Description") def String description;
	@XmlElement(name="PicUrl") def String picUrl;
	@XmlElement(name="Url") def String Url;
	
}