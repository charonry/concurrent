package com.charon.thread.pool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description 它没有容量，没有线程来取是放不进去的（一手交钱、一手交货）
 * @Author tangkang
 * @Date 2021/8/10 11:17
 * @Version 1.0
 **/
@Slf4j(topic = "SynchronousQueue")
public class SynchronousQueue {
    public static void main(String[] args) throws InterruptedException {
        java.util.concurrent.SynchronousQueue<Integer> integers = new java.util.concurrent.SynchronousQueue<>();
        new Thread(() -> {
            try {
                log.debug("putting {} ", 1);
                integers.put(1);
                log.debug("{} putted...", 1);

                log.debug("putting...{} ", 2);
                integers.put(2);
                log.debug("{} putted...", 2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"t1").start();

        TimeUnit.SECONDS.sleep(1);

        new Thread(() -> {
            try {
                log.debug("taking {}", 1);
                integers.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"t2").start();

        TimeUnit.SECONDS.sleep(1);

        new Thread(() -> {
            try {
                log.debug("taking {}", 2);
                integers.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"t3").start();
    }

}
