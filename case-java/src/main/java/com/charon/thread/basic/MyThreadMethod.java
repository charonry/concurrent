package com.charon.thread.basic;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author tangkang
 * @Date 2021/6/24 10:34
 * @Version 1.0
 **/
@Slf4j(topic = "ThreadMethod")
public class MyThreadMethod {
    public static void main(String[] args) throws InterruptedException {
        getThreadStatus();

        //getInerrupt();

    }

    private static void getInerrupt() throws InterruptedException {
        Thread t1 = new Thread( ()->{
            log.debug("sleep");
            try {
                log.debug("{}此时打断标志位{}",Thread.currentThread().getName(),Thread.currentThread().isInterrupted());
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"t1");

        t1.start();
        Thread.sleep(100);
        log.debug("interrupt");
        t1.interrupt();
        log.debug("打断标记为{}",t1.isInterrupted());
    }

    private static void getThreadStatus() throws InterruptedException {
        Thread thread = new Thread(()->{
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.error("error running"+e.getMessage());
            }
            log.debug("running ....");
        },"t1");
        log.debug("t1线程状态是{}",thread.getState());
        thread.start();
        log.debug("t1线程状态是{}",thread.getState());
        TimeUnit.MILLISECONDS.sleep(200);
        log.debug("t1线程状态是{}",thread.getState());
        Thread.sleep(700);
        log.debug("t1线程状态是{}",thread.getState());
    }


    private static void method1(int x) {
        int y = x + 1;
        Object m = method2();
        System.out.println(Thread.currentThread().getName()+ "对象"+m);
    }

    private static Object method2() {
        Object n = new Object();
        return n;
    }
}
