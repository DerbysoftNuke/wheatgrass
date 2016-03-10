package com.derby.nuke.wheatgrass.wechat;

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter

import com.derby.nuke.wheatgrass.repository.UserRepository
import com.derby.nuke.wheatgrass.wechat.service.WechatService

class LoginInterceptor extends HandlerInterceptorAdapter  {

	@Autowired
	def WechatService wechatService;
	@Autowired
	def UserRepository userRepository;

	@Override
	boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if(request.getRequestURI().contains("/wechat") && !request.getRequestURI().endsWith("/wechat") && !request.getRequestURI().contains("/wechat/email/verify") ){
			String openId = request.getSession().getAttribute("wechat.openId");
			if(openId == null){
				response.setHeader("Cache-Control", "no-cache");
				response.setHeader("Cache-Control", "no-store");
				response.setDateHeader("Expires", 0);
				response.setHeader("Pragma", "no-cache");
				
				String code = request.getParameter("code");
				def string = request.getQueryString();
				def queryString = "";
				if(string != null && string.length() > 0){
					queryString = "?"+string;
				}
				
				if(code == null){
					response.sendRedirect(wechatService.getUrlForProfile(URLEncoder.encode(request.getRequestURL().toString()+queryString, "UTF-8")));
					return false;
				}else{
					def userInfo = wechatService.getUserInfo(code);
					openId = userInfo.openid;
					request.getSession().setAttribute("wechat.userInfo", userInfo);
					def user = userRepository.getByOpenId(openId);
					
					if(!request.getRequestURI().contains("/wechat/email/bind") && (user == null || !user.validation)){
						response.sendRedirect(request.getContextPath()+"/wechat/email/bind");
						return false;
					}else{
						request.getSession().setAttribute("wechat.openId", openId);
					}
					return true;
				}
			}
		}
		return true;
	}
}
