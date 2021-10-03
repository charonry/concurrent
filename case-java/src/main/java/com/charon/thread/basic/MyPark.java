package com.charon.thread.basic;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @Description
 * @Author tangkang
 * @Date 2021/6/24 15:39
 * @Version 1.0
 **/
@Slf4j(topic = "park")
public class MyPark {
    public static void main(String[] args) throws InterruptedException {
        park();
    }

    private static void park() throws InterruptedException {
        Thread park = new Thread(() -> {
            log.debug("park...");
            // 不会清空打断状态
            LockSupport.park();
            log.debug("unpark...");
            log.debug("打断状态：{}",Thread.currentThread().isInterrupted());
            Thread.interrupted();
            log.debug("此时打断标志：{}",Thread.currentThread().isInterrupted());
            LockSupport.park();
            log.debug("unpark...");
        }, "park");
        park.start();

        TimeUnit.SECONDS.sleep(1);
        park.interrupt();

    }
}
