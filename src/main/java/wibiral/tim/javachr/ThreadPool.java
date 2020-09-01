package wibiral.tim.javachr;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

class ThreadPool {
    private final Thread[] threads;
    private final Worker[] workers;
    private final Semaphore[] semaphores;

    private final BlockingQueue<Runnable> pending = new LinkedBlockingQueue<>();

    ThreadPool(int workerTreads){
        this.threads = new Thread[workerTreads];
        this.workers = new Worker[workerTreads];
        this.semaphores = new java.util.concurrent.Semaphore[workerTreads];

        for (int i = 0; i < workerTreads; i++) {
            semaphores[i] = new Semaphore(1);
            try {
                semaphores[i].acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            workers[i] = new Worker(pending, semaphores[i]);
            threads[i] = new Thread(workers[i]);
            threads[i].start();
        }
    }

    boolean execute(Runnable runnable){
        try {
            pending.put(runnable);

        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        for(Semaphore semaphore : semaphores)
            semaphore.release();

        return true;
    }

    void block() throws InterruptedException {
        for(Semaphore semaphore : semaphores)
            semaphore.acquire();
    }

    void release(){
        for(Semaphore semaphore : semaphores)
            semaphore.release();
    }

    void kill(){
        for (Worker worker : workers)
            worker.kill();

        for(Semaphore semaphore : semaphores)
            semaphore.release();
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
