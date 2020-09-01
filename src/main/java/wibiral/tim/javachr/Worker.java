package wibiral.tim.javachr;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

class Worker implements Runnable {
    private final BlockingQueue<Runnable> pending;
    private final Semaphore semaphore;

    private boolean dead = false;
    private boolean hasExercise = false;

    Worker(BlockingQueue<Runnable> pending, Semaphore lock) {
        this.pending = pending;
        this.semaphore = lock;
    }

    /**
     * Kills the worker -> wont execute anymore exercises.
     */
    void kill(){
        dead = true;
    }

    /**
     * @return true if the worker has an exercise to do.
     */
    boolean isWorking(){
        return hasExercise;
    }

    @Override
    public void run() {
        while(!dead){
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(!pending.isEmpty()){
                try {
                    Runnable exercise = pending.poll(10, TimeUnit.MILLISECONDS);
                    if (exercise != null){
                        hasExercise = true;
                        exercise.run();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                hasExercise = false;
            }
        }
    }
}
