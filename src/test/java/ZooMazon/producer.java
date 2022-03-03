package ZooMazon;
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

public class producer {
    public static void main(String[] args) throws InterruptedException {
        Logger logger = LoggerFactory.getLogger(producer.class);
        Dotenv dotenv = Dotenv.configure().load();

        // where to define the properties
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
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        String topicName = "user-activity";

        while (true) {
            JSONObject message = generateMessage();
            // create a producer record
            ProducerRecord<String, String> record = new ProducerRecord<String, String>(topicName, message.toString());
            // send data
            producer.send(record);
            logger.info("Sent: " + message);

            Thread.sleep(10000);
        }
    }

    public static JSONObject generateMessage() {
        JSONObject result = new JSONObject();
        String[] operations = {"searched \uD83D\uDD0D", "bought ✅"};
        String[] customers = {"Judy Hopps\uD83D\uDC30", "Nick Wilde\uD83E\uDD8A", "Chief Bogo\uD83D\uDC03", "Officer Clawhauser\uD83D\uDE3C", "Mayor Lionheart \uD83E\uDD81", "Mr. Big \uD83E\uDE91", "Fru Fru\uD83D\uDC90"};
        String[] products = {"Donut \uD83C\uDF69 ", "Carrot \uD83E\uDD55", "Tie \uD83D\uDC54", "Glasses \uD83D\uDC53️", "Phone ☎️", "Ice cream \uD83C\uDF68", "Dress \uD83D\uDC57", "Pineapple pizza \uD83C\uDF55"};

        Random randomizer = new Random();

        result.put("customer", customers[randomizer.nextInt(7)]);
        result.put("product", products[randomizer.nextInt(7)]);
        result.put("operation", operations[randomizer.nextInt(30) < 25 ? 0 : 1]); // a very sophisticated weighted randomizer to prefer search over bought

        return result;
    }
}
