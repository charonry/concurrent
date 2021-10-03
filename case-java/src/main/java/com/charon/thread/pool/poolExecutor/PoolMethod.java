package com.charon.thread.pool.poolExecutor;

import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Description
 * @Author tangkang
 * @Date 2021/9/7 15:44
 * @Version 1.0
 **/
@Slf4j(topic = "PoolMethod")
public class PoolMethod {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        //submit(pool);
        //invokeAll(pool);
        //involeAny(pool);

        //shutdown(pool);
        scheduledExecute();

    }

    /**
     * 每周三晚上16:51:00执行
     */
    public static void scheduledExecute() {
        // 间隔时间
        long period = 1000 * 60 * 60 * 24 * 7;
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);
        // 获取周四时间
        LocalDateTime time = now.withHour(16).withMinute(51).withSecond(0).withNano(0).with(DayOfWeek.WEDNESDAY);
        // 如果 当前时间 > 本周周三，必须找到下周周三
        if(now.compareTo(time)>0){
            time = time.plusWeeks(1);
        }
        System.out.println(time);
        // 当前时间和执行时间的时间差
        long initailDelay = Duration.between(now, time).toMillis();
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
        scheduledThreadPool.scheduleAtFixedRate(()->{
            System.out.println("running.....");
        },initailDelay,period, TimeUnit.MILLISECONDS);
    }

    /**
     * shutdown：对于线程池的关闭(并不会影响到正在执行的任务)
     * shutdownNow：对于对于线程池的关闭(影响到正在执行的任务)
     * @param pool
     */
    public static void shutdown(ExecutorService pool) throws InterruptedException {
        Future<Integer> result1 = pool.submit(() -> {
            log.debug("task 1 running...");
            Thread.sleep(1500);
            log.debug("task 1 finish...");
            return 1;
        });

        Future<Integer> result2 = pool.submit(() -> {
            log.debug("task 2 running...");
            Thread.sleep(500);
            log.debug("task 2 finish...");
            return 2;
        });

        Future<Integer> result3 = pool.submit(() -> {
            log.debug("task 3 running...");
            Thread.sleep(1000);
            log.debug("task 3 finish...");
            return 3;
        });

        log.debug("shutdown");
       /* pool.shutdown();
        // 等待一段时间之后执行
        pool.awaitTermination(3, TimeUnit.SECONDS);*/
        List<Runnable> runnables = pool.shutdownNow();
        log.debug("other.... {}",runnables);

        /*Future<Integer> result4 = pool.submit(() -> {
            log.debug("task 4 running...");
            Thread.sleep(1000);
            log.debug("task 4 finish...");
            return 4;
        });*/

    }

    /**
     * 只返回先执行完成的结果
     * @param pool 线程池
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static void involeAny(ExecutorService pool) throws InterruptedException, ExecutionException {
        Object object = pool.invokeAny(Arrays.asList(
                () -> {
                    log.debug("begin 1");
                    Thread.sleep(1000);
                    log.debug("end 1");
                    return "1";
                },
                () -> {
                    log.debug("begin 2");
                    Thread.sleep(1500);
                    log.debug("end 2");
                    return "2";
                },
                () -> {
                    log.debug("begin 3");
                    Thread.sleep(500);
                    log.debug("end 3");
                    return "3";
                }
        ));
        log.debug("{}",object);
    }

    /**
     * 返回全部结果的执行
     * @param pool 线程池
     * @throws InterruptedException
     */
    public static void invokeAll(ExecutorService pool) throws InterruptedException {
        List<Future<Object>> futures = pool.invokeAll(Arrays.asList(
                () -> {
                    log.debug("begin1");
                    Thread.sleep(1000);
                    return "1";
                },
                () -> {
                    log.debug("begin2");
                    Thread.sleep(500);
                    return "2";
                },
                () -> {
                    log.debug("begin3");
                    Thread.sleep(1500);
                    return "3";
                }
        ));
        futures.forEach(f->{
            try {
                log.debug("{}", f.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 带返回结果的执行
     * @param pool 线程池
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void submit(ExecutorService pool) throws ExecutionException, InterruptedException {
        log.debug("running");
        Future<String> future = pool.submit(() -> {
            Thread.sleep(1000);
            return "ok";
        });

        log.debug("{}",future.get());
    }
}
