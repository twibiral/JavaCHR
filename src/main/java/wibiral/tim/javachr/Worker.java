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
                continue;
            }

            if(!pending.isEmpty()){
                try {
                    Runnable exercise = pending.poll(50, TimeUnit.MILLISECONDS);
                    if (exercise != null){
                        hasExercise = true;
                        exercise.run();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                hasExercise = false;
            }

            lock.unlock();
        }
    }
}
