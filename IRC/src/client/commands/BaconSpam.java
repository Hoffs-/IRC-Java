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

public class BaconSpam extends Command{

    public BaconSpam(Message msg, LinkedBlockingQueue<MessageOut> mq) throws IOException {
        super(msg, mq);
        this.setPermissionLevel("creator");
        if (this.isAllowed()) {
            try {
                int numb = Integer.parseInt(msg.getMessage().split(" ", 3)[1]);
                int i = 0;
                while (i < numb) {
                    mq.offer(new MessageOut(msg.getChannel(), msg.getMessage().split(" ", 3)[2]));
                    i++;
                }
            } catch (NumberFormatException e) {
                mq.offer(new MessageOut(msg.getChannel(), msg.getDisplayName() + " Invalid format. (!baconspam <amount> <message>"));
            }
        }
    }
}
