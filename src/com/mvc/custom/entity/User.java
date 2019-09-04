package com.mvc.custom.entity;

public class User {
	private int id;
	private String account;
	private String password;
	private String nick;
	private String sex;
	private int age;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public User(int id, String account, String password, String nick, String sex, int age) {
		super();
		this.id = id;
		this.account = account;
		this.password = password;
		this.nick = nick;
		this.sex = sex;
		this.age = age;
	}
	public User() {
		super();
	}
}
