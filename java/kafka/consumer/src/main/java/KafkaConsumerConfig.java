/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */

import java.util.Properties;
import java.util.stream.Collectors;

public class KafkaConsumerConfig {
    private static final long DEFAULT_MESSAGES_COUNT = 10;
    private static final String KAFKA_PREFIX = "KAFKA_";

    private final String topic;
    private final Long messageCount;
    private final TracingSystem tracingSystem;
    private final Properties properties;

    public KafkaConsumerConfig(String topic, Long messageCount, TracingSystem tracingSystem, Properties properties) {
        this.topic = topic;
        this.messageCount = messageCount;
        this.tracingSystem = tracingSystem;
        this.properties = properties;
    }

    public static KafkaConsumerConfig fromEnv() {
        String topic = System.getenv("STRIMZI_TOPIC");
        Long messageCount = System.getenv("STRIMZI_MESSAGE_COUNT") == null ? DEFAULT_MESSAGES_COUNT : Long.parseLong(System.getenv("STRIMZI_MESSAGE_COUNT"));
        TracingSystem tracingSystem = TracingSystem.forValue(System.getenv().getOrDefault("STRIMZI_TRACING_SYSTEM", ""));
        Properties properties = new Properties();
        properties.putAll(System.getenv()
                .entrySet()
                .stream()
                .filter(map -> map.getKey().startsWith(KAFKA_PREFIX))
                .collect(Collectors.toMap(map -> convertEnvVarToPropertyKey(map.getKey()), map -> map.getValue())));
        return new KafkaConsumerConfig(topic, messageCount, tracingSystem, properties);
    }

    public static String convertEnvVarToPropertyKey(String envVar) {
        System.out.println("ENV_VAR is " + envVar);
        return envVar.substring(envVar.indexOf("_") + 1).toLowerCase().replace("_", ".");
    }

    public String getTopic() {
        return topic;
    }

    public Long getMessageCount() {
        return messageCount;
    }

    public TracingSystem getTracingSystem() {
        return tracingSystem;
    }

    public Properties getProperties() {
        return properties;
    }
}
