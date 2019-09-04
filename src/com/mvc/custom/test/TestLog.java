package com.mvc.custom.test;


import com.mvc.custom.util.MyLog;


public class TestLog {

	public static void main(String[] args) {
		MyLog.info("开始测试");
		try{
			System.out.println(1/0);
		}catch(Exception e) {
			MyLog.error("错误信息",e);
		}
	}
}
