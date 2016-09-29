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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Tags {
    private List<String> emotes = new ArrayList<>();
    private List<String> badges = new ArrayList<>();

    private String rawTags = null;
    private String color = null;
    private String displayName = null;
    private String roomId = null;
    private String userId = null;
    private String msgId = null;
    private String login = null;
    private String systemMsg = null;
    private String id = null;
    private String language = null;
    private String banReason = null;
    private String timestamp = null;
    private String timestampTmi = null;

    private int msgParamMonths = 0;
    private int slow = 0;
    private int banDuration = 0;

    private boolean isMod = false;
    private boolean isSubscriber = false;
    private boolean isTurbo = false;
    private boolean isEmoteOnly = false;
    private boolean isR9k = false;
    private boolean isSubscriberOnly = false;


    Tags(String m) {
        this.rawTags = m;
        parseTags();
    }

    public boolean isMod() {
        return isMod;
    }

    public boolean isSubscriber() {
        return isSubscriber;
    }

    public boolean isTurbo() {
        return isTurbo;
    }

    public String[] getBadgesArray() {
        return new String[badges.size()];
    }

    public int getMsgParamMonths() {
        return msgParamMonths;
    }

    public String getId() {
        return id;
    }

    public String getSystemMsg() {
        return systemMsg;
    }

    public String getLogin() {
        return login;
    }

    public String getMsgId() {
        return msgId;
    }

    public String getUserId() {
        return userId;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColor() {
        return color;
    }

    public boolean isEmoteOnly() {
        return isEmoteOnly;
    }

    public boolean isR9k() {
        return isR9k;
    }

    public boolean isSubscriberOnly() {
        return isSubscriberOnly;
    }

    public int getSlow() {
        return slow;
    }

    public String getLanguage() {
        return language;
    }

    public String getBanReason() {
        return banReason;
    }

    public int getBanDuration() {
        return banDuration;
    }

    public String getTimestampTmi() {
        return timestampTmi;
    }

    public String getTimestamp() {
        return timestamp;
    }

    private void parseTags() {
        String tagSplit[] = this.rawTags.split(";");
        for (String tags : tagSplit) {
            String[] tag = tags.split("=");
            if (tag.length == 1) continue;
            switch (tag[0]) {
                case "badges":
                    String[] badgesSplit = tag[1].split(",");
                    Collections.addAll(this.badges, badgesSplit[0].split("/")[0]);
                    break;
                case "color":
                    this.color = tag[1];
                    break;
                case "display-name":
                    this.displayName = tag[1];
                    if (this.userId == null) this.userId = tag[1];
                    break;
                case "emotes":
                    String[] emotesSplit = tag[1].split("/");
                    Collections.addAll(this.emotes, emotesSplit);
                    break;
                case "id":
                    this.id = tag[1];
                    break;
                case "room-id":
                    this.roomId = tag[1];
                    break;
                case "subscriber":
                    if (tag[1].equals("1")) this.isSubscriber = true;
                    break;
                case "turbo":
                    if (tag[1].equals("1")) this.isTurbo = true;
                    break;
                case "mod":
                    if (tag[1].equals("1")) this.isMod = true;
                    break;
                case "user-id":
                    this.userId = tag[1];
                    if (this.displayName == null) this.userId = tag[1];
                    break;
                case "user-type":
                    //Do nothing.
                    break;
                case "login":
                    this.login = tag[1];
                    break;
                case "msg-id":
                    this.msgId = tag[1];
                    break;
                case "msg-param-months":
                    this.msgParamMonths = Integer.parseInt(tag[1]);
                    break;
                case "broadcaster-lang":
                    this.language = tag[1];
                    break;
                case "emote-only":
                    if (tag[1].equals("1")) this.isEmoteOnly = true;
                    break;
                case "r9k":
                    if (tag[1].equals("1")) this.isR9k = true;
                    break;
                case "slow":
                    this.slow = Integer.parseInt(tag[1]);
                    break;
                case "subs-only":
                    if (tag[1].equals("1")) this.isSubscriberOnly = true;
                    break;
                case "system-msg":
                    this.systemMsg = tag[1];
                    break;
                case "ban-duration":
                    this.banDuration = Integer.parseInt(tag[1]);
                    break;
                case "ban-reason":
                    this.banReason = tag[1];
                    if (this.banDuration == 0) this.banDuration = -1;
                    break;
                case "tmi-sent-ts":
                    this.timestampTmi = tag[1];
                    break;
                case "sent-ts":
                    this.timestamp = tag[1];
                    break;
                case "emote-sets":
                    break;
                default:
                    System.out.println("[TAGS][PARSING ERROR] Undefined tag: " + tag[0]);
            }
        }
    }
}
