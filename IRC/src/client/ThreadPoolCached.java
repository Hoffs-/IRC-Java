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

import client.utils.Logger;

import java.io.IOException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolCached {
    private Logger poolLogger;

    public ThreadPoolCached(Logger log) {
        this.poolLogger = log;
    }

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<>());

    public void startThread(Runnable Thread) throws IOException {
        poolLogger.write("Starting thread for " + Thread.toString() + ", active threads: " + this.getUsedThreads(), "CACHED");
        executor.execute(Thread);
    }

    public void stopExecutor() {
        executor.shutdown();
    }

    public int getUsedThreads() {
        return executor.getActiveCount();
    }
}
