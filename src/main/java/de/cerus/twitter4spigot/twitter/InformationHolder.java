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

package de.cerus.twitter4spigot.twitter;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.gson.JsonObject;
import de.cerus.twitter4spigot.Twitter4Spigot;
import de.cerus.twitter4spigot.util.JsonUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;

// Represents armorstands holding text
public class InformationHolder {

    private int id;
    private Hologram tweetHolder;
    private Hologram likeHolder;
    private Hologram retweetHolder;
    private Hologram commentHolder;
    private Hologram authorHolder;
    private Chest imageChest;

    public InformationHolder(int id, Hologram tweetHolder, Hologram likeHolder, Hologram retweetHolder, Hologram commentHolder, Hologram authorHolder, Chest imageChest) {
        this.id = id;
        this.tweetHolder = tweetHolder;
        this.likeHolder = likeHolder;
        this.retweetHolder = retweetHolder;
        this.commentHolder = commentHolder;
        this.authorHolder = authorHolder;
        this.imageChest = imageChest;
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        if (tweetHolder != null)
            object.add("tweet-holder", JsonUtil.locationToJson(tweetHolder.getLocation()));
        if (likeHolder != null)
            object.add("like-holder", JsonUtil.locationToJson(likeHolder.getLocation()));
        if (retweetHolder != null)
            object.add("retweet-holder", JsonUtil.locationToJson(retweetHolder.getLocation()));
        if (commentHolder != null)
            object.add("comment-holder", JsonUtil.locationToJson(commentHolder.getLocation()));
        if (authorHolder != null)
            object.add("author-holder", JsonUtil.locationToJson(authorHolder.getLocation()));
        if (imageChest != null)
            object.add("image-chest", JsonUtil.locationToJson(imageChest.getLocation()));
        return object;
    }

    public static InformationHolder fromJson(JsonObject object) {
        Twitter4Spigot plugin = Twitter4Spigot.getPlugin(Twitter4Spigot.class);
        int id = object.get("id").getAsInt();
        Hologram tweetHolder = object.has("tweet-holder") ?
                HologramsAPI.createHologram(plugin, JsonUtil.jsonToLocation(object.get("tweet-holder").getAsJsonObject())) : null;
        Hologram likeHolder = object.has("like-holder") ?
                HologramsAPI.createHologram(plugin, JsonUtil.jsonToLocation(object.get("like-holder").getAsJsonObject())) : null;
        Hologram retweetHolder = object.has("retweet-holder") ?
                HologramsAPI.createHologram(plugin, JsonUtil.jsonToLocation(object.get("retweet-holder").getAsJsonObject())) : null;
        Hologram commentHolder = object.has("comment-holder") ?
                HologramsAPI.createHologram(plugin, JsonUtil.jsonToLocation(object.get("comment-holder").getAsJsonObject())) : null;
        Hologram authorHolder = object.has("author-holder") ?
                HologramsAPI.createHologram(plugin, JsonUtil.jsonToLocation(object.get("author-holder").getAsJsonObject())) : null;
        Block chestBlock = object.has("image-chest") ?
                JsonUtil.jsonToLocation(object.get("image-chest").getAsJsonObject()).getBlock() : null;
        Chest chest = chestBlock != null && chestBlock.getState() instanceof Chest ? (Chest) chestBlock.getState() : null;
        return new InformationHolder(id, tweetHolder, likeHolder, retweetHolder, commentHolder, authorHolder, chest);
    }

    public Hologram getTweetHolder() {
        return tweetHolder;
    }

    public void setTweetHolder(Hologram tweetHolder) {
        if (this.tweetHolder != null)
            this.tweetHolder.delete();
        this.tweetHolder = tweetHolder;
    }

    public Hologram getLikeHolder() {
        return likeHolder;
    }

    public void setLikeHolder(Hologram likeHolder) {
        if (this.likeHolder != null)
            this.likeHolder.delete();
        this.likeHolder = likeHolder;
    }

    public Hologram getRetweetHolder() {
        return retweetHolder;
    }

    public void setRetweetHolder(Hologram retweetHolder) {
        if (this.retweetHolder != null)
            this.retweetHolder.delete();
        this.retweetHolder = retweetHolder;
    }

    public Hologram getCommentHolder() {
        return commentHolder;
    }

    public void setCommentHolder(Hologram commentHolder) {
        if (this.commentHolder != null)
            this.commentHolder.delete();
        this.commentHolder = commentHolder;
    }

    public Hologram getAuthorHolder() {
        return authorHolder;
    }

    public void setAuthorHolder(Hologram authorHolder) {
        if (this.authorHolder != null)
            this.authorHolder.delete();
        this.authorHolder = authorHolder;
    }

    public Chest getImageChest() {
        if (!(imageChest.getBlock() instanceof Chest))
            return null;
        return imageChest;
    }

    public void setImageChest(Chest imageChest) {
        if (this.imageChest != null)
            this.imageChest.setType(Material.AIR);
        this.imageChest = imageChest;
    }

    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }
}
