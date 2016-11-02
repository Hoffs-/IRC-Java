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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class Commands {
    private static final String CREATOR = "creator";
    private static final String TITLE = "title";
    private static final String GAME = "game";
    private static final String SPAM = "baconspam";
    private static final String BANME = "banme";
    private static final String TIMEOUTENEMY = "timeoutenemy";
    private static final String POINTS = "points";
    private Message m;
    private Map<String, String> commands = new HashMap<>();
    private LinkedBlockingQueue<MessageOut> mQ = new LinkedBlockingQueue<>();
    private Map<String, LinkedBlockingQueue<Message>> queueMap = new HashMap<>();

    public Commands() {
        this.InitializeLocalized();
    }

    private void InitializeLocalized() {
        try {
            Map<String, String> commandsLocalized = Settings.getSettings().getLocalized("Commands");
            commands.put(commandsLocalized.get("points"), "points");
            commands.put(commandsLocalized.get("creator"), "creator");
            commands.put(commandsLocalized.get("title"), "title");
            commands.put(commandsLocalized.get("game"), "game");
            commands.put(commandsLocalized.get("banme"), "banme");
            commands.put(commandsLocalized.get("timeoutenemy"), "timeoutenemy");
            commands.put(commandsLocalized.get("baconspam"), "baconspam");
            commands.put(commandsLocalized.get("points"), "points");
            commands.put("!race", "race");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Runnable getCommand(Message m, LinkedBlockingQueue<MessageOut> mq, Map<String, LinkedBlockingQueue<Message>> mpq) {
        this.m = m;
        this.mQ = mq;
        this.queueMap = mpq;
        try {
            if (commands.containsKey(this.m.getMessage().split(" ", 2)[0])) {
                //System.out.println(commands.get(this.m.getMessage().split(" ", 2)[0]));
                switch (commands.get(this.m.getMessage().split(" ", 2)[0])) { // Change to IF with localized names
                    case CREATOR:
                        return new Creator(this.m, this.mQ);//
                    case TITLE:
                    case GAME:
                        return new TwitchAPICommands(this.m, this.mQ);
                    case SPAM:
                        return new BaconSpam(this.m, this.mQ);//
                    case BANME:
                        return new AutoPoints(this.m, this.mQ);//
                    case TIMEOUTENEMY:
                        return new TimeoutEnemy(this.m, this.mQ);//
                    case POINTS:
                        return new Points(this.m, this.mQ);//
                    case "race":
                        return new RaceGame(this.m, this.mQ, this.queueMap);
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String toString() {
        return "CommandsFactory";
    }
}
