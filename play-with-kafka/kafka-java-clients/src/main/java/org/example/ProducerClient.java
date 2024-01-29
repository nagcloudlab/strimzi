package org.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;

import java.util.Properties;

public class ProducerClient {

    private static final Logger logger=org.slf4j.LoggerFactory.getLogger(ProducerClient.class);

    public static void main(String[] args) {

        Properties properties=new Properties();
        properties.put("bootstrap.servers","localhost:9092,localhost:9093,localhost:9094");
        properties.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        //properties.put("partitioner.class","org.example.CustomPartitioner");

        KafkaProducer<String,String> kafkaProducer=new KafkaProducer<String, String>(properties);

        // greeting-events from many languages ( en,tn,ar )

        for(int i=0;i<10;i++){

            String key="key-"+Integer.toString(i);
            String value="value-"+Integer.toString(i);

            ProducerRecord<String,String> producerRecord=new ProducerRecord<String, String>("greetings",key,value);
            kafkaProducer.send(producerRecord,(recordMetadata, e) -> {
                if(e==null){
                    logger.info("Received new metadata. \n" +
                            "Topic:" + recordMetadata.topic() + "\n" +
                            "Key :" + key + "\n" +
                            "Partition:" + recordMetadata.partition() + "\n" +
                            "Offset:" + recordMetadata.offset() + "\n" +
                            "Timestamp:" + recordMetadata.timestamp());
                }
                else{
                    logger.error("Error while producing",e);
                }
            });
        }

        kafkaProducer.flush();
        kafkaProducer.close();

    }
}
