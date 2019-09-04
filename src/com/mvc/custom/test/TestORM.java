package com.mvc.custom.test;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mvc.custom.entity.User;
import com.mvc.custom.mapper.UserMapper;
import com.mvc.custom.sqlsession.SqlSession;

public class TestORM {

	public static void main(String[] args) {
		
		 UserMapper userMapper = SqlSession.getUserMapper(UserMapper.class);
//		 User user = userMapper.selectUser("a123", "1234");
//		 System.out.println(user.getAccount() +"/"+ user.getPassword() +"/"+ user.getNick() );
//		 User u = new User(1,"abcde","abcde","abcde","ÄÐ",20);
//		 int i= userMapper.InsertUser("abcd","abcd","abcd","ÄÐ",250);
//		 System.out.println(i);
//		 User u1 = new User(15,"abcd","abcd","abcd","Å®",50);
//		 int i = userMapper.UpdateUser(16,"abcd","abcd","abcd","Å®",50);
		 int i= userMapper.DeleteUser(26);
//		 int i = userMapper.DeleteUser(15);
		 System.out.println(i);
		
	}

}
