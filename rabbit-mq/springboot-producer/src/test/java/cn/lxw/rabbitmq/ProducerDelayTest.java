package cn.lxw.rabbitmq;

import cn.lxw.rabbitmq.config.RabbitMqConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author xingweilin@clubfactory.com
 * @date 2021/2/18 2:45 下午
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ProducerDelayTest {

    /**
     * 注入调用方法
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void send() {

        //发送超时消息
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setExpiration("30000");
        rabbitTemplate.send(RabbitMqConfig.DELAY_SOURCE_EXCHANGE_NAME, "delay.message", new Message("延迟30s 消费".getBytes(), messageProperties));

        messageProperties = new MessageProperties();
        messageProperties.setExpiration("10000");
        rabbitTemplate.send(RabbitMqConfig.DELAY_SOURCE_EXCHANGE_NAME, "delay.message", new Message("延迟10s 消费".getBytes(), messageProperties));
    }
}
