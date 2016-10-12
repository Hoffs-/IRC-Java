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

import client.apis.TwitchAPI;
import client.utils.Message;
import client.utils.MessageOut;
import client.utils.PointsHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class AutoPoints extends Command {
    private PointsHelper pts = new PointsHelper();
    private int ptsToAdd = s.getPointsToAdd();
    private boolean customPts = false;

    AutoPoints(Message msg, LinkedBlockingQueue<MessageOut> mq) throws IOException {
        super(msg, mq);
    }

    public AutoPoints() throws IOException {
    }

    AutoPoints(Message msg, LinkedBlockingQueue<MessageOut> mq, int pts) throws IOException {
        super(msg, mq);
        this.ptsToAdd = pts;
        customPts = true;
    }

    @Override
    public void run() {
        TwitchAPI api = new TwitchAPI(s.getClientid(), s.getOauth());
        if (customPts) {
            this.process(this.m.getChannel(), api);
        } else {
            this.ptsToAdd = s.getPointsToAdd();
            ArrayList<String> arr = s.getChannels();
            for (String chan:arr) {
                if (api.isLive(chan)) this.process(chan, api);
            }
        }
    }

    private void process(String chan, TwitchAPI api) {
        JsonObject js = api.GET("http://tmi.twitch.tv/group/user/" + chan + "/chatters").get("chatters").getAsJsonObject();
        this.addPoints(js.get("moderators").getAsJsonArray(), ptsToAdd, chan);
        this.addPoints(js.get("staff").getAsJsonArray(), ptsToAdd, chan);
        this.addPoints(js.get("admins").getAsJsonArray(), ptsToAdd, chan);
        this.addPoints(js.get("global_mods").getAsJsonArray(), ptsToAdd, chan);
        this.addPoints(js.get("viewers").getAsJsonArray(), ptsToAdd, chan);
    }

    private void addPoints(JsonArray arr, int add, String chan) {
        for (int i = 0; i < arr.size(); i++) {
            String user = arr.get(i).getAsString();
            pts.AddPoints(user, add, chan);
        }
    }

}
