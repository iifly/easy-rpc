package ltd.pomeo.core.handler;

import ltd.pomeo.core.InvokerMessage;
import ltd.pomeo.core.client.RpcClient;
import io.netty.channel.nio.NioEventLoopGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author zhq
 * @date 2021/4/24
 */
public class RpcClientProxyHandler implements InvocationHandler {
    Logger log = LoggerFactory.getLogger(RpcClientProxyHandler.class);
    private final Class<?> clazz;
    private final RpcClient rpcClient;

    public RpcClientProxyHandler(Class<?> clazz) {
        this.clazz = clazz;
        rpcClient = new RpcClient();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         InvokerMessage invokerMessage = new InvokerMessage();
         invokerMessage.setClassName(clazz.getSimpleName());
         invokerMessage.setMethodName(method.getName());
         invokerMessage.setParamType(method.getParameterTypes());
         invokerMessage.setArgs(args);
         RpcClientHandler rpcClientHandler = new RpcClientHandler();
         NioEventLoopGroup group = null;
         try {
             group = rpcClient.remoteCall(invokerMessage,rpcClientHandler);
             return rpcClientHandler.getResult();
         } finally {
             assert group != null;
             log.info("Async close group ");
             group.shutdownGracefully();
         }

    }

    public <T> T createProxy(){
        return (T)Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
    }
}
