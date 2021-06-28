package boot.nettyClient;

import boot.nettyRpcModel.RpcRequest;
import boot.nettyRpcModel.RpcResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler extends ChannelInboundHandlerAdapter {
	private static final Log logger = LogFactory.getLog(ClientHandler.class);
	private volatile Channel channel;
	private static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup("ChannelGroups", GlobalEventExecutor.INSTANCE);
	//private static volatile ConcurrentHashMap<String, MessageCallBack> mapCallBack = new ConcurrentHashMap<String, MessageCallBack>();
	private ConcurrentHashMap<String, NettyRpcClient> clientMap;
	private RequestPendingCenter requestPendingCenter;
	private String key;

	public ClientHandler(ConcurrentHashMap<String, NettyRpcClient> clientMap,RequestPendingCenter requestPendingCenter) {
		this.clientMap = clientMap;
		this.requestPendingCenter = requestPendingCenter;
	}
	//代理对象要到这里执行这个方法 把请求参数发出去
	public ClientHandler sendRequest(RpcRequest request) throws Exception {
			 this.channel.writeAndFlush(request);
			 logger.info("channel.id().toString()"+channel.id().toString());
			 logger.info("ClientHandler.sendRequest()");
			 return this;
	}


	public String getChannelID(){
		ChannelId ChannelId = channel.id();
		return ChannelId.toString();
	}
	public MessageCallBack getCallBack(RpcRequest request)  {
		MessageCallBack callBack = new MessageCallBack(request);
		if(channel.isActive()){
			//mapCallBack.put(request.getMessageId(), callBack);
			return callBack;
		}else{
			return null;
		}
	}


	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		 //返回的数据
		 RpcResponse response = (RpcResponse)msg;
		 requestPendingCenter.set(response.getMessageId(),response);
		/* MessageCallBack callBack = mapCallBack.get(response.getMessageId());
		 if (callBack != null) {
			 mapCallBack.remove(response.getMessageId());
			 System.out.println(Thread.currentThread().getName()+"获得response"+response);
			 callBack.over(response);
		 }*/
     /*   System.out.println(Thread.currentThread().getName()+":channelRead");
		NettyClientTask nettyClientTask = new NettyClientTask(response, ctx, mapCallBack);
		//不要阻塞nio线程，读取消息丢给专门的线程池
		NettyRpcClient.submit(nettyClientTask, ctx, response);*/
		ClientHandler clientHandler = (ClientHandler)ctx.handler();

		logger.info("clientMap"+clientHandler.clientMap);

	}
	 public void close() {
		 //在这个ChannelFutureListener对象内部我们处理了 异步操作完成之后的关闭操作。
		 //监听写操作后才关闭
	     channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
	}
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);
		channel = ctx.channel();
		CHANNEL_GROUP.add(channel);
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelActive(ctx);
		logger.info("ClientHandler.channelActive()");
		/*Set<Map.Entry<String, NettyRpcClient>> set = clientMap.entrySet();
		for(Map.Entry<String, NettyRpcClient> map : set){
			String key = map.getKey();
			if(!clientMap2.contains(key)){
				clientMap2.put(channel.id().toString(),key);
			}
		}*/

	}
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		logger.info("ClientHandler.channelInactive()");
		CHANNEL_GROUP.find(channel.id());
		this.channel = ctx.channel();
		//String key = clientMap2.get(channel.id().toString());
		//服务端重启或关闭会触发
		logger.info("clientMap.channelInactive()"+clientMap);
		ClientHandler clientHandler = (ClientHandler)ctx.handler();
		logger.info("clientHandler.key:"+clientHandler.key);
		clientMap.remove(clientHandler.key);
		logger.info("clientMap.channelInactive()"+clientMap);
		/*if(NettyRpcClient.getInstance() != null){
			NettyRpcClient.getInstance().unLoad();
			*//*if(NettyRpcProxy.consistentHash != null){
					if(NettyRpcProxy.consistentHash != null){
						if(NettyRpcProxy.redisUtils.get("consistentHash") != null){
							NettyRpcProxy.consistentHash = (ConsistentHash<String>) NettyRpcProxy.redisUtils.get("consistentHash");
						}
					}
			}*//*

		}*/
	}
	/**
	 * 发生异常时调用
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.info("ClientHandler.exceptionCaught()");
		cause.printStackTrace();
		ctx.close();
	}

	public void setKey(String s) {
		this.key = s;
	}
}
