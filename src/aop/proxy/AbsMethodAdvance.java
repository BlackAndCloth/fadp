package aop.proxy;


import java.lang.reflect.Method;

import com.mvc.custom.util.StringUtils;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public abstract class AbsMethodAdvance implements MethodInterceptor {

	private Object targetObject;
	private String proxyMethodName;

	public Object createProxyObject(Object target) {
		
		this.targetObject = target;
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(this.targetObject.getClass());
		enhancer.setCallback(this);

		return enhancer.create();
	}

	@Override
	public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
		Object result;

		String proxyMethod = getProxyMethodName();

		if (StringUtils.isNotBlank(proxyMethod) && proxyMethod.equals(method.getName())) {
			doBefore();
		}

		result = methodProxy.invokeSuper(proxy, args);

		if (StringUtils.isNotBlank(proxyMethod) && proxyMethod.equals(method.getName())) {
			doAfter();
		}

		return result;
	}

	public abstract void doBefore();

	public abstract void doAfter();

	public String getProxyMethodName() {
		return proxyMethodName;
	}

	public void setProxyMethodName(String proxyMethodName) {
		this.proxyMethodName = proxyMethodName;
	}
}
