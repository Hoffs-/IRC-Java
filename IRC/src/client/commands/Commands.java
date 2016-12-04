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
    private static final String RACE = "race";
    private static final String VERSION = "version";
    private static final String GAMBLE = "gamble";
    private static final String PAUSE = "pause";
    private static final String TIMEOUTME = "timeoutme";
    private static final String RESUME = "resume";
    private Logger logger;
    private Message m;
    private Map<String, String> commands = new HashMap<>();
    private LinkedBlockingQueue<MessageOut> mQ = new LinkedBlockingQueue<>();
    private Map<String, LinkedBlockingQueue<Message>> queueMap = new HashMap<>();
    private Map<String, Map<String, Long>> cooldownMap = new HashMap<>();

    public Commands() {
        logger = new Logger("COMMANDS_LOG", "COMMAND");
        this.InitializeLocalized();
    }

    private void InitializeLocalized() {
        Map<String, String> commandsLocalized = Settings.getSettings().getLocalized("Commands");
        commands.put(commandsLocalized.get("points"), "points");
        commands.put(commandsLocalized.get("creator"), "creator");
        commands.put(commandsLocalized.get("title"), "title");
        commands.put(commandsLocalized.get("game"), "game");
        commands.put(commandsLocalized.get("banme"), "banme");
        commands.put(commandsLocalized.get("timeoutenemy"), "timeoutenemy");
        commands.put(commandsLocalized.get("baconspam"), "baconspam");
        commands.put(commandsLocalized.get("points"), "points");
        commands.put(commandsLocalized.get("race"), "race");
        commands.put(commandsLocalized.get("gamble"), "gamble");
        commands.put("!version", "version");
        commands.put("!pause", "pause");
        commands.put("!resume", "resume");
        commands.put("!timeoutme", "timeoutme");
    }

    public Runnable getCommand(Message m, LinkedBlockingQueue<MessageOut> mq, Map<String, LinkedBlockingQueue<Message>> mpq, Map<String, Map<String, Long>> cd) {
        this.m = m;
        this.mQ = mq;
        this.queueMap = mpq;
        this.cooldownMap = cd;
        try {
            if (commands.containsKey(this.m.getMessage().split(" ", 2)[0])) {
                logger.write("Trying to start command: " + commands.get(this.m.getMessage().split(" ", 2)[0]));
                switch (commands.get(this.m.getMessage().split(" ", 2)[0])) { // Change to IF with localized names
                    case CREATOR:
                        return new Creator(this.m, this.mQ, this.logger);//
                    case TITLE:
                    case GAME:
                        return new TwitchAPICommands(this.m, this.mQ, this.logger);
                    case SPAM:
                        return new BaconSpam(this.m, this.mQ, this.logger);//
                    case BANME:
                        return new AutoPoints(this.m, this.mQ, this.logger);//
                    case TIMEOUTENEMY:
                        return new TimeoutEnemy(this.m, this.mQ, this.logger);//
                    case POINTS:
                        return new Points(this.m, this.mQ, this.logger);//
                    case RACE:
                        return new RaceGame(this.m, this.mQ, this.logger, this.queueMap.get("raceQueue"));
                    case VERSION:
                        return new Version(this.m, this.mQ, this.logger);
                    case GAMBLE:
                        return new Gamble(this.m, this.mQ, this.logger, this.cooldownMap.get("gamble"));
                    case PAUSE:
                        return new Pause(this.m, this.mQ, this.logger);
                    case RESUME:
                        return new Resume(this.m, this.mQ, this.logger);
                    case TIMEOUTME:
                        return new TimeoutMe(this.m, this.mQ, this.logger);
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
