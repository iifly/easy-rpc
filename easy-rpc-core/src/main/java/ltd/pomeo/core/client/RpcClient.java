package ltd.pomeo.core.client;

import ltd.pomeo.core.InvokerMessage;
import ltd.pomeo.core.constant.Common;
import ltd.pomeo.core.handler.MyChannelInitializer;
import ltd.pomeo.core.handler.RpcClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;


import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Properties;

/**
 * @author zhq
 * @date 2021/5/9
 */
public class RpcClient {
    private static String host = "localhost";
    private static Integer port = 8868;
    public RpcClient() {
    }

    static {
        InputStream inputStream = RpcClient.class.getClassLoader().getResourceAsStream(Common.RESOURCE_FILE_NAME);
        Properties properties = new Properties();
        if(inputStream != null){
            try {
                properties.load(inputStream);
                if (properties.containsKey(Common.RPC_HOST_PROPERTY)) {
                    host = properties.getProperty(Common.RPC_HOST_PROPERTY);
                }
                if(properties.containsKey(Common.RPC_PORT_PROPERTY)){
                    port = Integer.parseInt(properties.getProperty(Common.RPC_PORT_PROPERTY));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public NioEventLoopGroup remoteCall(InvokerMessage message, RpcClientHandler rpcClientHandler) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        //1.创建 Bootstrap
        Bootstrap b = new Bootstrap();
        //2.指定 EventLoopGroup 来处理客户端事件。由于我们使用 NIO 传输，所以用到了 NioEventLoopGroup 的实现
        b.group(group)
                //3.使用的 channel 类型是一个用于 NIO 传输
                .channel(NioSocketChannel.class)
                //4.设置服务器的 InetSocketAddress
                .remoteAddress(new InetSocketAddress(host,port))
                //5.当建立一个连接和一个新的通道时，创建添加到 RpcClientHandler 实例到 channel pipeline
                .handler(new MyChannelInitializer(rpcClientHandler));
        //6.连接到远程;等待连接完成
        ChannelFuture future = b.connect().sync();
        future.channel().writeAndFlush(message);
        //7.阻塞直到 Channel 关闭
        future.channel().closeFuture().sync();
        return group;
    }
}
