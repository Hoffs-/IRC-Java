/**
 * Created by Hoffs-Laptop on 2016-09-13.
 */
import java.util.concurrent.*;

public class ThreadPoolFixed {

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());
    /*
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(5);
    }
    */

    public Future startThread(Runnable Thread) {
        System.out.print("Starting thread for " + Thread.toString() + "\n");
        return executor.submit(Thread);
    }

    public void stopExecutor() {
        this.executor.shutdownNow();
    }

    public int getUsedThreads() {
        return executor.getActiveCount();
    }
}
