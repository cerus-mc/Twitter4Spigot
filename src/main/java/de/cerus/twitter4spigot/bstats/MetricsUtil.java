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

package de.cerus.twitter4spigot.bstats;

import de.cerus.twitter4spigot.config.GeneralConfig;
import org.bstats.bukkit.Metrics;

public class MetricsUtil {

    private static Metrics metrics;

    private MetricsUtil() {
        throw new UnsupportedOperationException();
    }

    public static void setupMetrics(GeneralConfig generalConfig) {
        metrics = generalConfig.useMetrics() ? new Metrics(generalConfig.getPlugin()) : null;
    }

    public static Metrics getMetrics() {
        return metrics;
    }

    public static boolean metricsAvailable() {
        return metrics != null;
    }
}
