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

class BaconSpam extends Command{

    BaconSpam(Message msg, LinkedBlockingQueue<MessageOut> mq, Logger logger) throws IOException {
        super(msg, mq, logger);
        this.setPermissionLevel("creator");
        logger.write("Created.", "BaconSpam");
    }

    public void run() {
        logger.write("Started.", "BaconSpam");
        if (this.isAllowed()) {
            try {
                int numb = Integer.parseInt(this.m.getMessage().split(" ", 3)[1]);
                logger.write("Running with variables: " + numb + " " + this.m.getMessage().split(" ", 3)[2]);
                int i = 0;
                while (i < numb) {
                    mq.offer(new MessageOut(this.m.getChannel(), this.m.getMessage().split(" ", 3)[2]));
                    i++;
                }
            } catch (NumberFormatException e) {
                mq.offer(new MessageOut(this.m.getChannel(), this.m.getDisplayName() + " Invalid format. (!baconspam <amount> <message>)"));
            }
        }
        logger.write("Finished.", "BaconSpam");
    }

    public String toString() {
        return "BaconSpam";
    }
}
