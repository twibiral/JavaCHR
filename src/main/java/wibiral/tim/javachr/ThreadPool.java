package wibiral.tim.javachr;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class ThreadPool {
    private final Thread[] threads;

    private final Worker[] workers;
    private final BlockingQueue<Runnable> pending = new LinkedBlockingQueue<>();

    ThreadPool(int workerTreads){
        this.threads = new Thread[workerTreads];
        this.workers = new Worker[workerTreads];

        for (int i = 0; i < workerTreads; i++) {
            workers[i] = new Worker(pending);
            workers[i].lock();
            threads[i] = new Thread(workers[i]);
            threads[i].start();
        }
    }

    boolean execute(Runnable runnable){
        for(Worker worker : workers)
            worker.unlock();

        try {
            pending.put(runnable);

        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    void block() {
        for(Worker worker : workers)
            worker.lock();
    }

    void release(){
        for(Worker worker : workers)
            worker.unlock();
    }

    void kill(){
        for (Worker worker : workers)
            worker.kill();

        for(Thread t : threads)
            t.interrupt();
    }

    boolean isTerminated(){
        if(!pending.isEmpty())
            return false;

        for(Worker worker : workers){
            if(worker.isWorking())
                return false;
        }

        return true;
    }
}
