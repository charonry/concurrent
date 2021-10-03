package com.charon.thread.example;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @Description
 * @Author tangkang
 * @Date 2021/9/7 17:50
 * @Version 1.0
 **/
@Slf4j(topic = "StarvationPool")
public class Starvation {

    static final List<String> MENU= Arrays.asList("地三鲜", "宫保鸡丁", "辣子鸡丁", "烤鸡翅");
    static Random RANDOM = new Random();
    static String cooking() {
        return MENU.get(RANDOM.nextInt(MENU.size()));
    }

    public static void main(String[] args) {

        ExecutorService waiterPool = Executors.newFixedThreadPool(1);
        ExecutorService cookPool = Executors.newFixedThreadPool(1);
        
        for(int i = 0 ;i<2;i++){
            waiterPool.execute(()->{
                log.debug("处理点餐...");
                Future<String> future = cookPool.submit(() -> {
                    log.debug("做菜");
                    return cooking();
                });
                try {
                    log.debug("上菜: {}", future.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
        }


    }
}
