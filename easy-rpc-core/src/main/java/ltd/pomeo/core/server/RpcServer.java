package ltd.pomeo.core.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import ltd.pomeo.core.constant.Common;
import ltd.pomeo.core.handler.MyChannelInitializer;
import ltd.pomeo.core.handler.RpcServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Properties;

/**
 * @author zhq
 * @date 2021/4/24
 */

public class RpcServer {
    Logger log = LoggerFactory.getLogger(RpcServer.class);
    private static Integer port = 8868;
    public RpcServer() {

    }
    static {
        InputStream inputStream = RpcServer.class.getClassLoader().getResourceAsStream(Common.RESOURCE_FILE_NAME);
        if (inputStream != null) {
            Properties properties = new Properties();
            try {
                properties.load(inputStream);
                if (properties.containsKey(Common.RPC_PORT_PROPERTY)) {
                    port = Integer.parseInt(properties.getProperty(Common.RPC_PORT_PROPERTY));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() throws InterruptedException {
        //3.创建 EventLoopGroup
        NioEventLoopGroup parent = new NioEventLoopGroup();
        NioEventLoopGroup child = new NioEventLoopGroup();
        RpcServerHandler serverHandler = new RpcServerHandler();
        try {
            //4.创建 ServerBootstrap
            ServerBootstrap b = new ServerBootstrap();
            b.group(parent,child)
                    //5.指定使用 NIO 的传输 Channel
                    .channel(NioServerSocketChannel.class)
                    //6.设置 socket 地址使用所选的端口
                    .localAddress(new InetSocketAddress(port))
                    //7.添加 RpcServerHandler 到 Channel 的 ChannelPipeline
                    .childHandler(new MyChannelInitializer(serverHandler));
            //8.绑定的服务器;sync 等待服务器关闭
            ChannelFuture future = b.bind().sync();
            log.info(RpcServer.class.getSimpleName() + " started and listen on =>" + future.channel().localAddress());
            //9.关闭 channel 和 块，直到它被关闭
            future.channel().closeFuture().sync();
        } finally {
            //10.关闭 EventLoopGroup，释放所有资源。
            parent.shutdownGracefully().sync();
            child.shutdownGracefully().sync();
        }
    }
}
