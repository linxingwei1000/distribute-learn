package cn.lxw.rabbitmq.queue;

import cn.lxw.rabbitmq.CommonConnect;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * @author xingweilin@clubfactory.com
 * @date 2021/2/12 10:32 上午
 */
public class Consumer {

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.execute(() -> startMqConsumer(1));
        executorService.execute(() -> startMqConsumer(2));

    }

    private static void startMqConsumer(Integer num){
        Connection connection = null;
        try {
            connection = CommonConnect.createConnect();
            Channel channel =  connection.createChannel();

            //basicQos不设置，MQ自动把消息平均发送给consumer
            //basicQos==1，consumer消费完消息ack消息之后，才能再次从队列获取消息消费
            channel.basicQos(1);

            channel.basicConsume(CommonConnect.WORK_QUEUE, false, new QueueReceiver(channel, num));
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }

    }


    static class QueueReceiver extends DefaultConsumer{

        private Channel channel;

        private Integer consumerNum;

        /**
         * Constructs a new instance and records its association to the passed-in channel.
         *
         * @param channel the channel to which this consumer is attached
         */
        public QueueReceiver(Channel channel, Integer num) {
            super(channel);
            this.channel = channel;
            this.consumerNum = num;
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

            String message = new String(body);

            System.out.println("consumer["+ consumerNum+"] 消费消息：" + message);
            System.out.println("获取第消息tagId:" + envelope.getDeliveryTag());

            //false：只签收当前消息，true：当前消息tagId之前第消息一起签收
            channel.basicAck(envelope.getDeliveryTag(), false);
        }
    }
}
