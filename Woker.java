package blogDemo.ThreadDemo;

/*工作线程的任务*/
public class Woker implements Runnable{
    /*任务队列，方便后面取出任务*/
    private RunnableQueue runnableQueue;

    /*方便判断该内部任务对应的线程是否运行，确保可见性!*/
    private volatile boolean running = true;

    public Woker(RunnableQueue runnableQueue) {
        this.runnableQueue = runnableQueue;
    }

    @Override
    public void run() {
        /*当前对应的线程正在运行并且没有被中断*/
        while (running && !Thread.currentThread().isInterrupted()){
            Runnable task = runnableQueue.take();
            task.run();
        }
    }

    public void stop(){
        running = false;
    }

}
