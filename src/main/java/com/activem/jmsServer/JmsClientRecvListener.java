package com.activem.jmsServer;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.activemq.JMSUtil;



/**
 * 真正的消费者
 *伪同步 收到响应信息后的场景下不需要确认机制，此处实现MessageListener即可
 */
public class JmsClientRecvListener implements MessageListener {
	private Logger log = LoggerFactory.getLogger(JmsClientRecvListener.class);

	private JMSUtil clientrecv;

	public JMSUtil getClientrecv() {
		return clientrecv;
	}

	public void setClientrecv(JMSUtil c) {
		this.clientrecv =c;
	}

	public void onMessage(Message message) {
		try {
			clientrecv.recvmessagefromserver(message);

			
		} catch (Exception e) {
			log.error(e.getMessage());

		}

	}
}
