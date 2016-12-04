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
import client.utils.PointsHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

class Points extends Command implements Runnable {
    private static final String ADD = "add";
    private static final String REMOVE = "remove";
    private static final String GET = "get";
    private static final String ADDALL = "addall";
    private PointsHelper pts = new PointsHelper();
    private Map<String, String> commands = new HashMap<>();

    Points(Message msg, LinkedBlockingQueue<MessageOut> mq, Logger logger) throws IOException {
        super(msg, mq, logger);
        this.InitializeCommands();
        logger.write("Created.", "Points");
    }

    private void InitializeCommands() {
        commands.put(s.getLocalized("Points").get("command_add"), "add");
        commands.put(s.getLocalized("Points").get("command_remove"), "remove");
        commands.put(s.getLocalized("Points").get("command_get"), "get");
        commands.put(s.getLocalized("Points").get("command_addall"), "addall");
    }

    @Override
    public void run() {
        logger.write("Running.", "Points");
        String[] indices = this.m.getMessage().split(" ");
        if (indices.length < 2) {
            this.mq.offer(new MessageOut(this.m.getChannel(), s.getLocalized("Points").get("available")));
            return;
        }
        if (this.commands.containsKey(this.m.getMessage().split(" ", 3)[1])) {
            logger.write("Starting command: " + this.m.getMessage().split(" ", 2)[1], "Points");
            switch (this.commands.get(this.m.getMessage().split(" ", 3)[1])) {
                case ADD:
                    this.setPermissionLevel(s.getLocalized("Points", "Add").get("permission_level"));
                    if (this.isAllowed()) this.AddPoints();
                    break;
                case REMOVE:
                    this.setPermissionLevel(s.getLocalized("Points", "Remove").get("permission_level"));
                    if (this.isAllowed()) this.RemovePoints();
                    break;
                case GET:
                    this.setPermissionLevel(s.getLocalized("Points", "Get").get("permission_level"));
                    if (this.isAllowed()) this.GetPoints();
                    break;
                case ADDALL:
                    this.setPermissionLevel(s.getLocalized("Points", "AddAll").get("permission_level"));
                    if (this.isAllowed()) this.AutoPoints();
                    break;
                default:
                    break;
            }
        }
        logger.write("Finished.", "Points");
    }

    private void AutoPoints() {
        try {
            if (this.m.getMessage().split(" ").length > 2) {
                new AutoPoints(this.m, this.mq, this.logger, Integer.parseInt(this.m.getMessage().split(" ", 3)[2])).run();
            } else {
                new AutoPoints(this.m, this.mq, this.logger).run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void AddPoints() { // !points add <user> <amount> || !points add <amount>
        String[] indices = this.m.getMessage().split(" ");
        int amount;
        String user;
        if (indices.length > 2) {
            if (indices.length > 3) { // Adds to other user
                user = this.m.getMessage().split(" ", 4)[2];
                amount = Integer.parseInt(this.m.getMessage().split(" ", 4)[3]);
            } else {
                user = this.m.getDisplayName();
                amount = Integer.parseInt(this.m.getMessage().split(" ", 3)[2]);
            }
            int status = pts.AddPoints(user, amount, this.m.getChannel().toLowerCase());
            if (status != 0) {
                mq.offer(new MessageOut(this.m.getChannel(), s.getLocalized("Points", "Add").get("message")));
            } else {
                mq.offer(new MessageOut(this.m.getChannel(), s.getLocalized("Points").get("error")));
            }
        }
    }

    private void GetPoints() { // !points get <user> || !points get
        String[] indices = this.m.getMessage().split(" ");
        String user = this.m.getDisplayName();
        if (indices.length > 2) {
            user = this.m.getMessage().split(" " , 3)[2];
        }
        String points = pts.GetPoints(user, this.m.getChannel().toLowerCase());
        mq.offer(new MessageOut(this.m.getChannel(), String.format(s.getLocalized("Points", "Get").get("message"), user, points)));
    }

    private void RemovePoints() {
        String[] indices = this.m.getMessage().split(" ");
        String user = this.m.getDisplayName();
        int amount;
        if (indices.length > 2) {
            if (indices.length > 3) { // Removes from other user
                user = this.m.getMessage().split(" ", 4)[2];
                amount = Integer.parseInt(this.m.getMessage().split(" ", 4)[3]);
            } else {
                amount = Integer.parseInt(this.m.getMessage().split(" ", 3)[2]);
            }
            int status = pts.RemovePoints(user, amount, this.m.getChannel().toLowerCase());
            if (status != 0) {
                mq.offer(new MessageOut(this.m.getChannel(), s.getLocalized("Points", "Remove").get("message")));
            } else {
                mq.offer(new MessageOut(this.m.getChannel(), s.getLocalized("Points").get("error")));
            }
        }
    }

    public String toString() {
        return "Points";
    }
}
