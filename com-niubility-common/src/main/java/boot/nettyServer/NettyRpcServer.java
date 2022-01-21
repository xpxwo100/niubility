package boot.nettyServer;


import boot.nettyRpcModel.*;
import boot.zookeeper.ServiceRegistryAndDiscovery;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.flush.FlushConsolidationHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * netty启动
 *
 * @author hasee
 */
public class NettyRpcServer implements RpcServer {
    private static final Log logger = LogFactory.getLog(NettyRpcServer.class);
    private static NettyRpcServer server = null;
    private ServiceRegistryAndDiscovery serviceRegistryAndDiscovery;
    private String ip;
    private int port;
    private String serverName;
    public static ThreadFactory threadRpcFactory = new NamedThreadFactory("NettyRPC ThreadFactory");
    //方法返回到Java虚拟机的可用的处理器数量
    static int parallel = Runtime.getRuntime().availableProcessors() * 2;
    private static EventLoopGroup bossGroup = new NioEventLoopGroup();
    private static EventLoopGroup workGroup = new NioEventLoopGroup();
    private TaskExecutor nettyRecvInitializeTask = new NettyRecvInitializeTask();


    public NettyRpcServer(String serverName, String ip, int port, ServiceRegistryAndDiscovery serviceRegistryAndDiscovery) {
        this.serverName = serverName;
        this.ip = ip;
        this.port = port;
        this.serviceRegistryAndDiscovery = serviceRegistryAndDiscovery;
    }

    public void bind(int port) {
        try {
            //metrics
            MetricsHandler metricsHandler = new MetricsHandler();
            //业务线程池
            UnorderedThreadPoolEventExecutor business = new UnorderedThreadPoolEventExecutor(16, new DefaultThreadFactory("business"));
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workGroup);
            b.channel(NioServerSocketChannel.class);
            //option主要是针对boss线程组，child主要是针对worker线程组
            // ChannelOption.SO_BACKLOG用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，
            //用于临时存放已完成三次握手的请求的队列的最大长度。如果未设置或所设置的值小于1，Java将使用默认值50。
            //　服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接，多个客户端来的时候，
            //服务端将不能处理的客户端连接请求放在队列中等待处理，backlog参数指定了队列的大小
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.option(ChannelOption.SO_REUSEADDR, true);//这个参数表示允许重复使用本地地址和端口，
            b.option(ChannelOption.TCP_NODELAY, true);
            b.option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT);
            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);//内存池
            b.childOption(ChannelOption.SO_KEEPALIVE, true);//当设置该选项以后，如果在两小时内没有数据的通信时，TCP会自动发送一个活动探测数据报文。

            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ProtostuffCodecUtil util = new ProtostuffCodecUtil();
                    util.setRpcDirect(true);
                    ChannelPipeline pipeline = ch.pipeline();
                    //监控指标
                    pipeline.addLast("metricHandler", metricsHandler);
                    // LengthFieldBasedFrameDecoder 自定义长度解码器
				    /* 1) lengthFieldOffset = 0；//长度字段的偏差
		    		 2) lengthFieldLength = 4；//长度字段占的字节数
		    		 3) lengthAdjustment = 0；//添加到长度字段的补偿值
		    		 4) initialBytesToStrip = 4。//从解码帧中第一次去除的字节数*/
                    // 设置 0, 4, 0, 4 解码后的字节缓冲区丢弃了长度字段，仅仅包含消息体
                    pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4)); //LengthFieldBasedFrameDecoder 自定义长度解码器解决TCP黏包问题。
                    //心跳检测，1小时内没被调用就触发
                    //在出现超时事件时会被触发，包括读空闲超时或者写空闲超时；
                    pipeline.addLast(new IdleStateHandler(1, 0, 0, TimeUnit.HOURS));
                    //通过LengthFieldPrepender可以将待发送消息的长度写入到ByteBuf的前面，用writeInt写入，编码后的消息组成为长度字段+原消息的方式。
                    pipeline.addLast(new LengthFieldPrepender(4));
                    pipeline.addLast(new ProtostuffEncoder(util));
                    pipeline.addLast(new ProtostuffDecoder(util));
                    //减少flush的次数，牺牲延迟增加吞吐量，参数设置5为读取5次进行一次flush, consolidateWhenNoReadInProgress为true为异步情况下的优化flush
                    //FlushConsolidationHandler里的readInProgress参数
                    //同步 ：read -> writeAndFlush -> readComplete  readInProgress为true发生在read 到 readComplete之间
                    //异步 : read -> readComplete -> writeAndFlush（因为开启异步所以可能发生在readComplete之后） readInProgress为true发生在read 到 readComplete之间
                    pipeline.addLast("flushEnhance", new FlushConsolidationHandler(5, true));
                    pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                    pipeline.addLast(business, "handler", new ServerHandler(nettyRecvInitializeTask));
                    //pipeline.addLast("handler", new ServerHandler(nettyRecvInitializeTask));
                }
            });
            ChannelFuture channelFuture = b.bind(port);
            channelFuture.channel().closeFuture().sync();
            System.out.println("服务端启动");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

	 /*public static void main(String[] args) {
		 NettyRpcServer.startUp(9898);
	}*/

    public void unload() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
        nettyRecvInitializeTask.unloadExecutor();
    }

    @Override
    public void start() {
        //启动
        doRunServer();
    }

    /**
     * 启动 Netty RPC服务端
     */
    private void doRunServer() {
        new Thread(() -> {
            bind(port);
        }, "rpc-server-thread").start();
    }

    /**
     * baseZookeeper 注册节点
     *
     * @param serviceName
     * @param serviceBean
     * @return
     */
    @Override
    public boolean registerService(String serviceName, Object serviceBean) {
        try {
            RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
            //RpcService 定义的接口名称
            serviceName = rpcService.value().getName();
            //serviceBean 接口实现类
            nettyRecvInitializeTask.addInstance(serviceName, serviceBean);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void registerZookeeperNode() {
        try {
            serviceRegistryAndDiscovery.createNode0(serverName, ip + ":" + port + "", "base");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
