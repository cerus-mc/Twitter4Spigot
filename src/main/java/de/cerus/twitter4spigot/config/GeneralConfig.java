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

    private boolean useMetrics, useImageChests, linkFormatting, hashtagFormatting, mentionFormatting, valueMode;
    private String twitterConsumerKey, twitterConsumerSecret, twitterAccessToken, twitterAccessSecret, skullTextureTweet,
            skullTextureLikes, skullTextureRetweets, skullTextureComments, linkFormat, hashtagFormat, mentionFormat;

    public GeneralConfig(JavaPlugin plugin) {
        super(plugin, new File("plugins/Twitter4Spigot/config.yml"));
    }

    @Override
    public void loadValues() {
        FileConfiguration configuration = getConfiguration();
        useMetrics = configuration.getBoolean("enable-metrics");
        useImageChests = configuration.getBoolean("enable-image-chests");
        linkFormatting = configuration.getBoolean("formatting.links.enable");
        hashtagFormatting = configuration.getBoolean("formatting.hashtags.enable");
        mentionFormatting = configuration.getBoolean("formatting.mentions.enable");
        twitterConsumerKey = configuration.getString("twitter-consumer-key");
        twitterConsumerSecret = configuration.getString("twitter-consumer-secret");
        twitterAccessToken = configuration.getString("twitter-access-token");
        twitterAccessSecret = configuration.getString("twitter-access-secret");
        valueMode = configuration.getBoolean("skull-textures.value-mode");
        skullTextureTweet = configuration.getString("skull-textures.tweet");
        skullTextureLikes = configuration.getString("skull-textures.like");
        skullTextureRetweets = configuration.getString("skull-textures.retweet");
        skullTextureComments = configuration.getString("skull-textures.comments");
        linkFormat = configuration.getString("formatting.links.format");
        hashtagFormat = configuration.getString("formatting.hashtags.format");
        mentionFormat = configuration.getString("formatting.mentions.format");
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
        configuration.set("formatting.links.enable", true);
        configuration.set("formatting.links.format", "&3&n");
        configuration.set("formatting.hashtags.enable", true);
        configuration.set("formatting.hashtags.format", "&9");
        configuration.set("formatting.mentions.enable", true);
        configuration.set("formatting.mentions.format", "&b&n");
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
        if (!configuration.contains("formatting")) {
            configuration.set("formatting.links.enable", true);
            configuration.set("formatting.links.format", "&3&n");
            configuration.set("formatting.hashtags.enable", true);
            configuration.set("formatting.hashtags.format", "&9");
            configuration.set("formatting.mentions.enable", true);
            configuration.set("formatting.mentions.format", "&b&n");
            hasChanged = true;
        }
        if(!configuration.contains("skull-textures.value-mode")) {
            configuration.set("skull-textures.value-mode", false);
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

    public boolean isLinkFormatting() {
        return linkFormatting;
    }

    public boolean isHashtagFormatting() {
        return hashtagFormatting;
    }

    public boolean isMentionFormatting() {
        return mentionFormatting;
    }

    public String getLinkFormat() {
        return linkFormat;
    }

    public String getHashtagFormat() {
        return hashtagFormat;
    }

    public String getMentionFormat() {
        return mentionFormat;
    }

    public boolean isValueMode() {
        return valueMode;
    }
}
