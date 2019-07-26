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
import com.gargoylesoftware.htmlunit.html.HtmlS;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TwitterTest {

    public static void main(String[] args) throws IOException {
        final WebClient webClient = new WebClient();

        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

        HtmlPage page = webClient.getPage("https://twitter.com/realDonaldTrump/status/1154452163470483456");
        Spliterator<DomElement> spliterator = Spliterators.spliteratorUnknownSize(
                page.getBody().getChildElements().iterator(), Spliterator.NONNULL);
        Stream<DomElement> stream = StreamSupport.stream(spliterator, false);

        System.out.println("---");
        stream.forEach(domElement -> System.out.println(domElement.toString()));
        System.out.println("---");

        List<HtmlSpan> spans = new ArrayList<>();
        List<HtmlSpan> span = new ArrayList<>();
        List<String> text = new ArrayList<>();

        getAllSpans(page.getBody().getChildElements(), spans);

        getSpan(spans, span, text);
        System.out.println(spans.size());
        System.out.println("SPPAAANNN: v");
        System.out.println(span);

        span.forEach(htmlSpan -> System.out.println(htmlSpan.getTextContent()));

        File file = new File("page.txt");
        file.createNewFile();
        Files.write(file.toPath(), text);
    }

    private static void getAllSpans(Iterable<DomElement> page, List<HtmlSpan> spans) {
        page.forEach(domElement -> {
            if(domElement instanceof HtmlSpan)
                spans.add((HtmlSpan) domElement);
            getAllSpans(domElement.getChildElements(), spans);
        });
    }

    private static void getSpan(List<HtmlSpan> htmlSpans, List<HtmlSpan> spans, List<String> text) {
        if(!spans.isEmpty()) return;
        for (HtmlSpan span : htmlSpans) {
            text.add(span.getTextContent());

            if (span.getTextContent().matches(".* replies")) {
                spans.add(span);
                return;
            }

            Spliterator<DomElement> spliterator = Spliterators.spliteratorUnknownSize(
                    span.getChildElements().iterator(), Spliterator.NONNULL);
            Stream<DomElement> stream = StreamSupport.stream(spliterator, false);

            getSpan(stream.filter(domElement -> domElement instanceof HtmlSpan).map(domElement -> (HtmlSpan) domElement).collect(Collectors.toList()), spans, text);
        }
    }
}
