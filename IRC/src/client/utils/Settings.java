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


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;

public class Settings {
    private File fi = new File("settings/config.json");
    private File fo = new File("settings");
    private JsonObject settings;
    private String user;
    private String token;
    private String clientid;
    private String oauth;
    private int interval = 15;
    private int pointsToAdd = 0;
    private ArrayList<String> channels = new ArrayList<>();
    private CommandsSettings commandsSettings;
    private boolean isCommandsOn = false;

    private Settings() {
        this.getSettingsFromJson();
        commandsSettings = CommandsSettings.getCommandSettings();
    }

    public static synchronized Settings getSettings() {
        if (ref == null) {
            ref = new Settings();
        }
        return ref;
    }

    public synchronized Map<String, String> getLocalized(String token) { return this.commandsSettings.getMap(token); }

    public synchronized Map<String, String> getLocalized(String token1, String token2) { return this.commandsSettings.getMap(token1, token2); }

    public synchronized String getUser() {
        return this.user;
    }

    public synchronized String getToken() {
        return this.token;
    }

    public synchronized String getClientid() {
        return this.clientid;
    }

    public synchronized String getOauth() {
        return this.oauth;
    }

    public synchronized ArrayList<String> getChannels() {
        return this.channels;
    }

    public synchronized int getInterval() { return interval; }

    public synchronized int getPointsToAdd() {
        return pointsToAdd;
    }

    public synchronized void setPointsToAdd(int pointsToAdd) {
        this.pointsToAdd = pointsToAdd;
    }

    public synchronized boolean getIsCommandsOn() { return this.isCommandsOn; }

    public synchronized void setIsCommandsOn(boolean chk) { this.isCommandsOn = chk; }

    private void getSettingsFromJson() {
        JsonParser parser = new JsonParser();
        if (!this.fo.exists() || !this.fo.exists() || !(this.fi.length() > 0)) {
            this.writeDefault();
        }
        JsonElement obj = null;
        try {
            obj = parser.parse(new FileReader("settings/config.json"));
            this.settings = obj.getAsJsonObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (this.settings.toString().contains("null")) {
            System.out.println("Please set up your settings first");
            System.exit(0);
        } else {
            this.user = this.settings.get("username").getAsString();
            this.token = this.settings.get("token").getAsString();
            this.clientid = this.settings.get("clientid").getAsString();
            this.oauth = this.settings.get("oauth").getAsString();
            this.pointsToAdd = this.settings.get("point_increment").getAsInt();
            this.interval = this.settings.get("point_interval").getAsInt();
            JsonArray arr = this.settings.get("channels").getAsJsonArray();
            if (this.settings.get("commands_enabled").getAsString().equals("true")) this.isCommandsOn = true;
            for (JsonElement chan : arr) {
                channels.add(chan.getAsString());
            }
        }
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    private void writeDefault() {
        this.fo.mkdir();
        FileWriter set;
        try {
            set = new FileWriter(this.fi);
            JsonParser parser = new JsonParser();
            JsonObject defaultSettings = parser.parse("{\"username\":\"\",\"token\":\"oauth:\", \"clientid\":\"\", \"oauth\":\"\", \"point_increment\":\"10\", \"point_interval\":\"15\", \"channels\":[]}").getAsJsonObject();
            set.write(defaultSettings.toString());
            set.flush();
            set.close();
            System.out.println("Please set up your settings first");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    private static Settings ref;
}