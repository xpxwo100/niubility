package combookproductcontroller.util;

import boot.util.TestM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Slf4j
@Component
public class MesSend {
    public static volatile RabbitTemplate rabbitTemplate;


    @Autowired
    public  void initMesSend(RabbitTemplate rabbitTemplate){
        MesSend.rabbitTemplate = rabbitTemplate;
    }

    //发送消息
    public static void sendMsg(TestM t) {
        log.info("rabbitTemplate:"+rabbitTemplate.toString());
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        t.code = correlationId.getId();
        try {
            rabbitTemplate.convertAndSend("xpx", "xpx.a", t, correlationId);
            //记录数据库
            //
        }catch (Exception e){
            //记录数据库
            //
        }
    }
}
