/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.tracing.opentelemetry.OpenTelemetryOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class ProducerApp {

    private static final Logger log = LogManager.getLogger(ProducerApp.class);

    public static void main(String[] args) throws Exception {

        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setTracingOptions(new OpenTelemetryOptions());
        Vertx vertx = Vertx.vertx(vertxOptions);

        CountDownLatch messagesSentLatch = new CountDownLatch(1);
        CountDownLatch exitLatch = new CountDownLatch(1);

        ConfigStoreOptions envStore = new ConfigStoreOptions()
                .setType("env")
                .setConfig(new JsonObject().put("raw-data", true));

        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(envStore);

        ConfigRetriever retriever = ConfigRetriever.create(vertx, options);

        retriever.getConfig(ar -> {
            Map<String, Object> envConfig = ar.result().getMap();
            HttpKafkaProducerConfig httpKafkaConsumerConfig = HttpKafkaProducerConfig.fromMap(envConfig);

            HttpKafkaProducer httpKafkaProducer = new HttpKafkaProducer(httpKafkaConsumerConfig,  messagesSentLatch);
            vertx.deployVerticle(httpKafkaProducer, done -> {
                if (done.succeeded()) {
                    log.info("HTTP Kafka producer started successfully");
                } else {
                    log.error("Failed to deploy HTTP Kafka producer", done.cause());
                    System.exit(1);
                }
            });
        });
        log.info("Waiting for sending all messages");
        messagesSentLatch.await();
        vertx.close(done -> exitLatch.countDown());
        log.info("Waiting HTTP producer verticle to be closed");
        boolean releaseBeforeTimeout = exitLatch.await(60000, TimeUnit.MILLISECONDS);
        log.info("Latch released before Timeout: {}", releaseBeforeTimeout);
    }
}





