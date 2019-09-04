package com.mvc.custom.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mvc.custom.annotation.LPAutowired;
import com.mvc.custom.annotation.LPService;
import com.mvc.custom.dao.Dao;
import com.mvc.custom.entity.User;
import com.mvc.custom.service.LoginService;
import com.mvc.custom.util.MyLog;

@LPService("loginService")//key的规则：Interface Name 首字母小写当做ioc的key
public class LoginServiceImpl implements LoginService {
 
	@LPAutowired
	Dao userdao;
	
	public void login(String account, String password) {
		// TODO Auto-generated method stub
		System.out.println("账号:"+account+" 密码:"+password);
	}

	@Override
	public void loginyz(HttpServletRequest req, HttpServletResponse resp, String account, String pwd) {
		User user = (User) userdao.login(account, pwd);
		HttpSession session = req.getSession();
		if(user == null) {
			try {
				req.getRequestDispatcher("/login.html").forward(req, resp);
			} catch (ServletException e) {
				MyLog.error("",e);
				e.printStackTrace();
			} catch (IOException e) {
				MyLog.error("",e);
				e.printStackTrace();
			}
		}else {
			session.setAttribute("user", user);
			try {
				resp.sendRedirect("/mymvc/index.html");
			} catch (IOException e) {
				MyLog.error("",e);
				e.printStackTrace();
			}
		}
	}

	@Override
	public void zhuce(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException, NumberFormatException {
		String account =  req.getParameter("account");
		String password = req.getParameter("password");
		String nick = req.getParameter("nick");
		String sex = new String(req.getParameter("sex").getBytes("iso-8859-1"),"utf-8");
		int age = Integer.parseInt(req.getParameter("age"));
		User user = new User(0,account,password,nick,sex,age);
		int i = userdao.insert(user);
		if(i>0) {
			try {
				req.getRequestDispatcher("/login.html").forward(req, resp);
			} catch (ServletException e) {
				MyLog.error("",e);
				e.printStackTrace();
			} catch (IOException e) {
				MyLog.error("",e);
				e.printStackTrace();
			}
		}else {
			MyLog.error("数据库操作失败~");
		}
	}
 
}
