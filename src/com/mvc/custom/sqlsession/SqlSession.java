package com.mvc.custom.sqlsession;

import java.lang.reflect.Proxy;

import com.mvc.custom.proxy.UserMapperInvocationHandler;

public class SqlSession {
	public static <T> T getUserMapper(Class clazz) {
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz },
				new UserMapperInvocationHandler(clazz));
	}
}
