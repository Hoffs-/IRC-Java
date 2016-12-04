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
import client.utils.MessageOut;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class WriterThread implements Runnable {

    private String user;
    private String token;
    private Socket irc;
    private BufferedWriter outputIRC;
    private LinkedBlockingQueue<MessageOut> messageQueue = new LinkedBlockingQueue<>();
    private Future future;
    private Logger writerLogger = new Logger("WRITER_LOG", "WRITER_THREAD");
    private Logger chatLogger;
    private ArrayList<String> channels;

    public WriterThread(String user, String token, Socket c, LinkedBlockingQueue<MessageOut> queue, ArrayList<String> arr, Future f, Logger ch) {
        this.user = user;
        this.token = token;
        this.irc = c;
        this.messageQueue = queue;
        this.channels = arr;
        this.future = f;
        this.chatLogger = ch;
    }

    @Override
    public void run() {
        try {
            outputIRC = new BufferedWriter(new OutputStreamWriter(irc.getOutputStream()));
            authenticate();
            this.join(this.user);
            channels.forEach(this::join);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        long RateTime = Instant.now().getEpochSecond();
        int messageCount = 0;
        int messageLimit = 60;
        while (!future.isDone() || !messageQueue.isEmpty()) {
            try {
                MessageOut m = messageQueue.take();
                messageCount++;
                if (messageCount >= messageLimit && RateTime+40 > Instant.now().getEpochSecond()) {
                    long timeToSleep = RateTime+40 - Instant.now().getEpochSecond();
                    Thread.currentThread();
                    Thread.sleep((timeToSleep+5)*1000);
                }
                if (RateTime+40 < Instant.now().getEpochSecond()) {RateTime = Instant.now().getEpochSecond(); messageCount = 0;}
                writerLogger.write(m + " | LEFT IN QUEUE: " + messageQueue.size(), "RECEIVED");
                if (m.getType().equals("RAW")) this.sendRaw(m.getMessage());
                    else {this.sendMessage(m);}
                if (m.getType().equals("RAW")) writerLogger.write(m.getMessage().substring(0, m.getMessage().length()-4), "SENT");
                    else writerLogger.write(m.getMessage(), "SENT");
                if (!m.getMessage().contains("PONG")) chatLogger.write(String.format("[%s] <%s>: %s", m.getChannel(), this.user, m.getMessage()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void authenticate() {
        try {
            outputIRC.write("PASS " + this.token + "\r\n");
            outputIRC.write("NICK " + this.user + "\r\n");
            outputIRC.write("CAP REQ :twitch.tv/commands\r\n");
            outputIRC.write("CAP REQ :twitch.tv/tags\r\n");
            outputIRC.flush();
            writerLogger.write("Authenticated with the server", "AUTHENTICATION");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void join(String channel) {
        try {
            outputIRC.write("JOIN #" + channel + "\r\n");
            outputIRC.flush();
            writerLogger.write("Joined channel #" + channel, "JOIN");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(MessageOut message) {
        try {
            outputIRC.write("PRIVMSG #" + message.getChannel() + " :" + message.getMessage() + "\n");
            outputIRC.flush();
            System.out.printf("[%s] <%s>: %s%n", message.getChannel(), this.user, message.getMessage());
            writerLogger.write("to #" + message.getChannel() + ": " + message.getMessage(), "PRIVMSG");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRaw(String message) {
        try {
            outputIRC.write(message);
            outputIRC.flush();
            writerLogger.write(message.substring(0, message.length()-4), "RAW_MESSAGE");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void quit() {
        try {
            outputIRC.write("QUIT Leaving...\r\n");
            outputIRC.flush();
            writerLogger.write("Disconnected from the server.", "QUIT");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String toString(){
        return "WRITER THREAD";
    }
}