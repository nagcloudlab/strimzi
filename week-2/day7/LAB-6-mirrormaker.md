

Introducing MirrorMaker 2.0 to Strimzi

👉 MirrorMaker 2.0 is a new Kafka Connect-based tool for replicating data between Kafka clusters. 
👉 It is a complete rewrite of the original MirrorMaker tool, which was based on the Kafka consumer and producer. 👉 MirrorMaker 2.0 is part of the Apache Kafka project and is included in the Strimzi project.

👉 Which makes it a very useful tool for those wanting to ensure the availability and consistency of their enterprise data. And who doesn’t? Typical scenarios where you might consider MirrorMaker are for disaster recovery and data aggregation.



MirrorMaker 2.0 - the Kafka Connect(ion)

Using MirrorMaker 2.0, you just need to identify your source and target clusters. You then configure and deploy MirrorMaker 2.0 to make the connection between those clusters.

👉 MirrorMaker 2.0 is a Kafka Connect source and sink connector, which means it can be used to both consume and produce data to and from Kafka clusters.

https://strimzi.io/assets/images/posts/2020-03-30-mirrormaker.png



Bidirectional opportunities

https://strimzi.io/assets/images/posts/2020-03-30-mirrormaker-renaming.png



👉 MirrorMaker 2.0 can be used to replicate data in both directions between clusters. 
This means you can use it to replicate data from a source cluster to a target cluster, and also from the target cluster back to the source cluster.

👉 This is useful for scenarios where you have a primary and secondary data center, and you want to ensure that data is replicated in both directions between the two data centers. This can help to ensure that data is available and consistent in both data centers, and can also help to reduce the impact of network outages between the two data centers.




👉 MirrorMaker 2.0 can also be used to replicate data between different cloud providers, or between on-premises and cloud environments. This can be useful for scenarios where you want to migrate data between different environments, or where you want to ensure that data is replicated between different environments for disaster recovery purposes.


👉 MirrorMaker 2.0 can also be used to replicate data between different regions within the same cloud provider. This can be useful for scenarios where you want to ensure that data is replicated between different regions for disaster recovery purposes, or where you want to ensure that data is available and consistent in different regions for low-latency access.


👉 MirrorMaker 2.0 can also be used to replicate data between different Kafka versions. This can be useful for scenarios where you want to upgrade your Kafka clusters to a new version, and you want to ensure that data is replicated between the old and new versions of Kafka during the upgrade process.


👉 MirrorMaker 2.0 can also be used to replicate data between different Kafka configurations. This can be useful for scenarios where you want to ensure that data is replicated between different Kafka configurations, such as different security settings or different partitioning strategies.


👉 MirrorMaker 2.0 can also be used to replicate data between different Kafka topics. This can be useful for scenarios where you want to ensure that data is replicated between different topics, such as when you want to aggregate data from multiple topics into a single topic, or when you want to split data from a single topic into multiple topics.    


👉 MirrorMaker 2.0 can also be used to replicate data between different Kafka clusters. This can be useful for scenarios where you want to ensure that data is replicated between different Kafka clusters, such as when you want to migrate data between different clusters, or when you want to ensure that data is replicated between different clusters for disaster recovery purposes.


👉 MirrorMaker 2.0 can also be used to replicate data between different Kafka environments. This can be useful for scenarios where you want to ensure that data is replicated between different Kafka environments, such as between development, test, and production environments.


👉 MirrorMaker 2.0 can also be used to replicate data between different Kafka topologies. This can be useful for scenarios where you want to ensure that data is replicated between different Kafka topologies, such as between a single-node and multi-node Kafka cluster, or between a single-partition and multi-partition Kafka topic.


👉 MirrorMaker 2.0 can also be used to replicate data between different Kafka workloads. This can be useful for scenarios where you want to ensure that data is replicated between different Kafka workloads, such as between a batch and real-time workload, or between a high-throughput and low-latency workload.


👉 MirrorMaker 2.0 can also be used to replicate data between different Kafka applications. This can be useful for scenarios where you want to ensure that data is replicated between different Kafka applications, such as between a legacy and modern application, or between a monolithic and microservices application.


👉 MirrorMaker 2.0 can also be used to replicate data between different Kafka data formats. This can be useful for scenarios where you want to ensure that data is replicated between different data formats, such as between Avro and JSON, or between Protobuf and XML.


👉 MirrorMaker 2.0 can also be used to replicate data between different Kafka data models. This can be useful for scenarios where you want to ensure that data is replicated between different data models, such as between a relational and NoSQL data model, or between a document and graph data model.


👉 MirrorMaker 2.0 can also be used to replicate data between different Kafka data stores. This can be useful for scenarios where you want to ensure that data is replicated between different data stores, such as between a key-value and column-family data store, or between a document and wide-column data store. 


👉 MirrorMaker 2.0 can also be used to replicate data between different Kafka data access patterns. This can be useful for scenarios where you want to ensure that data is replicated between different data access patterns, such as between a read-heavy and write-heavy access pattern, or between a random and sequential access pattern.



👉 MirrorMaker 2.0 can also be used to replicate data between different Kafka data consistency models. This can be useful for scenarios where you want to ensure that data is replicated between different data consistency models, such as between eventual and strong consistency, or between causal and eventual consistency.


👉 MirrorMaker 2.0 can also be used to replicate data between different Kafka data durability models. This can be useful for scenarios where you want to ensure that data is replicated between different data durability models, such as between in-memory and disk-based durability, or between synchronous and asynchronous durability.


👉 MirrorMaker 2.0 can also be used to replicate data between different Kafka data privacy models. This can be useful for scenarios where you want to ensure that data is replicated between different data privacy models, such as between public and private data, or between encrypted and unencrypted data.


👉 MirrorMaker 2.0 can also be used to replicate data between different Kafka data governance models. This can be useful for scenarios where you want to ensure that data is replicated between different data governance models, such as between centralized and decentralized governance, or between strict and flexible governance.

---------------------------------------------------------------------------------------------------------------------




Source kafka Cluster

```bash
kubectl apply -f kafka-source.yaml -n kafka
```


Target kafka Cluster
    
```bash
kubectl apply -f kafka-target.yaml -n kafka
```

Mirror Maker

```bash
kubectl apply -f ./mirror-maker/kafka-mirror-maker-2.yaml -n kafka
```
