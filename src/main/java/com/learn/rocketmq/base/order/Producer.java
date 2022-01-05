package com.learn.rocketmq.base.order;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;

/**
 * Description:
 * date: 2022/1/5 21:41
 * Package: com.learn.rocketmq.base.order
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class Producer {

    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        producer.setNamesrvAddr("192.168.190.136:9876;192.168.190.137:9876");
        producer.start();
        List<OrderStep> orderSteps = OrderStep.buildOrders();
        for (int i = 0; i < orderSteps.size(); i++) {
            String body = orderSteps.get(i) + "";
            Message message = new Message("ordertopic", "order", "i" + i, body.getBytes());
            SendResult sendResult = producer.send(message, new MessageQueueSelector() {
                /**
                 * 控制顺序消费，需要把同一个订单id的消息发送往同一个队列中来控制顺序
                 * @param mqs 队列集合
                 * @param msg 消息对象
                 * @param arg 业务标识的参数，比如订单id
                 */
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    long orderId = (long) arg;
                    long index = orderId % mqs.size();
                    return mqs.get((int) index);
                }
            }, orderSteps.get(i).getOrderId());
            System.out.println("发送结果：" + sendResult);
        }
        producer.shutdown();
    }
}
