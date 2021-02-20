package cn.lxw.rabbitmq;

import cn.lxw.rabbitmq.config.RabbitMqConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class ProducerTest {

    /**
     * 注入调用方法
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void send(){
        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, "boot.hhhhh", "finis.12312313");
    }
}
