package boot.nettyClient;

import boot.nettyRpcModel.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;


public class NettyClientTask implements Callable<Boolean>{
	private static final Log logger = LogFactory.getLog(NettyClientTask.class);
	private RpcResponse response = null;
	private ChannelHandlerContext ctx = null;
	protected boolean returnNotNull = true;
	private ConcurrentHashMap<String, MessageCallBack> mapCallBack = null;

	public NettyClientTask(RpcResponse response,
			ChannelHandlerContext ctx,ConcurrentHashMap<String, MessageCallBack> mapCallBack) {
		this.response = response;
		this.ctx = ctx;
		this.mapCallBack = mapCallBack;
	}

	public NettyClientTask() {
	}

	@Override
	public Boolean call() throws Exception {
		try {
			 MessageCallBack callBack = mapCallBack.get(response.getMessageId());
			 if (callBack != null) {
				 mapCallBack.remove(response.getMessageId());
				 logger.info(Thread.currentThread().getName()+"NettyClientTask 获得response"+response);
				 callBack.over(response);
			 }
	        return Boolean.TRUE;
		}catch (Throwable t) {
            t.printStackTrace();
            logger.info("RPC client read invoke error!\n");
            return Boolean.FALSE;
		}
	}

	public String getStackTrace(Throwable ex) {
		StringWriter buf = new StringWriter();
		ex.printStackTrace(new PrintWriter(buf));
		return buf.toString();
	}
}
