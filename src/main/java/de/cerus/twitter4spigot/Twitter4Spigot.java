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
import de.cerus.twitter4spigot.dependencies.DependencyRetriever;
import de.cerus.twitter4spigot.storage.SubscriberStorage;
import de.cerus.twitter4spigot.twitter.SubscriberController;
import de.cerus.twitter4spigot.twitter.TwitterBot;
import de.cerus.twitter4spigot.util.SkullValueUtil;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

public class Twitter4Spigot extends CerusPlugin {
    @Override
    public void onPluginEnable() {
        DependencyRetriever dependencyRetriever = new DependencyRetriever();
        try {
            dependencyRetriever.retrieveDependencies();
        } catch (NoSuchMethodException | MalformedURLException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            System.out.println("Failed to retrieve dependencies");
            return;
        }

        GeneralConfig generalConfig = new GeneralConfig(this);
        generalConfig.initialize();
        generalConfig.load();

        SubscriberStorage subscriberStorage = new SubscriberStorage();
        subscriberStorage.initialize();
        subscriberStorage.load();

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

        SubscriberController subscriberController = new SubscriberController(twitterBot, subscriberStorage);

        subscriberController.startLoop(this, generalConfig);

        MetricsUtil.setupMetrics(generalConfig, subscriberController);

        registerAll(new T4SCommand(this, subscriberController));
    }

    @Override
    public void onPluginDisable() {

    }
}
