package Shopitopia;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class simpleConsumer {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(simpleConsumer.class.getName());
        Dotenv dotenv = Dotenv.configure().load();

        // connect to the cluster
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, dotenv.get("server"));
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "simple");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); //earliest/latest/none

        properties.put("security.protocol", "SSL");
        properties.put("ssl.truststore.location", dotenv.get("ssl.truststore.location"));
        properties.put("ssl.truststore.password", dotenv.get("ssl.truststore.password"));
        properties.put("ssl.keystore.type", dotenv.get("ssl.keystore.type"));
        properties.put("ssl.keystore.location", dotenv.get("ssl.keystore.location"));
        properties.put("ssl.keystore.password", dotenv.get("ssl.keystore.password"));
        properties.put("ssl.key.password", dotenv.get("ssl.key.password"));

        // step # 1 create consumer
        KafkaConsumer<String,String> consumer =
                new KafkaConsumer<String, String>(properties);
        String topicName = "customer-activity";
        // step # 2 subscribe consumer to our topics
        consumer.subscribe(Collections.singleton(topicName));
        while (true) {
            // step # 3 poll new data
            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(100));
            // step # 4 process new data
            for (ConsumerRecord<String, String> record : records) {
                logger.info("message: " + record.value());
            }
        }
    }
}
