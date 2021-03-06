package combookservice.service.controller;

import boot.nettyClient.NettyRpcUtil;
import boot.nettyRpcModel.AppRunTimeException;
import boot.nettyRpcModel.ObjectDto;
import combookservice.service.service.BaseService;
import combookservice.service.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class HiController {
    @Autowired
    public BaseService baseService;
    @Autowired
    Schedual2ServiceHi schedual2ServiceHi;
    @Autowired
    public RedisUtils redisUtils;
    @ResponseBody
    @RequestMapping(value = "/hi",method = RequestMethod.GET)
    public Object sayHi(@RequestParam String name){
        //return schedual2ServiceHi.sayHiFromClientOne(name);

        //redisUtils.set("aa","11111");
        //return redisUtils.get("aa");
        return 89;
    }


    @RequestMapping(value = "/linkBaseService",method = RequestMethod.GET)
    public Object linkBaseService(@RequestParam String name){
        //Object value = baseService.getConfig(name);
        //Object value = baseService.getData("base:"+name);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("sessionId", "base");
        List<HashMap<String,Object>> data = (List<HashMap<String, Object>>) redisUtils.get("b");
        List<Object> data2 = redisUtils.lGet("a",0,1);

        return data2;
    }

    @ResponseBody
    @RequestMapping(value = "/session")
    public Map<String, Object> getSession(HttpServletRequest request) {
        request.getSession().setAttribute("username", "admin");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("sessionId", request.getSession().getId());
        map.put("test","tes1234");
        Object value = baseService.getData("base");
        map.put("value",value);
        return map;
    }
   /* @ResponseBody
    @RequestMapping(value = "/get")
    public String get(HttpServletRequest request) {
        //String userName = (String) request.getSession().getAttribute("username");
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("session",request.getSession().getId());
        baseService.test2(map);
        return "??????";
    }*/
    static  int a = 0;
    public static AtomicInteger atomicInteger = new AtomicInteger(0);
    @ResponseBody
    @RequestMapping(value = "/get")
    public Object get(HttpServletRequest request) {
        String userName = (String) request.getSession().getAttribute("username");
        final HashMap<String, Object> map2 = new HashMap<>();
        HashMap<String, Object> value = new HashMap<>();
        value.put("isSuccess",true);
        map2.put("sad",56789);
        try {
            atomicInteger = new AtomicInteger(0);
            a = 0;
            long s = System.currentTimeMillis();
           /* if(cachedThreadPool == null){
                cachedThreadPool =  Executors.newFixedThreadPool(2000);
            }*/
            /*for (int i=0;i<2;i++){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i=0;i<1;i++){
                            synchronized (HiController.class) {
                                map2.put("a:",a++);
                                ObjectDto objectDto =  NettyRpcUtil.load("baseService", "test", map2);
                                System.out.println(Thread.currentThread().getName()+"  a++:"+a+" objectDto:"+objectDto.getObj());
                            }
                        }3
                    }
                }).start();
            }*/
            //?????????10000
         /*   int parallel = 1;

            //????????????
            StopWatch sw = new StopWatch();
            sw.start();

            CountDownLatch signal = new CountDownLatch(1);
            CountDownLatch finish = new CountDownLatch(parallel);

            for (int i=0;i<parallel;i++){
                 *//*   map2.put("a:",a++);
                    ObjectDto objectDto =  NettyRpcUtil.load("baseService", "test", map2);
                    System.out.println(Thread.currentThread().getName()+"  a++:"+a+" objectDto:"+objectDto.getObj());*//*
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            signal.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        map2.put("a:",atomicInteger.incrementAndGet());
                        ObjectDto objectDto =  NettyRpcUtil.load("com-niubility-service-base","baseService", "test", map2);

                        *//*synchronized (HiController.class){
                            a++;
                        }*//*
                        System.out.println(a+"----"+Thread.currentThread().getName()+" objectDto:"+objectDto.getObj());
                        finish.countDown();
                    }
                }).start();

            }

            //10000???????????????????????????????????????
            signal.countDown();
            finish.await();
            sw.stop();*/
            ObjectDto objectDto =  NettyRpcUtil.load(true,"com-niubility-service-base2","baseService", "test", map2);
            long end = System.currentTimeMillis();
            String tip = String.format(objectDto.getObj()+" RPC??????????????????: [%s] ??????",(end-s));
            System.out.println(tip);
            //1????????? 17.6???
          /*  while (a <2) {
            }*/
            //Thread.sleep(30000);

            // Thread.sleep(30000);
            //ObjectDto objectDto =  NettyRpcUtil.load("baseService", "test", map2);
            value.put("?????????", tip);
            value.put("???????????????:", atomicInteger.get());
        } catch(AppRunTimeException e){
            value.put("isSuccess",false);
            value.put("error",e.getMessage());
        } catch (Exception e) {
            value.put("isSuccess",false);
            value.put("error",e.getMessage());
        }
        return value;
    }
}
