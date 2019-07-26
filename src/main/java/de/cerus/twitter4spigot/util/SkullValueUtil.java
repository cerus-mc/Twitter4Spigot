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

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import de.cerus.twitter4spigot.config.GeneralConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class SkullValueUtil {

    private static Map<String, String> values = new HashMap<>();

    private static WebClient client;

    private SkullValueUtil() {
        throw new UnsupportedOperationException();
    }

    public static void initialize() {
        client = new WebClient();

        LoggerUtil.disable();

        client.getOptions().setThrowExceptionOnScriptError(false);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
    }

    public static boolean loadValue(String name, String link) {
        if (!link.matches("https://minecraft-heads.com/custom-heads/.*/.*")) return false;
        try {
            LoggerUtil.disable();
            HtmlPage page = client.getPage(link);
            List<DomElement> list = new ArrayList<>();
            getAllElements(page.getBody().getChildElements(), list);
            AtomicReference<String> value = new AtomicReference<>("");
            getValue(list, value);
            String textValue = value.get();
            page.cleanUp();
            if (textValue.equals(""))
                return false;
            values.put(name, textValue);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean hasDefaults() {
        return values.containsKey("tweet") &&
                values.containsKey("like") &&
                values.containsKey("retweet") &&
                values.containsKey("comments");
    }

    public static String getValue(String name) {
        return values.getOrDefault(name, "UNDEFINED");
    }

    private static void getAllElements(Iterable<DomElement> elements, List<DomElement> list) {
        elements.forEach(domElement -> {
            list.add(domElement);
            getAllElements(domElement.getChildElements(), list);
        });
    }

    private static void getValue(List<DomElement> elements, AtomicReference<String> value) {
        elements.stream()
                .filter(domElement -> domElement instanceof HtmlTextArea)
                .map(domElement -> (HtmlTextArea) domElement)
                .filter(htmlTextArea -> htmlTextArea.hasAttribute("id") &&
                        htmlTextArea.getAttribute("id").equals("UUID-Value"))
                .findAny().ifPresent(textArea -> value.set(textArea.getText()));
    }

    public static void loadDefaults(GeneralConfig generalConfig) {
        System.out.println("Attempting to load 'Tweet' texture...");
        boolean success = loadValue("tweet", generalConfig.getSkullTextureTweet());
        if (success) System.out.println("Success!");
        else System.out.println("Failed to load texture (is the link valid?)");

        System.out.println("Attempting to load 'Like' texture...");
        success = loadValue("like", generalConfig.getSkullTextureLikes());
        if (success) System.out.println("Success!");
        else System.out.println("Failed to load texture (is the link valid?)");

        System.out.println("Attempting to load 'Retweet' texture...");
        success = loadValue("retweet", generalConfig.getSkullTextureRetweets());
        if (success) System.out.println("Success!");
        else System.out.println("Failed to load texture (is the link valid?)");

        System.out.println("Attempting to load 'Comments' texture...");
        success = loadValue("comments", generalConfig.getSkullTextureComments());
        if (success) System.out.println("Success!");
        else System.out.println("Failed to load texture (is the link valid?)");
    }
}
