package com.charon.thread.parallelism.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;


/**
 * @Description 不支持条件变量、不支持重入
 * @Author tangkang
 * @Date 2021/9/17 10:44
 * @Version 1.0
 **/
@Slf4j(topic = "StampedLock")
public class MyStampedLock {
    public static void main(String[] args) {
        DataContainerStamped dataContainerStamped = new DataContainerStamped(88);
        new Thread(()->{
            dataContainerStamped.read(1);
        },"t1").start();
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            dataContainerStamped.write(22);
        }, "t2").start();
    }
}


@Slf4j(topic = "DataContainerStamped")
class DataContainerStamped {

    private int data;
    private final StampedLock stampedLock = new StampedLock();

    public DataContainerStamped(int data) {
        this.data = data;
    }

    public int read(int readTime){
        long stamp = stampedLock.tryOptimisticRead();
        log.debug("optimistic read locking...{}", stamp);
        try {
            TimeUnit.SECONDS.sleep(readTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(stampedLock.validate(stamp)){
            log.debug("read finish...{}, data:{}", stamp, data);
            return data;
        }
        // 锁升级 - 读锁
        log.debug("updating to read lock... {}", stamp);
        try {
            stamp = stampedLock.readLock();
            log.debug("read lock {}", stamp);
            TimeUnit.SECONDS.sleep(readTime);
            log.debug("read again finish...{}, data:{}", stamp, data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            log.debug("read unlock {}", stamp);
            stampedLock.unlockRead(stamp);
        }
        return  data ;
    }

    public void write(int newData) {
        long stamp = stampedLock.writeLock();
        log.debug("write lock {}", stamp);
        try {
            TimeUnit.SECONDS.sleep(2);
            this.data = newData;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            log.debug("write unlock {}", stamp);
            stampedLock.unlockWrite(stamp);
        }
    }
}