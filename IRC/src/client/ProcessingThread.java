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

import client.commands.AutoPoints;
import client.commands.Commands;
import client.utils.Logger;
import client.utils.Message;
import client.utils.MessageOut;
import client.utils.Settings;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.*;

public class ProcessingThread implements Runnable{

    private LinkedBlockingQueue<Message> messageQueueIn = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<MessageOut> messageQueueOut = new LinkedBlockingQueue<>();
    private Future ircFuture;
    private ThreadPoolCached poolCached;
    private Commands commands;
    private Logger processingLogger = new Logger("PROCESSING_LOG", "PROCESSING_THREAD");

    public ProcessingThread(LinkedBlockingQueue<Message> m, LinkedBlockingQueue<MessageOut> n, Future k, ThreadPoolCached th) throws IOException {
        this.messageQueueIn = m;
        this.messageQueueOut = n;
        this.ircFuture = k;
        this.poolCached = th;
        this.commands = new Commands();
    }

    @Override
    public void run() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        try {
            Runnable pts = new AutoPoints();
            scheduledExecutorService.scheduleAtFixedRate(pts, 0, Settings.getSettings().getInterval(), TimeUnit.MINUTES);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!ircFuture.isDone()) {
            try {
                Message m = messageQueueIn.take();
                processingLogger.write("Got message: " + m.getMessage() + ", of type: " + m.getMessageType());
                if (Objects.equals(m.getMessageType(), "SERVER_PING")) {
                    MessageOut out = new MessageOut("null", "PONG " + m.getRaw().substring(5) + "\r\n", "RAW");
                    messageQueueOut.offer(out);
                }
                if (Objects.equals(m.getMessageType(), "CHANNEL_MESSAGE") && m.getMessage().startsWith("!")) { // Might be a command, submit it to another thread.
                    Runnable run = commands.getCommand(m, messageQueueOut);
                    if (run != null) poolCached.startThread(run);
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
