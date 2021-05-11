package ltd.pomeo.core.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ltd.pomeo.core.EasyRpcContext;
import ltd.pomeo.core.InvokerMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author zhq
 * @date 2021/4/24
 * @Sharable 1.标识这类的实例之间可以在 channel 里面共享
 */
@ChannelHandler.Sharable
public class RpcServerHandler extends ChannelInboundHandlerAdapter {
    Logger log = LoggerFactory.getLogger(RpcServerHandler.class);
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("{}-connection",ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    /**
     * 每个信息入站都会调用
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        InvokerMessage invokerMessage = (InvokerMessage) msg;
        //2.打印消息
        log.info("receive msg:{}",invokerMessage.toString());
        //3.将所接收的消息返回给发送者。注意，这还没有冲刷数据
        String className = invokerMessage.getClassName();
        Object obj = EasyRpcContext.getBeanByClassName(className);
        String methodName = invokerMessage.getMethodName();
        Class<?>[] type = invokerMessage.getParamType();
        Method method = obj.getClass().getDeclaredMethod(methodName, type);
        Object[] args = invokerMessage.getArgs();
        Object result = method.invoke(obj, args);
        ctx.write(result);
    }

    /**
     * 通知处理器最后的 channelRead() 是当前批处理中的最后一条消息时调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //4.冲刷所有待审消息到远程节点。关闭通道后，操作完成
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 读操作时捕获到异常时调用
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //5.打印异常堆栈跟踪
        cause.printStackTrace();
        //6.关闭通道
        ctx.close();
    }
}
