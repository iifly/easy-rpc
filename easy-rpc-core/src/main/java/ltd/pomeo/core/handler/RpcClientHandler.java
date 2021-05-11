package ltd.pomeo.core.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author zhq
 * @date 2021/4/24
 */
@ChannelHandler.Sharable
public class RpcClientHandler extends ChannelInboundHandlerAdapter {
    Logger log = LoggerFactory.getLogger(RpcClientHandler.class);
    private Object result;

    public Object getResult() {
        return result;
    }

    /**
     * 服务器的连接被建立后调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //当被通知该 channel 是活动的时候就发送信息
        log.info("connection success");
    }

    /**
     * 数据后从服务器接收到调用
     * @param channelHandlerContext
     * @param in
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object in) throws Exception {
        //当被通知该 channel 是活动的时候就发送信息
        log.info("remoteCall result :{}",in.toString());
        result = in;

    }
    /**
     * 捕获一个异常时调用
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //记录日志错误并关闭 channel
        cause.printStackTrace();
        ctx.close();
    }

}
