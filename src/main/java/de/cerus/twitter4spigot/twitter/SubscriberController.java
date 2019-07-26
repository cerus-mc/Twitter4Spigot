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

import de.cerus.twitter4spigot.storage.SubscriberStorage;
import de.cerus.twitter4spigot.util.SkullValueUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class SubscriberController {

    private TwitterBot twitterBot;
    private SubscriberStorage subscriberStorage;

    public SubscriberController(TwitterBot twitterBot, SubscriberStorage subscriberStorage) {
        this.twitterBot = twitterBot;
        this.subscriberStorage = subscriberStorage;
    }

    public void startLoop(JavaPlugin plugin) {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if(!SkullValueUtil.hasDefaults()) {
                System.out.println("Loop rejected (hasDefaults=false)");
                return;
            }

            subscriberStorage.getSubscribers().forEach(subscriber -> subscriber.update(twitterBot, plugin));

            System.out.println("Loop done");
        }, 30*20, 2*60*20);
    }

    public void addSubscriber(TwitterSubscriber subscriber) {
        subscriberStorage.getSubscribers().add(subscriber);
        save();
    }

    public void removeSubscriber(TwitterSubscriber subscriber) {
        subscriberStorage.getSubscribers().remove(subscriber);
        save();
    }

    public void save() {
        for (TwitterSubscriber sub : subscriberStorage.getSubscribers()) {
            sub.setId(subscriberStorage.getSubscribers().indexOf(sub));
            for (InformationHolder informationHolder : sub.getInformationHolders()) {
                informationHolder.setId(sub.getInformationHolders().indexOf(informationHolder));
            }
        }
        subscriberStorage.save();
    }

    public TwitterSubscriber getSubscriber(int id) {
        return subscriberStorage.getSubscribers().stream()
                .filter(subscriber -> subscriber.getId() == id).findAny().orElse(null);
    }

    public List<TwitterSubscriber> getSubscribers() {
        return subscriberStorage.getSubscribers();
    }
}
