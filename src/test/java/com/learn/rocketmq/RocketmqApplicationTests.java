package com.learn.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RocketmqApplicationTests {

    /**
     * 同步消息
     */
    @Test
    public void syncProducerTest() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        producer.setNamesrvAddr("192.168.190.136:9876;192.168.190.137:9876");
        producer.start();
        for (int i = 0; i < 10; i++) {
            Message msg = new Message("base", "Tag1", ("Hello world" + i).getBytes());
            SendResult result = producer.send(msg);
            System.out.println("发送结果：" + result);
            TimeUnit.SECONDS.sleep(1);
        }
        producer.shutdown();

    }

    /**
     * 异步消息
     */
    @Test
    public void asyncProducerTest() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        producer.setNamesrvAddr("192.168.190.136:9876;192.168.190.137:9876");
        producer.start();
        for (int i = 0; i < 10; i++) {
            Message msg = new Message("base", "Tag2", ("Hello world" + i).getBytes());
            producer.send(msg, new SendCallback() {
                // 发送成功的回调函数
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.println("发送成功 " + sendResult);
                }

                // 发送失败的回调函数
                @Override
                public void onException(Throwable throwable) {
                    System.out.println("发送失败 " + throwable);
                }
            });
            TimeUnit.SECONDS.sleep(1);
        }
        producer.shutdown();

    }

    /**
     * 单向消息（这种方式主要用在不特别关心发送结果的场景，例如日志发送）
     */
    @Test
    public void oneWayProducerTest() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        producer.setNamesrvAddr("192.168.190.136:9876;192.168.190.137:9876");
        producer.start();
        for (int i = 0; i < 3; i++) {
            Message msg = new Message("base", "Tag3", ("单向消息test" + i).getBytes());
            producer.sendOneway(msg);
            TimeUnit.SECONDS.sleep(1);
        }
        producer.shutdown();
    }

    /**
     * 消费消息（此测试需要监听，要使用main函数运行）
     */
    @Test
    public void consumerTest() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group1");
        consumer.setNamesrvAddr("192.168.190.136:9876;192.168.190.137:9876");
        consumer.subscribe("base", "Tag3");
        consumer.setVipChannelEnabled(false);
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                for (MessageExt msg : msgs) {
                    System.out.println(new String(msg.getBody()));
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
    }

}
