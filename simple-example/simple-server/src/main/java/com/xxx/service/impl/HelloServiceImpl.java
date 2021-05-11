package com.xxx.service.impl;

import com.xxx.service.IHelloService;
import ltd.pomeo.core.annotation.RpcService;

/**
 * @author zhq
 * @date 2021/5/11
 */
@RpcService
public class HelloServiceImpl implements IHelloService {
    public String say(String name) {
        return "Hello " + name + " ,近来可好？";
    }
}
