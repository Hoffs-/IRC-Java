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
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class InputThread implements Runnable {
    private String channel;
    private LinkedBlockingQueue<MessageOut> messageQueue = new LinkedBlockingQueue<>();
    private Future future;

    public InputThread(LinkedBlockingQueue<MessageOut> q, Future f) {
        this.messageQueue = q;
        this.future = f;
    }

    @Override
    public void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while(!future.isDone()) {
            try {
                String line = br.readLine();
                this.send(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void send(String line) {
        MessageOut snd;
        if (line.startsWith("#")) {
            String[] msgParts = line.split(" ", 2);
            this.channel = msgParts[0].substring(1);
            snd = new MessageOut(this.channel, msgParts[1]);
            this.messageQueue.offer(snd);
        } else {
            if (!(this.channel == null)) {
                snd = new MessageOut(this.channel, line);
                this.messageQueue.offer(snd);
            } else {
                System.out.println("[ERROR] No channel specified");
            }
        }

    }
}
