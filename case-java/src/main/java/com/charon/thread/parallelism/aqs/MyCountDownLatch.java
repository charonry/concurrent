package com.charon.thread.parallelism.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description
 * @Author tangkang
 * @Date 2021/9/17 15:23
 * @Version 1.0
 **/
@Slf4j(topic = "CountDownLatch")
public class MyCountDownLatch {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(10);
        ExecutorService service = Executors.newFixedThreadPool(10, new ThreadFactory() {
            private AtomicInteger t = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r,"玩家_"+t.getAndIncrement());
            }
        });
        Random random = new Random();
        String[] all = new String[10];
        for(int j = 0 ;j<10;j++){
            int temp = j ;
            service.submit(()->{
                for(int i = 0 ;i <= 100; i ++){
                    try {
                        TimeUnit.MILLISECONDS.sleep(random.nextInt(50));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    all[temp] = Thread.currentThread().getName() + "(" + (i + "%") + ")";
                    System.out.print("\r" + Arrays.toString(all));
                }
                latch.countDown();
            });
        }
        latch.await();
        System.out.println("\n游戏开始...");
        service.shutdown();
    }
}
