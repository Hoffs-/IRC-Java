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

package client.apis;

import client.utils.HTTPClient;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Objects;

public class TwitchAPI extends HTTPClient {
    private String id = null;
    private String oauth = null;

    public TwitchAPI(String id, String oauth) {
        this.id = id;
        this.oauth = oauth;
    }

    @Override
    public JsonObject GET(String url) {
        String line;
        this.result.delete(0, this.result.length());
        HttpURLConnection conn = this.setupConn(url);
        try {
            conn.setRequestMethod("GET");
            InputStream in = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) { this.result.append(line); }
            this.json = parser.parse(this.result.toString().replace("\\", "")).getAsJsonObject();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        conn.disconnect();
        return json;
    }

    @Override
    public JsonObject PUT(String url, String params) {
        HttpURLConnection conn = this.setupConn(url);
        this.result.delete(0, this.result.length());
        try {
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Length", Integer.toString(params.length()));
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
            out.write(params);
            out.flush();
            out.close();
            InputStream in = conn.getErrorStream();
            if (in != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                String newResponse = result.toString().replace("\\", ""); // Removes escape characters from the string.
                json = parser.parse(newResponse).getAsJsonObject();
                in.close();
            } else {
                json.addProperty("status", "success");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        conn.disconnect();
        return json.getAsJsonObject();
    }

    @Override
    public JsonObject DELETE(String url) {
        this.result.delete(0, this.result.length());
        HttpURLConnection conn = this.setupConn(url);
        conn.disconnect();
        return null;
    }

    @Override
    public JsonObject POST(String url, String body) {
        this.result.delete(0, this.result.length());
        HttpURLConnection conn = this.setupConn(url);
        conn.disconnect();
        return null;
    }

    public boolean isLive(String channel) {
        JsonObject js = this.GET("https://api.twitch.tv/kraken/streams/" + channel);
        return (!Objects.equals(js.get("stream").toString(), "null"));
    }

    private HttpURLConnection setupConn(String url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) (new URL(url + "?ts=" + Instant.now().getEpochSecond())).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (conn != null) {
            conn.setRequestProperty("Client-ID", this.id);
            conn.setRequestProperty("Authorization", "OAuth " + this.oauth);
            conn.setRequestProperty("Accept", "application/vnd.twitchtv.v3+json");
        }
        return conn;
    }
}