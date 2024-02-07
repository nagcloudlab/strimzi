package org.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

// appln : my-user

public class ProducerClient {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(ProducerClient.class);

    public static void main(String[] args) throws InterruptedException {

        Properties properties = new Properties();
        properties.put("client.id", "producer-1");
        properties.put("bootstrap.servers", "20.235.194.21:9093");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("security.protocol", "SSL");
//        properties.setProperty("ssl.protocol", "TLSv1.2");
        properties.put("ssl.truststore.location", "/home/nag/strimzi/week-2/day8/kafka-truststore.jks");
        properties.put("ssl.truststore.password", "changeit");
//
        properties.put("ssl.keystore.location", "/home/nag/strimzi/week-2/day8/kafka-keystore.jks");
        properties.put("ssl.keystore.password", "foobar");

        // properties.put("partitioner.class","org.example.CustomPartitioner");

        // // safe producer congiuration properties
        // properties.put("acks","all");
        // properties.put("retries",Integer.MAX_VALUE);
        // properties.put("retry.backoff.ms",100);
        // properties.put("max.in.flight.requests.per.connection",1);
        // properties.put("enable.idempotence",true);
        // // high throughput producer configuration properties
        // properties.put("compression.type","snappy");
        // properties.put("batch.size",16384);
        // properties.put("linger.ms",20);
        // properties.put("buffer.memory",33554432);
        // properties.put("max.block.ms",60000);
        // properties.put("interceptor.classes","org.example.ProducerClientInterceptor");

        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(properties);

        String value = "Apache Kafka is a distributed event store and stream-processing platform. It is an open-source system developed by the Apache Software Foundation written in Java and Scala. The project aims to provide a unified, high-throughput, low-latency platform for handling real-time data feed\n" +
                "Apache Kafka is a distributed event store and stream-processing platform. It is an open-source system developed by the Apache Software Foundation written in Java and Scala. The project aims to provide a unified, high-throughput, low-latency platform for handling real-time data feed\n" +
                "Apache Kafka is a distributed event store and stream-processing platform. It is an open-source system developed by the Apache Software Foundation written in Java and Scala. The project aims to provide a unified, high-throughput, low-latency platform for handling real-time data feed\n" +
                "Apache Kafka is a distributed event store and stream-processing platform. It is an open-source system developed by the Apache Software Foundation write";

        for (int i = 0; i <Integer.MAX_VALUE; i++) {
            // String key="key-"+Integer.toString(i);
//            String value=Integer.toString(i);
            TimeUnit.MILLISECONDS.sleep(1);
            ProducerRecord<String, String> producerRecord1 = new ProducerRecord<String, String>("my-topic", 0,null,value);
            List.of(producerRecord1).forEach(producerRecord -> {
                kafkaProducer.send(producerRecord, (recordMetadata, e) -> {
                    if (e == null) {
                        logger.info("Received new metadata. \n" +
                                "Topic:" + recordMetadata.topic() + "\n" +
                                // "Key :" + key + "\n" +
                                "Partition:" + recordMetadata.partition() + "\n" +
                                "Offset:" + recordMetadata.offset() + "\n" +
                                "Timestamp:" + recordMetadata.timestamp());
                    } else {
                        logger.error("Error while producing", e);
                    }
                });
            });
        }

        kafkaProducer.flush();
        kafkaProducer.close();

    }
}
