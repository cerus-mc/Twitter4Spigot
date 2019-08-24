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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LoggerUtil {

    private static File logFile = new File("plugins/Twitter4Spigot/t4s-log.log");

    static {
        logFile.getParentFile().mkdirs();
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private LoggerUtil() {
        throw new UnsupportedOperationException();
    }

    public static void disable() {
        java.util.logging.Logger logger = Logger.getLogger("org.apache.http.conn.util");
        logger.setLevel(Level.OFF);
/*        logger.addHandler(new ConsoleHandler() {
            @Override
            public void publish(LogRecord record) {
                log(record);
            }
        });*/

        logger = Logger.getLogger("com.gargoylesoftware");
        logger.setLevel(Level.OFF);
/*        logger.addHandler(new ConsoleHandler() {
            @Override
            public void publish(LogRecord record) {
                log(record);
            }
        });*/
    }

    private static void log(LogRecord record) {
        try {
            Files.write(logFile.toPath(), Collections.singletonList(record.getMessage()), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
