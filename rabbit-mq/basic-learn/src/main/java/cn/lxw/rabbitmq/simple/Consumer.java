package cn.lxw.rabbitmq.simple;

import cn.lxw.rabbitmq.CommonConnect;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author xingweilin@clubfactory.com
 * @date 2021/2/12 10:32 上午
 */
public class Consumer {

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = CommonConnect.createConnect();
        Channel channel =  connection.createChannel();


        channel.basicConsume(CommonConnect.SIMPLE_QUEUE, false, new SimpleQueueReceiver(channel));

    }


    static class SimpleQueueReceiver extends DefaultConsumer{

        private Channel channel;

        /**
         * Constructs a new instance and records its association to the passed-in channel.
         *
         * @param channel the channel to which this consumer is attached
         */
        public SimpleQueueReceiver(Channel channel) {
            super(channel);
            this.channel = channel;
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

            String message = new String(body);

            System.out.println("获取消息：" + message);
            System.out.println("获取第消息tagId:" + envelope.getDeliveryTag());

            //false：只签收当前消息，true：当前消息tagId之前第消息一起签收
            channel.basicAck(envelope.getDeliveryTag(), false);
        }
    }
}
