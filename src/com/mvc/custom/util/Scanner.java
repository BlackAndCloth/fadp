package com.mvc.custom.util;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mvc.custom.annotation.LPController;
import com.mvc.custom.annotation.LPService;
import com.mvc.custom.annotation.Repository;

public class Scanner {
	public static List<String> classScanner(List<String> classNames,String packageName){
		URL url = Scanner.class.getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
		File dir = new File(url.getFile());
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {         //测试此抽象路径名表示的文件是否为目录
				// 递归读取包
				classScanner(classNames,packageName + "." + file.getName());
			} else {
				String className = packageName + "." + file.getName().replace(".class", "");
				classNames.add(className);
			}
		}
		return classNames;
	}
	
	public static Map<String,Object> instance(List<String> classNames,Map<String, Object> ioc){
		if (classNames.isEmpty()) {       //判断List是否为空，如果此列表不包含元素，则返回true
			System.out.println("className 为空");
			return null;
		}
		for (String className : classNames) {
			try {
				// 把类搞出来,反射来实例化(只有加@MyController需要实例化)
				Class<?> clazz = Class.forName(className); //通过类名拿到类对象
				if (clazz.isAnnotationPresent(LPController.class)) {  //如果此元素上存在指定类型的注释，则返回true，否则返回false
					// Class.getSimpleName() 返回源代码中给出的基础类的简单名称  Class.newInstance()创建由此 类对象表示的类的新实例
					ioc.put(toLowerFirstWord(clazz.getSimpleName()), clazz.newInstance());
				}else if (clazz.isAnnotationPresent(LPService.class)) {
					Object instance = clazz.newInstance();
					//返回该元素的注释指定的注释类型，如果存在于此元素，否则为null 
					LPService service = (LPService) clazz.getAnnotation(LPService.class); 
					String key = service.value();
					//key 是@LPService的值，instance为LoginServiceImpl的实例
					ioc.put(key, instance);
					
				}else if(clazz.isAnnotationPresent(Repository.class)) {
					Object instance = clazz.newInstance();
					Repository dao = (Repository)clazz.getAnnotation(Repository.class);
					String key = dao.value();
					ioc.put(key, instance);
				}
				else {
					continue;
				}
 
			} catch (Exception e) {
				MyLog.error("",e);
				e.printStackTrace();
				continue;
			}
		}
		return ioc;
	}
	private static String toLowerFirstWord(String name) {
		char[] charArray = name.toCharArray();
		charArray[0] += 32;
		return String.valueOf(charArray);
	}
	
	public static Set<Class<?>> getClass(List<String> classNames,Set<Class<?>> set){
		for(String s : classNames) {
			Class<?> cls = loadClass(s, false);;
			set.add(cls);
		}
		return set;
	}
	public static Class<?> loadClass(String className) {
		return loadClass(className, true);
	}
	public static Class<?> loadClass(String className, boolean isInitialized) {
		Class<?> cls;
		try {
			cls = Class.forName(className, isInitialized, getClassLoader());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		return cls;
	}
	public static ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
}
