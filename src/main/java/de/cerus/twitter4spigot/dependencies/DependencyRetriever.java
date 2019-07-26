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

package de.cerus.twitter4spigot.dependencies;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Collections;
import java.util.List;

public class DependencyRetriever {

    private List<Dependency> dependencies;

    public DependencyRetriever() {
        this.dependencies = Collections.singletonList(new Dependency("HTML-Unit", "htmlunit-2.35.0-OSGi.jar", "https://sourceforge.net/projects/htmlunit/files/htmlunit/2.35.0/htmlunit-2.35.0-OSGi.jar/download"));
    }

    public void retrieveDependencies() throws NoSuchMethodException, MalformedURLException, InvocationTargetException, IllegalAccessException {
        File dependencyFolder = new File("plugins/Twitter4Spigot/dependencies");
        dependencyFolder.mkdirs();

        for (Dependency dependency : dependencies) {
            System.out.println("Retrieving dependency " + dependency.getName() + "...");

            File dependencyFile = new File(dependencyFolder, dependency.getFileName());
            if (!dependencyFile.exists()) {
                System.out.println("Dependency does not exist, starting download");
                try {
                    ReadableByteChannel readableByteChannel = Channels.newChannel(new java.net.URL(dependency.getLink()).openStream());
                    FileOutputStream fileOutputStream = new FileOutputStream(dependencyFile);
                    fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            try {
                Method method = classLoader.getClass().getDeclaredMethod("addURL", java.net.URL.class);
                method.setAccessible(true);
                method.invoke(classLoader, dependencyFile.toURI().toURL());
            } catch (NoSuchMethodException e) {
                Method method = classLoader.getClass()
                        .getDeclaredMethod("appendToClassPathForInstrumentation", String.class);
                method.setAccessible(true);
                method.invoke(classLoader, dependencyFile.getAbsolutePath());
            }
            System.out.println("Dependency loaded");
        }
    }
}
