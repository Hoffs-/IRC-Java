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

package client.utils;

public class MessageOut {
    private String channel = null;
    private String type = "CHAT";
    private String message = null;

    public MessageOut(String chan, String msg, String mType) {
        this.channel = chan;
        this.message = msg;
        this.type = mType;
    }

    public MessageOut(String chan, String msg) {
        this.channel = chan;
        this.message = msg;
    }

    public String getChannel() {
        return channel;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
