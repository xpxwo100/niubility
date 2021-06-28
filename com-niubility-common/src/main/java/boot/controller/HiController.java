package boot.controller;

import boot.nettyClient.NettyRpcUtil;
import boot.nettyRpcModel.AppRunTimeException;
import boot.nettyRpcModel.ObjectDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class HiController {
    static  int a = 0;
    public static AtomicInteger atomicInteger = new AtomicInteger(0);
    @ResponseBody
    @RequestMapping(value = "/get")
    public Object get() {
        //String userName = (String) request.getSession().getAttribute("username");
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
                        }
                    }
                }).start();
            }*/
            //并行度10000
         /*   int parallel = 1;

            //开始计时
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

            //10000个并发线程瞬间发起请求操作
            signal.countDown();
            finish.await();
            sw.stop();*/
            ObjectDto objectDto =  NettyRpcUtil.load(true,"com-niubility-service-base","baseService", "test", map2);
            long end = System.currentTimeMillis();
            String tip = String.format(objectDto.getObj()+" RPC调用总共耗时: [%s] 毫秒",(end-s));
            System.out.println(tip);
            //1万并发 17.6秒
          /*  while (a <2) {
            }*/
            //Thread.sleep(30000);

           // Thread.sleep(30000);
           //ObjectDto objectDto =  NettyRpcUtil.load("baseService", "test", map2);
           value.put("时间：", tip);
            value.put("并发线程数:", atomicInteger.get());
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
