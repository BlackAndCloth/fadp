package com.mvc.custom.service;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LoginService {

	public void login(String account,String password);
	public void loginyz(HttpServletRequest req,HttpServletResponse resp,String account,String pwd);
	public void zhuce(HttpServletRequest req,HttpServletResponse resp) throws UnsupportedEncodingException, NumberFormatException;
	
}
