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

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import de.cerus.twitter4spigot.config.GeneralConfig;
import de.cerus.twitter4spigot.util.LoggerUtil;
import org.bukkit.Bukkit;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TwitterBot {

    private GeneralConfig generalConfig;
    private Twitter twitter;

    public TwitterBot(GeneralConfig generalConfig) {
        this.generalConfig = generalConfig;
    }

    public void initialize() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(generalConfig.getTwitterConsumerKey())
                .setOAuthConsumerSecret(generalConfig.getTwitterConsumerSecret())
                .setOAuthAccessToken(generalConfig.getTwitterAccessToken())
                .setOAuthAccessTokenSecret(generalConfig.getTwitterAccessSecret());
        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
    }

    public boolean doesUserExist(String name) {
        try {
            twitter.showUser(name);
            return true;
        } catch (TwitterException ignored) {
        }
        return false;
    }

    public List<Status> getTweets(String tag, int numberOfTweets, int queryCount) {
        List<Status> tweets = new ArrayList<>();

        Query query = new Query(tag);
        long lastID = Long.MAX_VALUE;

        while (tweets.size() < numberOfTweets) {
            if (numberOfTweets - tweets.size() > 100)
                query.setCount(queryCount);
            else
                query.setCount(numberOfTweets - tweets.size());
            try {
                QueryResult result = twitter.search(query);
                tweets.addAll(result.getTweets());
                System.out.println("Gathered " + tweets.size() + " tweets" + "\n");
                for (Status t : tweets) {
                    if (t.getId() < lastID)
                        lastID = t.getId();
                }
            } catch (TwitterException te) {
                System.out.println("Couldn't connect: " + te);
            }
            query.setMaxId(lastID - 1);
        }

        return tweets;
    }

    public long getTweetReplies(Status status) {
        String url = "https://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId();

        WebClient webClient = new WebClient(BrowserVersion.CHROME);

        LoggerUtil.disable();

        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

        HtmlPage page;
        try {
            page = webClient.getPage(url);
        } catch (IOException e) {
            return -1;
        }

        List<HtmlSpan> spans = new ArrayList<>();
        List<HtmlSpan> span = new ArrayList<>();

        getAllSpans(page.getBody().getChildElements(), spans);
        getSpan(spans, span);

        page.cleanUp();
        webClient.close();

        return span.isEmpty() ? -2 : Long.parseLong(span.get(0).getTextContent().replace(",", "").replace(" replies", ""));
    }

    private void getAllSpans(Iterable<DomElement> page, List<HtmlSpan> spans) {
        page.forEach(domElement -> {
            if (domElement instanceof HtmlSpan)
                spans.add((HtmlSpan) domElement);
            getAllSpans(domElement.getChildElements(), spans);
        });
    }

    private static void getSpan(List<HtmlSpan> htmlSpans, List<HtmlSpan> spans) {
        if (!spans.isEmpty()) return;
        for (HtmlSpan span : htmlSpans) {
            if (span.getTextContent().matches(".* replies")) {
                spans.add(span);
                return;
            }

            Spliterator<DomElement> spliterator = Spliterators.spliteratorUnknownSize(
                    span.getChildElements().iterator(), Spliterator.NONNULL);
            Stream<DomElement> stream = StreamSupport.stream(spliterator, false);

            getSpan(stream.filter(domElement -> domElement instanceof HtmlSpan).map(domElement -> (HtmlSpan) domElement).collect(Collectors.toList()), spans);
        }
    }

    public Twitter getTwitter() {
        return twitter;
    }
}
