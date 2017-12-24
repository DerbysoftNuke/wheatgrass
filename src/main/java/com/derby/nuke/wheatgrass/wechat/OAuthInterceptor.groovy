package com.derby.nuke.wheatgrass.wechat;

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter

import com.derby.nuke.wheatgrass.repository.UserRepository
import com.derby.nuke.wheatgrass.wechat.service.WechatService

class OAuthInterceptor extends HandlerInterceptorAdapter  {

	private static final def log = LoggerFactory.getLogger(OAuthInterceptor.class);

	@Autowired
	def WechatService wechatService;
	@Autowired
	def UserRepository userRepository;

	@Override
	boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		def userId = request.getSession().getAttribute(Consts.USER_ID);
		if(StringUtils.isNotBlank(userId) || !"GET".equalsIgnoreCase(request.getMethod())){
			return true;
		}

		userId = System.getProperty(Consts.DEBUG_USER_ID);
		if(StringUtils.isNotBlank(userId)){
			request.getSession().setAttribute(Consts.USER_ID, userId);
			return true;
		}

		if(isOAuthRequired(handler)){
			def code = request.getParameter("code");
			if(code == null){
				response.setHeader("Cache-Control", "no-cache");
				response.setHeader("Cache-Control", "no-store");
				response.setDateHeader("Expires", 0);
				response.setHeader("Pragma", "no-cache");

				def redirectUrl = buildRedirectUrl(request);
				log.debug("Start oauth to redirect to {}", redirectUrl);
				response.sendRedirect(wechatService.getUrlForCode(URLEncoder.encode(redirectUrl, "UTF-8")));
				return false;
			}else{
				userId = wechatService.getUserId(code);
				if(userId == null){
					throw new IllegalStateException("UserId not found");
				}

				request.getSession().setAttribute(Consts.USER_ID, userId);
			}
		}
		return true;
	}

	private isOAuthRequired(Object handler){
		if(!(handler instanceof HandlerMethod)){
			return false;
		}

		OAuthRequired annotation = handler.getMethodAnnotation(OAuthRequired.class);
		if(annotation != null){
			return annotation.value();
		}

		def annotations = handler.getBeanType().getAnnotationsByType(OAuthRequired.class);
		if(annotations != null && annotations.length > 0){
			return annotations[0].value();
		}

		annotations = handler.getBeanType().getSuperclass().getAnnotationsByType(OAuthRequired.class);
		if(annotations != null && annotations.length > 0){
			return annotations[0].value();
		}

		return false;
	}

	private buildRedirectUrl(HttpServletRequest request){
		String url = request.getRequestURL().toString();
		def param = request.getQueryString();
		if(param != null && param.length() > 0){
			url = url + "?" + param;
		}

		return url;
	}
}
