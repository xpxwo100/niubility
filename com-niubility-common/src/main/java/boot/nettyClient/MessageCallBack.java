package boot.nettyClient;

import boot.nettyRpcModel.RpcRequest;
import boot.nettyRpcModel.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 处理返回值
 */
public class MessageCallBack  implements Future<RpcResponse> {
	private static final Logger logger = LoggerFactory.getLogger(MessageCallBack.class);
	 private RpcResponse response;
	 private RpcRequest request;
	 private Lock lock = new ReentrantLock();
	 private Condition finish = lock.newCondition();
	 private Sync sync;
	 private long startTime;
	 private long responseTimeThreshold = 7000;

	 public  MessageCallBack(RpcRequest request) {
		this.request = request;
		this.startTime = System.currentTimeMillis();
		this.sync = new Sync();
	}
	 public void set(RpcResponse response) {
		  this.response = response;
	 }

	/**
	 * 等待返回值
	 * @param request
	 * @param clientHandler
	 * @return
	 * @throws InterruptedException
	 */
	/* public Object start(RpcRequest request, ClientHandler clientHandler) throws InterruptedException {
	        try {
	            lock.lock();
				//sendRequest(request,clientHandler);
	            //设定一下超时时间，rpc服务器太久没有相应的话，就默认返回空吧。
				System.out.println(Thread.currentThread().getName()+":await");
				finish.await(60, TimeUnit.SECONDS);//等待被over唤起
				//finish.await();
	            if (this.response != null) {
	            	 return this.response;
	            } else {
	                 return null;
	            }
	        } catch (Exception e) {
				e.printStackTrace();
				return null;
			} finally {
	            lock.unlock();
	        }
	 }*/

	/**
	 * 等待返回值
	 * @return
	 */
	public RpcResponse getValue(long time, TimeUnit unit)  {
		try {
			lock.lock();
			//sendRequest(request,clientHandler);
			//设定一下超时时间，rpc服务器太久没有相应的话，就默认返回空吧。
			System.out.println(Thread.currentThread().getName()+":await");
			finish.await(time, unit);//等待被over唤起
			//finish.await();
			if (this.response != null) {
				return this.response;
			} else {
				throw new RuntimeException("Timeout exception. Request id: " + this.request.getMessageId()
						+ ". Request class name: " + this.request.getClassName()
						+ ". Request method: " + this.request.getMethodName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			lock.unlock();
		}
	}
	/**
	 * 当读取返回数据的时候调用，唤起线程
	 * @param rpcResponse
	 */
	public void over(RpcResponse rpcResponse) {
		try {
			//服务端返回很快，存在先over再await的情况，必须保证先await 再 over
			lock.lock();
			System.out.println(Thread.currentThread().getName()+":over");
			this.response = rpcResponse;
			finish.signal();
		}finally {
			lock.unlock();
		}
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return sync.isDone();
	}

	@Override
	public RpcResponse get() throws InterruptedException, ExecutionException {
		sync.acquire(-1);
		if (this.response != null) {
			return this.response;
		} else {
			return null;
		}
	}

	@Override
	public RpcResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));
		System.out.println(Thread.currentThread().getName()+":await");
		if (success) {
			if (this.response != null) {
				return this.response;
			} else {
				return null;
			}
		} else {
			throw new RuntimeException("Timeout exception. Request id: " + this.request.getMessageId()
					+ ". Request class name: " + this.request.getClassName()
					+ ". Request method: " + this.request.getMethodName());
		}
	}
	public void done(RpcResponse reponse) {
		this.response = reponse;
		sync.release(1);
		// Thre	shold
		long responseTime = System.currentTimeMillis() - startTime;
		if (responseTime > this.responseTimeThreshold) {
			logger.warn("Service response time is too slow. Request id = " + reponse.getMessageId() + ". Response Time = " + responseTime + "ms");
		}
		System.out.println(Thread.currentThread().getName()+":over");
	}
	/**
	 * 发请求
	 */
	/*public void sendRequest(final RpcRequest request,final ClientHandler clientHandler) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					lock.lock();
                    //Thread.sleep(100);
					clientHandler.sendRequest(request);
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					lock.unlock();
				}
			}
		}).start();
	}*/

	static class Sync extends AbstractQueuedSynchronizer {

		private static final long serialVersionUID = 1L;

		//future status
		private final int done = 1;
		private final int pending = 0;

		@Override
		protected boolean tryAcquire(int arg) {
			return getState() == done;
		}

		@Override
		protected boolean tryRelease(int arg) {
			if (getState() == pending) {
				if (compareAndSetState(pending, done)) {
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		}

		protected boolean isDone() {
			return getState() == done;
		}
	}
}
