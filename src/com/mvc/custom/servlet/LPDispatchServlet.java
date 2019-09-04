package com.mvc.custom.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import com.mvc.custom.annotation.LPAutowired;
import com.mvc.custom.annotation.LPController;
import com.mvc.custom.annotation.Repository;
import com.mvc.custom.annotation.LPRequestMapping;
import com.mvc.custom.annotation.LPService;
import com.mvc.custom.util.MyLog;
import com.mvc.custom.util.Scanner;
 
public class LPDispatchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	//���������ļ�
	private Properties properties = new Properties();
	//����������������ʽ������+������
	private List<String> classNames = new ArrayList<>();
	// key = ����  value = ��ʵ�� ��������LPcontroller��LPserviceע����ࣩ
	private Map<String, Object> ioc = new HashMap<>();
	// key = ����URL  value = Ӧ�� method
	private Map<String, Method> handlerMapping = new HashMap<>();
	// key = ����URL  value = �����Ӧ���Ӧ��controller��ʵ��
	private Map<String, Object> controllerMap = new HashMap<>();
 
	@Override
	public void init(ServletConfig config) throws ServletException {
		// 1.���������ļ�
		//getInitParameter()���ó�ʼ����web.xml�д�ŵĲ���
		doLoadConfig(config.getInitParameter("contextConfigLocation"));
		// 2.��ʼ���������������,ɨ���û��趨�İ��������е���
		String packageName = properties.getProperty("scanPackage");
		Scanner.classScanner(classNames, packageName);
		
		
//		doScanner(properties.getProperty("scanPackage"));
		
		// 3.�õ�ɨ�赽����,ͨ���������,ʵ����,���ҷŵ�ioc������(k-v beanName-bean) beanNameĬ��������ĸСд
		
		Scanner.instance(classNames, ioc);
//		doInstance();
		// 4.��ʼ��HandlerMapping(��url��method��Ӧ��)
		initHandlerMapping();
		// 5������ע��
		doAutoWired();
	}
 
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}
 
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (handlerMapping.isEmpty()) {
			return;
		}
		//����url
		String url = req.getRequestURI();
		//��Ŀ��·��
		String contextPath = req.getContextPath();
		url = url.replace(contextPath, "").replaceAll("/+", "/");
		if (!this.handlerMapping.containsKey(url)) {  //�����ӳ�����ָ������ӳ�䣬�򷵻�true
			resp.getWriter().write("404 NOT FOUND!");
			MyLog.error("URL NOT FOUND!!!");
			return;
		}
		Method method = this.handlerMapping.get(url);
		// ��ȡ�����Ĳ����б�
		// ����һ�����������飬 ��������˳���ʾ�ɸö����ʾ�Ŀ�ִ���ļ���  ��ʽ����  ����
		Class<?>[] parameterTypes = method.getParameterTypes();
		// ��ȡ����Ĳ���
		//���أ�һ�����ɱ��java.util.Map��������������Ϊ��������ֵ��Ϊӳ��ֵ��
		Map<String, String[]> parameterMap = req.getParameterMap();
		// �������ֵ
		Object[] paramValues = new Object[parameterTypes.length];
		// �����Ĳ����б�
		for (int i = 0; i < parameterTypes.length; i++) {
			// ���ݲ������ƣ���ĳЩ����
			String requestParam = parameterTypes[i].getSimpleName();
			if (requestParam.equals("HttpServletRequest")) {
				// ������������ȷ�����ǿת����
				paramValues[i] = req;
				continue;
			}
			if (requestParam.equals("HttpServletResponse")) {
				paramValues[i] = resp;
				continue;
			}
			if (requestParam.equals("String")) {
				for (Entry<String, String[]> param : parameterMap.entrySet()) {
					String[] paramValue = param.getValue();
					String value = Arrays.toString(paramValue).replaceAll("\\[|\\]", "").replaceAll(",\\s", ",");
					paramValues[i++] = value;
				}
			}
		}
		// ���÷������������
		try {
			// ��һ��������method����Ӧ��ʵ��		
			// �ڶ���������method����Ĳ���
			// ��ioc������
			//�ھ���ָ�������ķ��������ϵ��ô˷��������ʾ�Ļ�������
			method.invoke(this.controllerMap.get(url), paramValues);
		} catch (Exception e) {
			MyLog.error("",e);
			e.printStackTrace();
		}
	}
 /**
  * ��application.properyies�ļ����ص�properties��
  */
	private void doLoadConfig(String location) {
		// ��web.xml�е�contextConfigLocation��Ӧvalueֵ���ļ����ص�������
		InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(location);
		try {
			// ��Properties�ļ������ļ��������
			properties.load(resourceAsStream);
		} catch (IOException e) {
			MyLog.error("",e);
			e.printStackTrace();
		} finally {
			// ����
			if (null != resourceAsStream) {
				try {
					resourceAsStream.close();
				} catch (IOException e) {
					MyLog.error("",e);
					e.printStackTrace();
				}
			}
		}
 
	}
 /**
  *��ȡcom.mvc.customĿ¼�µ�������|
  *�������� ����+���� ��ʽ������classNames(һ��List)��
  */
//	private void doScanner(String packageName) {
//		// �����е�.�滻��/
//		URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
//		File dir = new File(url.getFile());
//		for (File file : dir.listFiles()) {
//			if (file.isDirectory()) {         //���Դ˳���·������ʾ���ļ��Ƿ�ΪĿ¼
//				// �ݹ��ȡ��
//				doScanner(packageName + "." + file.getName());
//			} else {
//				String className = packageName + "." + file.getName().replace(".class", "");
//				classNames.add(className);
//			}
//		}
//	}
    /**
     * �õ���LPController��LPServiceע����࣬������������ʵ���Լ�=ֵ����ʽ����ioc(һ��HashMap)��
     */
//	private void doInstance() {
//		if (classNames.isEmpty()) {       //�ж�List�Ƿ�Ϊ�գ�������б�����Ԫ�أ��򷵻�true
//			System.out.println("className Ϊ��");
//			return;
//		}
//		for (String className : classNames) {
//			try {
//				// ��������,������ʵ����(ֻ�м�@MyController��Ҫʵ����)
//				Class<?> clazz = Class.forName(className); //ͨ�������õ������
//				if (clazz.isAnnotationPresent(LPController.class)) {  //�����Ԫ���ϴ���ָ�����͵�ע�ͣ��򷵻�true�����򷵻�false
//					// Class.getSimpleName() ����Դ�����и����Ļ�����ļ�����  Class.newInstance()�����ɴ� ������ʾ�������ʵ��
//					ioc.put(toLowerFirstWord(clazz.getSimpleName()), clazz.newInstance());
//				}else if (clazz.isAnnotationPresent(LPService.class)) {
//					Object instance = clazz.newInstance();
//					//���ظ�Ԫ�ص�ע��ָ����ע�����ͣ���������ڴ�Ԫ�أ�����Ϊnull 
//					LPService service = (LPService) clazz.getAnnotation(LPService.class); 
//					String key = service.value();
//					//key ��@LPService��ֵ��instanceΪLoginServiceImpl��ʵ��
//					ioc.put(key, instance);
//					
//				}else if(clazz.isAnnotationPresent(Repository.class)) {
//					Object instance = clazz.newInstance();
//					Repository dao = (Repository)clazz.getAnnotation(Repository.class);
//					String key = dao.value();
//					ioc.put(key, instance);
//				}
//				else {
//					continue;
//				}
// 
//			} catch (Exception e) {
//				MyLog.error("",e);
//				e.printStackTrace();
//				continue;
//			}
//		}
//	}
 
	private void initHandlerMapping() {
		if (ioc.isEmpty()) {
			return;
		}
		try {
			for (Entry<String, Object> entry : ioc.entrySet()) {
				//�õ�ioc�д������
				Class<? extends Object> clazz = entry.getValue().getClass();
				//�õ���LPcontrollerע�����
				if (!clazz.isAnnotationPresent(LPController.class)) {
					continue;
				}
				Object instance = entry.getValue();
				// ƴurlʱ,��controllerͷ��urlƴ�Ϸ����ϵ�url
 				String baseUrl = "";
				if (clazz.isAnnotationPresent(LPRequestMapping.class)) {
					LPRequestMapping annotation = clazz.getAnnotation(LPRequestMapping.class);
					baseUrl = annotation.value();
				}
				Method[] methods = clazz.getMethods();
				for (Method method : methods) {
					if (!method.isAnnotationPresent(LPRequestMapping.class)) {
						continue;
					}
					LPRequestMapping annotation = method.getAnnotation(LPRequestMapping.class);
					String url = annotation.value();
 
					url = (baseUrl + "/" + url).replaceAll("/+", "/");
					handlerMapping.put(url, method);
					controllerMap.put(url,  instance);
				}
			}
 
		} catch (Exception e) {
			MyLog.error("",e);
			e.printStackTrace();
		}
 
	}
 
	/**
	 * ����AutoWiredע�������ע��ֵ
	 */
	private void doAutoWired() {
		if (ioc.isEmpty()) {
			return;
		}
		// �������б��йܵĶ���
		for (Map.Entry<String, Object> entry : ioc.entrySet()) {
			// �������б�Autowiredע�������
			// getFields()���ĳ��������еĹ�����public�����ֶΣ���������;
			// getDeclaredFields()���ĳ����������������ֶΣ�������public��private��proteced�����ǲ���������������ֶΡ�
			Object instance = entry.getValue();
			//���ص�����Field����ӳ�˱�ʾ�����ӿ������������ֶ������
			Field[] fields = entry.getValue().getClass().getDeclaredFields();
			for (Field field : fields) {
				// û��autowired�Ĳ���Ҫעֵ
				if (!field.isAnnotationPresent(LPAutowired.class)) {
					continue;
				}
				String beanName;
				// ��ȡAutoWired����д��ֵ��Ʃ��@Autowired("abc")
				LPAutowired autowired = field.getAnnotation(LPAutowired.class);
				if ("".equals(autowired.value())) {
					// �� searchService��ע�⣬�˴��ǻ�ȡ���Ե�����������ĸСд�����������޹أ����Զ���@Autowired
					// SearchService abc�����ԡ�
					//getType() ����������ʾ���ֶε��������͵�Class����
					beanName = toLowerFirstWord(field.getType().getSimpleName());
				} else {
					beanName = autowired.value();
				}
				// ��˽�л���������Ϊtrue,��Ȼ���ʲ���
				field.setAccessible(true);
				// ȥӳ�������Ƿ���ڸ�beanName��Ӧ��ʵ������
				if (ioc.get(beanName) != null) {
					try {
						//��ָ����������ϵĴ�Field�����ʾ���ֶ�����Ϊָ������ֵ
						field.set(instance, ioc.get(beanName));
					} catch (IllegalAccessException e) {
						MyLog.error("",e);
						e.printStackTrace();
					}
				}
			}
		}
	}
 
	/**
	 * ���ַ���������ĸСд
	 */
	private String toLowerFirstWord(String name) {
		char[] charArray = name.toCharArray();
		charArray[0] += 32;
		return String.valueOf(charArray);
}
}
