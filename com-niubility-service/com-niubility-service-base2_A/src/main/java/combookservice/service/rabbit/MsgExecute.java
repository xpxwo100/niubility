package combookservice.service.rabbit;

import boot.util.FSTUtil;
import boot.util.MsgDto;
import boot.util.Usual;
import com.rabbitmq.client.Channel;
import combookservice.service.config.RabbitConfig;
import combookservice.service.mapper.UserMapper;
import combookservice.service.spring.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.HashMap;

/**
 * 消息消费者
 */
@Service
public class MsgExecute {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    public ConnectionFactory connectionFactory;
    @Autowired
    public UserMapper userMapper;
    /*@Bean
    public SimpleMessageListenerContainer messageContainer() {
        //加载处理消息A的队列
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        //设置接收多个队列里面的消息，这里设置接收队列A
        //假如想一个消费者处理多个队列里面的信息可以如下设置：
        //container.setQueues(queueA(),queueB(),queueC());
        container.setQueues(RabbitConfig.queueA());
        container.setExposeListenerChannel(true);
        //设置最大的并发的消费者数量
        container.setMaxConcurrentConsumers(10);
        //最小的并发消费者的数量
        container.setConcurrentConsumers(1);
        //设置确认模式手工确认
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageListener(new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                channel.basicQos(1);
                byte[] body = message.getBody();
                MsgDto b = (MsgDto) FSTUtil.decodeBuf(body,MsgDto.class);
                logger.info("A消费者：接收处理队列A当中的消息:" + b.getObj());
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        });
        return container;
    }*/

    @Bean
    public SimpleMessageListenerContainer messageContainer2() {
        //加载处理消息A的队列
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        //设置接收多个队列里面的消息，这里设置接收队列A
        //假如想一个消费者处理多个队列里面的信息可以如下设置：
        //container.setQueues(queueA(),queueB(),queueC());
        container.setQueues(RabbitConfig.queueA(),RabbitConfig.queueB(),RabbitConfig.queueC());
        container.setExposeListenerChannel(true);
        //设置最大的并发的消费者数量
        container.setMaxConcurrentConsumers(10);
        //最小的并发消费者的数量
        container.setConcurrentConsumers(1);
        //设置确认模式手工确认
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageListener(new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                channel.basicQos(1);
                byte[] body = message.getBody();
                String queue = message.getMessageProperties().getConsumerQueue();
                MsgDto b = (MsgDto) FSTUtil.decodeBuf(body,MsgDto.class);
                if(doExecute(b)){
                    logger.info(" B消费者：接收处理队列"+queue+"当中的消息:" + b.getObj());
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                }else{
                    logger.info(" B消费者：失败 接收处理队列"+queue+"当中的消息:" + b.getObj());
                    //channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
                    //拒绝消息，设置false不重回队列  true重回队列，会再次取这条消息处理
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                }
            }
        });

        return container;
    }
    @Autowired
    public PlatformTransactionManager transactionManager;
    /**
     * 具体处理
     * @param msgDto
     * @return
     */
    public Boolean doExecute(MsgDto msgDto){
        //1.获取事务控制管理器
        //DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) SpringContextUtil.getBean("transactionManager_MyBatis", DataSourceTransactionManager.class);
        //2.获取事务定义
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //3.设置事务隔离级别，开启新事务
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        //4.获得事务状态
        TransactionStatus transactionStatus = transactionManager.getTransaction(def);
        boolean isError = true;
        try {
            Object object = msgDto.getObj();
            HashMap<String,Object> map =  (HashMap<String,Object>)object;
            String s = Usual.f_getString(map.get("session"));
            userMapper.insetLog(s);
            //int i = 10/0;
        }catch (Exception exp){
            isError = false;
            return false;
        }finally{
            if (isError) {
                transactionManager.commit(transactionStatus);
            }else{
                transactionManager.rollback(transactionStatus);
            }
        }
        return true;
    }
}
