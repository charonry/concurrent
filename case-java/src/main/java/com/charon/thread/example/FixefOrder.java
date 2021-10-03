package com.charon.thread.example;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description 同步模式之固定顺序
 * @Author tangkang
 * @Date 2021/7/20 10:57
 * @Version 1.0
 **/
@Slf4j(topic = "FixefOrder")
public class FixefOrder {
    static ReentrantLock lock = new ReentrantLock();
    static Condition condition = lock.newCondition();

    static boolean flag = false;

    public static void main(String[] args) {
        //reentrant();
        park();
    }

    private static void park() {
        Thread t2 = new Thread(() -> {
            LockSupport.park();
            log.debug("2");
        }, "t2");
        t2.start();

        new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("1");
            LockSupport.unpark(t2);
        },"t1").start();
    }

    private static void reentrant() {
        new Thread(()->{
            lock.lock();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("1");
            condition.signal();
            flag = true ;
            lock.unlock();
        },"t1").start();

        new Thread(()->{
            lock.lock();
            try {
                while (!flag){
                    condition.await();
                }
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("2");
            lock.unlock();
        },"t2").start();
    }
}
