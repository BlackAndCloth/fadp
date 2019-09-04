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
	//储存配置文件
	private Properties properties = new Properties();
	//储存所有类名（格式：包名+类名）
	private List<String> classNames = new ArrayList<>();
	// key = 类名  value = 类实例 （类是有LPcontroller和LPservice注解的类）
	private Map<String, Object> ioc = new HashMap<>();
	// key = 请求URL  value = 应答 method
	private Map<String, Method> handlerMapping = new HashMap<>();
	// key = 请求URL  value = 请求的应答对应的controller类实例
	private Map<String, Object> controllerMap = new HashMap<>();
 
	@Override
	public void init(ServletConfig config) throws ServletException {
		// 1.加载配置文件
		//getInitParameter()调用初始化在web.xml中存放的参量
		doLoadConfig(config.getInitParameter("contextConfigLocation"));
		// 2.初始化所有相关联的类,扫描用户设定的包下面所有的类
		String packageName = properties.getProperty("scanPackage");
		Scanner.classScanner(classNames, packageName);
		
		
//		doScanner(properties.getProperty("scanPackage"));
		
		// 3.拿到扫描到的类,通过反射机制,实例化,并且放到ioc容器中(k-v beanName-bean) beanName默认是首字母小写
		
		Scanner.instance(classNames, ioc);
//		doInstance();
		// 4.初始化HandlerMapping(将url和method对应上)
		initHandlerMapping();
		// 5。属性注入
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
		//请求url
		String url = req.getRequestURI();
		//项目根路径
		String contextPath = req.getContextPath();
		url = url.replace(contextPath, "").replaceAll("/+", "/");
		if (!this.handlerMapping.containsKey(url)) {  //如果此映射包含指定键的映射，则返回true
			resp.getWriter().write("404 NOT FOUND!");
			MyLog.error("URL NOT FOUND!!!");
			return;
		}
		Method method = this.handlerMapping.get(url);
		// 获取方法的参数列表
		// 返回一个类对象的数组， 类以声明顺序表示由该对象表示的可执行文件的  形式参数  类型
		Class<?>[] parameterTypes = method.getParameterTypes();
		// 获取请求的参数
		//返回：一个不可变的java.util.Map，包含参数名作为键，参数值作为映射值。
		Map<String, String[]> parameterMap = req.getParameterMap();
		// 保存参数值
		Object[] paramValues = new Object[parameterTypes.length];
		// 方法的参数列表
		for (int i = 0; i < parameterTypes.length; i++) {
			// 根据参数名称，做某些处理
			String requestParam = parameterTypes[i].getSimpleName();
			if (requestParam.equals("HttpServletRequest")) {
				// 参数类型已明确，这边强转类型
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
		// 利用反射机制来调用
		try {
			// 第一个参数是method所对应的实例		
			// 第二个参数是method所需的参数
			// 在ioc容器中
			//在具有指定参数的方法对象上调用此方法对象表示的基础方法
			method.invoke(this.controllerMap.get(url), paramValues);
		} catch (Exception e) {
			MyLog.error("",e);
			e.printStackTrace();
		}
	}
 /**
  * 把application.properyies文件加载到properties中
  */
	private void doLoadConfig(String location) {
		// 把web.xml中的contextConfigLocation对应value值的文件加载到流里面
		InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(location);
		try {
			// 用Properties文件加载文件里的内容
			properties.load(resourceAsStream);
		} catch (IOException e) {
			MyLog.error("",e);
			e.printStackTrace();
		} finally {
			// 关流
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
  *读取com.mvc.custom目录下的所有类|
  *把类名以 包名+类名 格式储存在classNames(一个List)中
  */
//	private void doScanner(String packageName) {
//		// 把所有的.替换成/
//		URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
//		File dir = new File(url.getFile());
//		for (File file : dir.listFiles()) {
//			if (file.isDirectory()) {         //测试此抽象路径名表示的文件是否为目录
//				// 递归读取包
//				doScanner(packageName + "." + file.getName());
//			} else {
//				String className = packageName + "." + file.getName().replace(".class", "");
//				classNames.add(className);
//			}
//		}
//	}
    /**
     * 拿到有LPController和LPService注解的类，并把类名和类实例以键=值的形式放在ioc(一个HashMap)中
     */
//	private void doInstance() {
//		if (classNames.isEmpty()) {       //判断List是否为空，如果此列表不包含元素，则返回true
//			System.out.println("className 为空");
//			return;
//		}
//		for (String className : classNames) {
//			try {
//				// 把类搞出来,反射来实例化(只有加@MyController需要实例化)
//				Class<?> clazz = Class.forName(className); //通过类名拿到类对象
//				if (clazz.isAnnotationPresent(LPController.class)) {  //如果此元素上存在指定类型的注释，则返回true，否则返回false
//					// Class.getSimpleName() 返回源代码中给出的基础类的简单名称  Class.newInstance()创建由此 类对象表示的类的新实例
//					ioc.put(toLowerFirstWord(clazz.getSimpleName()), clazz.newInstance());
//				}else if (clazz.isAnnotationPresent(LPService.class)) {
//					Object instance = clazz.newInstance();
//					//返回该元素的注释指定的注释类型，如果存在于此元素，否则为null 
//					LPService service = (LPService) clazz.getAnnotation(LPService.class); 
//					String key = service.value();
//					//key 是@LPService的值，instance为LoginServiceImpl的实例
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
				//拿到ioc中储存的类
				Class<? extends Object> clazz = entry.getValue().getClass();
				//拿到有LPcontroller注解的类
				if (!clazz.isAnnotationPresent(LPController.class)) {
					continue;
				}
				Object instance = entry.getValue();
				// 拼url时,是controller头的url拼上方法上的url
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
	 * 给被AutoWired注解的属性注入值
	 */
	private void doAutoWired() {
		if (ioc.isEmpty()) {
			return;
		}
		// 遍历所有被托管的对象
		for (Map.Entry<String, Object> entry : ioc.entrySet()) {
			// 查找所有被Autowired注解的属性
			// getFields()获得某个类的所有的公共（public）的字段，包括父类;
			// getDeclaredFields()获得某个类的所有申明的字段，即包括public、private和proteced，但是不包括父类的申明字段。
			Object instance = entry.getValue();
			//返回的数组Field对象反映此表示的类或接口声明的所有字段类对象
			Field[] fields = entry.getValue().getClass().getDeclaredFields();
			for (Field field : fields) {
				// 没加autowired的不需要注值
				if (!field.isAnnotationPresent(LPAutowired.class)) {
					continue;
				}
				String beanName;
				// 获取AutoWired上面写的值，譬如@Autowired("abc")
				LPAutowired autowired = field.getAnnotation(LPAutowired.class);
				if ("".equals(autowired.value())) {
					// 例 searchService。注意，此处是获取属性的类名的首字母小写，与属性名无关，可以定义@Autowired
					// SearchService abc都可以。
					//getType() 返回类对象表示的字段的声明类型的Class对象
					beanName = toLowerFirstWord(field.getType().getSimpleName());
				} else {
					beanName = autowired.value();
				}
				// 将私有化的属性设为true,不然访问不到
				field.setAccessible(true);
				// 去映射中找是否存在该beanName对应的实例对象
				if (ioc.get(beanName) != null) {
					try {
						//将指定对象参数上的此Field对象表示的字段设置为指定的新值
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
	 * 把字符串的首字母小写
	 */
	private String toLowerFirstWord(String name) {
		char[] charArray = name.toCharArray();
		charArray[0] += 32;
		return String.valueOf(charArray);
}
}
