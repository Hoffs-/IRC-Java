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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

public class ProcessingThread implements Runnable{

    private LinkedBlockingQueue<Message> messageQueueIn = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<MessageOut> messageQueueOut = new LinkedBlockingQueue<>();
    private Future ircFuture;
    private ThreadPoolCached poolCached;
    private Commands commands;
    private Logger processingLogger = new Logger("PROCESSING_LOG", "PROCESSING_THREAD");
    private Map<String, LinkedBlockingQueue<Message>> queueMap = new HashMap<>();
    private Map<String, Map<String, Long>> cooldownMap = new HashMap<>();

    public ProcessingThread(LinkedBlockingQueue<Message> m, LinkedBlockingQueue<MessageOut> n, Future k, ThreadPoolCached th) throws IOException {
        this.messageQueueIn = m;
        this.messageQueueOut = n;
        this.ircFuture = k;
        this.poolCached = th;
        this.commands = new Commands();
        this.initializeQueues();
        this.initializeCooldownMap();
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
        Future raceFuture = null;
        while (!ircFuture.isDone()) {
            try {
                Message m = messageQueueIn.take();
                processingLogger.write("Got message: " + m.getMessage() + ", of type: " + m.getMessageType());
                if (Objects.equals(m.getMessageType(), "SERVER_PING")) {
                    MessageOut out = new MessageOut("null", "PONG " + m.getRaw().substring(5) + "\r\n", "RAW");
                    messageQueueOut.offer(out);
                }
                if (Objects.equals(m.getMessageType(), "CHANNEL_MESSAGE") && m.getMessage().startsWith("!")) { // Might be a command, submit it to another thread.
                    Runnable run = commands.getCommand(m, messageQueueOut, queueMap, cooldownMap);
                    if (run != null) {
                        boolean canCreateRaceThread = false;
                        if (Objects.equals(run.toString(), "racegame")) {
                            if (raceFuture == null) canCreateRaceThread = true;
                            else if (raceFuture.isDone()) canCreateRaceThread = true;
                        }
                        if (Objects.equals(run.toString(), "racegame") && canCreateRaceThread && m.getMessage().startsWith("!race prepare")) {
                            queueMap.get("raceQueue").clear();
                            raceFuture = poolCached.startThread(run);
                        } else if (!Objects.equals(run.toString(), "racegame")) {
                            poolCached.startThread(run);
                        }
                    }
                    if (raceFuture != null) {if (m.getMessage().startsWith("!race") && !raceFuture.isDone()) {queueMap.get("raceQueue").offer(m);}}
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initializeQueues() {
        queueMap.put("raceQueue", new LinkedBlockingQueue<Message>());
    }

    private void initializeCooldownMap() {
        cooldownMap.put("gamble", new HashMap<>());
    }

    public String toString(){
        return "Processing Thread";
    }

}
