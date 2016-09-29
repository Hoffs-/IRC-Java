/*
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *        Copyright 2016 Ignas Maslinskas
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package client;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolFixed {
    private Logger poolLogger;

    public ThreadPoolFixed(Logger log) {
        this.poolLogger = log;
    }

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());

    public Future startThread(Runnable Thread) throws IOException {
        poolLogger.write("Starting thread for " + Thread.toString() + ", active threads: " + this.getUsedThreads(), "FIXED");
        return executor.submit(Thread);
    }

    public void stopExecutor() {
        this.executor.shutdownNow();
    }

    public int getUsedThreads() {
        return executor.getActiveCount();
    }
}
