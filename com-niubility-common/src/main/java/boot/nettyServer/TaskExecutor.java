package boot.nettyServer;

import boot.nettyRpcModel.RpcRequest;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author jiangshenjie
 */
public interface TaskExecutor {

    /**
     * 异步执行业务
     *
     * @param ctx
     * @param rpcRequest
     */
    void execute(ChannelHandlerContext ctx, RpcRequest rpcRequest);

    /**
     * 添加服务实例
     *
     * @param serviceName
     * @param instance
     */
    void addInstance(String serviceName, Object instance);

    void unloadExecutor();

    void doWork(ChannelHandlerContext ctx, RpcRequest request);
}
