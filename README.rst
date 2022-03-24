Hands-on material for session "Apache Kafka simply explained" (Java version)
============================================================================

Running locally
---------------

1. We'll need an Apache Kafka cluster. Apache Kafka is an open source platform, so you can either `set it up from its source code <https://kafka.apache.org/quickstart#quickstart_download>`_ or use a fully managed option, for example sign up for  `30 day free trial with Aiven <https://aiven.io/kafka>`_. I'll be using the latter option.

2. Clone this repository and install the dependencies from `pom.xml`.

3. To connect to the remote Apache Kafka cluster we need to set up SSL configuration. Follow `these steps <https://developer.aiven.io/docs/products/kafka/howto/keystore-truststore.html>`_ to create keystore and truststore based on the  Access Key, Access Certificate and CA Certificate.

4. Copy .env.example, rename to .env and update it with information about truststore and keystore and their passwords. You'll have something similar to the content below (just don't use 'password' as password ;)):

.. code::

    server="name-of-my-server.aivencloud.com:port"
    ssl.truststore.location="../keys/client.truststore.jks"
    ssl.truststore.password="password"
    ssl.keystore.type="PKCS12"
    ssl.keystore.location="../keys/client.keystore.p12"
    ssl.keystore.password="password"
    ssl.key.password="password"

5. In your cluster `create a topic <https://developer.aiven.io/docs/products/kafka/howto/create-topic.html>`_ 'customer-activity' with 3 partitions.

Now you're ready for demo exercises. In these demos we'll focus on a single topic that contains events based on customer activity in an online shop.

Demo # 1: create a producer and a consumer
-----------------------------------------------
In this demo we'll look at a simple producer, that will send messages to the Kafka cluster; and a simple consumer that will read messages and print out their content.

1. Open Java file ``ZooMazon.prodSimple`` - a very simple producer. It creates a message every second and  sends it into the cluster. Run the method ``main`` to start the producer.
2. If the configuration is set up correctly, you'll see output similar to this:

.. code::

    [main] INFO org.apache.kafka.common.utils.AppInfoParser - Kafka version: 3.0.0
    [main] INFO org.apache.kafka.common.utils.AppInfoParser - Kafka commitId: 8cb0a5e9d3441999
    [main] INFO org.apache.kafka.common.utils.AppInfoParser - Kafka startTimeMs: 1648141938679
    [kafka-producer-network-thread | producer-1] INFO org.apache.kafka.clients.Metadata - [Producer clientId=producer-1] Cluster ID: 7iPfsgbgGAqgwQ5XsIL9ng
    [main] INFO ZooMazon.prodSimple - Sent: {"product":"Dress 👗","operation":"searched 🔍","customer":"Chief Bogo🐃"}
    [main] INFO ZooMazon.prodSimple - Sent: {"product":"Tie 👔","operation":"searched 🔍","customer":"Officer Clawhauser😼"}
    [main] INFO ZooMazon.prodSimple - Sent: {"product":"Tie 👔","operation":"bought ✅","customer":"Fru Fru💐"}
    [main] INFO ZooMazon.prodSimple - Sent: {"product":"Donut 🍩 ","operation":"searched 🔍","customer":"Mr. Big 🪑"}
    [main] INFO ZooMazon.prodSimple - Sent: {"product":"Donut 🍩 ","operation":"searched 🔍","customer":"Nick Wilde🦊"}

3. While producer creates new messages, open Java file ``ZooMazon.consSimple`` and run its method ``main`` to start the consumer. Consumer will connect to the cluster and read messages added by producer. You will see detailed information about connection to the cluster and once the connection is established the received messages:

.. code::

    [main] INFO org.apache.kafka.clients.consumer.internals.SubscriptionState - [Consumer clientId=consumer-first-1, groupId=first] Resetting offset for partition customer-activity-1 to position FetchPosition{offset=0, offsetEpoch=Optional.empty, currentLeader=LeaderAndEpoch{leader=Optional[35.228.93.149:12693 (id: 29 rack: null)], epoch=0}}.
    [main] INFO ZooMazon.consSimple - message {"product":"Carrot 🥕","operation":"bought ✅","customer":"Mr. Big 🪑"}
    [main] INFO ZooMazon.consSimple - message {"product":"Dress 👗","operation":"bought ✅","customer":"Mr. Big 🪑"}

3. Observe the results. Once you're done, terminate the consumer ``ZooMazon.consSimple``, but leave the producer, we'll use it in the next demo.

Demo # 2: observe how messages are spreaded across partitions
--------------------------------------------------------------------
In this demo we'll look at partitions and offsets.

1. You should have the producer ``ZooMazon.prodSimple`` already running.
2. Start ``ZooMazon.consPartitions``, this is our consumer, in addition to the message body, it outputs information about partitions and offsets for every partition.
3. Also, try out the consumer ``ZooMazon.consFiltered`` which outputs results for a single customer. You can see that currently the messages that are related to a single customer are spread across all partitions.
4. Terminate the producers and consumers that are running.

Demo # 3: add keys to messages
------------------------------------
When observing the consumer output you can see that messages are spread across partitions in a roundabout way.
Apache Kafka guarantees order only within a partition, so in some situations we want to control how messages are divided between partitions.
This can be done by assigning keys to the messages.

1. Run ``ZooMazon.prodKeys``, this producer uses customer name as key for a message.
2. Run ``ZooMazon.consPartitions`` and ``ZooMazon.consFiltered``. Observe that messages related to specific customers consistently fall into the same partitions.

Demo # 4: use a sink connector
------------------------------------

In the last demo we'll demonstrate how you can move data from Apache Kafka topic into another data store with the help of a connector.
We'll move data into an OpenSearch cluster.

1. You'll need an OpenSearch cluster. Either `set it up from source code <https://opensearch.org/downloads.html#docker-compose>`_ or use a managed option, such as `Aiven for OpenSearch <https://aiven.io/opensearch>`_.

2. `Enable Apache Kafka Connect <https://developer.aiven.io/docs/products/kafka/kafka-connect/howto/enable-connect.html>`_.

3. Follow article `how to use OpenSearch connector <https://developer.aiven.io/docs/products/kafka/kafka-connect/howto/opensearch-sink.html>`_. In particular look at the example `which uses JSON schema <https://developer.aiven.io/docs/products/kafka/kafka-connect/howto/opensearch-sink.html#example-create-an-opensearch-sink-connector-on-a-topic-with-a-json-schema>`_ to transfer data.

4. To help you, I've created an example of configuration. File `connector.json`, which is located in to root folder of this repository contains necessary parameters to connect to OpenSearch and send data. You only need to update OpenSearch connection information:

.. code::

  "connection.url": "https://opensearch-cluster-address:port",
  "connection.username": "your OpenSearch cluster login name",
  "connection.password": "your OpenSearch cluster password",

License
-------

This work is licensed under the Apache License, Version 2.0. Full license text is available in the LICENSE file and at http://www.apache.org/licenses/LICENSE-2.0.txt