package com.charon.thread.basic;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description
 * @Author tangkang
 * @Date 2021/6/24 10:34
 * @Version 1.0
 **/
@Slf4j(topic = "FramesTest")
public class MyFrames {
    public static void main(String[] args) throws InterruptedException {
        new Thread(()->method1(20),"thread1").start();
        method1(10);


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
