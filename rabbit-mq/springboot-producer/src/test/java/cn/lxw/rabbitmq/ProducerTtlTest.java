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
public class ProducerTtlTest {

    /**
     * 注入调用方法
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void send() {

        //发送超时消息
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setExpiration("10000");
        rabbitTemplate.send(RabbitMqConfig.TTL_EXCHANGE_NAME, "ttl.hhhhh", new Message("testest".getBytes(), messageProperties));

        rabbitTemplate.convertAndSend(RabbitMqConfig.TTL_EXCHANGE_NAME, "ttl.hhhhh", "finis.12312313");

        try {
            Thread.sleep(15 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
