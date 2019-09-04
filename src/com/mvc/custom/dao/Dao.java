package com.mvc.custom.dao;


public interface Dao<T> {

	public int insert(T t);
	
	public T login(String account,String password); 
	
}
