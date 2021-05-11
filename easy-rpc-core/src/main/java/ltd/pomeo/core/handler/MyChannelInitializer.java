package ltd.pomeo.core.handler;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @author zhq
 * @date 2021/5/8
 */
public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {
    ChannelInboundHandlerAdapter handler;

    public MyChannelInitializer(ChannelInboundHandlerAdapter handler) {
        this.handler = handler;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                //编码器
                .addLast(new ObjectEncoder())
                //解码器
                .addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)))
                .addLast(handler);
    }
}
