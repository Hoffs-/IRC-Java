/*
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *        Copyright $year Ignas Maslinskas
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package client;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class ProcessingThread implements Runnable{

    private LinkedBlockingQueue<Message> messageQueueIn = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<String> messageQueueOut = new LinkedBlockingQueue<>();
    private Future ircFuture;
    private ThreadPoolCached poolCached;
    private Logger processingLogger = new Logger("PROCESSING_LOG", "PROCESSING_THREAD");

    public ProcessingThread(LinkedBlockingQueue<Message> m, LinkedBlockingQueue<String> n, Future k, ThreadPoolCached th) throws IOException {
        this.messageQueueIn = m;
        this.messageQueueOut = n;
        this.ircFuture = k;
        this.poolCached = th;
    }

    @Override
    public void run() {
        while (!ircFuture.isDone()) {
            try {
                Message m = messageQueueIn.take();
                processingLogger.write("Got message: " + m.getMessage() + ", of type: " + m.getMessageType());
                if (Objects.equals(m.getMessageType(), "SERVER_PING")) {
                    messageQueueOut.offer("PONG " + m.getRaw().substring(5) + "\r\n");
                }
                if (Objects.equals(m.getMessageType(), "CHANNEL_MESSAGE") && m.getMessage().startsWith("!")) { // Might be a command, submit it to another thread.
                    //
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String toString(){
        return "Processing Thread";
    }

}
