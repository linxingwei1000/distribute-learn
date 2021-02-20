package cn.lxw.rabbitmq.simple;

import cn.lxw.rabbitmq.CommonConnect;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author xingweilin@clubfactory.com
 * @date 2021/2/12 10:32 上午
 */
public class Producer {

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = CommonConnect.createConnect();
        Channel channel =  connection.createChannel();

        //创建队列，如果队列已存在，则使用队列
        //第一个参数：队列名称
        //第二个参数：是否持久化
        //第三个参数：是否私有化
        //第四个参数：队列为空时，是否删除队列
        //第五个参数：而外信息，暂时不填
        channel.queueDeclare(CommonConnect.SIMPLE_QUEUE, false, false, false, null);


        String message = "lxw-123123";

        //第一个参数：交换机，简单模式无需填写
        //第二个参数：队列名
        //第三个参数：额外属性设置
        //第四个参数：消息内容
        channel.basicPublish("", CommonConnect.SIMPLE_QUEUE, null, message.getBytes());

        System.out.println("===消息【" + message + "】发送成功===");

        connection.close();
        channel.close();

    }
}
