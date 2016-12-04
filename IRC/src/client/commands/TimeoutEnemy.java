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

import client.utils.Logger;
import client.utils.Message;
import client.utils.MessageOut;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

class TimeoutEnemy extends Command{

    TimeoutEnemy(Message msg, LinkedBlockingQueue<MessageOut> mq, Logger logger) throws IOException {
        super(msg, mq, logger);
        this.setPermissionLevel("user");
        logger.write("Created.", "TimeoutEnemy");
    }

    public void run() {
        logger.write("Running with: channel = " + this.m.getChannel() + ", user = " + this.m.getDisplayName() + ", message = " + this.m.getMessage(), "TimeoutEnemy");
        if (this.isAllowed()) {
            mq.offer(new MessageOut(this.m.getChannel(), String.format("/timeout "+"%s 360 Sacrifice had to be made.", this.m.getDisplayName())));
            String[] arrS = this.m.getMessage().split(" ", 3);
            if (arrS[1].startsWith("@")) arrS[1] = arrS[1].substring(1);
            mq.offer(new MessageOut(this.m.getChannel(), String.format("/timeout "+"%s 90 You have an enemy.", arrS[1])));
        }
        logger.write("Finished", "TimeoutEnemy");
    }

    public String toString() {
        return "TimeoutEnemy";
    }
}

