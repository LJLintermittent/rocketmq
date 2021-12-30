## 《RocketMQ概述》

### 消息队列的作用

1.限流削峰：MQ可以将系统的超量请求暂存其中，以便系统后期可以慢慢进行处理，从而避免请求的丢失或者系统被压垮。

![image](https://cdn.jsdelivr.net/gh/chen-xing/figure_bed_02/cdn/20211231001948450.png)

2.异步解耦：上游系统对下游系统的调用若为同步调用，则会大大降低系统的吞吐量与并发度，且系统的耦合度太高，而异步调用则会解决这些问题，所以两层之间若要做到由同步向异步的转换，一般做法就是在这两层中间加一个MQ层

![image](https://cdn.jsdelivr.net/gh/chen-xing/figure_bed_02/cdn/20211231002354081.png)

3.数据收集：分布式系统会产生海量级数据，如：业务日志，监控数据，用户行为等，针对这些数据流进行实时或者批量采集汇总，然后对这些数据流进行大数据分析，这是当前互联网平台的必备技术，通过MQ完成此类数据收集是最好的选择

### RocketMQ基本概念

一.消息标识

rocketmq中的每个消息都有一个唯一的messageID，且可以携带具有业务标识的key，以方便对消息的查询，不过需要注意的是，messageId有两个，生产者send的时候会生成一个messageId(msgID)，当消息到达broker后，broker也会自动生成一个messageId（offsetMsgId）,msgId，offsetMsgId与key都称为消息的标识

msgId：由producer端生成，其生成规则为：producerId+进程id+messageClientIDSetter类的classloader的hashcode+当前时间，atomicInteger自增计算器

offsetmsgId：由broker端生成，生成规则为:brokerId+物理分区的offset

key:由用户指定的业务相关的唯一标识

二.系统架构

1.producer

消息生产者，负责生产消息，producer通过mq的负载均衡模块选择相应的broker集群队列进行消息投递，投递的过程支持快速失败并且低延迟。rocketmq中的消息生产者都是以生产者组的形式出现的，生产者组是同一类生产者的集合，这类producer发送相同的topic类型的消息

2.consumer

消息消费者，负责消费消息，一个消息消费者会从broker服务器中获取到消息， 并对消息进行相关业务处理。消息消费者也都是以消费者组的形式出现的，消费者组是同一类消费者的集合，这类consumer消费的是同一个topic类型的消息，消费者组使得在消息消费方面，实现负载均衡和容错的目标变得非常容易。



