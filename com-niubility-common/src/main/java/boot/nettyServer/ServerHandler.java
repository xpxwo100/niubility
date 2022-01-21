package boot.nettyServer;

import boot.nettyRpcModel.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 业务处理类
 * @author hasee
 *
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

	private ThreadLocal threadLocal = new ThreadLocal();
	private TaskExecutor executor;
	public ServerHandler(TaskExecutor executor) {
		this.executor = executor;

	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		//处理请求
        System.out.println(Thread.currentThread().getName()+":channelRead处理请求");
		RpcRequest request = (RpcRequest)msg;
		//NettyRecvInitializeTask nettyRecvInitializeTask = new NettyRecvInitializeTask(request,response,ctx);
		//不要阻塞nio线程，复杂的业务逻辑丢给专门的线程池
		//executor.doWork(ctx, request);
		executor.execute(ctx, request);
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);

	}
	/**
	 * 异常处理
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);



	}
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		 if (evt instanceof IdleStateEvent) {
	            IdleStateEvent event = (IdleStateEvent) evt;
	            if (event.state() == IdleState.READER_IDLE) {
					int loss_connect_time = 0;
	            	if(threadLocal.get() == null){
						threadLocal.set(0);
					}else{
						loss_connect_time = (int) threadLocal.get();
					}
					threadLocal.set(loss_connect_time++);
	                System.out.println("1小时 没有接收到客户端的信息了");
	                if ((int)threadLocal.get() > 2) {
	                    System.out.println("关闭这个不活跃的channel");
	                    ctx.channel().close();
	                }
	            }
	        } else {
	            super.userEventTriggered(ctx, evt);
	        }
	}
}
