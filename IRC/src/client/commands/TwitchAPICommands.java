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
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

class TwitchAPICommands extends Command {

    TwitchAPICommands(Message msg, LinkedBlockingQueue<MessageOut> mq) throws IOException {
        super(msg, mq);
        this.setPermissionLevel("creator");
  }

    @Override
    public void run() {
        if (!this.isAllowed()) return;
        if (this.m.getMessage().startsWith("!game")) {
            String[] arr = this.m.getMessage().split(" ", 2);

            if (arr.length > 1) {
                JsonObject innerbody = new JsonObject();
                innerbody.addProperty("game", this.m.getMessage().split(" ", 2)[1]);
                JsonObject obj = new JsonObject();
                obj.add("channel", innerbody);
                System.out.println(obj.toString());
                TwitchAPI api = new TwitchAPI(s.getClientid(), s.getOauth());
                api.PUT("https://api.twitch.tv/kraken/channels/" + this.m.getChannel(), obj.toString());
            }
            else {
                TwitchAPI api = new TwitchAPI(s.getClientid(), s.getOauth());
                this.mq.offer(new MessageOut(this.m.getChannel().toLowerCase(), api.GET("https://api.twitch.tv/kraken/channels/" + this.m.getChannel()).get("game").toString()));
            }
        }
    }
}

