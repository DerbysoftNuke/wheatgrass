package com.derby.nuke.wheatgrass;

import java.io.UnsupportedEncodingException;

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession;

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.util.StringUtils;
import org.springframework.web.filter.AbstractRequestLoggingFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import org.springframework.web.util.WebUtils;

class HttpLoggingFilter extends AbstractRequestLoggingFilter {

	private static final Logger logger = LoggerFactory.getLogger("http.StreamLog");
	private String beforeMessagePrefix = DEFAULT_BEFORE_MESSAGE_PREFIX;
	private String beforeMessageSuffix = DEFAULT_BEFORE_MESSAGE_SUFFIX;
	private String afterMessagePrefix = DEFAULT_AFTER_MESSAGE_PREFIX;
	private String afterMessageSuffix = DEFAULT_AFTER_MESSAGE_SUFFIX;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		boolean shouldLog = shouldLog(request);
		if(!shouldLog){
			filterChain.doFilter(request, response);
			return;
		}

		boolean isFirstRequest = !isAsyncDispatch(request);
		HttpServletRequest requestToUse = request;
		HttpServletResponse responseToUse = response;

		if (isIncludePayload() && isFirstRequest && !(request instanceof ContentCachingRequestWrapper)) {
			requestToUse = new ContentCachingRequestWrapper(request);
			responseToUse = new ContentCachingResponseWrapper(response);
		}

		if (shouldLog && isFirstRequest) {
			beforeRequest(requestToUse, getBeforeMessage(requestToUse));
		}
		try {
			filterChain.doFilter(requestToUse, responseToUse);
		}
		finally {
			if (shouldLog && !isAsyncStarted(requestToUse)) {
				afterRequest(requestToUse, getAfterMessage(requestToUse, responseToUse));
			}
		}
	}

	@Override
	protected boolean shouldLog(HttpServletRequest request) {
		def uri = request.getRequestURI();
		return logger.isInfoEnabled() && !uri.contains("/js") && !uri.contains("/img") && !uri.contains("/css") && !uri.contains("favicon.ico") && !uri.contains(".ttf");
	}

	/**
	 * Writes a log message before the request is processed.
	 */
	@Override
	protected void beforeRequest(HttpServletRequest request, String message) {
		logger.info("Receive request <| {}", message);
	}

	/**
	 * Writes a log message after the request is processed.
	 */
	@Override
	protected void afterRequest(HttpServletRequest request, String message) {
		logger.info("Send response >| {}", message);
	}

	private String getBeforeMessage(HttpServletRequest request) {
		return createMessage(request, this.beforeMessagePrefix, this.beforeMessageSuffix);
	}

	private String getAfterMessage(HttpServletRequest request, HttpServletResponse response) {
		return createMessage(request, response, this.afterMessagePrefix, this.afterMessageSuffix);
	}

	protected String createMessage(HttpServletRequest request, String prefix, String suffix) {
		StringBuilder msg = new StringBuilder();
		msg.append(prefix);
		msg.append("uri=").append(request.getRequestURI());
		if (isIncludeQueryString() && request.getQueryString() != null) {
			msg.append('?').append(request.getQueryString());
		}
		if (isIncludeClientInfo()) {
			String client = request.getRemoteAddr();
			if (StringUtils.hasLength(client)) {
				msg.append(";client=").append(client);
			}
			HttpSession session = request.getSession(false);
			if (session != null) {
				msg.append(";session=").append(session.getId());
			}
			String user = request.getRemoteUser();
			if (user != null) {
				msg.append(";user=").append(user);
			}
		}
		if (isIncludePayload()) {
			ContentCachingRequestWrapper wrapper =
					WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
			if (wrapper != null) {
				byte[] buf = wrapper.getContentAsByteArray();
				if (buf.length > 0) {
					int length = Math.min(buf.length, getMaxPayloadLength());
					String payload;
					try {
						payload = new String(buf, 0, length, wrapper.getCharacterEncoding());
					}
					catch (UnsupportedEncodingException ex) {
						payload = "[unknown]";
					}
					msg.append(";payload=").append(payload);
				}
			}
		}
		msg.append(suffix);
		return msg.toString();
	}

	protected String createMessage(HttpServletRequest request, HttpServletResponse response, String prefix, String suffix) {
		StringBuilder msg = new StringBuilder();
		msg.append(prefix);
		msg.append("uri=").append(request.getRequestURI());
		if (isIncludeClientInfo()) {
			String client = request.getRemoteAddr();
			if (StringUtils.hasLength(client)) {
				msg.append(";client=").append(client);
			}
			HttpSession session = request.getSession(false);
			if (session != null) {
				msg.append(";session=").append(session.getId());
			}
			String user = request.getRemoteUser();
			if (user != null) {
				msg.append(";user=").append(user);
			}
		}
		msg.append(";status=").append(response.getStatus());
		if (isIncludePayload()) {
			ContentCachingResponseWrapper wrapper =
					WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
			if (wrapper != null) {
				byte[] buf = wrapper.getContentAsByteArray();
				if (buf.length > 0) {
					int length = Math.min(buf.length, getMaxPayloadLength());
					String payload;
					try {
						payload = new String(buf, 0, length, wrapper.getCharacterEncoding());
					}
					catch (UnsupportedEncodingException ex) {
						payload = "[unknown]";
					}
					msg.append(";payload=").append(payload);
					wrapper.copyBodyToResponse();
				}
			}
		}
		msg.append(suffix);
		return msg.toString();
	}

	public void setBeforeMessagePrefix(String beforeMessagePrefix) {
		this.beforeMessagePrefix = beforeMessagePrefix;
		super.setBeforeMessagePrefix(beforeMessagePrefix);
	}

	public void setBeforeMessageSuffix(String beforeMessageSuffix) {
		this.beforeMessageSuffix = beforeMessageSuffix;
		super.setBeforeMessageSuffix(beforeMessageSuffix);
	}

	public void setAfterMessagePrefix(String afterMessagePrefix) {
		this.afterMessagePrefix = afterMessagePrefix;
		super.setAfterMessagePrefix(afterMessagePrefix);
	}

	public void setAfterMessageSuffix(String afterMessageSuffix) {
		this.afterMessageSuffix = afterMessageSuffix;
		super.setAfterMessageSuffix(afterMessageSuffix);
	}
}
