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

import client.utils.Message;
import client.utils.MessageOut;
import client.utils.Settings;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

class Resume extends Command {
    Resume(Message msg, LinkedBlockingQueue<MessageOut> mq) throws IOException {
        super(msg, mq);
    }

    @Override
    public void run() {
        try {
            Settings.getSettings().setIsCommandsOn(true);
            this.mq.offer(new MessageOut(this.m.getChannel(), "Bot command execution resumed"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return "resume";
    }
}
