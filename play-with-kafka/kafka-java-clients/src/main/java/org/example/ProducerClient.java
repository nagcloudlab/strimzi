package org.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;

import java.util.Properties;

public class ProducerClient {

    private static final Logger logger=org.slf4j.LoggerFactory.getLogger(ProducerClient.class);

    public static void main(String[] args) {

        Properties properties=new Properties();
        properties.put("client.id","producer-1");
        properties.put("bootstrap.servers","localhost:9092,localhost:9093,localhost:9094");
        properties.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        //properties.put("partitioner.class","org.example.CustomPartitioner");
        // safe producer
        properties.put("acks","all");
        properties.put("retries",Integer.MAX_VALUE);
        properties.put("retry.backoff.ms",100);
        properties.put("max.in.flight.requests.per.connection",1);
        properties.put("enable.idempotence",true);
        // high throughput producer
        properties.put("compression.type","snappy");
        properties.put("batch.size",16384);
        properties.put("linger.ms",20);
        properties.put("buffer.memory",33554432);
        properties.put("max.block.ms",60000);
        properties.put("interceptor.classes","org.example.ProducerClientInterceptor");



        KafkaProducer<String,String> kafkaProducer=new KafkaProducer<String, String>(properties);

        for(int i=1;i<=1;i++){
            String key="key-"+Integer.toString(i);
            String value="value-"+Integer.toString(i);
            ProducerRecord<String,String> producerRecord=new ProducerRecord<String, String>("topic6",key,value);
            kafkaProducer.send(producerRecord,(recordMetadata, e) -> {
                if(e==null){
                    logger.info("Received new metadata. \n" +
                            "Topic:" + recordMetadata.topic() + "\n" +
//                            "Key :" + key + "\n" +
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
