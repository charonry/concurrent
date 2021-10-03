package com.charon.thread.parallelism.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @Description
 * @Author tangkang
 * @Date 2021/9/17 16:12
 * @Version 1.0
 **/
@Slf4j(topic = "CyclicBarrier")
public class MyCyclicBarrier {
    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(2);
        /**
         * 任务完成后运行线程
         */
        CyclicBarrier barrier = new CyclicBarrier(2,()->{
            log.debug("first second finish...");
        });

        for(int i = 0 ; i < 3 ; i++){
            service.submit(()->{
                log.debug(" first task begin....");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    log.debug(" first task end....");
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });

            service.submit(()->{
                log.debug(" second task begin....");
                try {
                    TimeUnit.SECONDS.sleep(2);
                    barrier.await();
                    log.debug(" second task end....");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }


        log.debug("next main...");
        service.shutdown();

    }

}
