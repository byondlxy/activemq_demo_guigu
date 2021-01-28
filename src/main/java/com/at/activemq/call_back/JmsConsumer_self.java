package com.at.activemq.call_back;


import lombok.SneakyThrows;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

// 消息的消费者  也就是回答消息的系统
public class JmsConsumer_self {
    public static final String ACTIVEMQ_URL = "tcp://192.168.199.150:61616";
    // public static final String ACTIVEMQ_URL = "nio://192.168.17.3:61608";
    public static final String QUEUE_NAME = "jdbc01";

    public static void main(String[] args) throws Exception {

        System.out.println("我是2号消费者");
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        Connection connection = activeMQConnectionFactory.createConnection();

        connection.start();

        Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);

        Queue queue = session.createQueue(QUEUE_NAME);

        //创建消费者
        MessageConsumer messageConsumer = session.createConsumer(queue);


//          同步阻塞方式reveive()   空参数的receive方法是阻塞，有参数的为等待时间
//          订阅者或消费者使用MessageConsumer 的receive() 方法接收消息，receive 在接收之前一直阻塞
//            while(true){
//            // 这里是 TextMessage 是因为消息发送者是 TextMessage ， 接受处理的
//            // 也应该是这个类型的消息
//            TextMessage message = (TextMessage)messageConsumer.receive(4000);  // 4秒/无限等待消费
//            if (null != message){
//                System.out.println("****消费者的消息："+message.getText());
//            }else {
//                break;
//            }
//        }


        messageConsumer.setMessageListener(new MessageListener() {
            @SneakyThrows
            public void onMessage(Message message) {
                if (null != message && message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    textMessage.acknowledge();
                    try {
                        System.out.println("****消费者的消息：" + textMessage.getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // 保证控制台不灭  不然activemq 还没连上就关掉了连接
        System.in.read();
        session.commit();
        messageConsumer.close();
        session.close();
        connection.close();
    }
}
