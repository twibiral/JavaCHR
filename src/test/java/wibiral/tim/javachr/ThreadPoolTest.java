package wibiral.tim.javachr;

import org.junit.Test;
import wibiral.tim.javachr.constraints.Constraint;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.CheckedOutputStream;

import static org.junit.Assert.*;

public class ThreadPoolTest {

    @Test
    public void execute() throws InterruptedException {
        ThreadPool pool = new ThreadPool(4);
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
//        System.out.println("Terminated!");

        pool.block();
        System.out.println("Blocked!");
        pool.execute(() -> System.out.println("Executing after blocking!"));
        Thread.sleep(150);

        System.out.println("Releasing!");
//        Thread.sleep(150);
        pool.release();
//
        pool.block();
        System.out.println("Blocked!");

//        Thread.sleep(60 * 1000);
        System.out.println("Terminating...");
    }

    @Test
    public void kill() {
    }
}