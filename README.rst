Hands-on material for session "Apache Kafka simply explained" (Java version)
============================================================================

Hello all! This repository contains a set of short exercises to get familiar with Apache Kafka. You'll need to do couple of setup steps and then you can run examples of producers and consumers that I've preapared for you.

Preparation steps
------------------

1. You'll need an Apache Kafka cluster. Apache Kafka is an open source platform, so you can either `set it up and run from its source code <https://kafka.apache.org/quickstart#quickstart_download>`_ or use a fully managed option, for  this experiments you can use a free trial of `Aiven for Apache Kafka <https://aiven.io/kafka>`_ (Disclaimer for transparency - I work at Aiven 🙂). I'll be using the latter option.

2. Clone this repository and install the dependencies from **pom.xml**.

3. To connect to the remote Apache Kafka cluster we need to set up SSL configuration. Follow `these steps <https://developer.aiven.io/docs/products/kafka/howto/keystore-truststore.html>`_ to create keystore and truststore based on the  Access Key, Access Certificate and CA Certificate.

4. Copy .env.example, rename to .env and update it with information about the location of truststore and keystore files and their passwords. You'll have something similar to the content below (just don't use 'password' as password 😉):

.. code::

    server="name-of-my-server.aivencloud.com:port"
    ssl.truststore.location="../keys/client.truststore.jks"
    ssl.truststore.password="password"
    ssl.keystore.type="PKCS12"
    ssl.keystore.location="../keys/client.keystore.p12"
    ssl.keystore.password="password"
    ssl.key.password="password"

5. In your cluster create a topic with the name *customer-activity* that contains 3 partitions, for example for Aiven's managed version you can use the UI and create a topic `directly from the console <https://developer.aiven.io/docs/products/kafka/howto/create-topic.html>`_.

Now you're ready for demo exercises. In these demos we'll focus on a single topic that contains events based on customer activity in an online shop.

Demo # 1: create a producer and a consumer
-----------------------------------------------
In this demo we'll look at a simple producer. This producer will send messages to the Kafka cluster; and a simple consumer will read messages and print out their content.

1. Open Java file ``Shopitopia.simpleProducer`` - a very simple producer. It creates a message every second and  sends it into the cluster. Run the method ``main`` to start the producer.
2. If the configuration is set up correctly, you'll see output similar to this:

.. code::

    [main] INFO org.apache.kafka.common.utils.AppInfoParser - Kafka version: 3.0.0
    [main] INFO org.apache.kafka.common.utils.AppInfoParser - Kafka commitId: 8cb0a5e9d3441999
    [main] INFO org.apache.kafka.common.utils.AppInfoParser - Kafka startTimeMs: 1648141938679
    [kafka-producer-network-thread | producer-1] INFO org.apache.kafka.clients.Metadata - [Producer clientId=producer-1] Cluster ID: 7iPfsgbgGAqgwQ5XsIL9ng
    [main] INFO Shopitopia.simpleProducer - Sent: {"product":"Dress 👗","operation":"searched 🔍","customer":"Chief Bogo🐃"}
    [main] INFO Shopitopia.simpleProducer - Sent: {"product":"Tie 👔","operation":"searched 🔍","customer":"Officer Clawhauser😼"}
    [main] INFO Shopitopia.simpleProducer - Sent: {"product":"Tie 👔","operation":"bought ✅","customer":"Fru Fru💐"}
    [main] INFO Shopitopia.simpleProducer - Sent: {"product":"Donut 🍩 ","operation":"searched 🔍","customer":"Mr. Big 🪑"}
    [main] INFO Shopitopia.simpleProducer - Sent: {"product":"Donut 🍩 ","operation":"searched 🔍","customer":"Nick Wilde🦊"}

3. While producer creates new messages, open Java file ``Shopitopia.consSimple`` and run its method ``main`` to start the consumer. Consumer will connect to the cluster and read messages added by producer. You will see detailed information about connection to the cluster and once the connection is established the received messages:

.. code::

    [main] INFO org.apache.kafka.clients.consumer.internals.SubscriptionState - [Consumer clientId=consumer-first-1, groupId=first] Resetting offset for partition customer-activity-1 to position FetchPosition{offset=0, offsetEpoch=Optional.empty, currentLeader=LeaderAndEpoch{leader=Optional[35.228.93.149:12693 (id: 29 rack: null)], epoch=0}}.
    [main] INFO Shopitopia.consSimple - message {"product":"Carrot 🥕","operation":"bought ✅","customer":"Mr. Big 🪑"}
    [main] INFO Shopitopia.consSimple - message {"product":"Dress 👗","operation":"bought ✅","customer":"Mr. Big 🪑"}

3. Observe the results. Once you're done, terminate the consumer ``Shopitopia.consSimple``, but leave the producer, we'll use it in the next demo.

Demo # 2: observe how messages are spreaded across partitions
--------------------------------------------------------------------
In this demo we'll look at partitions and offsets.

1. You should have the producer ``Shopitopia.simpleProducer`` already running.
2. Start ``Shopitopia.consPartitions``, this is our consumer, in addition to the message body, it outputs information about partitions and offsets for every partition.
3. Also, try out the consumer ``Shopitopia.consFiltered`` which outputs results for a single customer. You can see that currently the messages that are related to a single customer are spread across all partitions.
4. Terminate the producers and consumers that are running.

Demo # 3: add keys to messages
------------------------------------
When looking at the consumer output you can see that messages are spread across partitions in some random way.
It is important to understand that Apache Kafka guarantees order only within a partition. This means that if we want to preserve message orders coming from our customers we need to write all messages related to a single customer into the same partition.
This can be done by assigning keys to the messages. All messages with the same key will be added to the same partition.

1. Run ``Shopitopia.prodKeys``, this producer uses customer name as key for a message.
2. Run ``Shopitopia.consPartitions`` and ``Shopitopia.consFiltered``. Observe that messages related to specific customers consistently fall into the same partitions.

Demo # 4: use a sink connector
------------------------------------

In the last demo we'll demonstrate how you can use Apache Kafka connectors to move data from Apache Kafka topic into another data store.
In this example we'll move data (sink data) into an OpenSearch cluster.

1. You'll need an OpenSearch cluster. Either `set it up from source code <https://opensearch.org/downloads.html#docker-compose>`_ or use a managed option, such as `Aiven for OpenSearch <https://aiven.io/opensearch>`_.

2. Either set it up `manually <https://kafka.apache.org/documentation/#connect_running>`_ or enable Apache Kafka Connect in the managed version, for example this is how to do so `in Aiven for Apache Kafka <https://developer.aiven.io/docs/products/kafka/kafka-connect/howto/enable-connect.html>`_.

3. You'll need to create a configuration file to be used by the connector. Follow this article `how to use OpenSearch connector <https://developer.aiven.io/docs/products/kafka/kafka-connect/howto/opensearch-sink.html>`_. In particular look at the example `which uses JSON schema <https://developer.aiven.io/docs/products/kafka/kafka-connect/howto/opensearch-sink.html#example-create-an-opensearch-sink-connector-on-a-topic-with-a-json-schema>`_ to transfer data.

4. To help you, I've created an example of configuration. File `connector.json`, which is located in the root folder of this repository contains necessary parameters to connect to OpenSearch and send data. You only need to update OpenSearch connection information:

.. code::

  "connection.url": "https://opensearch-cluster-address:port",
  "connection.username": "your OpenSearch cluster login name",
  "connection.password": "your OpenSearch cluster password",

Resources and additional materials
----------------------------------
#. `Official docs for Apache Kafka <https://kafka.apache.org/>`_.
#. `Official docs for Apache Kafka Connect API <https://kafka.apache.org/documentation/#connect>`_.
#. `Official docs for Apache Kafka Streams <https://kafka.apache.org/documentation/streams/>`_.
#. `A ready fake data generator <https://developer.aiven.io/docs/products/kafka/howto/fake-sample-data.html>`_ to source data into Apache Kafka cluster.
#. `How to use kcat <https://developer.aiven.io/docs/products/kafka/howto/kcat.html>`_. A very handy utility to work with Apache Kafka from command line.
#. `How to use Karapace schema registry <https://aiven.io/blog/what-is-karapace>`_ to align the structure of data coming to Kafka cluster.
#. `How to use Apache Kafka Connect as streaming bridge between different database technologies <https://aiven.io/blog/db-technology-migration-with-apache-kafka-and-kafka-connect>`_.

License
-------

This work is licensed under the Apache License, Version 2.0. Full license text is available in the LICENSE file and at http://www.apache.org/licenses/LICENSE-2.0.txt