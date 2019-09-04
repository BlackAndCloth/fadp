package com.mvc.custom.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mvc.custom.annotation.LPAutowired;
import com.mvc.custom.annotation.LPController;
import com.mvc.custom.annotation.LPRequestMapping;
import com.mvc.custom.service.LoginService;

@LPController
@LPRequestMapping("/test")
public class TestController {
 
	@LPAutowired
	LoginService loginService1;
	
	@LPRequestMapping("/login")
	public void login(HttpServletRequest req, HttpServletResponse resp,String account,String pwd) throws IOException{
		loginService1.login(account, pwd); 
		resp.getWriter().write("request success,param:"+account+"-"+pwd);
	}
	@LPRequestMapping("/loginyz")
	public void loginyz(HttpServletRequest req,HttpServletResponse resp,String account,String pwd) {
		loginService1.loginyz(req, resp, account, pwd);
	}
	@LPRequestMapping("/zhuce")
	public void zhuce(HttpServletRequest req,HttpServletResponse resp) throws UnsupportedEncodingException {
		loginService1.zhuce(req, resp);
	}
}
