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

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SkullValueTest {

    public static void main(String[] args) throws IOException {
        WebClient client = new WebClient();

        client.getOptions().setThrowExceptionOnScriptError(false);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);

        HtmlPage page = client.getPage("https://minecraft-heads.com/custom-heads/miscellaneous/18215-health-full");

        List<DomElement> list = new ArrayList<>();
        getAllElements(page.getBody().getChildElements(), list);
        AtomicReference<String> value = new AtomicReference<>("f");
        getValue(list, value);
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
}
