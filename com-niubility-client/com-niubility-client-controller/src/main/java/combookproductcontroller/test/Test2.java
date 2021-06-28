package combookproductcontroller.test;

import java.util.Arrays;
import java.util.HashMap;

public class Test2 {


    public static void main(String[] args) throws InterruptedException {
       /* Arrays.asList( "a", "b", "d" ).forEach(e -> System.out.println( e ) );

        System.out.println(1040%100);
        ArrayBlockingQueue<String> arrayBlockingQueue = new ArrayBlockingQueue<>(2);
        arrayBlockingQueue.put("叫练");
        arrayBlockingQueue.put("叫练");
        //输出arrayBlockingQueue的长度
        System.out.println(arrayBlockingQueue.size());*/
        /*AtomicInteger  STOP_CODE = new AtomicInteger();
        AtomicInteger  STOP_CODE2 = new AtomicInteger();
        ArrayBlockingQueue<String> blockingQueue = new ArrayBlockingQueue(10);
       // blockingQueue.put("叫练");
       // blockingQueue.put("叫练");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        System.out.println(111);
                        String el = blockingQueue.take();
                        STOP_CODE.decrementAndGet();
                        System.out.println(222);
                        System.out.println(el);
                    } catch (InterruptedException e) {
                        System.out.println(e.toString());
                        break;
                    }
                }
                System.out.println("out");
            }
        });
        thread.start();
        System.out.println("消费ok");


        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    blockingQueue.put("叫练1");
                    STOP_CODE.incrementAndGet();
                    blockingQueue.put("叫练2");
                    STOP_CODE.incrementAndGet();
                    blockingQueue.put("叫练3");
                    STOP_CODE.incrementAndGet();
                    blockingQueue.put("叫练4");
                    STOP_CODE.incrementAndGet();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("ok");
                STOP_CODE2.set(8);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (true) {
                    System.out.println(STOP_CODE.get());
                    System.out.println(STOP_CODE2.get());
                    if(STOP_CODE.get() == 0 && STOP_CODE2.get() == 8){
                        thread.interrupt();
                        System.out.println("interrupt");
                        break;
                    }
                }
            }
        });
        thread2.start();
        HashMap<String, Integer> hs = new HashMap<String, Integer>();
        hs.put("一", 1);
        hs.put("二", 2);
        hs.put("三", null);
        hs.put(null, null);

        // 都可以存取null，所以不可以通过ge()返回值来判断键是否为null
        System.out.println(hs.get("三"));
        System.out.println(hs.get(null));*/
       /* ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        Condition condition2 = lock.newCondition();
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                lock.lock();
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("1");
                lock.unlock();
                System.out.println("unlock");
            }
        }).start();
        System.out.println("get lock");*/
        /*new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                lock.lock();
                System.out.println("get lock");
                System.out.println("2");
                Thread.sleep(3000);
                System.out.println("2----");
                condition.signal();
                condition2.signal();
                lock.unlock();
            }
        }).start();

        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                lock.lock();
                System.out.println("33");
                condition2.await();
                System.out.println("get lock");
                System.out.println("3");
                Thread.sleep(3000);
                System.out.println("3----");
                lock.unlock();
            }
        }).start();*/
     /*   HashMap<String, Integer> hs = new HashMap<String, Integer>();
        hs.put("一", 1);
        hs.put("二", 2);
        hs.put("三", null);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

            }
        };*/

        Runnable runnable2 = ()-> System.out.println("33");

       /* ArrayList<String> aa = new ArrayList<>();
        aa.add("qwe");
        aa.add("qerawe2");
        aa.add("qerwe3");
        aa.add("qwee4");
        aa.add("qsase5");
        aa.add("qwee4");*/
        String a = "asd1afaf";
        Arrays.stream(a.split("1",1)).forEach(s ->System.out.println(s));
        System.out.println(4%10);

        //System.out.println(aa.stream().count());
       /* aa.forEach(s -> System.out.println(s));
        Stream<Integer> s  = Stream.of(3,3,3,33,3,3);
        s.forEach(a -> System.out.println(a));
        aa.stream().filter(e->!e.equals("qwe")).forEach(a -> System.out.println(a));
        aa.stream().distinct().forEach(a -> System.out.println(a));
        aa.stream().limit(3).forEach(a -> System.out.println(a));*/
        //aa.stream().skip(3).forEach(a -> System.out.println(a));
       /* aa.stream().sorted(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {

                if(o1.length()>o2.length()){
                    return 1;
                }

                return 0;
            }
        }).forEach(a -> System.out.println(a));
        aa.stream().map(a->a.toString()).forEach(a -> System.out.println(a));*/
       /* aa.parallelStream().map(a->a.toString()).forEach(a -> System.out.println(a));

        List<String> sdad = aa.stream().map(a->a.toString()).collect(Collectors.toList();
        LocalDateTime LocalDateTime=  java.time.LocalDateTime.now();
        LocalDateTime.getDayOfYear();*/
        HashMap<String, Integer> hs = new HashMap<String, Integer>();
        hs.put("一", 1);
        hs.put("二", 2);
        hs.put("三", null);
    }

}
