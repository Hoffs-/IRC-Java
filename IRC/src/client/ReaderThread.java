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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class ReaderThread implements Runnable {

    private Socket irc;
    private Logger rawLogger = new Logger("RAW_READER_LOG", "RAW");
    private LinkedBlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();

    public ReaderThread(Socket c, LinkedBlockingQueue<Message> queue) {
        this.irc = c;
        this.messageQueue = queue;
    }

    @Override
    public void run() {
        try {
            String line;
            BufferedReader inputIRC = new BufferedReader(new InputStreamReader(irc.getInputStream()));
            while ((line = inputIRC.readLine( )) != null) {
                rawLogger.write(line, this.toString());
                Message chatMessage = new Message(line);
                messageQueue.offer(chatMessage);
                if (chatMessage.getMessage() != null) {
                    System.out.printf("[%s] <%s>: %s%n", chatMessage.getChannel(), chatMessage.getDisplayName(), chatMessage.getMessage());
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    public String toString(){
        return "READER THREAD";
    }
}