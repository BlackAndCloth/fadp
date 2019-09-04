package aop.util;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


import com.mvc.custom.annotation.Aspect;
import com.mvc.custom.annotation.PointCut;
import com.mvc.custom.util.Scanner;

import aop.proxy.AbsMethodAdvance;

public class ApplicationContext {

	public static ConcurrentHashMap<String, Object> proxyBeanMap = new ConcurrentHashMap<String, Object>();

	static {
		initAopBeanMap("aop.demo");
	}

	public static void initAopBeanMap(String basePath) {
		try {
			List<String> classNames = new ArrayList<>();
			Scanner.classScanner(classNames, basePath);
			Set<Class<?>> classSet = new HashSet<>();
			Scanner.getClass(classNames, classSet);
			for (Class clazz : classSet) {
				if (clazz.isAnnotationPresent(Aspect.class)) {
					Method[] methods = clazz.getMethods();

					for(Method method : methods) {

						if (method.isAnnotationPresent(PointCut.class)) {
							PointCut pointCut = (PointCut) method.getAnnotations()[0];
							String pointCutStr = pointCut.value();
							String[] pointCutArr = pointCutStr.split("_");

							String className = pointCutArr[0];
							String methodName = pointCutArr[1];
							Object targetObj= null;
								targetObj = ReflectionUtil.newInstance(className);
							AbsMethodAdvance proxyer = (AbsMethodAdvance) ReflectionUtil.newInstance(clazz);
							proxyer.setProxyMethodName(methodName);

							Object object = proxyer.createProxyObject(targetObj);

							if (object != null) {
								proxyBeanMap.put(targetObj.getClass().getSimpleName().toLowerCase(), object);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
