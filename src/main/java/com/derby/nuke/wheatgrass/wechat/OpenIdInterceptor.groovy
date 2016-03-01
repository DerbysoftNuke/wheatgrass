package com.derby.nuke.wheatgrass.wechat;

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView

import com.derby.nuke.wheatgrass.wechat.service.WechatService

class OpenIdInterceptor implements HandlerInterceptor  {

	@Autowired
	def WechatService wechatService;

	@Override
	boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if(request.getRequestURI().contains("/wechat") && !request.getRequestURI().endsWith("/wechat") && !request.getRequestURI().contains("/wechat/verify_email") ){
			String openId = request.getSession().getAttribute("wechat.openId");
			if(openId == null){
				String code = request.getParameter("code");
				if(code == null){
					response.sendRedirect(wechatService.getUrlForCode(URLEncoder.encode(request.getRequestURL().toString(), "UTF-8")));
					return false;
				}else{
					openId = wechatService.getOpenId(code);
					request.getSession().setAttribute("wechat.openId", openId);
					return true;
				}
			}
		}
		return true;
	}

	@Override
	void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		// TODO Auto-generated method stub

	}
}
