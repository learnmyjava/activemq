package com.activemq;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.jms.Destination;
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
public class JMSUtil {
	/**以报文流水id 为主键的一个线程安全的haspmap*/
	 ConcurrentMap<String, RecvMessageBean> excuterthreadsmap = new ConcurrentHashMap<String, RecvMessageBean>();

	/**
	 * 异步无返回
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
	 * 异步无返回
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
					textMessage.setStringProperty("txncode", "0230");

					return textMessage;
				}
			});
		}

		long endtime = System.currentTimeMillis();
		System.out.println("发送总耗时:" + (endtime - starttime) + "ms");

	}
	
	/***
	 * 伪同步
	 *发送端作为消费者 将接收的消息放到 excuterthreadsmap，在根据消息id获取，在同一个方法中实现伪同步
	 *
	 *
	 *Semaphore 在计数器不为 0 的时候对线程就放行，一旦达到 0，那么所有请求资源的新线程都会被阻塞，包括增加请求到许可的线程，Semaphore 是不可重入的。
		每一次请求一个许可都会导致计数器减少 1，同样每次释放一个许可都会导致计数器增加 1，一旦达到 0，新的许可请求线程将被挂起。

	 */
	@Test
	public void mqQueueback(){
		//第一步
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		long starttime = System.currentTimeMillis();
		
		final String msgid= UUID.randomUUID().toString();
		System.out.println("发送消息id:"+msgid);
		
		RecvMessageBean recvMessageBean = new RecvMessageBean();
		excuterthreadsmap.putIfAbsent(msgid, recvMessageBean);
		
		// 第二步：从容器中获得JMSTemplate对象。
		JmsTemplate jmsTemplate = (JmsTemplate) applicationContext
				.getBean("jmsTemplate");
		// 第三步：从容器中获得一个Destination对象
		Queue queue = (Queue) applicationContext.getBean("txnQueue");
		final Destination respDestqueue = (Queue) applicationContext.getBean("txnReplyQueue");//返回目的
		
		boolean resultflag = false;//该资源上是否有回文
		// Topic queue = (Topic) applicationContext.getBean("txnTopic");
		// 第四步：使用JMSTemplate对象发送消息，需要知道Destination
		jmsTemplate.send(queue, new MessageCreator() {

			@Override
			public Message createMessage(Session session)
					throws JMSException {
				Message textMessage = session.createTextMessage("hello consumer I am producer");
				textMessage.setJMSReplyTo(respDestqueue);
				
				// 如果消费者设置了过滤器，此处发送前需要指定此参数
				textMessage.setStringProperty("reqTxnSeq", msgid);
				textMessage.setStringProperty("receiveSystem", "1210000001");
				textMessage.setStringProperty("txncode", "0220");
				return textMessage;
			}
		});

		long endtime = System.currentTimeMillis();
		System.out.println("发送总耗时:" + (endtime - starttime) + "ms");
		
		try {
			//挂在资源上,等待getReceiveTimeout ms 内是否有回文
			resultflag = recvMessageBean.getRecvsemap().tryAcquire(jmsTemplate.getReceiveTimeout(),TimeUnit.MILLISECONDS);//获取一个许可
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
		RecvMessageBean tempResultBean = excuterthreadsmap.remove(msgid);
		if(resultflag && tempResultBean !=null ){
			Message msg = tempResultBean.getRecvmsg();
			if(msg !=null ){
				TextMessage message = (TextMessage) msg;
				try {
					String returnmsg = message.getText();
					System.out.println("伪同步响应:"+returnmsg);
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}else{
				System.out.println("没有响应");
			}
		}
		
		
	}

	
	public void recvmessagefromserver(Message message) {
		
		TextMessage msg = (TextMessage) message;
		try {
			String returnmessage = msg.getText();
			System.out.println("收到信息:"+returnmessage);
			String msgid = message.getStringProperty("reqTxnSeq");
			System.out.println("收到信息id:"+msgid);

			RecvMessageBean tempresvBean = excuterthreadsmap.get(msgid);//key 对应的bean(发送之前放入map)
			if(tempresvBean !=null ){
				tempresvBean.setRecvmsg(message);//将返回的message设置给bean
				tempresvBean.getRecvsemap().release();//将消息挂在资源上，同时唤醒工作线程    //释放一个许可
			}else{
				System.out.println(" recvmessagefromserver key [{}]: is null"+msgid);

			}
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
	}

}
