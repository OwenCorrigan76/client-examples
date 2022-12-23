import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
public abstract class AbstractConfig {
    public static final String KAFKA_PREFIX = "KAFKA_";

    private final Properties properties;

    public static String convertEnvVarToPropertyKey(String envVar) {
        return envVar.substring(envVar.indexOf("_") + 1).toLowerCase().replace("_", ".");
    }

    // common fromEnv that a child class can inherit
    public static AbstractConfig fromEnv(){
        Properties properties = new Properties();
        properties.putAll(System.getenv()
                .entrySet()
                .stream()
                .filter(mapEntry -> mapEntry.getKey().startsWith(AbstractConfig.KAFKA_PREFIX))
                .collect(Collectors.toMap(mapEntry -> AbstractConfig.convertEnvVarToPropertyKey(mapEntry.getKey()), Map.Entry::getValue)));
        return new AbstractConfig(properties);
    }
}
