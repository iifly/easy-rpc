package ltd.pomeo.core;

import ltd.pomeo.core.annotation.EnableEasyRpc;
import ltd.pomeo.core.annotation.RpcCilent;
import ltd.pomeo.core.annotation.RpcScan;
import ltd.pomeo.core.annotation.RpcService;
import ltd.pomeo.core.handler.RpcClientProxyHandler;
import ltd.pomeo.core.server.RpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhq
 * @date 2021/5/8
 */
public class EasyRpcContext {
    Logger log = LoggerFactory.getLogger(EasyRpcContext.class);
    private final Class<?> appConfig;

    public EasyRpcContext(Class<?> appConfig) {
        this.appConfig = appConfig;
        init();
    }
    private void init(){
        if (appConfig.isAnnotationPresent(RpcScan.class)) {
            RpcScan rpcScan = appConfig.getDeclaredAnnotation(RpcScan.class);
            String packageName = rpcScan.value();
            log.info("rpcService scan base package name :{}",packageName);
            rpcServiceScan(packageName);
            registryRpcService();
        }
        if(appConfig.isAnnotationPresent(EnableEasyRpc.class)){
            String name = appConfig.getName();
            String simpleName = appConfig.getSimpleName();
            String packageName = name.substring(0,name.indexOf(simpleName)-1);
            log.info("rpcClient scan base package name :{}",packageName);
            rpcClientInject(packageName);
        }
    }
    /**
     * 在注册中心注册服务需要有容器存放
     */

    public static ConcurrentHashMap<String, Object> rpcServiceMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Class<?>, Object> rpcClientMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, RpcServiceDefinition> rpcServiceDefinitionMap = new ConcurrentHashMap<>();

    private void rpcServiceScan(String packageName){
        Set<String> set = MyScanner.scanClassName(packageName);
        for (String className : set) {
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(RpcService.class)) {
                    RpcService rpcService = clazz.getDeclaredAnnotation(RpcService.class);
                    String beanName = rpcService.value();
                    if ("".equals(beanName)) {
                        beanName = clazz.getSimpleName().substring(0, 1)
                                .toLowerCase()
                                .concat(clazz.getSimpleName().substring(1));
                    }
                    RpcServiceDefinition rpcServiceDefinition = new RpcServiceDefinition(beanName,clazz);
                    String classSimpleName = clazz.getInterfaces()[0].getSimpleName();
                    rpcServiceDefinitionMap.put(classSimpleName,rpcServiceDefinition);
                }
            } catch (ClassNotFoundException e) {
                log.error("rpcServiceScan Exception",e);
            }
        }
    }
    private void registryRpcService(){
        for (Map.Entry<String, RpcServiceDefinition> entry : rpcServiceDefinitionMap.entrySet()) {
            RpcServiceDefinition rpcServiceDefinition = entry.getValue();
            Class<?> clazz = rpcServiceDefinition.getClazz();
            Object bean = createBean(clazz);
            String beanName = rpcServiceDefinition.getBeanName();
            rpcServiceMap.put(beanName,bean);
        }
    }

    private void rpcClientInject(String packageName){
        Set<String> set = MyScanner.scanClassName(packageName);
        for (String className : set) {
            try {
                Class<?> clazz = Class.forName(className);
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(RpcCilent.class)) {
                        field.setAccessible(true);
                        Constructor<?> constructor = clazz.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        Object instance = constructor.newInstance();
                        if (rpcClientMap.containsKey(clazz)) {
                            field.set(instance,rpcClientMap.get(clazz));
                        }else{
                            RpcClientProxyHandler proxyHandler = new RpcClientProxyHandler(field.getType());
                            Object proxy = proxyHandler.createProxy();
                            field.set(instance,proxy);
                            rpcClientMap.put(clazz,instance);
                        }
                    }
                }
            } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
                log.error("rpcClientInject Exception",e);
            }
        }
    }

    private static Object createBean(Class<?> clazz){
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("build " + clazz.getSimpleName() + " bean fail！");
    }

    public static Object getBeanByClassName(String className){
        if (rpcServiceDefinitionMap.containsKey(className)) {
            RpcServiceDefinition rpcServiceDefinition = rpcServiceDefinitionMap.get(className);
            String beanName = rpcServiceDefinition.getBeanName();
            return rpcServiceMap.get(beanName);
        }
        throw new RuntimeException("not have " + className + " class!");
    }
    public static Object getBean(String beanName){
        if (rpcServiceMap.containsKey(beanName)) {
            return rpcServiceMap.get(beanName);
        }
        throw new RuntimeException("not have " + beanName + " bean!");
    }
    public <T> T getClientBean(Class<T> clazz){
        if (rpcClientMap.containsKey(clazz)) {
            return (T)rpcClientMap.get(clazz);
        }
        throw new RuntimeException("not have " + clazz + " bean!");
    }
    public void run(){
        if (this.appConfig != null) {
            run(this.appConfig);
        }
        throw new RuntimeException("config is null");
    }
    public static void run(Class<?> appConfig){
        try {
            new EasyRpcContext(appConfig);
            RpcServer server = new RpcServer();
            //2.呼叫服务器的 start() 方法
            server.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
