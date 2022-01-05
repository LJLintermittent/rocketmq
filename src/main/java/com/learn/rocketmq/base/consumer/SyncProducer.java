package com.learn.rocketmq.base.consumer;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * date: 2022/1/1 18:47
 * Package: com.learn.rocketmq.base.consumer
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class SyncProducer {

    public static void main(String[] args) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        DefaultMQProducer mqProducer = new DefaultMQProducer("group1");
        mqProducer.setNamesrvAddr("192.168.190.136:9876;192.168.190.137:9876");
        mqProducer.start();
        for (int i = 0; i < 10; i++) {
            Message msg = new Message("base", "Tag1", ("Hello world" + i).getBytes());
            SendResult result = mqProducer.send(msg,1000000);
            System.out.println("发送结果：" + result);
            TimeUnit.SECONDS.sleep(1);
        }
        mqProducer.shutdown();
    }
}
