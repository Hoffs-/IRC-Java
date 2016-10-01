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


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Settings {
    private File fi = new File("settings/config.json");
    private File fo = new File("settings");
    private JsonObject settings;
    private String user;
    private String token;
    private ArrayList<String> channels = new ArrayList<>();

    private Settings() throws IOException {
        this.getSettingsFromJson();
    }

    public static synchronized Settings getSettings() throws IOException {
        if (ref == null) {
            ref = new Settings();
        }
        return ref;
    }

    public synchronized String getUser() {
        return this.user;
    }

    public synchronized String getToken() {
        return this.token;
    }

    public synchronized ArrayList<String> getChannels() {
        return this.channels;
    }

    private void getSettingsFromJson() throws IOException {
        JsonParser parser = new JsonParser();
        if (!this.fo.exists() || !this.fo.exists() || !(this.fi.length() > 0)) {
            this.writeDefault();
        }
        JsonElement obj = parser.parse(new FileReader("settings/config.json"));
        this.settings = obj.getAsJsonObject();
        if (this.settings.toString().contains("null")) {
            System.out.println("Please set up your settings first");
            System.exit(0);
        } else {
            this.user = this.settings.get("username").getAsString();
            this.token = this.settings.get("token").getAsString();
            JsonArray arr = this.settings.get("channels").getAsJsonArray();
            for (JsonElement chan : arr) {
                channels.add(chan.getAsString());
            }
        }
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    private void writeDefault() throws IOException {
        this.fo.mkdir();
        FileWriter set = new FileWriter(this.fi);
        JsonParser parser = new JsonParser();
        JsonObject defaultSettings = parser.parse("{\n" +
                "  \"username\" : \"null\",\n" +
                "  \"token\" : \"null\",\n" +
                "  \"channels\": [\n" +
                "    \"ricknotastley\"\n" +
                "  ]\n" +
                "}").getAsJsonObject();
        set.write(defaultSettings.toString());
        set.flush();
        set.close();
        System.out.println("Please set up your settings first");
        System.exit(0);
    }

    private static Settings ref;
}