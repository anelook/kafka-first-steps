package Shopitopia;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.Random;

public class prodOpenSearch {
    public static void main(String[] args) throws InterruptedException {
        Logger logger = LoggerFactory.getLogger(prodOpenSearch.class);
        Dotenv dotenv = Dotenv.configure().load();

        // connect to the cluster
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, dotenv.get("server"));
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        properties.put("security.protocol", "SSL");
        properties.put("ssl.truststore.location", dotenv.get("ssl.truststore.location"));
        properties.put("ssl.truststore.password", dotenv.get("ssl.truststore.password"));
        properties.put("ssl.keystore.type", dotenv.get("ssl.keystore.type"));
        properties.put("ssl.keystore.location", dotenv.get("ssl.keystore.location"));
        properties.put("ssl.keystore.password",dotenv.get("ssl.keystore.password"));
        properties.put("ssl.key.password", dotenv.get("ssl.key.password"));

        // create a producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        String topicName = "customer-activity-opensearch";

        while (true) {
            // generate new message
            JSONObject message = generateMessage();

            // create a producer record
            String key = String.valueOf(System.currentTimeMillis()); // use time of event as a key
            String value = message.toString();
            ProducerRecord<String, String> record = new ProducerRecord<>(topicName, key, value);

            // send data
            producer.send(record);
            logger.info("Sent: " + message);

            // pause before sending next message
            Thread.sleep(1000);
        }
    }

    public static JSONObject generateMessage() {
        JSONObject message = new JSONObject();
        String[] operations = {"searched \uD83D\uDD0D", "bought ✅"};
        String[] customers = {"Judy Hopps\uD83D\uDC30", "Nick Wilde\uD83E\uDD8A", "Chief Bogo\uD83D\uDC03", "Officer Clawhauser\uD83D\uDE3C", "Mayor Lionheart \uD83E\uDD81", "Mr. Big \uD83E\uDE91", "Fru Fru\uD83D\uDC90"};
        String[] products = {"Donut \uD83C\uDF69 ", "Carrot \uD83E\uDD55", "Tie \uD83D\uDC54", "Glasses \uD83D\uDC53️", "Phone ☎️", "Ice cream \uD83C\uDF68", "Dress \uD83D\uDC57", "Pineapple pizza \uD83C\uDF55"};

        // randomly assign values
        Random randomizer = new Random();
        message.put("customer", customers[randomizer.nextInt(7)]);
        message.put("product", products[randomizer.nextInt(7)]);
        message.put("operation", operations[randomizer.nextInt(30) < 25 ? 0 : 1]); // a very sophisticated weighted randomizer to prefer 'search' over 'bought'

        return message;
    }
}
