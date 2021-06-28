package combookservice.service.service;

import combookservice.service.controller.Schedual2ServiceHi;
import combookservice.service.mapper.UserMapper;
import combookservice.service.rabbit.MsgRabbitSend;
import combookservice.service.util.RedissonUtil;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class BaseService implements IBaseService{
    private static final Logger log = LoggerFactory.getLogger(BaseService.class);

    @Autowired
    public Schedual2ServiceHi schedual2ServiceHi;
    @Autowired
    public UserMapper userMapper;
    @Autowired
    public RedisTemplate redisTemplate;
    @Autowired
    private RedissonUtil redissonUtil;
    @Autowired
    private MsgRabbitSend msgRabbitSend;
    public Object getConfig(String name){
        Object object = schedual2ServiceHi.getConfig(name);
        return object;
    }

    public Object getData(String name) {
        HashMap<String,Object> map = new HashMap<>();
        String sql = "SELECT * FROM USER";
        map.put("sql",sql);
        List<HashMap<String,Object>> data =  userMapper.selectMapList(map);

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
    public Object test(HashMap<String,Object> map) throws InterruptedException {
        map.put("msg","服务端base2 BaseService 类 ,我是第1台，是的！");
        log.info(map.toString());
        return map;
    }
    public Object testA(HashMap<String,Object> map,String a) throws InterruptedException {
        map.put("msg","来自服务端base2 BaseService 类 ");
        int i = 200+99;
        map.put("testA",i);
        log.info(map.toString());
        //Thread.sleep(9000);
        return map;
    }
    public Object test2(HashMap<String,Object> map)  {

        msgRabbitSend.sendMsg(map);

        return map;
    }
    @Transactional
    public Object test3(HashMap<String,Object> map)  {
        doTest();
        return "tttt";
    }
    public void doTest(){
        userMapper.insetLog("aaaa");
        int i =10/0;
    }
}
