package com.xxx.controller;

import com.xxx.service.IHelloService;
import ltd.pomeo.core.annotation.RpcCilent;

/**
 * @author zhq
 * @date 2021/5/11
 */
public class HelloController {
    @RpcCilent
    IHelloService helloService;
    public String say(String name){
        return helloService.say(name);
    }
}
