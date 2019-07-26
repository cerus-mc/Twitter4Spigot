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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.cerus.ceruslib.item.ItemBuilder;
import de.cerus.twitter4spigot.config.GeneralConfig;
import de.cerus.twitter4spigot.maprenderer.ImageRenderer;
import de.cerus.twitter4spigot.util.SkullValueUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import twitter4j.MediaEntity;
import twitter4j.Query;
import twitter4j.Status;
import twitter4j.TwitterException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TwitterSubscriber {

    private int id;
    private Mode mode;
    private String additionalInfo;
    private List<InformationHolder> informationHolders;

    public TwitterSubscriber(int id, Mode mode, String additionalInfo, List<InformationHolder> informationHolders) {
        this.id = id;
        this.mode = mode;
        this.additionalInfo = additionalInfo;
        this.informationHolders = informationHolders;
    }

    public void update(TwitterBot twitterBot, JavaPlugin plugin, GeneralConfig config) {
        try {
            if (twitterBot.getTwitter().getRateLimitStatus().values().stream().anyMatch(status -> status.getRemaining() == 0)) {
                return;
            }
        } catch (TwitterException e) {
            e.printStackTrace();
            return;
        }

        Status status;

        switch (mode) {
            case USER_RECENT:
                if (!twitterBot.doesUserExist(additionalInfo)) return;
                try {
                    Query query = new Query("from:" + additionalInfo);
                    query.setCount(1);
                    query.setResultType(Query.ResultType.recent);
                    List<Status> tweets = twitterBot.getTwitter().search(query).getTweets();
                    if (tweets.isEmpty()) return;
                    status = tweets.get(0);
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return;
                }
                break;
            case USER_POPULAR:
                if (!twitterBot.doesUserExist(additionalInfo)) return;
                try {
                    Query query = new Query("from:" + additionalInfo);
                    query.setCount(1);
                    query.setResultType(Query.ResultType.popular);
                    List<Status> tweets = twitterBot.getTwitter().search(query).getTweets();
                    if (tweets.isEmpty()) return;
                    status = tweets.get(0);
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return;
                }
                break;
            case HASHTAG_RECENT:
                Query query = new Query("#" + additionalInfo);
                query.setCount(1);
                query.setResultType(Query.ResultType.recent);
                try {
                    List<Status> tweets = twitterBot.getTwitter().search(query).getTweets();
                    if (tweets.isEmpty()) return;
                    status = tweets.get(tweets.size() - 1);
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return;
                }
                break;
            case HASHTAG_POPULAR:
                query = new Query("#" + additionalInfo);
                query.setCount(1);
                query.setResultType(Query.ResultType.popular);
                try {
                    List<Status> tweets = twitterBot.getTwitter().search(query).getTweets();
                    if (tweets.isEmpty()) return;
                    status = tweets.get(tweets.size() - 1);
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return;
                }
                break;
            default:
                return;
        }

        String tweet = status.getText();
        List<String> lines = new ArrayList<>();
        if (tweet.length() > 30) {
            int index = 0;
            while (tweet.length() > index) {
                int endIndex = tweet.length() < index + 30 ? tweet.length() : index + 30;
                lines.add(tweet.substring(index, endIndex));
                index += 30;
            }
        } else lines.add(tweet);

        int likes = status.getFavoriteCount();
        int retweets = status.getRetweetCount();

        new Thread(() -> {
            long comments = twitterBot.getTweetReplies(status);

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                for (InformationHolder informationHolder : informationHolders) {
                    Hologram commentHolder = informationHolder.getCommentHolder();
                    if (commentHolder == null) continue;
                    if (commentHolder.size() == 2)
                        commentHolder.getLine(1).removeLine();
                    if (comments == -1) {
                        commentHolder.appendTextLine("§cFailed to load tweet");
                    } else if (comments == -2) {
                        commentHolder.appendTextLine("§cFailed to parse tweet");
                    } else
                        commentHolder.appendTextLine(comments + " Comments");
                }
            });
        }).start();

        new Thread(() -> {
            if (config.useImageChests() && status.getMediaEntities() != null && status.getMediaEntities().length > 0) {
                List<BufferedImage> images = new ArrayList<>();
                MediaEntity[] mediaEntities = status.getMediaEntities();
                for (MediaEntity mediaEntity : mediaEntities) {
                    if (mediaEntity.getType().equals("photo")) {
                        try {
                            images.add(ImageIO.read(new java.net.URL(mediaEntity.getMediaURLHttps())));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                for (InformationHolder informationHolder : informationHolders) {
                    if (informationHolder.getImageChest() != null && config.useImageChests()) {
                        Chest chest = informationHolder.getImageChest();
                        chest.getBlockInventory().clear();

                        for (BufferedImage image : images) {
                            plugin.getServer().getScheduler().runTask(plugin, () -> {
                                MapView map = Bukkit.createMap(chest.getWorld());
                                map.getRenderers().forEach(map::removeRenderer);
                                map.addRenderer(new ImageRenderer(image));
                                chest.getBlockInventory().addItem(new ItemStack(Material.MAP, 1, map.getId()));
                            });
                        }
                    }
                }
            }
        }).start();

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            for (InformationHolder informationHolder : informationHolders) {
                if (informationHolder.getAuthorHolder() != null) {
                    Hologram authorHolder = informationHolder.getAuthorHolder();
                    authorHolder.clearLines();
                    authorHolder.appendTextLine("§l@" + status.getUser().getScreenName());
                }

                if (informationHolder.getTweetHolder() != null) {
                    Hologram tweetHolder = informationHolder.getTweetHolder();
                    tweetHolder.clearLines();
                    tweetHolder.appendItemLine(new ItemBuilder(Material.SKULL_ITEM).withSubId(3).toSkullBuilder()
                            .withTexture(SkullValueUtil.getValue("tweet")).build());
                    lines.forEach(tweetHolder::appendTextLine);
                }

                if (informationHolder.getLikeHolder() != null) {
                    Hologram likeHolder = informationHolder.getLikeHolder();
                    likeHolder.clearLines();
                    likeHolder.appendItemLine(new ItemBuilder(Material.SKULL_ITEM).withSubId(3).toSkullBuilder()
                            .withTexture(SkullValueUtil.getValue("like")).build());
                    likeHolder.appendTextLine(likes + " Likes");
                }

                if (informationHolder.getRetweetHolder() != null) {
                    Hologram retweetHolder = informationHolder.getRetweetHolder();
                    retweetHolder.clearLines();
                    retweetHolder.appendItemLine(new ItemBuilder(Material.SKULL_ITEM).withSubId(3).toSkullBuilder()
                            .withTexture(SkullValueUtil.getValue("retweet")).build());
                    retweetHolder.appendTextLine(retweets + " Retweets");
                }

                if (informationHolder.getCommentHolder() != null) {
                    Hologram commentHolder = informationHolder.getCommentHolder();
                    commentHolder.clearLines();
                    commentHolder.appendItemLine(new ItemBuilder(Material.SKULL_ITEM).withSubId(3).toSkullBuilder()
                            .withTexture(SkullValueUtil.getValue("comments")).build());
                    commentHolder.appendTextLine("Loading comments...");
                }
            }
        });
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        object.addProperty("mode", mode.toString());
        object.addProperty("additional-info", additionalInfo);
        JsonArray array = new JsonArray();
        informationHolders.forEach(informationHolder -> array.add(informationHolder.toJson()));
        object.add("information-holders", array);
        return object;
    }

    public static TwitterSubscriber fromJson(JsonObject object) {
        int id = object.get("id").getAsInt();
        Mode mode = Mode.valueOf(object.get("mode").getAsString());
        String additionalInfo = object.get("additional-info").getAsString();
        List<InformationHolder> informationHolders = new ArrayList<>();
        object.get("information-holders").getAsJsonArray().forEach(element ->
                informationHolders.add(InformationHolder.fromJson(element.getAsJsonObject())));
        return new TwitterSubscriber(id, mode, additionalInfo, informationHolders);
    }

    public int getId() {
        return id;
    }

    public Mode getMode() {
        return mode;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public List<InformationHolder> getInformationHolders() {
        return informationHolders;
    }

    void setId(int id) {
        this.id = id;
    }

    public enum Mode {
        USER_RECENT,
        USER_POPULAR,
        HASHTAG_RECENT,
        HASHTAG_POPULAR
    }
}
