package cn.lxw.rabbitmq.mqlistener;

import com.google.common.collect.Maps;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * @author xingweilin@clubfactory.com
 * @date 2021/2/18 2:52 下午
 */
@Component
@Slf4j
public class RabbitMqAckListener {

    Map<String, Integer> consumeTime = Maps.newHashMap();

    /**
     * RabbitListener中的参数用于表示监听监听的队列
     */
    @RabbitListener(queues = "boot_queue")
    @RabbitHandler
    public void listenerQueue(Channel channel, Message message) {
        String messageText = new String(message.getBody());

        try {
            if (messageText.contains("finish")) {
                log.info("-------------消费消息成功：{}", messageText);

                //false：只确认当前消息id；
                //true：确认消费当前消费id之前的所有消息
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                Integer times = consumeTime.getOrDefault(messageText, 0);
                log.info("-------------消息id：{} 消费消息失败：{} 消费次数：{}", message.getMessageProperties().getDeliveryTag(), messageText, times);

                //消费超过次数
                if (times >= 3) {
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                    consumeTime.remove(messageText);
                }else{
                    //false：不重新丢人消息队列
                    //true：重新丢人消息队列
                    channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
                }

                ++times;
                consumeTime.put(messageText, times);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
