package boot.nettyServer;

import boot.nettyRpcModel.RpcRequest;
import boot.nettyRpcModel.RpcResponse;
import boot.nettyRpcModel.RpcThreadPool;
import com.google.common.util.concurrent.*;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang.reflect.MethodUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 独立的线程业务处理
 * @author hasee
 *
 */
public class NettyRecvInitializeTask  implements  TaskExecutor{
	private static final Log logger = LogFactory.getLog(NettyRecvInitializeTask.class);
	//private RpcRequest request = null;
	//private RpcResponse response = null;
	private ChannelHandlerContext ctx = null;
	protected boolean returnNotNull = true;
	public static final String FILTER_RESPONSE_MSG = "Illegal request,NettyRPC server refused to respond!";
	private static volatile ListeningExecutorService threadPoolExecutor;
	public NettyRecvInitializeTask(RpcRequest request, RpcResponse response,
			ChannelHandlerContext ctx) {
		//this.request = request;
		//this.response = response;
		this.ctx = ctx;
	}
	/**
	 * 用于存储已经注册的服务实例
	 */
	private Map<String, Object> serviceInstanceMap = new HashMap<>();
	public NettyRecvInitializeTask() {
	}


	@Override
	public  void unloadExecutor() {
		if(threadPoolExecutor != null){
			threadPoolExecutor.shutdown();
		}
	}


	/**
	 * 調用接口
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	private Object reflect(RpcRequest request) throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException,
			InstantiationException, ClassNotFoundException {
		String serviceName = request.getClassName();
		String methodName = request.getMethodName();
		Object[] parameters = request.getParametersVal();
		//NettyRpcLoadImpl nettyRpcLoadImpl = new NettyRpcLoadImpl();
		Object serviceBean = this.serviceInstanceMap.get(serviceName);
		return MethodUtils.invokeMethod(serviceBean, methodName,parameters);
	}

	public String getStackTrace(Throwable ex) {
		StringWriter buf = new StringWriter();
		ex.printStackTrace(new PrintWriter(buf));
		return buf.toString();
	}

	@Override
	public void execute(ChannelHandlerContext ctx, RpcRequest rpcRequest) {
		if (threadPoolExecutor == null) {
			synchronized (NettyRpcServer.class) {
				if (threadPoolExecutor == null) {
					threadPoolExecutor = MoreExecutors.listeningDecorator((ThreadPoolExecutor) RpcThreadPool.getExecutor(16, -1));

				}
			}
		}
		RpcResponse response = new RpcResponse();
		ListenableFuture<Boolean> listenableFuture =  threadPoolExecutor.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				try {
					RpcResponse res = (RpcResponse) reflect(rpcRequest);
					response.setMessageId(rpcRequest.getMessageId());
					response.setResult(res.getResult());
					response.setError(res.getError());
					response.setReturnNotNull(res.isReturnNotNull());
					System.out.println("threadPoolExecutor.submit"+Thread.currentThread().getName());
					return Boolean.TRUE;
				}catch (Throwable t) {
					t.printStackTrace();
					logger.debug("RPC Server invoke error!\n");
					return Boolean.FALSE;
				}
			}

		});
		Futures.addCallback(listenableFuture, new FutureCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				//防止oom
				if(ctx.channel().isActive() && ctx.channel().isWritable()) {
					//业务逻辑处理成功后把结果返回客户端
					ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
						@Override
						public void operationComplete(ChannelFuture channelFuture) throws Exception {
							//logger.debug("RPC Server Send message-id respone: " + rpcRequest.getMessageId()+"value:"+response.getResult());
							System.out.println("RPC Server Send message-id respone: " + rpcRequest.getMessageId()+"value:"+response.getResult());
						}
					});
				}
			}
			@Override
			public void onFailure(Throwable t) {
				t.printStackTrace();
			}
		});
	}
	public void doWork(ChannelHandlerContext ctx, RpcRequest rpcRequest) {
		RpcResponse response = new RpcResponse();
		try {
			RpcResponse res = (RpcResponse) reflect(rpcRequest);
			response.setMessageId(rpcRequest.getMessageId());
			response.setResult(res.getResult());
			response.setError(res.getError());
			response.setReturnNotNull(res.isReturnNotNull());
			System.out.println("threadPoolExecutor.submit"+Thread.currentThread().getName());
		}catch (Throwable t) {
			t.printStackTrace();
			logger.debug("RPC Server invoke error!\n");
		}finally {
			if(ctx.channel().isActive() && ctx.channel().isWritable()) {
				//业务逻辑处理成功后把结果返回客户端
				ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture channelFuture) throws Exception {
						//logger.debug("RPC Server Send message-id respone: " + rpcRequest.getMessageId()+"value:"+response.getResult());
						System.out.println("RPC Server Send message-id respone: " + rpcRequest.getMessageId()+"value:"+response.getResult());
					}
				});
			}
		}

	}
	@Override
	public void addInstance(String serviceName, Object instance) {
		this.serviceInstanceMap.put(serviceName, instance);
	}
}
