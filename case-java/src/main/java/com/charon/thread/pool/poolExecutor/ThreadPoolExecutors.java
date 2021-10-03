package com.charon.thread.pool.poolExecutor;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description
 * @Author tangkang
 * @Date 2021/8/10 11:02
 * @Version 1.0
 **/
@Slf4j(topic = "ThreadPoolExecutors")
public class ThreadPoolExecutors {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //createFiexdThreadPool();
        //createCachedThreadPool();
        //createSingleThreadPool();
        createScheduledThreadPool();

    }

    private static void createScheduledThreadPool() throws ExecutionException, InterruptedException {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        log.debug("start...");
        Future<?> future = pool.schedule(() -> {
            log.debug("task1");
            int i = 1 / 0;
           /* try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }, 1, TimeUnit.SECONDS);
        log.debug("return:{}",future.get());

       /* pool.scheduleAtFixedRate(()->{
            log.debug("task2");
        },1,1,TimeUnit.SECONDS);*/

        /*pool.scheduleWithFixedDelay(()->{
            log.debug("task3");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },1,1,TimeUnit.SECONDS);*/
    }


    private static void createSingleThreadPool() {
        ExecutorService pool = Executors.newSingleThreadExecutor(new ThreadFactory() {
            private AtomicInteger t = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "mySinglePoolThread_" + t.getAndIncrement());
            }
        });
        pool.execute(() -> {
            log.debug("1");
            int i = 1 / 0;
        });

        pool.execute(() -> {
            log.debug("2");
        });

        pool.execute(() -> {
            log.debug("3");
        });
    }

    private static void createCachedThreadPool() {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool(new ThreadFactory() {
            private AtomicInteger t = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "myCachedPoolThread_" + t.getAndIncrement());
            }
        });
        cachedThreadPool.execute(()->{
            log.debug("1");
        });
        cachedThreadPool.execute(()->{
            log.debug("2");
        });
        cachedThreadPool.execute(()->{
            log.debug("3");
        });
    }

    public static void createFiexdThreadPool() {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2, new ThreadFactory() {
            private AtomicInteger t = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r,"myFixedPoolThread_"+t.getAndIncrement());
            }
        });
        fixedThreadPool.execute(()->{
            log.debug("1");
        });
        fixedThreadPool.execute(()->{
            log.debug("2");
        });
        fixedThreadPool.execute(()->{
            log.debug("3");
        });
    }
}
