package com.charon.thread.basic;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @Description
 * @Author tangkang
 * @Date 2021/6/23 16:31
 * @Version 1.0
 **/
@Slf4j(topic = "CallableTest")
public class MyThread {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask task = new FutureTask(()->{
            log.debug(" task runing");
            Thread.sleep(100);
            return 100;
        });
        new Thread(task,"callable").start();
        log.debug("{}",task.get());


        new Thread(()->{
            while (true){
                log.debug("da yin dayin");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    log.error("睡眠失败");
                }
            }
        },"threa1").start();
    }
}
