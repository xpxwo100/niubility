package boot.nettyClient;

import boot.nettyRpcModel.*;
import boot.nettyServer.ServerHandler;
import com.google.common.util.concurrent.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * netty客户端
 * @author xpx
 *
 */
public class NettyRpcClient  implements Callable<Boolean> {
	private static final Log logger = LogFactory.getLog(NettyRpcClient.class);
    private static volatile NettyRpcClient nettyRpcClient;
    private static volatile ConcurrentHashMap<String, NettyRpcClient> clientMap;
    private String host;
    private int port;
    private ClientHandler clientHandler = null;
	public  final RequestPendingCenter REQUEST_PENDING_CENTER = new RequestPendingCenter();
	private ClientPool clientPool;
	public  static volatile ListeningExecutorService threadPoolExecutor;
    private static volatile ListeningExecutorService threadPoolExecutorForRead;
    private EventLoopGroup group = new NioEventLoopGroup();
    private Lock lock = new ReentrantLock();
	private Lock lock2 = new ReentrantLock();
    //private Condition connectStatus = lock.newCondition();
    private Condition handlerStatus = lock.newCondition();
	private String service_name;
	private NettyRpcClient client;
	public NettyRpcClient(String host, int port,String service_name) {
        this.host = host;
        this.port = port;
		this.service_name = service_name;
    }

	public void setClient(NettyRpcClient client) {
		this.client = client;
	}

	public NettyRpcClient getClient() {
		return client;
	}

	private NettyRpcClient() {
	}
	public static NettyRpcClient getInstance() {
	        if (nettyRpcClient == null) {
	            synchronized (NettyRpcClient.class) {
	                if (nettyRpcClient == null) {
	                	nettyRpcClient = new NettyRpcClient();
	                }
	            }
	        }
			if (clientMap == null) {
				synchronized (NettyRpcClient.class) {
					if (clientMap == null) {
						clientMap =  new ConcurrentHashMap<String, NettyRpcClient>();
					}
				}
			}
			if (threadPoolExecutor == null) {
				synchronized (NettyRpcClient.class) {
					if (threadPoolExecutor == null) {
						threadPoolExecutor = MoreExecutors.listeningDecorator((ThreadPoolExecutor) RpcThreadPool.getExecutor(16, -1)) ;
					}
				}
			}
	       return nettyRpcClient;
	 }
	 public NettyRpcClient getConnect(String host, int port, String service_name) throws InterruptedException {
			 this.host = host;
			 this.port = port;
			 this.service_name = service_name;
			 System.out.println("clientMap"+clientMap);
			 NettyRpcClient clientNew = null;
			if (clientMap.containsKey(this.service_name +"_"+ this.host + this.port)) {
				System.out.println("clientMap"+clientMap);
				return clientMap.get(this.service_name +"_"+ this.host + this.port);
			}else {
				if (clientNew == null) {
					synchronized (NettyRpcClient.class) {
						if (clientNew == null) {
							clientNew = new NettyRpcClient(this.host, this.port, this.service_name);
							clientNew.setClient(clientNew);
							//clientMap.put(service_name+"_"+host + port, client);
							System.out.println(this.service_name +"_"+ this.host + this.port +" 新建一个服务端"+clientNew);
						}
					}
				}
			}
			if(clientNew != null){
				//加入线程池
				ListenableFuture<Boolean> listenableFuture = threadPoolExecutor.submit(clientNew);
				//判断连接是否成功
				/*Futures.addCallback(listenableFuture, new FutureCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						try {
							lock.lock();

							if (clientHandler == null) {
								handlerStatus.await();
							}
							if (result.equals(Boolean.TRUE) && clientHandler != null) {
								connectStatus.signalAll();
							}
						} catch (InterruptedException ex) {
						} finally {
							lock.unlock();
						}
					}

					@Override
					public void onFailure(Throwable t) {
						t.printStackTrace();
					}
				}, threadPoolExecutor);*/
			}
			return clientNew;
	 }

	/**
	 * 卸载资源关闭连接
	 */
	public void unLoad() {
		 	if(clientHandler != null){clientHandler.close();}
		   /* if(threadPoolExecutor !=null){
		    	threadPoolExecutor.shutdown();
				threadPoolExecutor = null;
		    }
		    if(threadPoolExecutorForRead !=null){
		    	threadPoolExecutorForRead.shutdown();
		    	threadPoolExecutorForRead = null;
		    }*/
	        if(group != null){
	        	group.shutdownGracefully();
	        }
	        nettyRpcClient = null;
			System.out.println("clientMap"+clientMap);
		    System.out.println("NettyRpcClient.unLoad() nettyRpcClient = null");
	  }
     public static NettyRpcClient get(){
    	 return nettyRpcClient;
     }
     @Override
     public Boolean call() {
    		try {
				//业务线程池
				UnorderedThreadPoolEventExecutor business = new UnorderedThreadPoolEventExecutor(10, new DefaultThreadFactory("business"));

				Bootstrap bootstrap = new Bootstrap();
		        bootstrap.group(group);
		        bootstrap.channel(NioSocketChannel.class);
		        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
		        bootstrap.option(ChannelOption.TCP_NODELAY, true);
		        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		        bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT);
		        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
		            @Override
		            public void initChannel(SocketChannel channel) throws Exception {
		            	ProtostuffCodecUtil util = new ProtostuffCodecUtil();
		                util.setRpcDirect(false);
		            	channel.pipeline()
		                        .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
		                        .addLast(new LengthFieldPrepender(4))
		                        .addLast(new ProtostuffEncoder(util))
		                        .addLast(new ProtostuffDecoder(util))
		                		.addLast(new ClientHandler(clientMap,REQUEST_PENDING_CENTER));
								//.addLast(business,"handler",new ClientHandler(clientMap,REQUEST_PENDING_CENTER));
					}
		        });
		        ChannelFuture future = bootstrap.connect(host, port);
		        future.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						if(future.isSuccess()){
							ClientHandler clientHandler = future.channel().pipeline().get(ClientHandler.class);
							clientHandler.setKey(service_name+"_"+host + port);
							clientMap.put(service_name+"_"+host + port, getClient());
							logger.info("service_name"+service_name);
							logger.info("service_name 2"+getClient().service_name);
							logger.info("clientMap"+clientMap);
							logger.info(Thread.currentThread().getName()+"获得clientHandler"+clientHandler);
							setClientHandler(clientHandler);
						}else{
							logger.info("clientMap"+clientMap);
							clientMap.remove(service_name+"_"+host + port);
							/* EventLoop loop = (EventLoop) group.schedule(new Runnable() {
			                        @Override
			                        public void run() {
			                            logger.info("NettyRPC server is down,start to reconnecting to: " + host+ ':' + port);
			                            call();
			                        }
			                    }, 10, TimeUnit.SECONDS);//线程等待 10秒
							*/
						}
					}
				});
		 	}catch (Exception e) {
				e.printStackTrace();
				unLoad();
			}
    	 return Boolean.TRUE;
     }


	 public ClientHandler getClientHandler() throws InterruptedException {
	        try {
	            lock.lock();
	            //Netty服务端链路没有建立完毕之前，先挂起等待
	            if (clientHandler == null) {
					handlerStatus.await(40*1000, TimeUnit.MILLISECONDS);
	            }
				System.out.println("getClientHandler:"+clientHandler);
	            return this.clientHandler;
	        } finally {
	            lock.unlock();
	        }
	}
	 
	 public void setClientHandler(ClientHandler clientHandler) {
	        try {
	            lock.lock();
	            this.clientHandler = clientHandler;
	            logger.info("NettyRpcClient.setClientHandler()");
	            //唤醒等待客户端RPC线程
	            handlerStatus.signal();
	        } finally {
	            lock.unlock();
	        }
	    }
	/**
	 * 客户端读取消息的操作
	 * @param nettyClientTask
	 * @param ctx
	 * @param response
	 */
	public static void submit(NettyClientTask nettyClientTask,
			ChannelHandlerContext ctx, RpcResponse response) {
		 if (threadPoolExecutorForRead == null) {
	            synchronized (NettyRpcClient.class) {
	                if (threadPoolExecutorForRead == null) {
	                	threadPoolExecutorForRead = MoreExecutors.listeningDecorator((ThreadPoolExecutor) RpcThreadPool.getExecutor(100, -1));
	                }
	            }
	        }
		 ListenableFuture<Boolean> listenableFuture =  threadPoolExecutorForRead.submit(nettyClientTask);
		 Futures.addCallback(listenableFuture, new FutureCallback<Boolean>() {
			 @Override
			public void onSuccess(Boolean result) {
				 //业务逻辑处理成功
			}
			@Override
			public void onFailure(Throwable t) {
				t.printStackTrace();
			}
		 });
	}

}
