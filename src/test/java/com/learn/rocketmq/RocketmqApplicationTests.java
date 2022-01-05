package com.learn.rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RocketmqApplicationTests {

    /**
     * 同步消息
     */
    @Test
    public void SyncProducerTest() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        producer.setNamesrvAddr("192.168.190.136:9876:9876;192.168.190.136:9876:9876");
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
    public void AsyncProducerTest() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        producer.setNamesrvAddr("192.168.190.136:9876:9876;192.168.190.136:9876:9876");
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
    public void OneWayProducerTest() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        producer.setNamesrvAddr("192.168.190.136:9876:9876;192.168.190.136:9876:9876");
        producer.start();
        for (int i = 0; i < 3; i++) {
            Message msg = new Message("base", "Tag3", ("单向消息" + i).getBytes());
            producer.sendOneway(msg);
            TimeUnit.SECONDS.sleep(1);
        }
        producer.shutdown();
    }

}
