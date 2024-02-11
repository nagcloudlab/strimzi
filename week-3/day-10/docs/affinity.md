

There are several ways of getting the best out of Apache Kafka:

👉 Make sure Kafka pods are not scheduled on the same node as other performance intensive applications
👉 Make sure Kafka pods are scheduled on the nodes with the most suitable hardware
👉 Use nodes which are dedicated to Kafka only


Pod Anti-Affinity
----------------------

Strimzi supports pod affinity for Kafka, Zookeeper, Kafka Connect and Topic&User Operator. 
You can use it to specify the pods which should never run on the same node as Kafka pods. 
Affinity can be specified in the Custom Resources (CR) under the affinity property.


```yaml
apiVersion: kafka.strimzi.io/v1alpha1
kind: Kafka
metadata:
  name: my-cluster
spec:
  kafka:
    ...
    affinity:
      podAntiAffinity:
        requiredDuringSchedulingIgnoredDuringExecution:
          - labelSelector:
              matchExpressions:
                - key: application
                  operator: In
                  values:
                    - postgresql
                    - mongodb
          topologyKey: "kubernetes.io/hostname"
    ...
  zookeeper:
```

Node affinity
----------------------

Not all nodes are born equal. 
It is quite common that a big Kubernetes or OpenShift cluster consists of many different types of nodes. 
Some are optimized for CPU heavy workloads, some for memory, while other might be optimized for storage (fast local SSDs) or network.
Using different nodes helps to optimize both costs and performance. But as a user of such a heterogeneous cluster you need to be able to schedule your workloads to the right node.

```yaml
apiVersion: kafka.strimzi.io/v1alpha1
kind: Kafka
metadata:
  name: my-cluster
spec:
  kafka:
    ...
    affinity:
      nodeAffinity:
        requiredDuringSchedulingIgnoredDuringExecution:
          nodeSelectorTerms:
            - matchExpressions:
              - key: node-type
                operator: In
                values:
                - fast-network
    ...
  zookeeper:
    ...
```
When needed, you can also combine node affinity together with pod affinity:
  
```yaml
apiVersion: kafka.strimzi.io/v1alpha1
kind: Kafka
metadata:
  name: my-cluster
spec:
  kafka:
    ...
    affinity:
      nodeAffinity:
        requiredDuringSchedulingIgnoredDuringExecution:
          nodeSelectorTerms:
            - matchExpressions:
              - key: node-type
                operator: In
                values:
                - fast-network
      podAntiAffinity:
        requiredDuringSchedulingIgnoredDuringExecution:
          - labelSelector:
              matchExpressions:
                - key: application
                  operator: In
                  values:
                    - postgresql
                    - mongodb
          topologyKey: "kubernetes.io/hostname"
    ...
  zookeeper:
    ...  

```

Dedicated nodes
----------------------

Pod affinity is a great way to influence pod scheduling, but it’s not enough entirely prevent the noisy neighbour problem. With dedicated nodes you can make sure that there will be only Kafka pods and system services such as log collectors or software defined networks sharing the node. 
There will be no other pods scheduled on such machine which could affect or disturb the performance of the Kafka brokers.


kubectl taint nodes az-nodepool1-26366840-vmss000000 dedicated=kafka:NoSchedule
kubectl label nodes az-nodepool1-26366840-vmss000000 dedicated=Kafka

```yaml
apiVersion: kafka.strimzi.io/v1alpha1
kind: Kafka
metadata:
  name: my-cluster
spec:
  kafka:
    ...
    tolerations:
      - key: "dedicated"
        operator: "Equal"
        value: "Kafka"
        effect: "NoSchedule"
    affinity:
      nodeAffinity:
        requiredDuringSchedulingIgnoredDuringExecution:
          nodeSelectorTerms:
          - matchExpressions:
            - key: dedicated
              operator: In
              values:
              - Kafka
    ...
  zookeeper:
    ...
```


Example

To show in detail how the dedicated nodes work, I deployed a 3 node Kafka cluster into my Kubernetes cluster running in AWS. My Kubernetes cluster had 1 master node and 6 worker nodes:

$ kubectl get nodes -o wide
NAME                         STATUS    ROLES     AGE       VERSION   EXTERNAL-IP      OS-IMAGE                KERNEL-VERSION              CONTAINER-RUNTIME
ip-10-0-0-60.ec2.internal    Ready     master    7m        v1.10.5   35.171.124.109   CentOS Linux 7 (Core)   3.10.0-862.3.2.el7.x86_64   docker://18.6.0
ip-10-0-0-124.ec2.internal   Ready     <none>    7m        v1.10.5   34.238.153.57    CentOS Linux 7 (Core)   3.10.0-862.3.2.el7.x86_64   docker://18.6.0
ip-10-0-1-115.ec2.internal   Ready     <none>    7m        v1.10.5   107.23.251.223   CentOS Linux 7 (Core)   3.10.0-862.3.2.el7.x86_64   docker://18.6.0
ip-10-0-2-31.ec2.internal    Ready     <none>    7m        v1.10.5   184.72.149.131   CentOS Linux 7 (Core)   3.10.0-862.3.2.el7.x86_64   docker://18.6.0
ip-10-0-0-236.ec2.internal   Ready     <none>    33s       v1.10.5   54.152.210.78    CentOS Linux 7 (Core)   3.10.0-862.3.2.el7.x86_64   docker://18.6.0
ip-10-0-1-18.ec2.internal    Ready     <none>    35s       v1.10.5   54.152.8.252     CentOS Linux 7 (Core)   3.10.0-862.3.2.el7.x86_64   docker://18.6.0
ip-10-0-2-61.ec2.internal    Ready     <none>    55s       v1.10.5   54.209.246.85    CentOS Linux 7 (Core)   3.10.0-862.3.2.el7.x86_64   docker://18.6.0

I picked 3 nodes on which I set the taints and labels:

$ kubectl taint nodes ip-10-0-0-124.ec2.internal dedicated=Kafka:NoSchedule
$ kubectl taint nodes ip-10-0-1-115.ec2.internal dedicated=Kafka:NoSchedule
$ kubectl taint nodes ip-10-0-2-31.ec2.internal dedicated=Kafka:NoSchedule
$ kubectl label nodes ip-10-0-0-124.ec2.internal dedicated=Kafka
$ kubectl label nodes ip-10-0-1-115.ec2.internal dedicated=Kafka
$ kubectl label nodes ip-10-0-2-31.ec2.internal dedicated=Kafka

As a result, the cluster has 3 dedicated nodes for Kafka andf 3 nodes for Zookeeper, Strimzi operators and Kafka clients. 
Now we can deploy the Strimzi Cluster Operator which is scheduled on one of the nodes which are not dedicated to Kafka:

$ kubectl get pods -o wide
NAME                                        READY     STATUS    RESTARTS   AGE       IP                NODE
strimzi-cluster-operator-586d499cd7-bzqsg   1/1       Running   0          1m        192.168.29.1      ip-10-0-2-61.ec2.internal

With the Cluster Operator running, Kafka can be deployed using following resource:

```yaml
apiVersion: kafka.strimzi.io/v1alpha1
kind: Kafka
metadata:
  name: my-cluster
spec:
  kafka:
    replicas: 3
    readinessProbe:
      initialDelaySeconds: 15
      timeoutSeconds: 5
    livenessProbe:
      initialDelaySeconds: 15
      timeoutSeconds: 5
    config:
      offsets.topic.replication.factor: 3
      transaction.state.log.replication.factor: 3
      transaction.state.log.min.isr: 2
    tolerations:
      - key: "dedicated"
        operator: "Equal"
        value: "Kafka"
        effect: "NoSchedule"
    storage:
      type: ephemeral
    rack:
      topologyKey: "failure-domain.beta.kubernetes.io/zone"
    affinity:
      nodeAffinity:
        requiredDuringSchedulingIgnoredDuringExecution:
          nodeSelectorTerms:
          - matchExpressions:
            - key: dedicated
              operator: In
              values:
              - Kafka
  zookeeper:
    replicas: 3
    readinessProbe:
      initialDelaySeconds: 15
      timeoutSeconds: 5
    livenessProbe:
      initialDelaySeconds: 15
      timeoutSeconds: 5
    storage:
      type: ephemeral
  topicOperator: {}
```

The Cluster Operator will deploy the StatfulSets for Zookeeper and Kafka as well as the Topic Operator. But only the Kafka pods will be scheduled to the dedicated nodes:

$ kubectl get pods -o wide
NAME                                        READY     STATUS    RESTARTS   AGE       IP                NODE
my-cluster-kafka-0                          2/2       Running   0          2m        192.168.158.65    ip-10-0-1-115.ec2.internal
my-cluster-kafka-1                          2/2       Running   0          2m        192.168.221.65    ip-10-0-2-31.ec2.internal
my-cluster-kafka-2                          2/2       Running   0          2m        192.168.226.67    ip-10-0-0-124.ec2.internal
my-cluster-topic-operator-fb6cb47d-qqjrz    2/2       Running   0          1m        192.168.180.194   ip-10-0-1-18.ec2.internal
my-cluster-zookeeper-0                      2/2       Running   0          2m        192.168.180.193   ip-10-0-1-18.ec2.internal
my-cluster-zookeeper-1                      2/2       Running   0          2m        192.168.17.65     ip-10-0-0-236.ec2.internal
my-cluster-zookeeper-2                      2/2       Running   0          2m        192.168.29.2      ip-10-0-2-61.ec2.internal
strimzi-cluster-operator-586d499cd7-bzqsg   1/1       Running   0          9m        192.168.29.1      ip-10-0-2-61.ec2.internal

