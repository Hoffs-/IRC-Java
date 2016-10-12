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


public abstract class HTTPClient {
    protected JsonParser parser = new JsonParser();
    protected JsonObject json = new JsonObject();
    protected StringBuilder result = new StringBuilder();

    protected HTTPClient() {
    }

    public abstract JsonObject GET(String url);

    public abstract JsonObject PUT(String url, String body);

    public abstract JsonObject DELETE(String url);

    public abstract JsonObject POST(String url, String body);
}