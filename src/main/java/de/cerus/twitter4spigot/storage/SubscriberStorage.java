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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.cerus.twitter4spigot.twitter.TwitterSubscriber;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SubscriberStorage extends JsonStorage {

    private List<TwitterSubscriber> subscribers = new ArrayList<>();

    public SubscriberStorage() {
        super(new File("plugins/Twitter4Spigot/data/subscribers.json"));
    }

    @Override
    public void loadValues(JsonObject object) {
        if (!object.has("subscribers")) return;
        JsonArray array = object.get("subscribers").getAsJsonArray();
        array.forEach(element -> subscribers.add(TwitterSubscriber.fromJson(element.getAsJsonObject())));
    }

    public void save() {
        JsonObject parent = new JsonObject();
        JsonArray array = new JsonArray();
        subscribers.forEach(subscriber -> array.add(subscriber.toJson()));
        parent.add("subscribers", array);
        save(parent);
    }

    public List<TwitterSubscriber> getSubscribers() {
        return subscribers;
    }
}
