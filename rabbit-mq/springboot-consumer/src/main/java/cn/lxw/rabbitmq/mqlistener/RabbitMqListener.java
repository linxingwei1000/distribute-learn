package cn.lxw.rabbitmq.mqlistener;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author xingweilin@clubfactory.com
 * @date 2021/2/18 2:52 下午
 */
//@Component
public class RabbitMqListener {

    /**
     * RabbitListener中的参数勇于表示监听监听的队列
     */
    @RabbitListener(queues = "boot_queue")
    public void listenerQueue(Message message){
        System.out.println("message:" + message);
    }
}
