package com.charon.thread.example;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 两阶段终止
 * @Author tangkang
 * @Date 2021/6/24 15:12
 * @Version 1.0
 **/
@Slf4j(topic = "TwoPhaseTermination")
public class TwoPhaseTermination {
    public static void main(String[] args) throws InterruptedException {
        Monitor monitor = new Monitor();
        monitor.start();
        Thread.sleep(3500);
        log.debug("停止监控");
        monitor.stop();
    }
}

/**
 * 监控线程
 */
@Slf4j(topic = "TwoPhaseTermination")
class Monitor{
    private  Thread monitorThread;

    // 停止标记
    private volatile boolean stop = false;
    // 判断是否执行过 start 方法
    private boolean starting = false;

    // 启动监控线程
    public void  start(){
        synchronized (this){
            if(starting){
                return;
            }
            starting = true;
        }
        monitorThread = new Thread(()->{
            while (true){
                Thread currentThread = Thread.currentThread();
                if(stop){
                    log.debug("处理事故");
                    break;
                }
                try {
                    Thread.sleep(1000);
                    log.debug("执行监控记录");
                } catch (InterruptedException e) {
                    log.debug("发生了异常，异常内容{}",e.getMessage());
                }
            }
        },"monitor");

        monitorThread.start();
    }

    // 停止监控线程
    public void stop(){
        stop = true;
        // 防止运行时间过长,立即中断
        monitorThread.interrupt();
    }
}