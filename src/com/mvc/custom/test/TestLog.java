package com.mvc.custom.test;


import com.mvc.custom.util.MyLog;


public class TestLog {

	public static void main(String[] args) {
		MyLog.info("��ʼ����");
		try{
			System.out.println(1/0);
		}catch(Exception e) {
			MyLog.error("������Ϣ",e);
		}
	}
}
