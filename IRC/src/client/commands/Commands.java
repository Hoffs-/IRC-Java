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

package client.commands;

import client.Message;
import client.MessageOut;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class Commands implements Runnable{
    private static final String CREATOR = "creator";
    private static final String TITLE = "title";
    private static final String GAME = "game";
    private static final String ME = "me";
    private static final String SPAM = "baconspam";
    private static final String BANME = "banme";
    private Message m;
    private LinkedBlockingQueue<MessageOut> mQ = new LinkedBlockingQueue<>();

    public Commands(Message msg, LinkedBlockingQueue<MessageOut> mm) {
        this.m = msg;
        this.mQ = mm;
    }

    @Override
    public void run() {
        try {
            switch (this.m.getMessage().split(" ", 2)[0].substring(1)) {
                case CREATOR:
                    new Creator(this.m, this.mQ);
                    break;
                case TITLE:
                    break;
                case GAME:
                    break;
                case ME:
                    break;
                case SPAM:
                    new BaconSpam(this.m, this.mQ);
                    break;
                case BANME:
                    new BanMe(this.m, this.mQ);
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
