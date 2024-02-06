

Configuring node pools
----------------------

👉  A node pool refers to a distinct group of Kafka nodes within a Kafka cluster. 
👉  Each pool has its own unique configuration, which includes mandatory settings for   the number of replicas, roles, and storage allocation.


Assigning IDs to node pools for scaling operations
--------------------------------------------------

When you create a Kafka cluster, you must assign a unique ID to each node pool. This ID is used to identify the pool when you perform scaling operations, such as adding or removing nodes.


To add a range of IDs, you assign the following annotations to the KafkaNodePool resource:

👉 strimzi.io/next-node-ids to add a range of IDs that are used for new brokers
👉 strimzi.io/remove-node-ids to add a range of IDs for removing existing brokers

During the scaling operation, IDs are used as follows:

👉 Scale up picks up the lowest available ID in the range for the new node.
👉 Scale down removes the node with the highest available ID in the range.


Assigning IDs for scaling up
```bash
kubectl annotate kafkanodepool pool-a strimzi.io/next-node-ids="[0,1,2,10-20,30]"
```
The lowest available ID from this range is used when adding a node to pool-a.

Assigning IDs for scaling down
```bash
kubectl annotate kafkanodepool pool-b strimzi.io/remove-node-ids="[60-50,9,8,7]"
```
The highest available ID from this range is used when removing a node from pool-b.



Adding nodes to a node pool
---------------------------

To add nodes to a node pool, you can use the following command:

```bash
kubectl get pod -o wide -n kafka -w
kubectl scale kafkanodepool pool-a --replicas=4 -n kafka
```

Reassign the partitions after increasing the number of nodes in the node pool.

After scaling up a node pool, you can use the Cruise Control add-brokers mode to move partition replicas from existing brokers to the newly added brokers.


Removing nodes from a node pool
-------------------------------

Reassign the partitions before decreasing the number of nodes in the node pool.

Before scaling down a node pool, you can use the Cruise Control remove-brokers mode to move partition replicas off the brokers that are going to be removed.

To remove nodes from a node pool, you can use the following command:

```bash
kubectl get pod -o wide -n kafka -w
kubectl scale kafkanodepool pool-a --replicas=3 -n kafka
```


Moving nodes between node pools
-------------------------------

This procedure describes how to move nodes between source and target Kafka node pools without downtime. 

In this procedure, we start with two node pools:

pool-a with three replicas is the target node pool
pool-b with three replicas is the source node pool

We scale up pool-a, and reassign partitions and scale down pool-b, which results in the following:

pool-a with four replicas
pool-b with two replicas

Create a new node in the target node pool.
```bash
kubectl scale kafkanodepool pool-a --replicas=4 -n kafka
kubectl get pod -n kafka -w
```
Reassign the partitions from the old node to the new node.

Before scaling down the source node pool, you can use the Cruise Control remove-brokers mode to move partition replicas off the brokers that are going to be removed.

Scale down the source node pool.
```bash
kubectl scale kafkanodepool pool-b --replicas=2 -n kafka
kubectl get pod -n kafka -w
```


Managing storage using node pools
---------------------------------

Create the node pool with its own storage settings.
    
```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: KafkaNodePool
metadata:
  name: pool-a
  labels:
    strimzi.io/cluster: my-cluster
spec:
  replicas: 3
  storage:
    type: jbod
    volumes:
      - id: 0
        type: persistent-claim
        size: 500Gi
        class: gp2-ebs
    ...    
```  

Apply the node pool configuration for pool-a
```bash
kubectl apply -f pool-a.yaml
``` 

To migrate to a new storage class, create a new node pool with the required storage configuration:
```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: KafkaNodePool
metadata:
  name: pool-b
  labels:
    strimzi.io/cluster: my-cluster
spec:
  roles:
    - broker
  replicas: 3
  storage:
    type: jbod
    volumes:
      - id: 0
        type: persistent-claim
        size: 1Ti
        class: gp3-ebs
  # ... 
```


Reassign the partitions from pool-a to pool-b.

When migrating to a new storage configuration, you can use the Cruise Control remove-brokers mode to move partition replicas off the brokers that are going to be removed.

After the reassignment process is complete, delete the old node pool:
```bash
kubectl delete kafkanodepool pool-a -n kafka
```


Managing storage affinity using node pools
------------------------------------------

In situations where storage resources, such as local persistent volumes, are constrained to specific worker nodes, or availability zones, configuring storage affinity helps to schedule pods to use the right nodes

Node pools allow you to configure affinity independently. In this procedure, we create and manage storage affinity for two availability zones: zone-1 and zone-2.

You can configure node pools for separate availability zones, but use the same storage class. We define an all-zones persistent storage class representing the storage resources available in each zone.

The storage class and affinity is specified in node pools representing the nodes in each availability zone:

pool-zone-1
pool-zone-2.


Define the storage class for use with each availability zone:
```yaml
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: all-zones
provisioner: kubernetes.io/my-storage
parameters:
  type: ssd
volumeBindingMode: WaitForFirstConsumer
```


Create node pools representing the two availability zones, specifying the all-zones storage class and the affinity for each zone:
Node pool configuration for zone-1
```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: KafkaNodePool
metadata:
  name: pool-zone-1
  labels:
    strimzi.io/cluster: my-cluster
spec:
  replicas: 3
  storage:
    type: jbod
    volumes:
      - id: 0
        type: persistent-claim
        size: 500Gi
        class: all-zones
  template:
    pod:
      affinity:
        nodeAffinity:
          requiredDuringSchedulingIgnoredDuringExecution:
            nodeSelectorTerms:
              - matchExpressions:
                - key: topology.kubernetes.io/zone
                  operator: In
                  values:
                  - zone-1
  # ...
```

Node pool configuration for zone-2
```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: KafkaNodePool
metadata:
  name: pool-zone-2
  labels:
    strimzi.io/cluster: my-cluster
spec:
  replicas: 4
  storage:
    type: jbod
    volumes:
      - id: 0
        type: persistent-claim
        size: 500Gi
        class: all-zones
  template:
    pod:
      affinity:
        nodeAffinity:
          requiredDuringSchedulingIgnoredDuringExecution:
            nodeSelectorTerms:
              - matchExpressions:
                - key: topology.kubernetes.io/zone
                  operator: In
                  values:
                  - zone-2
  # ...
```

Apply the node pool configuration.

Check the status of the deployment and wait for the pods in the node pools to be created and have a status of READY.

kubectl get pods -n <my_cluster_operator_namespace>




Migrating existing Kafka clusters to use Kafka node pools
---------------------------------------------------------

To migrate an existing Kafka cluster to use Kafka node pools, you can use the following procedure:

Procedure

Create a new KafkaNodePool resource.
Name the resource kafka.
Point a strimzi.io/cluster label to your existing Kafka resource.
Set the replica count and storage configuration to match your current Kafka cluster.
Set the roles to broker.

```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: KafkaNodePool
metadata:
  name: kafka
  labels:
    strimzi.io/cluster: my-cluster
spec:
  replicas: 3
  roles:
    - broker
  storage:
    type: jbod
    volumes:
      - id: 0
        type: persistent-claim
        size: 100Gi
        deleteClaim: false
```
Apply the KafkaNodePool resource:

kubectl apply -f <node_pool_configuration_file>
By applying this resource, you switch Kafka to using node pools.
There is no change or rolling update and resources are identical to how they were before.

