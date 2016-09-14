/**
 * Created by Hoffs-Laptop on 2016-09-13.
 */
import java.util.concurrent.*;

public class ThreadPoolCached {

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());
    /*
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(5);
    }
    */

    public void startThread(Runnable Thread) {
        System.out.print("Starting thread for " + Thread.toString() + "\n");
        executor.execute(Thread);
    }

    public void stopExecutor() {
        executor.shutdown();
    }

    public int getUsedThreads() {
        return executor.getActiveCount();
    }
}
