package com.mvc.custom.dao.impl;


import com.mvc.custom.annotation.Repository;
import com.mvc.custom.dao.Dao;
import com.mvc.custom.entity.User;
import com.mvc.custom.mapper.UserMapper;
import com.mvc.custom.sqlsession.SqlSession;


@Repository("dao")
public class UserDaoImpl implements Dao<User> {

	
	
	@Override
	public User login(String account,String password) {
		UserMapper userMapper = SqlSession.getUserMapper(UserMapper.class);
		return userMapper.selectUser(account, password);
		
	}

	@Override
	public int insert(User t) {
		UserMapper userMapper = SqlSession.getUserMapper(UserMapper.class);
		String account = t.getAccount();
		String password = t.getPassword();
		String nick = t.getNick();
		String sex = t.getSex();
		int age = t.getAge();
		return userMapper.InsertUser(account, password, nick, sex, age);
	}
	

}
