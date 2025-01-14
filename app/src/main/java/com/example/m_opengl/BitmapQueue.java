package com.example.m_opengl;


import android.graphics.Bitmap;

import java.util.LinkedList;
import java.util.Queue;

public class BitmapQueue<T> {
    private final Queue<T> queue;
    private final int MAX_SIZE = 50; // 设置队列最大容量

    private static BitmapQueue m = null;

    public static BitmapQueue getInstance() {
        if (m == null) {
            m = new BitmapQueue();
        }
        return m;
    }

    public BitmapQueue() {
        this.queue = new LinkedList<>();
    }

    // 添加消息到队列
    public synchronized void enqueue(T message) {
        if (queue.size() >= MAX_SIZE) {
            // 如果队列已经满了，清理队列
            Bitmap removed = (Bitmap) queue.poll();
            if (removed != null) {
                //removed.recycle(); // 释放内存
            }
        }
        queue.add(message);
        //System.out.println("Message enqueued: " + message);
        notify(); // 唤醒等待的线程
    }

    // 从队列中获取消息
    public synchronized T dequeue()  {
        while (queue.isEmpty()) {
            try {
                wait(); // 队列为空时，等待消息
            } catch (InterruptedException e) {

            }
        }
        T message = queue.poll();
        //System.out.println("Message dequeued: " + message);
        return message;
    }

    // 检查队列是否为空
    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    // 获取队列大小
    public synchronized int size() {
        return queue.size();
    }

    // 清空队列
    public synchronized void clear() {
        queue.clear();
        //System.out.println("Queue cleared");
    }

}
