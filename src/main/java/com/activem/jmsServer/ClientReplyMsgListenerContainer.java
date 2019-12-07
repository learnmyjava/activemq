
package com.activem.jmsServer;

import org.springframework.jms.listener.DefaultMessageListenerContainer;


/**
 * 消费者(接受者)监听器
 * @author lihonghui
 *
 */
public class ClientReplyMsgListenerContainer extends DefaultMessageListenerContainer {

	public ClientReplyMsgListenerContainer() {
		super();
		this.setMessageSelector("");
	}
	
	public void setMessageSelector(String messageSelector) {
		
		
	}
	

	
/*	public String getLocalSystem() {
		return localSystem;
	}
	public void setLocalSystem(String localSystem) {
		this.localSystem = localSystem;
		 String selector = JmsCode.MSG_HEADER_SENDID+"= '" + JmsCode.INST_ID + "' AND "+JmsCode.MSG_HEADER_RECEIVESYSTEM+"= '"+localSystem+"'";

			//设置过滤器
			if (this.getMessageSelector()==null ||"".equals(this.getMessageSelector())){
				super.setMessageSelector(selector);
			}else{
				super.setMessageSelector(this.getMessageSelector()+" AND "+selector);

			}
	}*/

	

}
