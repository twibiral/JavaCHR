package wibiral.tim.javachr;

import org.junit.Test;

import static org.junit.Assert.*;

public class ThreadPoolTest {

    @Test
    public void execute() throws InterruptedException {
        ThreadPool pool = new ThreadPool(8);
        for (int i = 0; i < 8; i++) {
            int finalI = i;
            pool.execute(() -> {
                int x = 0;
                while (x < Integer.MAX_VALUE)
                    x++;

                System.out.println("Thread " + finalI + " terminated!");
            });
        }

        while(!pool.isTerminated());
        System.out.println("Terminated!");

        while (true);
    }

    @Test
    public void kill() {
    }
}