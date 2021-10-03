package com.charon.thread.example;

import lombok.extern.slf4j.Slf4j;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description 同步模式之保护性暂停
 * @Author tangkang
 * @Date 2021/7/10 11:14
 * @Version 1.0
 **/
@Slf4j(topic = "GuardedSuspension")
public class GuardedSuspension {
    public static void main(String[] args) throws InterruptedException {
        /*GuardedObject guardedObject = new GuardedObject();
        new Thread(()->{
            log.debug("开始获取数据");
            Object obj = guardedObject.get(2000);
            log.debug("获取数据{}之后结束",obj);
        },"t1").start();

        new Thread(()->{
            log.debug("开始制造数据");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            guardedObject.complete(null);
            log.debug("制造数据结束");
        },"t2").start();
    }*/
        for(int i = 0 ;i<3;i++){
            new People().start();
        }
        TimeUnit.SECONDS.sleep(1);

        for (int i :MailBoxes.getIds()){
            new Postman(i,"这是第"+i+"个人的信").start();
        }
    }
}

@Slf4j(topic = "GuardedObject")
class GuardedObject{

    private int id;

    private Object response;

    public GuardedObject() {
    }

    public GuardedObject(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public Object get(long timeOut)  {
        long begin = System.currentTimeMillis();
        long passTime = 0;
        while (response == null){
            // 这一轮循环等待的时间
            long waitTime = timeOut - passTime;
            if(waitTime<=0){
                break;
            }
            synchronized (this){
                try {
                    // 存在虚假唤醒的情况
                    this.wait(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            passTime = System.currentTimeMillis()-begin;
            //System.out.println("passTime= "+ passTime);
        }
        return response;
    }

    public void complete(Object repsonse){
        synchronized (this){
            this.response= repsonse;
            this.notify();
        }
    }
}

@Slf4j(topic = "MailBoxes")
class MailBoxes{
    private static Map<Integer,GuardedObject> boxes = new ConcurrentHashMap<>();
    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    // 产生唯一id
    public static int generateId(){
        return atomicInteger.incrementAndGet();
    }

    // 创建一个GuardedObject
    public static GuardedObject createGuardedObjec(){
        GuardedObject guardedObject = new GuardedObject(generateId());
        boxes.put(guardedObject.getId(),guardedObject);
        return guardedObject;
    }

    // 获取指定的GuardedObjec
    public static GuardedObject getGuardedObjec(int i){
        return  boxes.remove(i);
    }

    // 获取所有id
    public static Set<Integer> getIds(){
        return boxes.keySet();
    }
}

@Slf4j(topic = "People")
class People extends Thread{
    @Override
    public void run() {
        GuardedObject guardedObjec = MailBoxes.createGuardedObjec();
        log.debug("开始收信id:{}",guardedObjec.getId());
        Object mail = guardedObjec.get(5000);
        log.debug("收到信id:{},内容是{}",guardedObjec.getId(),mail);
    }
}

@Slf4j(topic = "Postman")
class Postman extends Thread{

    private int id;

    private String mail;

    public Postman(int id,String mail){
        this.id = id;
        this.mail = mail;
    }

    @Override
    public void run() {
        GuardedObject guardedObjec = MailBoxes.getGuardedObjec(id);
        log.debug("开始送信id:{}，内容:{}",id,mail);
        guardedObjec.complete(mail);
    }
}
