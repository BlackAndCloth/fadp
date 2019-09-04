package aop.demo;

import com.mvc.custom.annotation.Aspect;
import com.mvc.custom.annotation.PointCut;

import aop.proxy.AbsMethodAdvance;

@Aspect
public class TestAspect extends AbsMethodAdvance {

	@PointCut("aop.demo.Test_doSomeThing")
	public void testAspect() {
	}

	@Override
	public void doBefore() {
		System.out.println(" ִ�з���ǰִ�� ");
	}

	@Override
	public void doAfter() {
		System.out.println("ִ�з�����ִ��");
	}
}
