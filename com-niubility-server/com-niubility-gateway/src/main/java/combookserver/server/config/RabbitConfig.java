package combookserver.server.config;

import com.alibaba.fastjson.JSON;
import combookserver.server.test.Test;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
@Slf4j
@Configuration
public class RabbitConfig {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Bean
    public MessageConverter MessageConverter(){
       return new Jackson2JsonMessageConverter();
    }

    //对象创建完成后
    @PostConstruct
    public  void initRabbitTemplate(){

        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println();
                log.info(" 回调id:" + correlationData);
                if (ack) {
                    log.info("消息发布成功");
                } else {
                    log.info("消息发布失败:" + cause);
                }
            }
        });
        //消息没有投递队列就触发
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                byte[] dada = message.getBody();
                Test test = JSON.parseObject(dada,Test.class);
                System.out.println(test.toString());
                System.out.println("returnedMessage"+message.toString()+"--"+replyCode+"--"+replyText+"--"+exchange+"--"+routingKey);
            }
        });
    }
}
