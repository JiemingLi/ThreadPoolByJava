package blogDemo.ThreadDemo;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class BaseThreadPool extends Thread implements ThreadPool {

    /*初始化线程数*/
    private int initSize;

    /*最大工作线程数*/
    private int maxSize;

    /*核心线程数*/
    private int coreSize;

    /*当前活跃线程数*/
    private  int activityCount = 0;

    /*指定任务队列的大小数*/
    private int queueSize;


    /*创建工作线程的工厂*/
    private ThreadFactory threadFactory;

    /*任务队列*/
    private RunnableQueue runnableQueue;

    //工作线程队列
    private final static Queue<ThreadTask> threadQueue = new ArrayDeque<>();

    //本线程池默认的拒绝策略
    private final static DenyPolicy DEFAULT_DENY_POLICY = new DenyPolicy.IgnoreDenyPolicy();

    /*默认的线程工厂*/
    private final static ThreadFactory DEFAULT_THREAD_FACTORY =new DefaultThreadFactory();

    /*线程池是否关闭，默认为false*/
    boolean isShutdown = false;

    private  long keepAliveTime;

    private  TimeUnit timeUnit ;

    public BaseThreadPool(int initSize, int maxSize, int coreSize,int queueSize) {
        this(initSize,maxSize,coreSize,queueSize,DEFAULT_THREAD_FACTORY,DEFAULT_DENY_POLICY,10,TimeUnit.SECONDS);
    }

    public BaseThreadPool(int initSize, int maxSize, int coreSize, int queueSize, ThreadFactory threadFactory, DenyPolicy denyPolicy, long keepAliveTime, TimeUnit timeUnit) {
        this.initSize = initSize;
        this.maxSize = maxSize;
        this.coreSize = coreSize;
        this.threadFactory = threadFactory;
        //自定义任务的队列
        this.runnableQueue = new LinkRunnableQueue(queueSize,denyPolicy,this);
        this.keepAliveTime = keepAliveTime;
        this.timeUnit = timeUnit;
        this.init();
    }


    public void init(){

        /*启动本线程池*/
        this.start();

        /*初始化initSize个线程在线程池中*/
        for (int i = 0; i < initSize; i++) {
            newThread();
        }
    }

    public void newThread(){
        /*创建工作线程，然后让工作线程等待任务到来被唤醒*/
        Woker woker = new Woker(runnableQueue);
        Thread thread = threadFactory.createThread(woker);

        /*将线程和任务包装在一起*/
        ThreadTask threadTask = new ThreadTask(thread,woker);
        threadQueue.offer(threadTask);
        this.activityCount++;
        /*启动刚才新建的线程*/
        thread.start();
    }

    //把工作线程和内部任务绑定在一起
    class ThreadTask{
        Thread thread;
        Woker woker;
        public ThreadTask(Thread thread, Woker woker) {
            this.thread = thread;
            this.woker = woker;
        }
    }

//   线程池中去掉某个工作线程
    public void removeThread(){
        this.activityCount--;
        ThreadTask task = threadQueue.remove();
        task.woker.stop();
    }


    @Override
    public void run() {
        while (!isShutdown && !isInterrupted()){
            try {
                timeUnit.sleep(keepAliveTime);
            } catch (InterruptedException e) {
               //到这里就是关闭线程池了
                isShutdown = true;
                continue;
            }
//          这里同步代码块，保证了每次访问的时候都是最新的数据！
            synchronized (this){
                if(isShutdown) break;
                //任务队列不为空，并且当前可以工作的线程小于coreCount，那么说明工作
                // 线程数不够，就先增加到maxSize.
                //比如说coreSize 为20，initSize为10，maxSize 为30,
                //突然一下子来了20分线程进来，但是工作线程只有15个，由于某种原因可能
                //那15个工作现场还没执行完，那么此时的任务队列肯定还有剩余的，发现此
                //时线程还没到coreSize
                //那么就直接开maxSize个线程先把
                //如果发现现在工作的的线程已经过了coreSize就先不增加线程数啦
                if(runnableQueue.size() > 0 && activityCount < coreSize){
                    for (int i = runnableQueue.size(); i < maxSize; i++) {
                        newThread();
                    }
                }
//                任务队列为空，并且当前可以工作的线程数大于coreCount，工作线程数太多啦！那么就减少到coreCount
                if(runnableQueue.size() == 0 &&  activityCount > coreSize){
                    for (int i = coreSize; i < activityCount; i++) {
                        removeThread();
                    }
                }
            }
        }
    }

    /*也就是将任务加入到任务队列中*/
    @Override
    public void execute(Runnable runnable) {
        if(this.isShutdown){
            throw new IllegalStateException("the pool is shutdown...");
        }

        this.runnableQueue.offer(runnable);
    }

    /*shutdown 就要把 Woker 给停止 和 对应的线程给中断*/
    @Override
    public void shutDown() {
        synchronized (this){
            if(isShutDown())
                return;
            isShutdown = true;
            /*全部线程停止工作*/
            for (ThreadTask task: threadQueue
                 ) {
                task.woker.stop();
                task.thread.interrupt();
            }
        }
    }

    @Override
    public int getInitSize() {
        if(this.isShutdown){
            throw new IllegalStateException("the pool is shutdown...");
        }
        return initSize;
    }

    @Override
    public int getMaxSize() {
        if(this.isShutdown){
            throw new IllegalStateException("the pool is shutdown...");
        }
        return maxSize;
    }

    @Override
    public int getQueueSize() {
        if(this.isShutdown){
            throw new IllegalStateException("the pool is shutdown...");
        }
        return queueSize;
    }

    @Override
    public int getActivityCount() {
        if(this.isShutdown){
            throw new IllegalStateException("the pool is shutdown...");
        }
        return this.activityCount;
    }

    @Override
    public boolean isShutDown() {
        if(this.isShutdown){
            throw new IllegalStateException("the pool is shutdown...");
        }
        return isShutdown;
    }

    @Override
    public int getCoreSize() {
        return coreSize;
    }
}
