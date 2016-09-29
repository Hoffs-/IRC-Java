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

package client;

public class Message {
    private String raw = null;
    private String rawType = null;
    private String rawTags = null;

    private Tags messageTags = null;

    private String displayName = null;
    private String channel = null;
    private String messageType = null;
    private String message = null;
    private String user = null;

    Message(String m) {
        this.raw = m;
        parseMessage();
    }

    private void parseMessage() {
        if (this.raw != null) {
            if (this.raw.startsWith("PING")) { // If message is PING
                this.messageType = "SERVER_PING";
                this.rawType = "PING";
            }
            if (this.raw.startsWith(":")) { // If it's server message
                String[] messageParts = this.raw.split(" ", 4);
                if (messageParts[0].contains("twitchnotify")) { // If it's a new subscriber
                    String[] chatParts = messageParts[3].substring(1).split(" ", 2);
                    this.messageType = "CHANNEL_SUBSCRIBER_NEW";
                    this.channel = messageParts[2].substring(1);
                    this.message = messageParts[3].substring(1);
                    this.displayName = chatParts[0];
                } else if (messageParts[1].contains("CLEARCHAT")) { // If it's a CLEARCHAT message (Timeout/Ban/Clear)
                    this.messageType =  "CHANNEL_CLEAR_CHAT";
                    this.rawType = messageParts[1];
                    this.channel = messageParts[2].substring(1);
                } else {
                    this.messageType = "SERVER_RAW";
                }
            }
            if (this.raw.startsWith("@")) {
                String[] messageParts = this.raw.split(" ", 5);
                this.rawType = messageParts[2];
                this.channel = messageParts[3].substring(1);
                this.rawTags = messageParts[0].substring(1);
                this.messageTags = new Tags(rawTags);
                this.displayName = this.getDisplayName(messageTags);
                switch (messageParts[2]) {
                    case "ROOMSTATE":
                        parseROOMSTATE(messageParts);
                        break;
                    case "USERSTATE":
                        parseUSERSTATE(messageParts);
                        break;
                    case "USERNOTICE":
                        parseUSERNOTICE(messageParts);
                        break;
                    case "NOTICE":
                        parseNOTICE(messageParts);
                        break;
                    case "PRIVMSG":
                        parsePRIVMSG(messageParts);
                        break;
                    case "CLEARCHAT":
                        parseCLEARCHAT(messageParts);
                        break;
                    default:
                        System.out.println("Invalid Message Type in switch statement");
                }
            }
        }
    }

    private void parseROOMSTATE(String[] messageParts) { // On join or changing chat mode
        this.messageType = "CHANNEL_STATE";
    }

    private void parseUSERSTATE(String[] messageParts) { // On join
        this.messageType = "USER_STATE";
    }

    private void parseUSERNOTICE(String[] messageParts) { // Only resubscribing?
        this.messageType = "USER_RESUBSCRIBING";
    }

    private void parseNOTICE(String[] messageParts) {
        this.messageType = "CHANNEL_NOTICE";
    }

    private void parsePRIVMSG(String[] messageParts) {
        this.messageType = "CHANNEL_MESSAGE";
        this.message = messageParts[4].substring(1);
        this.user = messageParts[1].substring(1).split("!")[0];
    }

    private void parseCLEARCHAT(String[] messageParts) {
        if (this.messageTags.getBanDuration() > 0) {
            this.messageType = "CHAT_TIMEOUT";
        } else if (this.messageTags.getBanDuration() == -1) {
            this.messageType = "CHAT_BAN";
        }
    }

    private String getDisplayName(Tags t) {
        String n;
        if ((t.getDisplayName() == null) && this.raw.contains("!")) {
            String[] messageParts = this.raw.split(" ", 3);
            String[] nameParts = messageParts[1].split("!", 2);
            n = nameParts[1].substring(1);
        } else {
            n = t.getDisplayName();
        }
        return n;
    }

    public String getMessage() {
        return message;
    }

    public String getUser() {
        return user;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getChannel() {
        return channel;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRaw() {
        return raw;
    }

    public String getRawType() {
        return rawType;
    }

    public String getRawTags() {
        return rawTags;
    }

    public Tags getMessageTags() {
        return messageTags;
    }
}
