package com.charon.thread.example;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description 多线程的协作统筹作业
 * @Author tangkang
 * @Date 2021/6/24 16:22
 * @Version 1.0
 **/
@Slf4j(topic = "ThreadSequence")
public class ThreadWork {
    public static void main(String[] args)  {


        Thread t2 = new Thread(()->{
            try {
                log.debug("刷牙");
                TimeUnit.SECONDS.sleep(2);
                log.debug("洗脸");
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"t2");

        Thread t1 = new Thread(()->{
            try {
                log.debug("洗水壶");
                TimeUnit.SECONDS.sleep(1);
                log.debug("烧水");
                TimeUnit.SECONDS.sleep(5);
                t2.join();
                log.debug("出门");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        },"t1");

        t1.start();
        t2.start();
    }
}
