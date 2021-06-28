//package combookservice.service.rabbit;
//
//import boot.util.FSTUtil;
//import boot.util.MsgDto;
//import combookservice.service.config.RabbitConfig;
//import combookservice.service.entity.Boy;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.amqp.AmqpException;
//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.core.MessagePostProcessor;
//import org.springframework.amqp.rabbit.connection.CorrelationData;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.UUID;
//
//@Component
//public class MsgRabbitSend implements RabbitTemplate.ConfirmCallback {
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    //由于rabbitTemplate的scope属性设置为ConfigurableBeanFactory.SCOPE_PROTOTYPE，所以不能自动注入
//    private RabbitTemplate rabbitTemplate;
//
//
//    @Autowired
//    public MsgRabbitSend(RabbitTemplate rabbitTemplate) {
//        this.rabbitTemplate = rabbitTemplate;
//        rabbitTemplate.setConfirmCallback(this); //rabbitTemplate如果为单例的话，那回调就是最后设置的内容
//    }
//
//    public void sendMsg(Object content) {
//        MsgDto msgDto = new MsgDto();
//        msgDto.setObj(content);
//        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
//        byte[] bytes = FSTUtil.encodeBuf(msgDto,msgDto.getClass());
//        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_A, RabbitConfig.ROUTINGKEY_A, bytes, correlationId);
//        DlxMsg messagePostProcessor = new DlxMsg(10000l);
//        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_A,RabbitConfig.ROUTINGKEY_A, bytes,messagePostProcessor,correlationId);
//    }
//    @Override
//    public void confirm(CorrelationData correlationData, boolean ack, String s) {
//        logger.info(" 回调id:" + correlationData);
//        if (ack) {
//            logger.info("消息成功消费");
//        } else {
//            logger.info("消息消费失败:" + s);
//        }
//    }
//    class DlxMsg implements MessagePostProcessor {
//
//        private final Long ttl; // 毫秒
//
//        public DlxMsg(Long ttl) {
//            this.ttl = ttl;
//        }
//
//        @Override
//        public Message postProcessMessage(Message message) throws AmqpException {
//            message.getMessageProperties().setExpiration(ttl.toString()); // 设置per-message的失效时间
//
//            return message;
//        }
//
//    }
//    public static void main(String[] args) {
//        Boy boy = new Boy(15,"tom",23);
//        MsgDto objectDto = new MsgDto();
//        objectDto.setObj(boy);
//
//
//        byte[] a  = FSTUtil.encodeBuf(objectDto,objectDto.getClass());
//
//        MsgDto b = (MsgDto)FSTUtil.decodeBuf(a,MsgDto.class);
//        System.out.println(b.getObj().toString());
//    }
//}
