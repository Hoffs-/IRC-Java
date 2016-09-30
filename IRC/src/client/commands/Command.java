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
import client.Tags;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class Command { // Default template for Commands
    Settings s = Settings.getSettings();
    protected String user = s.getUser();
    protected String token = s.getToken();
    protected String messageType = "CHAT";
    protected Message m;
    protected LinkedBlockingQueue<MessageOut> mq;
    protected String permissionLevel = "user";

    public Command(Message msg, LinkedBlockingQueue<MessageOut> mq) throws IOException {
        permissionLevel = "user";
        this.m = msg;
        this.mq = mq;
    }

    protected boolean isAllowed() {
        Tags curruser = this.m.getMessageTags();
        boolean can = false;
        boolean isBroadcaster = false;
        if (curruser.getDisplayName().toLowerCase().equals(this.m.getChannel().toLowerCase())) isBroadcaster = true;
        switch (permissionLevel) {
            case "user":
                can = true;
                break;
            case "subscriber":
                if (curruser.isSubscriber() || curruser.isMod() || isBroadcaster) can = true;
                break;
            case "mod":
                if (curruser.isMod() || isBroadcaster) can = true;
                break;
            case "broadcaster":
                if (isBroadcaster) can = true;
                break;
        }
        return can;
    }

    protected void setPermissionLevel(String perm) {
        this.permissionLevel = perm;
    }

    protected void setMessageType(String type) {
        this.messageType = type;
    }

}
