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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class CommandsSettings {
    private File fi = new File("settings/commands.json");
    private File fo = new File("settings");
    private JsonObject commandsObj = null;

    private CommandsSettings() throws IOException {
        this.parseJson();
    }

    static synchronized CommandsSettings getCommandSettings() throws IOException {
        if (ref == null) {
            ref = new CommandsSettings();
        }
        return ref;
    }

    synchronized Map<String, String> getMap(String key) {
        JsonObject arr = this.commandsObj.get(key).getAsJsonObject();
        return makeMap(arr);
    }

    synchronized Map<String, String> getMap(String key1, String key2) {
        JsonObject arr = this.commandsObj.get(key1).getAsJsonObject().get(key2).getAsJsonObject();
        return makeMap(arr);
    }

    private void parseJson() throws IOException {
        JsonParser parser = new JsonParser();
        if (!this.fo.exists() || !this.fo.exists() || !(this.fi.length() > 0)) {
            this.writeDefault();
        }
        JsonElement obj = parser.parse(new FileReader("settings/commands.json"));
        this.commandsObj = obj.getAsJsonObject();
        if (this.commandsObj.toString().contains("null")) {
            System.out.println("Please set up your command settings first");
            System.exit(0);
        }
    }

    private Map<String, String> makeMap(JsonObject arr) {
        Map<String, String> mp = new HashMap<>();
        Set<Map.Entry<String, JsonElement>> entries = arr.entrySet();
        for (Map.Entry<String, JsonElement> entry: entries) {
            mp.put(entry.getKey(), entry.getValue().toString().substring(1, entry.getValue().toString().length()-1));
        }
        return mp;
    }

    private void writeDefault() throws IOException {
        this.fo.mkdir();
        FileWriter set = new FileWriter(this.fi);
        JsonParser parser = new JsonParser();
        String defaultLocalization = "{\n" +
                "  \"Commands\": {\n" +
                "    \"points\": \"!points\",\n" +
                "    \"creator\": \"!creator\",\n" +
                "    \"banme\": \"!banme\",\n" +
                "    \"timeoutenemy\": \"!timeoutenemy\",\n" +
                "    \"baconspam\": \"!baconspam\",\n" +
                "    \"game\": \"!game\",\n" +
                "    \"title\": \"!title\"\n" +
                "  },\n" +
                "  \"Points\": {\n" +
                "    \"command_add\" : \"add\",\n" +
                "    \"command_remove\" : \"remove\",\n" +
                "    \"command_get\" : \"get\",\n" +
                "    \"command_addall\" : \"addall\",\n" +
                "    \"available\" : \"Available commands are: !points <add|remove|get|addall>\",\n" +
                "    \"Get\" : {\n" +
                "      \"permission_level\" : \"user\",\n" +
                "      \"message\" : \"%s has %s points!\"\n" +
                "    },\n" +
                "    \"Add\" : {\n" +
                "      \"permission_level\" : \"broadcaster\",\n" +
                "      \"message\" : \"Points successfully added!\"\n" +
                "    },\n" +
                "    \"Remove\" : {\n" +
                "      \"permission_level\" : \"broadcaster\",\n" +
                "      \"message\" : \"Points successfully removed!\"\n" +
                "    },\n" +
                "    \"AddAll\" : {\n" +
                "      \"permission_level\" : \"broadcaster\"\n" +
                "    },\n" +
                "    \"error\" : \"Something went wrong!\"\n" +
                "  },\n" +
                "  \"Creator\": {\n" +
                "    \"permission_level\" : \"creator\",\n" +
                "    \"message\" : \"My creator and owner is Hoffs!\"\n" +
                "  },\n" +
                "  \"TimeoutEnemy\": {\n" +
                "    \"permission_level\" : \"user\",\n" +
                "    \"initiator\" : \"%s 360 Sacrifice had to be made.\",\n" +
                "    \"enemy\" : \"%s 90 You have an enemy.\"\n" +
                "  }\n" +
                "}";
        JsonObject defaultSettings = parser.parse(defaultLocalization).getAsJsonObject();
        set.write(defaultSettings.toString());
        set.flush();
        set.close();
        System.out.println("Please set up your settings first");
        System.exit(0);
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    private static CommandsSettings ref;
}