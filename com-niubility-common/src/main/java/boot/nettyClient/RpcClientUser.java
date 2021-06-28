package boot.nettyClient;

import boot.nettyRpcModel.ObjectDto;
import boot.nettyRpcModel.RpcRequest;
import boot.nettyRpcModel.RpcResponse;
import boot.util.PoolObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author xpx
 * @description rpc 客户端
 * @createDate 2020/4/27
 */
public class RpcClientUser extends PoolObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClientUser.class);

    /**
     * 发送请求
     *
     * @param isSyn
     * @param requestBody
     * @param clientPool
     * @return RpcResponseBody
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public RpcResponse send(Boolean isSyn, NettyRpcClient rpcClientInit, RpcRequest requestBody, ClientPool clientPool) throws Exception {
        try{
            //组装数据
            //添加future到请求等待中心
            MessageCallBack messageCallBack = new MessageCallBack(requestBody);
            rpcClientInit.REQUEST_PENDING_CENTER.add(requestBody.getMessageId(), messageCallBack);
            //发送请求
            rpcClientInit.getClientHandler().sendRequest(requestBody);
            //得到结果
            //RpcResponse responseBody = messageCallBack.getValue(6, TimeUnit.SECONDS);
            RpcResponse responseBody = null;
            if(isSyn){
                 responseBody =  messageCallBack.getValue(4, TimeUnit.SECONDS);
            }else{
                 responseBody =  messageCallBack.getValue(30, TimeUnit.SECONDS);
            }
            /*if (responseBody != null) {
                channelFuture.channel().closeFuture().sync();
            }*/
            return responseBody;
        } catch (Exception e) {
            RpcResponse responseBody = new RpcResponse();
            responseBody.setError(e);
            return responseBody;//ResultVo.error("请求超时", e);
        } finally {
            //GROUP.shutdownGracefully();
            //returnObject();
        }
    }
}
