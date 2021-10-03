package com.charon.thread.basic;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;


/**
 * @Description
 * @Author tangkang
 * @Date 2021/7/20 9:57
 * @Version 1.0
 **/
@Slf4j(topic = "MyReentrantLock")
public class MyReentrantLock {

    static boolean hasCigarette = false;
    static boolean hasTakeout = false;
    static ReentrantLock room = new ReentrantLock();
    static Condition waitCigaretteSet = room.newCondition();
    static Condition waitTakeoutSet = room.newCondition();

    public static void main(String[] args) throws InterruptedException {

        new Thread(()->{
            room.lock();
            try {
                log.debug("有烟没？[{}]", hasCigarette);
                while (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    try {
                        waitCigaretteSet.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("可以开始干活了");
            } finally {
                room.unlock();
            }
        },"t1").start();

        new Thread(() -> {
            room.lock();
            try {
                log.debug("外卖送到没？[{}]", hasTakeout);
                while (!hasTakeout) {
                    log.debug("没外卖，先歇会！");
                    try {
                        waitTakeoutSet.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("可以开始干活了");
            } finally {
                room.unlock();
            }
        }, "t2").start();

        TimeUnit.SECONDS.sleep(1);
        new Thread(() -> {
            room.lock();
            try {
                hasTakeout = true;
                waitTakeoutSet.signal();
            } finally {
                room.unlock();
            }
        }, "送外卖的").start();

        TimeUnit.SECONDS.sleep(1);

        new Thread(() -> {
            room.lock();
            try {
                hasCigarette = true;
                waitCigaretteSet.signal();
            } finally {
                room.unlock();
            }
        }, "送烟的").start();
    }
}
