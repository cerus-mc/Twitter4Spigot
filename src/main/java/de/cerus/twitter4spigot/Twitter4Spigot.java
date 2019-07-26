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

package de.cerus.twitter4spigot;

import de.cerus.ceruslib.CerusPlugin;
import de.cerus.twitter4spigot.bstats.MetricsUtil;
import de.cerus.twitter4spigot.commands.T4SCommand;
import de.cerus.twitter4spigot.config.GeneralConfig;
import de.cerus.twitter4spigot.storage.SubscriberStorage;
import de.cerus.twitter4spigot.twitter.SubscriberController;
import de.cerus.twitter4spigot.twitter.TwitterBot;
import de.cerus.twitter4spigot.util.SkullValueUtil;

public class Twitter4Spigot extends CerusPlugin {
    @Override
    public void onPluginEnable() {
        GeneralConfig generalConfig = new GeneralConfig(this);
        generalConfig.initialize();
        generalConfig.load();

        MetricsUtil.setupMetrics(generalConfig);

        SkullValueUtil.initialize();
        getServer().getScheduler().runTaskAsynchronously(this, () -> SkullValueUtil.loadDefaults(generalConfig));

        TwitterBot twitterBot = new TwitterBot(generalConfig);
        try {
            twitterBot.initialize();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to login! Please ensure that your credentials are correct.");
            return;
        }

        SubscriberStorage subscriberStorage = new SubscriberStorage();
        subscriberStorage.initialize();
        subscriberStorage.load();

        SubscriberController subscriberController = new SubscriberController(twitterBot, subscriberStorage);

        subscriberController.startLoop(this);

        registerAll(new T4SCommand(this, subscriberController));
    }

    @Override
    public void onPluginDisable() {

    }
}
