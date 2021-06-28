package boot.nettyRpcModel;

import java.util.concurrent.*;

/**
 * 线程工具
 * @author hasee
 *
 */
public class RpcThreadPool {
	 public static Executor getExecutor(int threads, int queues) {
	        String name = "RpcThreadPool";
		 new LinkedBlockingQueue();
	        return new ThreadPoolExecutor(threads, threads, 0, TimeUnit.MILLISECONDS,
	                queues == 0 ? new SynchronousQueue<Runnable>()//同步队列
	                        : (queues < 0 ? new LinkedBlockingQueue<Runnable>()
	                                : new LinkedBlockingQueue<Runnable>(queues)),
	                new NamedThreadFactory(name, true), new AbortPolicyWithReport(name));
	    }
}

