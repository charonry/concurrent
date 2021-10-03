package com.charon.thread.share;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @Description
 * @Author tangkang
 * @Date 2021/7/8 16:22
 * @Version 1.0
 **/
@Slf4j(topic = "TestBiased")
public class TestBiased {
    static Thread t1,t2,t3;

    public static void main(String[] args) throws InterruptedException {
        Dog dog = new Dog();
        /*// 禁用掉这个对象的偏向锁
        dog.hashCode();
        // -XX:BiasedLockingStartupDelay=0 关闭延迟
        // -XX:-UseBiasedLocking 禁用偏向锁 启用将-变成+
        // -XX:-EliminateLocks 锁消除
        log.debug("{}",ClassLayout.parseInstance(dog).toPrintable());
        synchronized (dog){
            log.debug("{}",ClassLayout.parseInstance(dog).toPrintable());
        }

        log.debug("{}",ClassLayout.parseInstance(dog).toPrintable());*/
        /*new Thread(()->{
            log.debug("{}",ClassLayout.parseInstance(dog).toPrintable());
            synchronized (dog){
                log.debug("{}",ClassLayout.parseInstance(dog).toPrintable());
            }

            log.debug("{}",ClassLayout.parseInstance(dog).toPrintable());
            synchronized (TestBiased.class){
                TestBiased.class.notify();
            }
        },"t1").start();

        new Thread(()->{
            synchronized (TestBiased.class){
                try {
                    TestBiased.class.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("{}",ClassLayout.parseInstance(dog).toPrintable());
            synchronized (dog){
                log.debug("{}",ClassLayout.parseInstance(dog).toPrintable());
            }

            log.debug("{}",ClassLayout.parseInstance(dog).toPrintable());
        },"t2").start();*/
        threadNewDirection();
    }

    private static void threadNewDirection() throws InterruptedException {
        Vector<Dog> list = new Vector<>();

        int loopNumber = 38;
        t1 = new Thread(() -> {
            for (int i = 0; i < loopNumber; i++) {
                Dog d = new Dog();
                list.add(d);
                synchronized (d) {
                    log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                }
            }
            LockSupport.unpark(t2);
        }, "t1");
        t1.start();

        t2 = new Thread(() -> {
            LockSupport.park();
            log.debug("===============> ");
            for (int i = 0; i < loopNumber; i++) {
                Dog d = list.get(i);
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                synchronized (d) {
                    log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                }
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
            }
            LockSupport.unpark(t3);
        }, "t2");
        t2.start();

        t3 = new Thread(() -> {
            LockSupport.park();
            log.debug("===============> ");
            for (int i = 0; i < loopNumber; i++) {
                Dog d = list.get(i);
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                synchronized (d) {
                    log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                }
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
            }
        }, "t3");
        t3.start();

        t3.join();
        log.debug(ClassLayout.parseInstance(new Dog()).toPrintable());
    }
}

class Dog{

}