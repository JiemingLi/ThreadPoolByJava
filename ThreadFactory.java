package blogDemo.ThreadDemo;

public interface ThreadFactory {
    //创建一个新的线程
    Thread createThread(Runnable runnable);
}
