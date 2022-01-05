package com.learn.rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
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

}
