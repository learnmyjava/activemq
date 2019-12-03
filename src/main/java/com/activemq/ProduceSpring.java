package com.activemq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public final class ProduceSpring {

	/**
	 * @param args
	 */
	private final static Logger LOG = LoggerFactory
			.getLogger(ProduceSpring.class);

	/**
	 * Hide Utility Class Constructor
	 */
	private ProduceSpring() {
	}

	public static void main(String[] args) {
		/* 开始加载spring配置文件 */
		ConfigurableApplicationContext spirngload = getApplicationContext();
		/* 注册shutdown钩子 */
		spirngload.registerShutdownHook();
	}

	public static ConfigurableApplicationContext getApplicationContext() {
		return new ClassPathXmlApplicationContext("applicationContext.xml");
	}
}
