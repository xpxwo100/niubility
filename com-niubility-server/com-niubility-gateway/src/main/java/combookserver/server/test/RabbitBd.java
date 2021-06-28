package combookserver.server.test;

import boot.util.TestM;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;
@Component
public class RabbitBd {
    @Autowired
    AmqpAdmin amqpAdmin;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Bean
    public Exchange CreatedExechange(){
        Exchange e = new TopicExchange("xpx", true, false );
        return e;
    }
    @Bean
    public Queue CreatedQueue(){
        Queue q = new Queue("xpx1", true, false, false);
        return q;
    }
    @Bean
    public Binding CreatedBind(){
        //String destination, DestinationType destinationType, String exchange, String routingKey,
        //			Map<String, Object> arguments
        Binding b = new Binding("xpx1", Binding.DestinationType.QUEUE,
                    "xpx","xpx.a",null
                );

        return b;
    }
    public void sendMsg(Test t){
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend("xpx","xpx.a",t,correlationId);
    }
    //@RabbitListener(queues = "xpx1")
    public void getMsg(Message message, TestM test, Channel channel){
        message.getMessageProperties().getCorrelationId();
        System.out.println("消費消息getMsg"+test);
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);//确认消息已消费
        } catch (IOException e) {
            e.printStackTrace();
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,true);//确认消息已消费
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

   /* @RabbitHandler
    public void getMsg2(Message message, Object test, Channel cccc){
        message.getMessageProperties().getCorrelationId();
        System.out.println("消費消息getMsg2"+test);
    }*/
}
