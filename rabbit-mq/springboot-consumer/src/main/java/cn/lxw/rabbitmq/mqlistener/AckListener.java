package cn.lxw.rabbitmq.mqlistener;

import com.google.common.collect.Maps;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * @author xingweilin@clubfactory.com
 * @date 2021/2/18 4:59 下午
 *
 * spring mvc 的方式
 */
@Slf4j
//@Component
public class AckListener implements ChannelAwareMessageListener {

    Map<Long, Integer> consumeTime = Maps.newHashMap();

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        String messageText = new String(message.getBody());

        try {
            if (messageText.contains("finish")) {
                log.info("-------------消费消息成功：{}", messageText);

                //false：只确认当前消息id；
                //true：确认消费当前消费id之前的所有消息
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                Integer times = consumeTime.getOrDefault(message.getMessageProperties().getDeliveryTag(), 0);
                log.info("-------------消费消息失败：{} 消费次数：{}", messageText, times);

                //false：不重新丢人消息队列
                //true：重新丢人消息队列
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);

                //消费超过次数
                if(times>=3){
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                }
                consumeTime.put(message.getMessageProperties().getDeliveryTag(), ++times);
            }
        } catch (IOException e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            e.printStackTrace();
        }
    }
}
