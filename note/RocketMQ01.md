## 《RocketMQ概述》

### 消息队列的作用

1.限流削峰：MQ可以将系统的超量请求暂存其中，以便系统后期可以慢慢进行处理，从而避免请求的丢失或者系统被压垮。

![image](https://cdn.jsdelivr.net/gh/chen-xing/figure_bed_02/cdn/20211231001948450.png)

2.异步解耦：上游系统对下游系统的调用若为同步调用，则会大大降低系统的吞吐量与并发度，且系统的耦合度太高，而异步调用则会解决这些问题，所以两层之间若要做到由同步向异步的转换，一般做法就是在这两层中间加一个MQ层

![image](https://cdn.jsdelivr.net/gh/chen-xing/figure_bed_02/cdn/20211231002354081.png)

3.数据收集：分布式系统会产生海量级数据，如：业务日志，监控数据，用户行为等，针对这些数据流进行实时或者批量采集汇总，然后对这些数据流进行大数据分析，这是当前互联网平台的必备技术，通过MQ完成此类数据收集是最好的选择

4.数据分发：通过MQ可以让数据在多个系统之间进行沟通，数据的产生方不需要关心谁来使用数据，只需要将数据发送给消息队列，数据使用方直接在消息队列中直接获取数据即可

![image](https://cdn.jsdelivr.net/gh/chen-xing/figure_bed_02/cdn/20220101093721754.png)

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

3.broker

暂存和传输消息

4.nameserver

管理broker，相当于注册中心

5.topic

区分消息的种类，一个发送者发送消息给一个或者多个topic，一个消息的接收者可以订阅一个或者多个topic消息

6.message queue

相当于是topic的分区，用于并行发送和接收消息

### 集群特点

nameserver是一个无状态的节点，可以集群部署，节点之间没有任何信息同步

broker部署相对复杂，broker分为master和slave，一个master对应多个slave，但是一个slave只能对应一个master，master与slave之间的对应关系通过指定相同的brokername，不同的brokerID来定义，brokerid为0表示master，非0表示slave，master‘也可以部署多个，每个broker与nameserver集群中的所有节点建立长连接，定时注册topic信息到所有的nameserver

producer与nameserver集群中的其中一个节点（随机选择）建立长连接，定期从nameserver取topic路由信息，并向提供topic服务的master建立长连接，且定时向master发送心跳，producer完全无状态，可以集群部署

consumer与nameserver集群中的其中一个节点建立长连接，定期从nameserver中取topic路由信息，并向提供topic服务的master，slave建立长连接，且定时向master，slave发送心跳，consumer既可以从master订阅消息，也可以从slave订阅消息，订阅规则由broker配置决定

### RocketMQ集群启动命令

~~~shell
首先分别在192.168.190.136和192.168.190.137启动NameServer
nohup sh mqnamesrv &

在192.168.190.136上启动master1和slave2
nohup sh mqbroker -c /mydata/rocketmq/rocketmq-all-4.4.0-bin-release/conf/2m-2s-sync/broker-a.properties &
nohup sh mqbroker -c /mydata/rocketmq/rocketmq-all-4.4.0-bin-release/conf/2m-2s-sync/broker-b-s.properties &

在192.168.190.137上启动master2和slave1
nohup sh mqbroker -c /mydata/rocketmq/rocketmq-all-4.4.0-bin-release/conf/2m-2s-sync/broker-b.properties &
nohup sh mqbroker -c /mydata/rocketmq/rocketmq-all-4.4.0-bin-release/conf/2m-2s-sync/broker-a-s.properties &

~~~

~~~wiki
排坑：在对应的broker配置文件中，一定要加上虚拟机的外网IP，即可以在另一台机器上进行访问，比如同一个局域网下可以访问的192.168.190.136这种ip，这样本地的Java代码才能连接到rocketmq集群。
在配置文件中配置brokerIP1=192.168.190.136 信息
~~~

