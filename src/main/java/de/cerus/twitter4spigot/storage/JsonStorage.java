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

package de.cerus.twitter4spigot.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;

public abstract class JsonStorage {

    private File file;

    public JsonStorage(File file) {
        this.file = file;
    }

    public void initialize() {
        if (file.getParentFile() != null)
            file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        try {
            String json = String.join("\n", Files.readAllLines(file.toPath()));
            JsonObject object = json.equals("") ? null : new JsonParser().parse(json).getAsJsonObject();
            loadValues(object == null ? new JsonObject() : object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void loadValues(JsonObject object);

    public void save(JsonObject parent) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(parent);
        try {
            Files.write(file.toPath(), Collections.singleton(json));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
