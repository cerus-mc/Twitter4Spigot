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

package de.cerus.twitter4spigot.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class GeneralConfig extends Config {

    private boolean useMetrics;
    private boolean useImageChests;
    private String twitterConsumerKey;
    private String twitterConsumerSecret;
    private String twitterAccessToken;
    private String twitterAccessSecret;
    private String skullTextureTweet;
    private String skullTextureLikes;
    private String skullTextureRetweets;
    private String skullTextureComments;

    public GeneralConfig(JavaPlugin plugin) {
        super(plugin, new File("plugins/Twitter4Spigot/config.yml"));
    }

    @Override
    public void loadValues() {
        FileConfiguration configuration = getConfiguration();
        useMetrics = configuration.getBoolean("enable-metrics");
        useImageChests = configuration.getBoolean("enable-image-chests");
        twitterConsumerKey = configuration.getString("twitter-consumer-key");
        twitterConsumerSecret = configuration.getString("twitter-consumer-secret");
        twitterAccessToken = configuration.getString("twitter-access-token");
        twitterAccessSecret = configuration.getString("twitter-access-secret");
        skullTextureTweet = configuration.getString("skull-textures.tweet");
        skullTextureLikes = configuration.getString("skull-textures.like");
        skullTextureRetweets = configuration.getString("skull-textures.retweet");
        skullTextureComments = configuration.getString("skull-textures.comments");
    }

    @Override
    public void setDefaults() {
        FileConfiguration configuration = getConfiguration();
        configuration.options().header("You can create a Twitter app at https://developer.twitter.com/en/apps/create");
        configuration.set("enable-metrics", true);
        configuration.set("enable-image-chests", true);
        configuration.set("twitter-consumer-key", "Change me");
        configuration.set("twitter-consumer-secret", "Change me");
        configuration.set("twitter-access-token", "Change me");
        configuration.set("twitter-access-secret", "Change me");
        configuration.set("skull-textures.tweet", "https://minecraft-heads.com/custom-heads/miscellaneous/20490-twitter");
        configuration.set("skull-textures.like", "https://minecraft-heads.com/custom-heads/miscellaneous/18215-health-full");
        configuration.set("skull-textures.retweet", "https://minecraft-heads.com/custom-heads/miscellaneous/2359-repeat");
        configuration.set("skull-textures.comments", "https://minecraft-heads.com/custom-heads/miscellaneous/26112-speech-bubble");
        save();
    }

    @Override
    public void updateConfig() {
        FileConfiguration configuration = getConfiguration();
        boolean hasChanged = false;
        if (!configuration.contains("enable-image-chests")) {
            configuration.set("enable-image-chests", true);
            hasChanged = true;
        }
        if (hasChanged)
            save();
    }

    public boolean useMetrics() {
        return useMetrics;
    }

    public boolean useImageChests() {
        return useImageChests;
    }

    public String getTwitterConsumerKey() {
        return twitterConsumerKey;
    }

    public String getTwitterConsumerSecret() {
        return twitterConsumerSecret;
    }

    public String getTwitterAccessToken() {
        return twitterAccessToken;
    }

    public String getTwitterAccessSecret() {
        return twitterAccessSecret;
    }

    public String getSkullTextureTweet() {
        return skullTextureTweet;
    }

    public String getSkullTextureLikes() {
        return skullTextureLikes;
    }

    public String getSkullTextureRetweets() {
        return skullTextureRetweets;
    }

    public String getSkullTextureComments() {
        return skullTextureComments;
    }
}
