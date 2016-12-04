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

class TimeoutMe extends Command {

    TimeoutMe(Message msg, LinkedBlockingQueue<MessageOut> mq, Logger logger) throws IOException {
        super(msg, mq, logger);
        this.setPermissionLevel("user");
        logger.write("Created.", "TimeoutMe");
    }

    public void run() {
        logger.write("Running with: channel = " + this.m.getChannel() + ", user = " + this.m.getDisplayName() + ", message = " + this.m.getMessage(), "TimeoutMe");
        if (this.isAllowed() && (this.m.getMessage().split(" ").length > 1)) mq.offer(new MessageOut(this.m.getChannel(), "/timeout " + this.m.getDisplayName() + " " + this.m.getMessage().split(" ", 2)[1]));
        else {
            mq.offer(new MessageOut(this.m.getChannel(), "Invalid command format. Usage !timeoutme <duration>."));
        }
        logger.write("Finished.", "TimeoutMe");
    }

    public String toString() {
        return "BanMe";
    }
}

