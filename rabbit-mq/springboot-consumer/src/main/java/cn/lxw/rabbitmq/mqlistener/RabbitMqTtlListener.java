package cn.lxw.rabbitmq.mqlistener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author xingweilin@clubfactory.com
 * @date 2021/2/18 2:52 下午
 */
@Component
@Slf4j
public class RabbitMqTtlListener {

    /**
     * RabbitListener中的参数用于表示监听监听的队列
     */
    @RabbitListener(queues = "ttl_queue")
    @RabbitHandler
    public void listenerQueue(Channel channel, Message message) {
        String messageText = new String(message.getBody());

        log.info("-------------消费消息：{}", messageText);

        //false：只确认当前消息id；
        //true：确认消费当前消费id之前的所有消息
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
