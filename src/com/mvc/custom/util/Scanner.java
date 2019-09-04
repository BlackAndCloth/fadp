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
			if (file.isDirectory()) {         //���Դ˳���·������ʾ���ļ��Ƿ�ΪĿ¼
				// �ݹ��ȡ��
				classScanner(classNames,packageName + "." + file.getName());
			} else {
				String className = packageName + "." + file.getName().replace(".class", "");
				classNames.add(className);
			}
		}
		return classNames;
	}
	
	public static Map<String,Object> instance(List<String> classNames,Map<String, Object> ioc){
		if (classNames.isEmpty()) {       //�ж�List�Ƿ�Ϊ�գ�������б�����Ԫ�أ��򷵻�true
			System.out.println("className Ϊ��");
			return null;
		}
		for (String className : classNames) {
			try {
				// ��������,������ʵ����(ֻ�м�@MyController��Ҫʵ����)
				Class<?> clazz = Class.forName(className); //ͨ�������õ������
				if (clazz.isAnnotationPresent(LPController.class)) {  //�����Ԫ���ϴ���ָ�����͵�ע�ͣ��򷵻�true�����򷵻�false
					// Class.getSimpleName() ����Դ�����и����Ļ�����ļ�����  Class.newInstance()�����ɴ� ������ʾ�������ʵ��
					ioc.put(toLowerFirstWord(clazz.getSimpleName()), clazz.newInstance());
				}else if (clazz.isAnnotationPresent(LPService.class)) {
					Object instance = clazz.newInstance();
					//���ظ�Ԫ�ص�ע��ָ����ע�����ͣ���������ڴ�Ԫ�أ�����Ϊnull 
					LPService service = (LPService) clazz.getAnnotation(LPService.class); 
					String key = service.value();
					//key ��@LPService��ֵ��instanceΪLoginServiceImpl��ʵ��
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
