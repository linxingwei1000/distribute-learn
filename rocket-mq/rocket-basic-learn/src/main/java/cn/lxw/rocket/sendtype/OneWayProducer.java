package cn.lxw.rocket.sendtype;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * @author xingweilin@clubfactory.com
 * @date 2021/2/22 5:20 下午
 */
public class OneWayProducer {

    public static void main(String[] args) throws MQClientException, InterruptedException {

        DefaultMQProducer producer = new DefaultMQProducer("ProducerGroupName");
//        producer.setNamesrvAddr("192.168.232.128:9876");
        producer.start();

        for (int i = 0; i < 20; i++){
            try {
                {
                    Message msg = new Message("TopicTest",
                            "TagA",
                            "OrderID188",
                            "Hello world".getBytes(RemotingHelper.DEFAULT_CHARSET));
                    //同步传递消息，消息会发给集群中的一个Broker节点。
//                    SendResult sendResult = producer.send(msg);
//                    System.out.printf("%s%n", sendResult);
                    producer.sendOneway(msg);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        producer.shutdown();
    }
}
