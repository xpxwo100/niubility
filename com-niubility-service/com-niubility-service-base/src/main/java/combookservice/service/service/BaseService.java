package combookservice.service.service;

import boot.nettyClient.NettyRpcUtil;
import boot.nettyRpcModel.ObjectDto;
import boot.util.TestM;
import com.rabbitmq.client.Channel;
import combookservice.service.controller.Schedual2ServiceHi;
import combookservice.service.mapper.UserMapper;
import combookservice.service.util.RedisUtils;
import combookservice.service.util.RedissonUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
@Slf4j
@Service
public class BaseService implements IBaseService {
    //private static final Logger log = LoggerFactory.getLogger(BaseService.class);

    @Autowired
    public Schedual2ServiceHi schedual2ServiceHi;
    @Autowired
    public UserMapper userMapper;
    @Autowired
    public RedisTemplate redisTemplate;
    @Autowired
    private RedissonUtil redissonUtil;
  /*  @Autowired
    private MsgRabbitSend msgRabbitSend;*/
    @Autowired
    private RedisUtils redisUtils;

    public Object getConfig(String name) {
        Object object = schedual2ServiceHi.getConfig(name);
        return object;
    }

    public Object getData(String name) {
        HashMap<String, Object> map = new HashMap<>();
        String sql = "SELECT * FROM test  limit 10";
        map.put("sql", sql);
        List<HashMap<String, Object>> data = userMapper.selectMapList(map);
        redisUtils.lSet("a", data);
        redisUtils.set("b", data);
        return data;
    }

    public void testLock(String recordId) {
        RLock lock = redissonUtil.getRLock(recordId);
        try {
            boolean bs = lock.tryLock(5, 6, TimeUnit.SECONDS);
            lock.lock(60, TimeUnit.SECONDS); //设置60秒自动释放锁  （默认是30秒自动过期）
            if (bs) {
                // 业务代码
                log.info("进入业务代码: " + recordId);

                lock.unlock();
            } else {
                Thread.sleep(300);
            }
        } catch (Exception e) {
            log.error("", e);
            lock.unlock();
        }
    }

    public Object test(HashMap<String, Object> map) throws InterruptedException {
        map.put("msg", "来自服务端base BaseService 类 ");
        log.info(map.toString());
        return map;
    }

    public Object testA(HashMap<String, Object> map, String a) throws InterruptedException {
        map.put("msg", "来自服务端base BaseService 类 ");
        int i = 200 + 99;
        map.put("testA", i);
        log.info(map.toString());
        //Thread.sleep(9000);
        return map;
    }

    public Object tests(String s) {
        return s;
    }

    public Object test2(HashMap<String, Object> map) {

        //msgRabbitSend.sendMsg(map);

        return map;
    }

    @Transactional(rollbackFor = Exception.class)
    public Object test3(HashMap<String, Object> map) {
        doTest();
        return "tttt";
    }

    public void doTest() {
        userMapper.insetLog("aaaa");


    }


    public void nettyTest() {
        HashMap<String, Object> map = new HashMap<>();
        String sql = "SELECT * FROM USER";
        map.put("sql", sql);
        ObjectDto objectDto = NettyRpcUtil.load(true, "com-niubility-service-base2", "baseService", "test", map);
    }

    public Object findData(HashMap<String, Object> map) {
        String key = (String) map.get("key");
        Object s = redisUtils.get(key);
        if (s != null) {
            return s;
        } else {
            RLock lock = redissonUtil.getRLock(key);//防止緩存擊穿
            try {
                lock.tryLock(30, TimeUnit.SECONDS);
                Object s1 = redisUtils.get(key);
                if (s1 != null) {
                    return s1;
                }
                List<HashMap<String, Object>> a1 = userMapper.selectMapList(map);
                if (a1 == null) {
                    redisUtils.set(key, null, 60);//防止緩存穿透
                } else {
                    redisUtils.set(key, a1);
                }
                return a1;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
        return s;
    }

//    public void updateData(HashMap<String, Object> map) {
//        String key = (String) map.get("key");
//        //先淘汰缓存
//        //再写数据库（这两步和原来一样）
//        //休眠1秒，再次淘汰缓存
//        redisUtils.del(key);
//        if (s) {
//            userMapper.insetLog(key);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    Boolean s1 = redisUtils.del(key);
//                }
//            }).start();
//        }
//    }
    @RabbitListener(queues = "xpx1")
    public void getMsg(Message message, TestM test, Channel channel){
        message.getMessageProperties().getCorrelationId();
        try {
            System.out.println("消費消息getMsg"+test);
            log.info("消費消息getMsg"+test);
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
}
