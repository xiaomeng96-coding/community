package com.nowcoder.community;


import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author : zhiHao
 * @since : 2022/5/28
 */
public class BlockQueueTest {

    public static void main(String[] args) {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);
        new Thread(new Producer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
    }

    private static class Producer implements Runnable {

        private final BlockingQueue<Integer> queue;
        public Producer (BlockingQueue<Integer> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 100; i++) {
                    Thread.sleep(20);
                    queue.put(i);
                    System.out.println(Thread.currentThread().getName() + "produce :" + queue.size());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class Consumer implements Runnable {

        private final BlockingQueue<Integer> queue;
        public Consumer (BlockingQueue<Integer> queue){
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(new Random().nextInt(1000));
                    queue.take();
                    System.out.println(Thread.currentThread().getName() + "consumer :" + queue.size());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
