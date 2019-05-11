package blogDemo.ThreadDemo;

import java.util.LinkedList;

public class LinkRunnableQueue implements RunnableQueue{

    private int limit;

    private DenyPolicy denyPolicy;

    private ThreadPool threadPool;


    private LinkedList<Runnable> queue = new LinkedList<>();


    public LinkRunnableQueue(int queueSize,DenyPolicy denyPolicy,ThreadPool pool) {
        this.limit = queueSize;
        this.denyPolicy = denyPolicy;
        this.threadPool = pool;
    }

    @Override
    public void offer(Runnable runnable) {
        synchronized (queue){
        if(queue.size() > limit){
            denyPolicy.reject(runnable,threadPool);
        }else{
            queue.addLast(runnable);
            queue.notifyAll();
        }
    }
}

    @Override
    public Runnable take() {
        synchronized (queue){
            while (queue.size() == 0){
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return queue.removeFirst();
        }

    }

    @Override
    public int size() {
       synchronized (queue){
           return queue.size();
       }
    }
}
