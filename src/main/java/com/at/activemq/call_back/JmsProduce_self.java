package com.at.activemq.call_back;


import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageProducer;
import org.apache.activemq.AsyncCallback;

import javax.jms.*;
import java.util.UUID;


// 带接收回调的异步发送
public class JmsProduce_self {
    public static final String ACTIVEMQ_URL = "tcp://192.168.199.150:61616";
    public static final String QUEUE_NAME = "jdbc01";


    public static void main(String[] args) throws Exception {

        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);

        // 设置允许有数据丢失  
        activeMQConnectionFactory.setUseAsyncSend(true);

        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        //创建会话session
        //两个参数。第一个叫事务/第二个叫签收
        Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
        //创建目的地(主题还是队列)
        Queue queue = session.createQueue(QUEUE_NAME);
        //创建消息生产者
        ActiveMQMessageProducer messageProducer = (ActiveMQMessageProducer) session.createProducer(queue);
        messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
      //  messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);   // 持久化  如果开启
        TextMessage textMessage = null;
        for (int i = 1; i < 4; i++) {
            textMessage = session.createTextMessage("msg--" + i);
            messageProducer.send(textMessage);
           // textMessage.setJMSMessageID(UUID.randomUUID().toString() + "--  orderr");
           // String msgid = textMessage.getJMSMessageID();
        }
        messageProducer.close();
        session.commit();
        session.close();
        connection.close();
        System.out.println("  **** 消息发送到MQ完成 ****");
    }
}
