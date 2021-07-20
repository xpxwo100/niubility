package combookproductcontroller.controller;

import boot.nettyClient.NettyRpcUtil;
import boot.nettyRpcModel.AppRunTimeException;
import boot.nettyRpcModel.ObjectDto;
import boot.nettyRpcModel.RpcThreadPool;
import boot.util.TestM;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import combookproductcontroller.interfaces.SchedualServiceHi;
import combookproductcontroller.util.MesSend;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
@Api(value = "desc of class")
@RestController
public class HiController {
    @Autowired
    SchedualServiceHi schedualServiceHi;

    @RequestMapping("/hi")
    public String home(@RequestParam(value = "name", defaultValue = "forezp") String name) {
        return schedualServiceHi.sayHiFromClientOne(name);
    }
    @ApiOperation(value = "desc of method", notes = "")
    @RequestMapping("/hi2")
    public Object linkBaseService(@RequestParam(value = "name", defaultValue = "forezp") String name) {
        Object sda = schedualServiceHi.linkBaseService(name);

        return sda;
    }

    @ResponseBody
    @RequestMapping(value = "/session")
    public Map<String, Object> getSession(HttpServletRequest request) {
        request.getSession().setAttribute("username", "admin");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("sessionId", request.getSession().getId());
        TestM t = new TestM();
        t.code = request.getSession().getId();
        t.state = 100;
        t.obj = map;
        MesSend.sendMsg(t);
        Object linkBaseService = schedualServiceHi.linkBaseService(request.getSession().getId());
        map.put("linkBaseService", linkBaseService.toString());
        return map;
    }

    static int a = 0;
    static ExecutorService cachedThreadPool;
    @Autowired
    public static AtomicInteger atomicInteger = new AtomicInteger(0);

    @ResponseBody
    @RequestMapping(value = "/get")
    public Object get(HttpServletRequest request) {
        String userName = (String) request.getSession().getAttribute("username");
        final HashMap<String, Object> map2 = new HashMap<>();
        HashMap<String, Object> value = new HashMap<>();
        value.put("isSuccess", true);
        map2.put("sad", 56789);
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
                        }
                    }
                }).start();
            }*/
            //并行度10000
            int parallel = 200;


            //开始计时
            StopWatch sw = new StopWatch();
            sw.start();

            CountDownLatch signal = new CountDownLatch(1);
            CountDownLatch finish = new CountDownLatch(parallel);
            for (int i = 0; i < parallel; i++) {
                 /*   map2.put("a:",a++);
                    ObjectDto objectDto =  NettyRpcUtil.load("baseService", "test", map2);
                    System.out.println(Thread.currentThread().getName()+"  a++:"+a+" objectDto:"+objectDto.getObj());*/
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            signal.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        map2.put("a:", atomicInteger.incrementAndGet());
                        ObjectDto objectDto = NettyRpcUtil.load(true, "com-niubility-service-base", "baseService", "test", map2);
                        /*synchronized (HiController.class){
                            a++;
                        }*/
                        System.out.println(a + "----" + Thread.currentThread().getName() + " objectDto:" + objectDto.getObj());
                        finish.countDown();
                    }
                }).start();

            }

            //10000个并发线程瞬间发起请求操作
            signal.countDown();
            finish.await();
            sw.stop();

            String tip = String.format("RPC调用总共耗时: [%s] 毫秒", sw.getTime());
            System.out.println(tip);
            //1万并发 17.6秒
          /*  while (a <2) {
            }*/
            //Thread.sleep(30000);
            long end = System.currentTimeMillis();
            // Thread.sleep(30000);
            //ObjectDto objectDto =  NettyRpcUtil.load("baseService", "test", map2);
            value.put("时间：", tip);
            value.put("并发线程数:", atomicInteger.get());
        } catch (AppRunTimeException e) {
            value.put("isSuccess", false);
            value.put("error", e.getMessage());
        } catch (Exception e) {
            value.put("isSuccess", false);
            value.put("error", e.getMessage());
        }
        return value;
    }

    @ResponseBody
    @RequestMapping(value = "/get2")
    public Object get2(HttpServletRequest request) {
        String userName = (String) request.getSession().getAttribute("username");
        final HashMap<String, Object> map2 = new HashMap<>();
        HashMap<String, Object> value = new HashMap<>();
        value.put("isSuccess", true);
        map2.put("sad", 56789);
        try {
            atomicInteger = new AtomicInteger(0);
            a = 0;
            long s = System.currentTimeMillis();
           /* if(cachedThreadPool == null){
                cachedThreadPool =  Executors.newFixedThreadPool(2000);
            }*/
            /*for (int i=0;i<2;i++){打赏
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i=0;i<1;i++){
                            synchronized (HiController.class) {
                                map2.put("a:",a++);
                                ObjectDto objectDto =  NettyRpcUtil.load("baseService", "test", map2);
                                System.out.println(Thread.currentThread().getName()+"  a++:"+a+" objectDto:"+objectDto.getObj());
                            }
                        }
                    }
                }).start();
            }*/
            //并行度10000
            int parallel = 10;

            //开始计时
            StopWatch sw = new StopWatch();
            sw.start();

            CountDownLatch signal = new CountDownLatch(1);
            CountDownLatch finish = new CountDownLatch(parallel);

            for (int i = 0; i < parallel; i++) {
                 /*   map2.put("a:",a++);
                    ObjectDto objectDto =  NettyRpcUtil.load("baseService", "test", map2);
                    System.out.println(Thread.currentThread().getName()+"  a++:"+a+" objectDto:"+objectDto.getObj());*/
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            signal.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        map2.put("a:", atomicInteger.incrementAndGet());
                        ObjectDto objectDto = NettyRpcUtil.load(true, "com-niubility-service-base2", "baseService", "test", map2);
                        /*synchronized (HiController.class){
                            a++;
                        }*/
                        System.out.println(a + "----" + Thread.currentThread().getName() + " objectDto:" + objectDto.getObj());
                        finish.countDown();
                    }
                }).start();

            }

            //10000个并发线程瞬间发起请求操作
            signal.countDown();
            finish.await();
            sw.stop();

            String tip = String.format("RPC调用总共耗时: [%s] 毫秒", sw.getTime());
            System.out.println(tip);
            //1万并发 17.6秒
          /*  while (a <2) {
            }*/
            //Thread.sleep(30000);
            long end = System.currentTimeMillis();
            // Thread.sleep(30000);
            //ObjectDto objectDto =  NettyRpcUtil.load("baseService", "test", map2);
            value.put("时间：", tip);
            value.put("并发线程数:", atomicInteger.get());
        } catch (AppRunTimeException e) {
            value.put("isSuccess", false);
            value.put("error", e.getMessage());
        } catch (Exception e) {
            value.put("isSuccess", false);
            value.put("error", e.getMessage());
        }
        return value;
    }

    @ResponseBody
    @RequestMapping(value = "/send")
    public Object send(HttpServletRequest request) {
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("session", request.getSession().getId());
        ObjectDto objectDto = NettyRpcUtil.load(true, "com-niubility-service-base", "baseService", "test", map2);
        // msgRabbitSend.sendMsg(map2);
        return "发送成功";
    }

    private ListeningExecutorService threadPoolExecutor = MoreExecutors.listeningDecorator((ThreadPoolExecutor) RpcThreadPool.getExecutor(16, -1));

    @ResponseBody
    @RequestMapping(value = "/test")
    public Object test(HttpServletRequest request) throws InterruptedException {
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("session", request.getSession().getId());
        ObjectDto objectDto = NettyRpcUtil.load(false, "com-niubility-service-base", "baseService", "testA", map2, "fuck");
        System.out.println("执行业务逻辑:顶顶顶顶顶顶顶顶顶顶");
        return objectDto;
    }

    @ResponseBody
    @RequestMapping(value = "/test3")
    public Object test3(HttpServletRequest request) throws InterruptedException {
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("session", request.getSession().getId());
        ObjectDto objectDto = NettyRpcUtil.load(true, "com-niubility-service-base", "baseService", "testA", map2, "fuck");

        return objectDto;
    }

    @ResponseBody
    @RequestMapping(value = "/test2")
    public Object test2(HttpServletRequest request) throws InterruptedException {
        return null;
    }

    @Autowired
    private DiscoveryClient discoveryClient;  //拉取所有的服务信息


   /* @GetMapping
    @ResponseBody
    //@HystrixCommand(fallbackMethod = "testFallback")  //通过此注解使得此方法熔断后执行熔断方法
    @HystrixCommand(fallbackMethod = "testFallback", threadPoolProperties = {
            @HystrixProperty(name = "coreSize", value = "20"),
            @HystrixProperty(name = "queueSizeRejectionThreshold", value = "20"),
            //@HystrixProperty(name = "circuitBreakerForceOpen", value = "true")
    }, commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "8000")
    })
   *//* public String test5(@RequestParam("id") String id){

        //可能会有多个同名服务，故使用list
        List<ServiceInstance> instances = discoveryClient.getInstances("com-niubility-service-base");
        ServiceInstance instance = instances.get(0);
        return this.restTemplate.getForObject("http://"+instance.getHost()+":"+ instance.getPort() +"/test/" + id,String.class);
//        return this.restTemplate.getForObject("http://localhost:8079/test/"+id , String.class);

    }*//*

    *//**
     * 熔断之后执行的方法
     * @param id
     * @return
     *//*
    public String testFallback(String id) {
        return "熔断--服务正忙，请求稍后再试！";

    }*/
}
