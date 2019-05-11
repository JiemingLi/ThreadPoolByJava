package blogDemo.ThreadDemo;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {
    public static void main(String[] args) {
        ThreadPool threadPool = new BaseThreadPool(4,30,6,30);
        for (int i = 0; i < 20; i++) {
            threadPool.execute(() -> {
                System.out.println(Thread.currentThread().getName() + " is running and done.");
            });
        }
//        ThreadPool threadPool = new BaseThreadPool(2,6,4,100);
//        for (int i = 0;git i < 10; i++) {
//            threadPool.execute(new Runnable() {
//                @Override
//                public void run() {
//                    System.out.println(Thread.currentThread().getName() + " is running and done.");
//                }
//            });
//        }
    }
}
