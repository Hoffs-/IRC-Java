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

import client.*;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class Client {
    private String ircIp;
    private Socket socketIRC;
    private ReaderThread ircReader;
    private WriterThread ircWriter;
    private ProcessingThread ircProccesing;
    private ThreadPoolFixed poolFixed;
    private ThreadPoolCached poolCached;
    private LinkedBlockingQueue<Message> messageQueueIn = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<String> messageQueueOut = new LinkedBlockingQueue<>();
    private Settings settings;

    public Client(String ip, ThreadPoolFixed pool, ThreadPoolCached poolC) {
        this.ircIp = ip;
        this.poolFixed = pool;
        this.poolCached = poolC;
    }

    public void connect() {
        try {
            settings = Settings.getSettings();
            Future m;
            socketIRC = new Socket(ircIp, 6667);
            ircReader = new ReaderThread(socketIRC, messageQueueIn);
            m = poolFixed.startThread(ircReader);
            ircWriter = new WriterThread(settings.getUser(), settings.getToken(), socketIRC, messageQueueOut, settings.getChannels(), m);
            ircProccesing = new ProcessingThread(messageQueueIn, messageQueueOut, m, poolCached);
            poolFixed.startThread(ircWriter);
            poolFixed.startThread(ircProccesing);
        } catch (IOException e) {
            e.printStackTrace();
            poolFixed.stopExecutor();
            try {
                socketIRC.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
