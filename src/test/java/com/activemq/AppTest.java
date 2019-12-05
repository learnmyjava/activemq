package com.activemq;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/**
 * Unit test for simple App.
 */
public class AppTest {
	/**
	 * 点对点 将消息发送到mq队列存储 一条消息只能被一个消费者消费
	 * 没有被消费的会被存储
	 */
	@Test
	public void mqQueue() {

		// ApplicationContext applicationContext = new
		// ClassPathXmlApplicationContext("class:spring/applicationContext-jms.xml");
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		// 第二步：从容器中获得JMSTemplate对象。
		long starttime = System.currentTimeMillis();
		for (int i = 1; i <= 1; i++) {
			final String sendmessage = String.valueOf(i);
			JmsTemplate jmsTemplate = (JmsTemplate) applicationContext
					.getBean("jmsTemplate");
			// 第三步：从容器中获得一个Destination对象
			Queue queue = (Queue) applicationContext.getBean("txnQueue");
			// Topic queue = (Topic) applicationContext.getBean("txnTopic");
			// 第四步：使用JMSTemplate对象发送消息，需要知道Destination
			jmsTemplate.send(queue, new MessageCreator() {

				@Override
				public Message createMessage(Session session)
						throws JMSException {
					TextMessage textMessage = session.createTextMessage("第"
							+ sendmessage + "条消息:hello consumer I am producer");
					// 如果消费者设置了过滤器，此处发送前需要指定此参数
					textMessage.setStringProperty("receiveSystem", "1210000001");
					textMessage.setStringProperty("txncode", "0230");
					return textMessage;
				}
			});
		}

		long endtime = System.currentTimeMillis();
		System.out.println("发送总耗时:" + (endtime - starttime) + "ms");

	}

	/**
	 * 发布订阅 
	 * 生产者开启持久化订阅持久化消息到文件或者数据库(数据库: mq重启后消息被清除)
	 * 
	 * 1.当消费者开启持久化成功注册到mq之后,如果该消费者掉线后再重新监听到mq,会拉取到其掉线期间的消息
	 * 注册到mq之前的时间段内，mq持久化的消息，该消费者获取不到
	 * 
	 * 2.如果消费者没有开启持久化注册到mq之后，当其掉线重新上线后，不能获取这其间的消息(借助广播理解)
	 * 
	 */
	@Test
	public void mqTopic() {

		// ApplicationContext applicationContext = new
		// ClassPathXmlApplicationContext("class:spring/applicationContext-jms.xml");
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		// 第二步：从容器中获得JMSTemplate对象。
		long starttime = System.currentTimeMillis();
		for (int i = 1; i <= 1; i++) {
			final String sendmessage = String.valueOf(i);
			JmsTemplate jmsTemplate = (JmsTemplate) applicationContext
					.getBean("jmsTemplate");
			// 第三步：从容器中获得一个Destination对象
			Topic topic = (Topic) applicationContext.getBean("txnTopic");
			// 第四步：使用JMSTemplate对象发送消息，需要知道Destination
			jmsTemplate.send(topic, new MessageCreator() {

				@Override
				public Message createMessage(Session session)
						throws JMSException {
					TextMessage textMessage = session.createTextMessage("第"
							+ sendmessage + "条消息:hello consumer I am producer");
					// 如果消费者设置了过滤器，此处发送前需要指定此参数
					textMessage
							.setStringProperty("receiveSystem", "1210000001");
					textMessage.setStringProperty("txncode", "0220");

					return textMessage;
				}
			});
		}

		long endtime = System.currentTimeMillis();
		System.out.println("发送总耗时:" + (endtime - starttime) + "ms");

	}

}
