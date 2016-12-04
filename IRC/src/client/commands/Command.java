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

import client.utils.*;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

abstract class Command implements Runnable { // Default template for Commands
    Settings s;
    protected String user;
    protected Message m;
    protected Logger logger;
    private String token;
    private String messageType = "CHAT";
    LinkedBlockingQueue<MessageOut> mq;
    private String permissionLevel = "user";

    Command() throws IOException {
        permissionLevel = "user";
        this.s = Settings.getSettings();
        user = s.getUser();
        token = s.getToken();
    }

    Command(Message msg, LinkedBlockingQueue<MessageOut> mq, Logger logger) throws IOException {
        permissionLevel = "user";
        this.m = msg;
        this.mq = mq;
        this.s = Settings.getSettings();
        this.logger = logger;
        user = s.getUser();
        token = s.getToken();
    }

    boolean isAllowed() {
        Tags curruser = this.m.getMessageTags();
        boolean can = false;
        boolean isBroadcaster = false;
        if (curruser.getUserId().toLowerCase().equals(this.m.getChannel().toLowerCase())) isBroadcaster = true;
        switch (permissionLevel) {
            case "user":
                can = true;
                break;
            case "subscriber":
                if (curruser.isSubscriber() || curruser.isMod() || isBroadcaster) can = true;
                if (this.m.getDisplayName().toLowerCase().equals("ricknotastley")) can = true;
                break;
            case "mod":
                if (curruser.isMod() || isBroadcaster) can = true;
                if (this.m.getDisplayName().toLowerCase().equals("ricknotastley")) can = true;
                break;
            case "broadcaster":
                if (isBroadcaster) can = true;
                if (this.m.getDisplayName().toLowerCase().equals("ricknotastley")) can = true;
                break;
            case "creator":
                if (this.m.getDisplayName().toLowerCase().equals("ricknotastley")) can = true;
            default:
                break;
        }
        return can;
    }

    void setPermissionLevel(String perm) {
        this.permissionLevel = perm;
    }

    protected void setMessageType(String type) {
        this.messageType = type;
    }

    @Override
    public void run() {

    }


}
