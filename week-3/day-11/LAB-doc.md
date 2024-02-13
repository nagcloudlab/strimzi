


- Kafka upgrades are performed by the Cluster Operator through rolling updates of the Kafka nodes.
- If you encounter any issues with the new version, Strimzi can be downgraded to the previous version.


Upgrade without downtime

- For topics configured with high availability (replication factor of at least 3 and evenly distributed partitions), the upgrade process should not cause any downtime for consumers and producers.

- The upgrade triggers rolling updates, where brokers are restarted one by one at different stages of the process. 
- During this time, overall cluster availability is temporarily reduced, which may increase the risk of message loss in the event of a broker failure.


A. Required upgrade sequence
----------------------------

- To upgrade brokers and clients without downtime, you must complete the Strimzi upgrade procedures in the following order:

1. Make sure your Kubernetes cluster version is supported.

    - Strimzi 0.39.0 requires Kubernetes 1.21 and later.
    - You can upgrade Kubernetes with minimal downtime.

2. Upgrade the Cluster Operator.

3. Upgrade Kafka depending on the cluster configuration:

    - If using Kafka in KRaft mode, update the Kafka version and spec.kafka.metadataVersion to upgrade all Kafka brokers and client applications.

    - If using ZooKeeper-based Kafka, update the Kafka version and inter.broker.protocol.version to upgrade all Kafka brokers and client applications.

From Strimzi 0.39, upgrades and downgrades between KRaft-based clusters are supported.




B. Strimzi upgrade paths
------------------------

1. Incremental upgrade
2. Multi-version upgrade



👉 Support for Kafka versions when upgrading



C. Upgrading Kubernetes with minimal downtime
--------------------------------------------


1. Configure pod disruption budgets

2. Roll pods using one of these methods:

    - Use the AMQ Streams Drain Cleaner (recommended)
    - Apply an annotation to your pods to roll them manually


Rolling pods using the Strimzi Drain Cleaner
Rolling pods manually while keeping topics available


D. Upgrading the Cluster Operator
-----------------------------------

 - The availability of Kafka clusters managed by the Cluster Operator is not affected by the upgrade operation.

1. Take note of any configuration changes made to the existing Cluster Operator resources (in the /install/cluster-operator directory). Any changes will be overwritten by the new version of the Cluster Operator.
2. Update your custom resources to reflect the supported configuration options available for Strimzi version 0.39.0.
3. Update the Cluster Operator.
    - Modify the installation files for the new Cluster Operator version according to the namespace the Cluster Operator is running in.
    - If you modified one or more environment variables in your existing Cluster Operator Deployment, edit the install/cluster-operator/060-Deployment-strimzi-cluster-operator.yaml file to use those environment variables.
4. When you have an updated configuration, deploy it along with the rest of the installation resources:
```bash
kubectl replace -f install/cluster-operator
```
Wait for the rolling updates to complete.    

5. If the new Operator version no longer supports the Kafka version you are upgrading from, the Cluster Operator returns an error message to say the version is not supported. Otherwise, no error message is returned.

5. Get the image for the Kafka pod to ensure the upgrade was successful:
```bash
kubectl get pod -n my-kafka-namespace -l strimzi.io/name=my-kafka-cluster -o=jsonpath='{.items[*].spec.containers[*].image}'
```


E. Upgrading Kafka when using ZooKeeper
---------------------------------------

If you are using a ZooKeeper-based Kafka cluster, an upgrade requires an update to the Kafka version and the inter-broker protocol version.

https://strimzi.io/docs/operators/latest/deploying#assembly-upgrade-zookeeper-str




