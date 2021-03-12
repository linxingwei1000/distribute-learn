package cn.lxw.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xingweilin@clubfactory.com
 * @date
 * 2021/3/10 4:35 下午
 */
@RestController
public class KafkaController {

    private final static String TOPIC_NAME = "my-replicated-topic";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @RequestMapping("/send")
    public void send() {
        kafkaTemplate.send(TOPIC_NAME, 0, "key", "this is a msg");
    }
}
