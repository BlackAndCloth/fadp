package aop.test;


import aop.demo.Test;
import aop.util.ApplicationContext;

import java.util.concurrent.ConcurrentHashMap;

public class Main {

	public static void main(String[] args) {
		ApplicationContext applicationContext = new ApplicationContext();
		ConcurrentHashMap<String, Object> proxyBeanMap = ApplicationContext.proxyBeanMap;

		Test test = (Test) proxyBeanMap.get("test");
		test.doSomeThing();

		System.out.println("-------------");

		test.doWtihNotProxy();
	}
}
