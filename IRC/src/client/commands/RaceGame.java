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
import client.utils.PointsHelper;
import client.utils.Settings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

class RaceGame extends Command implements Runnable {
    private Socket socket;
    private LinkedList<String> players = new LinkedList<>();
    private int prizepool = 0;
    private int joinPrice = 0;
    private boolean isStarted;
    private LinkedBlockingQueue<Message> mQueue;

    RaceGame(Message msg, LinkedBlockingQueue<MessageOut> mq, Map<String, LinkedBlockingQueue<Message>> mpq) throws IOException {
        super(msg, mq);
        this.mQueue = mpq.get("raceQueue");
    }

    public void run() {
        this.isStarted = false;
        while (true) {
            try {
                this.m = mQueue.take();
                String messageIndices[] = m.getMessage().split(" ");
                if (messageIndices.length > 1) {
                    if (messageIndices[1].startsWith(Settings.getSettings().getLocalized("Race").get("command_prepare"))) {
                        setPermissionLevel(Settings.getSettings().getLocalized("Race").get("permission_prepare"));
                        if (isAllowed() && !this.isStarted) this.prepareRace();
                    } else if (messageIndices[1].startsWith(Settings.getSettings().getLocalized("Race").get("command_join"))) {
                        setPermissionLevel(Settings.getSettings().getLocalized("Race").get("permission_join"));
                        if (isAllowed() && this.isStarted) this.joinRace();
                    } else if (messageIndices[1].startsWith("debug")) {
                        mq.offer(new MessageOut(m.getChannel(), "Race info: Players = " + players.size() + ", Join price = " + this.joinPrice + ", Price pool = " + this.prizepool + ". isStarted = " + this.isStarted));
                    } else if (messageIndices[1].startsWith("stop")) {
                        this.isStarted = false;
                        this.socket.close();
                    } else if (messageIndices[1].startsWith(Settings.getSettings().getLocalized("Race").get("command_start"))) {
                        setPermissionLevel(Settings.getSettings().getLocalized("Race").get("permission_start"));
                        if (isAllowed()) this.startRace();
                    }
                }
                if (!this.isStarted) break;
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void prepareRace() {
        String indices[] = m.getMessage().split(" ");
        if (indices.length > 2) {
            try {
                this.joinPrice = Integer.parseInt(indices[2]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            this.joinPrice = 25;
        }
        this.isStarted = true;
        this.socketSetup();
    }

    private void joinRace() {
        if (!this.players.contains(this.m.getDisplayName())) {
            PointsHelper pts = new PointsHelper();
            if (Integer.parseInt(pts.GetPoints(m.getDisplayName(), m.getChannel())) >= this.joinPrice) {
                pts.RemovePoints(m.getDisplayName(), this.joinPrice, m.getChannel());
                this.prizepool += this.joinPrice;
                this.players.add(m.getDisplayName());
                String mout = "{ channel : '" + m.getChannel() + "', user: '" + m.getDisplayName() + "'}";
                this.socket.emit("join", new JsonParser().parse(mout).getAsJsonObject());
            }
        }
    }

    private void startRace() {
        if (this.players.isEmpty()) {
            mq.offer(new MessageOut(m.getChannel(), "Can't start a race without any players!"));
        } else {
            String mout = "{ channel : '" + m.getChannel() + "'}";
            this.socket.emit("start", new JsonParser().parse(mout).getAsJsonObject());
            this.isStarted = false;
            mq.offer(new MessageOut(m.getChannel(), "Race started! Good luck to everyone! Prize pool: " + prizepool));
        }
    }

    private void socketSetup() {
        try {
            this.socket = IO.socket("http://localhost:3000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        this.socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                String mout = "{ channel : '" + m.getChannel() + "'}";
                socket.emit("prepare", new JsonParser().parse(mout).getAsJsonObject());
                mq.offer(new MessageOut(m.getChannel(), "Race is about to begin! Write '!race join' to participate. Entry cost: " + joinPrice));
            }

        }).on("results", new Emitter.Listener() {
            
            @Override
            public void call(Object... args) {
                PointsHelper pts = new PointsHelper();
                JsonParser parser = new JsonParser();
                JsonObject obj = parser.parse(args[0].toString()).getAsJsonObject();
                String channel = obj.get("channel").getAsString();
                String winner = parser.parse(obj.get("results").toString().replace("\\", "").substring(1, obj.get("results").toString().replace("\\", "").length()-1)).getAsJsonArray().get(0).toString();
                pts.AddPoints(winner.substring(1, winner.length()-1), prizepool, channel);
                mq.offer(new MessageOut(channel, winner.substring(1, winner.length()-1) + " won the race and took " + prizepool + " points home."));
                socket.close();
                isStarted = false;
                Thread.currentThread().interrupt();
            }
            
        });
        this.socket.connect();
    }

    public String toString() {
        return "racegame";
    }
}
