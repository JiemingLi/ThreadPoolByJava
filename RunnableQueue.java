package blogDemo.ThreadDemo;

//任务队列
public interface RunnableQueue {
    //把新增进来的任务添加到任务队列中
    void offer(Runnable runnable);

    //从任务队列中获取任务，然后执行
    Runnable take();

    //查找任务队列的大小
    int size();
}
