package blogDemo.ThreadDemo;


public interface DenyPolicy {
    void reject(Runnable runnable,ThreadPool pool);
    /*拒绝就是什么都不做*/
    class IgnoreDenyPolicy implements DenyPolicy{

        @Override
        public void reject(Runnable runnable, ThreadPool pool) {

        }
    }

    //该拒绝策略会向任务提交者抛出异常
    class AbortDenyPolicy implements DenyPolicy {

        @Override
        public void reject(Runnable runnable, ThreadPool threadPool) {
            throw new RuntimeException("The runnable " + runnable + "will be abort");

        }
    }

    /*拒绝策略就是如果线程池还没有关闭，该提交的线程完成先*/
    class RunnerDenyPolicy implements DenyPolicy {
        @Override
        public void reject(Runnable runnable, ThreadPool threadPool) {
            if (!threadPool.isShutDown()) {
                runnable.run();
            }

        }
    }

}




