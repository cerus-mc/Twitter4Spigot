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

package de.cerus.twitter4spigot.commands;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import de.cerus.ceruslib.commandframework.Arguments;
import de.cerus.ceruslib.commandframework.Command;
import de.cerus.twitter4spigot.twitter.InformationHolder;
import de.cerus.twitter4spigot.twitter.TwitterBot;
import de.cerus.twitter4spigot.twitter.TwitterSubscriber;
import de.cerus.twitter4spigot.util.SkullValueUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;

public class T4STestCommand extends Command {

    private TwitterBot twitterBot;

    public T4STestCommand(JavaPlugin plugin, TwitterBot twitterBot) {
        super(plugin, "t4stest");
        this.twitterBot = twitterBot;
    }

    @Override
    public boolean onPlayerCommand(Player player, org.bukkit.command.Command command, String s, Arguments arguments) {
        if (arguments.size() != 2) {
            player.sendMessage("/" + s + " <Mode> <More Info>");
            return true;
        }

        TwitterSubscriber.Mode mode = TwitterSubscriber.Mode.valueOf(arguments.get(0).getArgument());

        if (!SkullValueUtil.hasDefaults()) {
            player.sendMessage("Please wait while the default skull textures are loading");
            return true;
        }

        Hologram one = HologramsAPI.createHologram(getPlugin(), player.getLocation());
        Hologram two = HologramsAPI.createHologram(getPlugin(), player.getLocation().clone().add(2, -2, 0));
        Hologram three = HologramsAPI.createHologram(getPlugin(), player.getLocation().clone().add(0, -2, 0));
        Hologram four = HologramsAPI.createHologram(getPlugin(), player.getLocation().clone().add(-2, -2, 0));
        Hologram five = HologramsAPI.createHologram(getPlugin(), player.getLocation().clone().add(0, 1, 0));
        InformationHolder holder = new InformationHolder(0, one, two, three, four, five);

        TwitterSubscriber subscriber = new TwitterSubscriber(0, mode, arguments.get(1).getArgument(), Collections.singletonList(holder));
        player.sendMessage("Updating [" + mode + "{" + arguments.get(1).getArgument() + "}]..");
        subscriber.update(twitterBot, getPlugin());
        player.sendMessage("Done");
        return true;
    }
}
