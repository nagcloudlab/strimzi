package org.example;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class ConsumerClient {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(ConsumerClient.class);

    public static void main(String[] args) {

        Properties properties = new Properties();
        properties.put("bootstrap.servers", "20.235.194.21:9093");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        properties.put("security.protocol", "SSL");
//        properties.setProperty("ssl.protocol", "TLSv1.2");
        properties.put("ssl.truststore.location", "/home/nag/strimzi/week-2/day8/kafka-truststore.jks");
        properties.put("ssl.truststore.password", "changeit");


        properties.put("ssl.keystore.location", "/home/nag/strimzi/week-2/day8/kafka-keystore.jks");
        properties.put("ssl.keystore.password", "foobar");

        properties.put("group.id", "my-group");
        properties.put("auto.offset.reset", "earliest");

        // partion assignment strategy

        // properties.put("partition.assignment.strategy",
        // "org.apache.kafka.clients.consumer.CooperativeStickyAssignor");
        // properties.put("assignment.consumer.priority", "3");
        // custom partition assignment strategy
        // properties.put("partition.assignment.strategy","com.example.FailoverAssignorConfig");

        // static membership configuration
        // properties.put("group.instance.id", args[0]);

        // offset commit configuration
        // properties.put("enable.auto.commit", "false");
        // properties.put("auto.commit.interval.ms", "5000");

        // poll behaviour configuration
        // properties.put("max.poll.interval.ms", "300000");
        // properties.put("fetch.min.bytes", "1");
        // properties.put("fetch.max.bytes", "52428800");
        // properties.put("max.poll.records", "500");
        // properties.put("max.partition.fetch.bytes", "1048576");

        // properties.put("heartbeat.interval.ms", "3000");
        // properties.put("session.timeout.ms", "45000");

        // properties.put("request.timeout.ms", "30000");
        // properties.put("client.id", "consumer-demo");

        // properties.put("client.rack", "rack1");
        // properties.put("group.initial.rebalance.delay.ms", "3000");

        Map<TopicPartition, OffsetAndMetadata> currentProcessedOffsets = new HashMap<>(); // or redis...

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);
        kafkaConsumer.subscribe(Arrays.asList("my-topic"));

        // get a reference to the current thread
        final Thread mainThread = Thread.currentThread();
        // adding the shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Detected a shutdown, let's exit by calling consumer.wakeup()...");
            kafkaConsumer.wakeup();
            // join the main thread to allow the execution of the code in the main thread
            try {
                mainThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

        try {
            while (true) {
                ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(100); // Fetch Request
                // logger.info("Received " + consumerRecords.count() + " records");
                consumerRecords.forEach(consumerRecord -> {
                    logger.info(" Partition:" + consumerRecord.partition() + " Offset:" + consumerRecord.offset());
                    logger.info("Value:" + consumerRecord.value());
                    // try {
                    // TimeUnit.SECONDS.sleep(3); // Processing
                    // } catch (InterruptedException e) {
                    // throw new RuntimeException(e);
                    // }
                    currentProcessedOffsets.put(new TopicPartition(consumerRecord.topic(), consumerRecord.partition()),
                            new OffsetAndMetadata(consumerRecord.offset() + 1, "no metadata"));
                });
                kafkaConsumer.commitSync(currentProcessedOffsets); // Manual Commit Request
            }
        } catch (WakeupException e) {
            System.out.println("Wake up exception! " + e);
        } catch (Exception e) {
            System.out.println("Unexpected exception " + e);
        } finally {
            kafkaConsumer.commitSync(currentProcessedOffsets);
            kafkaConsumer.close(); // Leaving Request
            System.out.println("The consumer is now gracefully closed");
        }

    }
}
