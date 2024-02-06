

Configuring Kafka and ZooKeeper storage

Strimzi provides flexibility in configuring the data storage options of Kafka and ZooKeeper.

The supported storage types are:

👉 Ephemeral (Recommended for development only)
👉 Persistent
👉 JBOD (Kafka only; not available for ZooKeeper)




Data storage considerations

👉 For Strimzi to work well, an efficient data storage infrastructure is essential. 
👉 We strongly recommend using block storage. Strimzi is only tested for use with block  storage. 
👉 File storage, such as NFS, is not tested and there is no guarantee it will work.




Choose one of the following options for your block storage:

👉 A cloud-based block storage solution, such as Amazon Elastic Block Store (EBS)
👉 Persistent storage using local persistent volumes
👉 Storage Area Network (SAN) volumes accessed by a protocol such as Fibre Channel or iSCSI




File systems

👉 Kafka uses a file system for storing messages. 
👉 Strimzi is compatible with the XFS and ext4 file systems, which are commonly used with Kafka.




Disk usage

👉 Use separate disks for Apache Kafka and ZooKeeper.
👉 Solid-state drives (SSDs), though not essential, can improve the performance of Kafka in large clusters where data is sent to and received from multiple topics asynchronously. 
👉 SSDs are particularly effective with ZooKeeper, which requires fast, low latency data access.



1. Ephemeral storage

👉 Ephemeral data storage is transient. 
👉 All pods on a node share a local ephemeral storage space. 
👉 Data is retained for as long as the pod that uses it is running. 
👉 The data is lost when a pod is deleted.

Because of its transient nature, ephemeral storage is only recommended for development and testing.
Ephemeral storage is not suitable for single-node ZooKeeper clusters or Kafka topics with a replication factor of 1.



Example ephemeral storage configuration
```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: Kafka
metadata:
  name: my-cluster
spec:
  kafka:
    storage:
      type: ephemeral
    # ...
  zookeeper:
    storage:
      type: ephemeral
    # ...
```

Mount path of Kafka log directories
/var/lib/kafka/data/kafka-logIDX


2. Persistent storage

👉 Persistent storage is suitable for production environments.
👉 It retains data even when a pod is deleted.
👉 Persistent storage is provided by a PersistentVolumeClaim (PVC) that is bound to a PersistentVolume (PV) in the cluster.


You have two options for specifying the storage type:

👉 Single volume
👉 JBOD (Just a Bunch Of Disks)

storage.type: persistent-claim
If you choose persistent-claim as the storage type, a single persistent storage volume is defined.

storage.type: jbod
When you select jbod as the storage type, you have the flexibility to define an array of persistent storage volumes using unique IDs.


In a production environment, it is recommended to configure the following:

🖐️ For Kafka or node pools, set storage.type to jbod with one or more persistent volumes.
🖐️ For ZooKeeper, set storage.type as persistent-claim for a single persistent volume.


Persistent storage also has the following configuration options:

👉 id (optional)A storage identification number. This option is mandatory for storage volumes defined in a JBOD storage declaration. Default is 0.
👉 size: The size of the persistent volume.
👉 class: The storage class to use for the persistent volume.
👉 deleteClaim: When set to true, the PersistentVolumeClaim (PVC) is deleted when the Kafka or ZooKeeper cluster is deleted.
👉 selector (optional)
Configuration to specify a specific PV. Provides key:value pairs representing the labels of the volume selected.


Example persistent storage configuration
```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: Kafka
metadata:
  name: my-cluster
spec:
  kafka:
    storage:
      type: jbod
      volumes:
      - id: 0
        type: persistent-claim
        size: 100Gi
        deleteClaim: false
      - id: 1
        type: persistent-claim
        size: 100Gi
        deleteClaim: false
      - id: 2
        type: persistent-claim
        size: 100Gi
        deleteClaim: false
    # ...
  zookeeper:
    storage:
      type: persistent-claim
      size: 1000Gi
    # ...
```

Example persistent storage configuration with specific storage class

```yaml
# ...
storage:
  type: persistent-claim
  size: 500Gi
  class: my-storage-class
# ...
```

Example persistent storage configuration with selector
```yaml
# ...
storage:
  type: persistent-claim
  size: 1Gi
  selector:
    hdd-type: ssd
  deleteClaim: true
# ...

```


Example storage configuration with class overrides

```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: Kafka
metadata:
  labels:
    app: my-cluster
  name: my-cluster
  namespace: myproject
spec:
  # ...
  kafka:
    replicas: 3
    storage:
      type: jbod
      volumes:
      - id: 0
        type: persistent-claim
        size: 100Gi
        deleteClaim: false
        class: my-storage-class
        overrides:
        - broker: 0
          class: my-storage-class-zone-1a
        - broker: 1
          class: my-storage-class-zone-1b
        - broker: 2
          class: my-storage-class-zone-1c
      # ...
  # ...
  zookeeper:
    replicas: 3
    storage:
      deleteClaim: true
      size: 100Gi
      type: persistent-claim
      class: my-storage-class
      overrides:
        - broker: 0
          class: my-storage-class-zone-1a
        - broker: 1
          class: my-storage-class-zone-1b
        - broker: 2
          class: my-storage-class-zone-1c
  # ...
```

As a result of the configured overrides property, the volumes use the following storage classes:

The persistent volumes of ZooKeeper node 0 use my-storage-class-zone-1a.
The persistent volumes of ZooKeeper node 1 use my-storage-class-zone-1b.
The persistent volumes of ZooKeeper node 2 use my-storage-class-zone-1c.
The persistent volumes of Kafka broker 0 use my-storage-class-zone-1a.
The persistent volumes of Kafka broker 1 use my-storage-class-zone-1b.
The persistent volumes of Kafka broker 2 use my-storage-class-zone-1c.




Resizing persistent volumes

👉 Persistent volumes can be resized by updating the size field in the persistent volume claim (PVC) definition.
👉 The size field can be updated to a larger value, but not to a smaller value.
👉 The PVC must be in the Bound state to be resized.
👉 The resize operation is not immediate and may take some time to complete.

👉 Storage reduction is only possible when using multiple disks per broker.
👉 You can remove a disk after moving all partitions on the disk to other volumes within the same broker (intra-broker) or to other brokers within the same cluster (intra-cluster).


Kafka configuration to increase the volume size to 2000Gi
```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: Kafka
metadata:
  name: my-cluster
spec:
  kafka:
    # ...
    storage:
      type: persistent-claim
      size: 2000Gi
      class: my-storage-class
    # ...
  zookeeper:
    # ...
```
kubectl apply -f <kafka_configuration_file>

Kubernetes increases the capacity of the selected persistent volumes in response to a request from the Cluster Operator. When the resizing is complete, the Cluster Operator restarts all pods that use the resized persistent volumes. This happens automatically.

👉 The resizing operation is not immediate and may take some time to complete.
kubectl get pv



3. JBOD storage

👉 JBOD (Just a Bunch Of Disks) is a storage configuration that uses multiple disks to store data.
👉 JBOD is only available for Kafka; it is not available for ZooKeeper.
👉 JBOD is suitable for large-scale Kafka clusters that require high throughput and low latency.
👉 JBOD is not suitable for single-node Kafka clusters or Kafka topics with a replication factor of 1.


Example JBOD storage configuration
```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: Kafka
metadata:
  name: my-cluster
spec:
  kafka:
    storage:
      type: jbod
      volumes:
      - id: 0
        type: persistent-claim
        size: 100Gi
        deleteClaim: false
      - id: 1
        type: persistent-claim
        size: 100Gi
        deleteClaim: false
  # ...
```


Adding volumes to JBOD storage

👉 You can add volumes to a JBOD storage configuration by adding a new volume definition to the volumes array.
👉 The id field is mandatory for each volume definition.
👉 The id field must be unique within the volumes array.
👉 The size field is mandatory for each volume definition.
👉 The deleteClaim field is optional and defaults to false.
👉 The type field is mandatory for each volume definition and must be set to persistent-claim.


Example JBOD storage configuration with additional volumes
```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: Kafka
metadata:
  name: my-cluster
spec:
  kafka:
    # ...
    storage:
      type: jbod
      volumes:
      - id: 0
        type: persistent-claim
        size: 100Gi
        deleteClaim: false
      - id: 1
        type: persistent-claim
        size: 100Gi
        deleteClaim: false
      - id: 2
        type: persistent-claim
        size: 100Gi
        deleteClaim: false
    # ...
  zookeeper:
    # ...
```

Removing volumes from JBOD storage

👉 You can remove volumes from a JBOD storage configuration by removing the volume definition from the volumes array.

To avoid data loss, you have to move all partitions before removing the volumes.



Configuring CPU and memory resource limits and requests

👉 You can configure CPU and memory resource limits and requests for Kafka and ZooKeeper.
👉 The resource limits and requests are specified in the Kafka and ZooKeeper configurations.





