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
public class ProducerDLXTest {

    /**
     * 注入调用方法
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void send() throws InterruptedException {
        rabbitTemplate.convertAndSend(RabbitMqConfig.DLX_SOURCE_EXCHANGE_NAME, "dlx.message", "死信队列：异常抛弃消息");

        rabbitTemplate.convertAndSend(RabbitMqConfig.DLX_SOURCE_EXCHANGE_NAME, "dlx.message", "死信队列：队列满被挤出的消息1");

        rabbitTemplate.convertAndSend(RabbitMqConfig.DLX_SOURCE_EXCHANGE_NAME, "dlx.message", "死信队列：队列满被挤出的消息2");

        rabbitTemplate.convertAndSend(RabbitMqConfig.DLX_SOURCE_EXCHANGE_NAME, "dlx.message", "死信队列：超时1");

        rabbitTemplate.convertAndSend(RabbitMqConfig.DLX_SOURCE_EXCHANGE_NAME, "dlx.message", "死信队列：超时2");
    }
}
