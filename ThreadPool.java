package blogDemo.ThreadDemo;

public interface ThreadPool {

    /*线程池执行任务*/
    void execute(Runnable runnable);

    /*关闭线程池*/
    void shutDown();

    /*获取线程池初始化大小*/
    int getInitSize();

    /*获取线程池的最大数*/
    int getMaxSize();

    /*获取任务队列的大小*/
    int getQueueSize();

    /*获取线程池活跃的线程数*/
    int getActivityCount();

    /*判断线程池是否关闭*/
    boolean isShutDown();

    /*获取线程池中的核心数*/
    int getCoreSize();
}
