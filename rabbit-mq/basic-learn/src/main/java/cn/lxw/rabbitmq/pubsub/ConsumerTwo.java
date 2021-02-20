package cn.lxw.rabbitmq.pubsub;

import cn.lxw.rabbitmq.CommonConnect;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author xingweilin@clubfactory.com
 * @date 2021/2/12 10:32 上午
 */
public class ConsumerTwo {

    public static void main(String[] args) throws IOException, TimeoutException {

        Connection connection = CommonConnect.createConnect();
        Channel channel = connection.createChannel();

        //创建队列，如果队列已存在，则使用队列
        //第一个参数：队列名称
        //第二个参数：是否持久化
        //第三个参数：是否私有化
        //第四个参数：队列为空时，是否删除队列
        //第五个参数：而外信息，暂时不填
        channel.queueDeclare(CommonConnect.EXCHANGE_QUEUE_TWO, false, false, false, null);

        //队列名
        //交换机名
        //路由key，在广播模式下，该值不用填
        channel.queueBind(CommonConnect.EXCHANGE_QUEUE_TWO, CommonConnect.EXCHANGE_WEATHER, "");
        channel.basicQos(1);

        channel.basicConsume(CommonConnect.EXCHANGE_QUEUE_TWO, false, new QueueReceiver(channel));

    }


    static class QueueReceiver extends DefaultConsumer {

        private Channel channel;

        /**
         * Constructs a new instance and records its association to the passed-in channel.
         *
         * @param channel the channel to which this consumer is attached
         */
        public QueueReceiver(Channel channel) {
            super(channel);
            this.channel = channel;
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

            String message = new String(body);

            System.out.println("consumerOne 消费消息：" + message);

            //false：只签收当前消息，true：当前消息tagId之前第消息一起签收
            channel.basicAck(envelope.getDeliveryTag(), false);
        }
    }
}
