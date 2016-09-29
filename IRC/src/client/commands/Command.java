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
import client.Settings;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class Command { // Default template for Commands
    Settings s = Settings.getSettings();
    private String user = s.getUser();
    private String token = s.getToken();
    private String messageType = "CHAT";
    private Message m;
    private LinkedBlockingQueue<MessageOut> mq;

    public Command(Message msg, LinkedBlockingQueue<MessageOut> mq) throws IOException {
        this.m = msg;
        this.mq = mq;
    }

}
