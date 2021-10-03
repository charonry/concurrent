package com.charon.thread.parallelism.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @Description 信号量 只适合单机版本;限制现场数而非资源数
 * @Author tangkang
 * @Date 2021/9/17 14:08
 * @Version 1.0
 **/
@Slf4j(topic = "Semaphore")
public class MySemaphore {
    public static void main(String[] args) {
        // 1. 创建 semaphore 对象
        Semaphore semaphore = new Semaphore(3);
        for(int i = 0 ;i<10 ; i++){
            new Thread(()->{
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    log.debug("running....");
                    TimeUnit.SECONDS.sleep(1);
                    log.debug("end....");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    semaphore.release();
                }
            }).start();
        }
    }
}
