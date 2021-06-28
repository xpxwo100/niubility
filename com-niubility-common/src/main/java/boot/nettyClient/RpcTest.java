package boot.nettyClient;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RpcTest {
	public static  int a = 0;
	final  Lock lock = new ReentrantLock();
	final  Condition finish = lock.newCondition();
	 public static void main(String[] args) throws InterruptedException {

		/* long s = System.currentTimeMillis();
	 	for (int i=0;i<100;i++){
				new Thread(new Runnable() {
					@Override
					public void run() {
						HashMap<String, Object> map2 = new HashMap<>();
						map2.put("sad",56789);
						//for (int i=0;i<2;i++){
							ObjectDto objectDto =  NettyRpcUtil.load("baseService", "test", map2);
							a++;
						//}
					}
				}).start();
			}
			// System.out.println("a:"+b);
		 	long end = System.currentTimeMillis();
 			 while (a <1000) {
            }*/


		 /*HashMap<String, Object> map2 = new HashMap<>();
		 map2.put("sad",56789);
		 for (int i=0;i<25;i++){
			 ObjectDto objectDto =  NettyRpcUtil.load("baseService", "test", map2);
			 a++;
		 }*/
	 }

	 public  void dad(){
		 try {
			 lock.lock();
			 //finish.signalAll();
			 Thread.sleep(3000);
			 finish.signal();
			 a = 190;
			 System.out.println(Thread.currentThread().getName()+":over2");
		 } catch (InterruptedException e) {
			 e.printStackTrace();
		 } finally {
			 lock.unlock();
		 }

	 }
	public static AtomicInteger atomicInteger = new AtomicInteger(0);
	private ConcurrentHashMap<Integer, String> mapCallBack = new ConcurrentHashMap<Integer, String>();
	@Test
	public  void dad2() throws InterruptedException {


		for (int i=0;i<10;i++){
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						lock.lock();
						finish.await();
						//finish.await(10, TimeUnit.SECONDS);//等待被over唤起
						System.out.println(Thread.currentThread().getName()+":signalAll");
						System.out.println(Thread.currentThread().getName()+"----"+mapCallBack.get(atomicInteger.get()));
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						lock.unlock();
					}
				}
			}).start();
		}
		Thread.sleep(3000);
		for (int i=0;i<3;i++){
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						lock.lock();
						//finish.signalAll();
						Thread.sleep(3000);
						finish.signal();
						mapCallBack.put(atomicInteger.incrementAndGet(),atomicInteger.incrementAndGet()+Thread.currentThread().getName());
						System.out.println(Thread.currentThread().getName()+":over1");
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						lock.unlock();
					}
				}
			}).start();
		}

		while (true){}
		/*new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					lock.lock();
					//finish.signalAll();
					Thread.sleep(3000);
					finish.signal();
					a = 3333;
					System.out.println(Thread.currentThread().getName()+":over2");
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
			}
		}).start();*/
	}
}
