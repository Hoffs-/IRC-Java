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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;


public abstract class HTTPClient {
    protected JsonParser parser = new JsonParser();
    protected JsonObject json = null;
    protected StringBuilder result = new StringBuilder();
    static final String GET = "GET";
    static final String PUT = "PUT";
    static final String DELETE = "DELETE";
    static final String POST = "POST";

    protected HttpURLConnection conn;

    public HTTPClient(String rUrl) {
        try {
            this.conn = (HttpURLConnection) (new URL(rUrl + "?ts=" + Instant.now().getEpochSecond())).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract JsonObject GET();

    public abstract JsonObject PUT(String body);

    public abstract JsonObject DELETE();

    public abstract JsonObject POST(String body);
}

/*
public class HTTPClient {
    private URL url = null;
    private HttpURLConnection conn; //= (HttpURLConnection) url.openConnection();
    private String urlParams = null;
    private Settings settings;
    private String clientid;
    private String oauth;

    public HTTPClient(String requestUrl) {
        try {
            url = new URL(requestUrl);
            this.conn = (HttpURLConnection) url.openConnection();
            settings = Settings.getSettings();
            this.clientid = settings.getClientid();
            this.oauth = settings.getOauth();
            this.conn.setRequestProperty("Accept", "application/vnd.twitchtv.v3+json");
            this.conn.setRequestProperty("Client-ID", this.clientid);
            this.conn.setRequestProperty("Authorization", "OAuth " + this.oauth);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JsonObject GET(String params) {
        JsonElement json = null;
        JsonParser parser = new JsonParser();
        try {
            this.urlParams = params;
            this.conn.setRequestMethod("GET");
            InputStream in = this.conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) { result.append(line); }
            String newResponse = result.toString().replace("\\", ""); // Removes escape characters from the string.
            json = parser.parse(newResponse).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json.getAsJsonObject();
    }

    public JsonObject PUT(String params) {
        JsonElement json = null;
        JsonParser parser = new JsonParser();
        try {
            this.urlParams = params;
            this.conn.setDoOutput(true);
            this.conn.setRequestMethod("PUT");
            this.conn.setRequestProperty("content-type", "application/json");
            this.conn.setRequestProperty("Content-Length", Integer.toString(params.length()));
            OutputStreamWriter out = new OutputStreamWriter(this.conn.getOutputStream(), StandardCharsets.UTF_8);
            out.write(params);
            out.flush();
            System.out.println(this.conn.getResponseCode());
            System.out.println(this.conn.getResponseMessage());
            InputStream in = this.conn.getErrorStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            System.out.println(this.conn.getResponseCode());
            String line;
            while ((line = reader.readLine()) != null) { result.append(line); }
            String newResponse = result.toString().replace("\\", ""); // Removes escape characters from the string.
            json = parser.parse(newResponse).getAsJsonObject();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json.getAsJsonObject();
    }
}
*/