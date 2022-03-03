Hands-on material for session "Apache Kafka simply explained"
==============================================================

Prerequisites
-------------

To run examples from this repository you'll need:

1. A Kafka cluster. You can `set it up manually <https://kafka.apache.org/downloads>`_ or you can use a fully managed service, such as `Aiven for Apache Kafka <https://aiven.io/kafka>`_.
2. Java installed - https://java.com/download/manual.jsp

Running locally
---------------

1. Clone the repository and install the dependencies from `pom.xml`.

2. Copy .env.example, rename to .env. This is the file where we store connection properties to access the Kafka cluster

    a. Set your Kafka cluster url to `server property`.
    b.


You're all set!


How to use
----------




Structure of this repository
----------------------------

`producer.java` - generating messages and sending them to Kafka cluster

`consumer.java` - reading messages from Kafka cluster


License
-------

This work is licensed under the Apache License, Version 2.0. Full license text is available in the LICENSE file and at http://www.apache.org/licenses/LICENSE-2.0.txt