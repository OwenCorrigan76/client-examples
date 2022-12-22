/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */

public class CommonConfig {
    public static final String KAFKA_PREFIX = "KAFKA_";

    public static String convertEnvVarToPropertyKey(String envVar) {
        return envVar.substring(envVar.indexOf("_") + 1).toLowerCase().replace("_", ".");
    }
}
