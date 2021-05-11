package com.xxx;

import ltd.pomeo.core.EasyRpcContext;
import ltd.pomeo.core.annotation.RpcScan;

/**
 * @author zhq
 * @date 2021/5/11
 */
@RpcScan("com.xxx.service")
public class RpcServerApp {
    public static void main(String[] args) {
        EasyRpcContext.run(RpcServerApp.class);
    }
}
