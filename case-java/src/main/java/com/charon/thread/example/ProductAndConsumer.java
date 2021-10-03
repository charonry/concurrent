package com.charon.thread.example;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description 生产者和消费者
 * @Author tangkang
 * @Date 2021/7/10 15:46
 * @Version 1.0
 **/
@Slf4j(topic = "ProductAndConsumer")
public class ProductAndConsumer {
    public static void main(String[] args) {
        MessageQueue messageQueue = new MessageQueue(2);

        /*for (int i = 0 ;i<3;i++){
            int id = i+1;
            new Thread(()->{
                Message message = new Message(id,"这是第"+(id)+"个消息");
                messageQueue.put(message);
            },"生产者"+(i+1)).start();
        }*/
        AtomicInteger atomicInteger = new AtomicInteger(0);
        new Thread(()->{
            while (true){
                try {
                    TimeUnit.SECONDS.sleep(1);
                    int i = atomicInteger.incrementAndGet();
                    messageQueue.put(new Message(i,"这是第"+(i)+"个消息"));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"生产者").start();

        new Thread(()->{
            while (true){
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                messageQueue.take();
            }
        },"消费者").start();
    }
}


@Slf4j(topic = "MessageQueue")
class MessageQueue{
    // 消息的队列集合
    private LinkedList<Message> list = new LinkedList();
    // 队列容量
    private  int capcity;

    public MessageQueue() {
    }

    public MessageQueue(int capcity) {
        this.capcity = capcity;
    }

    // 获取消息
    public Message take(){
        synchronized (list){
            while (list.isEmpty()){
                try {
                    log.debug("队列为空, 消费者线程等待");
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 从列头获取消息返回
            Message message = list.removeFirst();
            log.debug("已消费消息 {}", message);
            list.notify();
            return  message;
        }
    }

    // 存入消息
    public void put(Message message){
        synchronized (list){
            while (list.size() == capcity){
                try {
                    log.debug("队列已满, 生产者线程等待");
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 将消息加入到队列尾部
            list.addLast(message);
            log.debug("已生产消息 {}", message);
            list.notify();
        }
    }

}

@Slf4j(topic = "Message")
final  class Message{
    private int id;
    private Object value;

    public Message() {
    }

    public Message(int id, Object value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", value=" + value +
                '}';
    }
}
