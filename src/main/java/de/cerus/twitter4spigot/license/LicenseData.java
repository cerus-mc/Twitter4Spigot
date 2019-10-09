/*
 *  Copyright (c) 2018 Cerus
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 * Cerus
 *
 */

package de.cerus.twitter4spigot.license;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class LicenseData {

    private static String userId = "%%__USER__%%";
    private static String resourceId = "%%__RESOURCE__%%";
    private static String nonce = "%%__NONCE__%%";
    private static String userName = "Unknown";

    private LicenseData() {
        throw new UnsupportedOperationException();
    }

    public static void fetchUserName() throws IOException {
        if (getUserId().equals("Unknown")) return;

        HttpURLConnection connection = (HttpURLConnection) new java.net.URL("https://api.spiget.org/v2/authors/" + getUserId()).openConnection();
        connection.setRequestProperty("User-Agent", "T4S-Plugin");
        connection.setRequestMethod("GET");
        connection.setDoInput(true);

        StringBuilder contentBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

/*            String s;
            while ((s = reader.readLine()) != null)
                contentBuilder.append(s);*/

        JsonObject object = new JsonParser().parse(/*contentBuilder.toString()*/reader).getAsJsonObject();
        userName = object.get("name").getAsString();
    }

    public static String getUserId() {
        // This has to be done in this hacky way because Spigot replaces these Strings
        return userId.replace(String.valueOf(new char[]{'%', '%', '_', '_', 'U', 'S', 'E', 'R', '_', '_', '%', '%'}), "Unknown");
    }

    public static String getResourceId() {
        // This has to be done in this hacky way because Spigot replaces these Strings
        return resourceId.replace(String.valueOf(new char[]{'%', '%', '_', '_', 'R', 'E', 'S', 'O', 'U', 'R', 'C', 'E', '_', '_', '%', '%'}), "Unknown");
    }

    public static String getNonce() {
        // This has to be done in this hacky way because Spigot replaces these Strings
        return nonce.replace(String.valueOf(new char[]{'%', '%', '_', '_', 'N', 'O', 'N', 'C', 'E', '_', '_', '%', '%'}), "Unknown");
    }

    public static String getUserName() {
        return userName;
    }

    public static String toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("user-id", getUserId());
        object.addProperty("user-name", getUserName());
        object.addProperty("resource-id", getResourceId());
        object.addProperty("unique-id", getNonce());
        return object.toString();
    }
}
