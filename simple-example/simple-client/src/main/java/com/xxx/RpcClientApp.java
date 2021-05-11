package com.xxx;

import com.xxx.controller.HelloController;
import ltd.pomeo.core.EasyRpcContext;
import ltd.pomeo.core.annotation.EnableEasyRpc;

/**
 * @author zhq
 * @date 2021/5/11
 */
@EnableEasyRpc
public class RpcClientApp {
    public static void main(String[] args) {
        EasyRpcContext context = new EasyRpcContext(RpcClientApp.class);
        HelloController bean = context.getClientBean(HelloController.class);
        System.out.println(bean.say("悟空"));
    }
}
