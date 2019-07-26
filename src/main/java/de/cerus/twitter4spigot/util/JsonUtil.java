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

package de.cerus.twitter4spigot.util;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class JsonUtil {

    private JsonUtil() {
        throw new UnsupportedOperationException();
    }

    public static JsonObject locationToJson(Location location) {
        JsonObject object = new JsonObject();
        object.addProperty("world", location.getWorld().getName());
        object.addProperty("x", location.getX());
        object.addProperty("y", location.getY());
        object.addProperty("z", location.getZ());
        object.addProperty("pitch", location.getPitch());
        object.addProperty("yaw", location.getYaw());
        return object;
    }

    public static Location jsonToLocation(JsonObject object) {
        World world = Bukkit.getWorld(object.get("world").getAsString());
        double x = object.get("x").getAsDouble();
        double y = object.get("y").getAsDouble();
        double z = object.get("z").getAsDouble();
        float pitch = object.get("pitch").getAsFloat();
        float yaw = object.get("yaw").getAsFloat();
        return new Location(world, x, y, z, yaw, pitch);
    }
}
