package wibiral.tim.javachr;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

class Worker implements Runnable {
    private final BlockingQueue<Runnable> pending;

    private boolean dead = false;
    private boolean hasExercise = false;

    private final ReentrantLock lock = new ReentrantLock();

    Worker(BlockingQueue<Runnable> pending) {
        this.pending = pending;
    }

    /**
     * Kills the worker -> wont execute anymore exercises.
     */
    void kill(){
        dead = true;
    }

    void lock(){
        lock.lock();
    }

    void unlock(){
        if(lock.isHeldByCurrentThread())
            lock.unlock();
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
                lock.lockInterruptibly();

            } catch (InterruptedException e) {
                // Interrupted when pool shuts down while waiting for lock.
                // Worker is dead -> stops execution after continue.
                hasExercise = false;
                continue;
            }

            if(!pending.isEmpty()){
                hasExercise = true;
                try {
                    Runnable exercise = pending.poll(1, TimeUnit.MILLISECONDS);
                    if (exercise != null){
                        exercise.run();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    hasExercise = false;
                }

            } else {
                hasExercise = false;
            }

            lock.unlock();
        }
    }
}
