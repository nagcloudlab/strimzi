

Specifying affinity, tolerations, and topology spread constraints

Use affinity, tolerations and topology spread constraints to schedule the pods of kafka resources onto nodes. 
Affinity, tolerations and topology spread constraints are configured using the affinity, tolerations, and topologySpreadConstraint properties in following resources:

Kafka.spec.kafka.template.pod
Kafka.spec.zookeeper.template.pod
Kafka.spec.entityOperator.template.pod
KafkaConnect.spec.template.pod
KafkaBridge.spec.template.pod
KafkaMirrorMaker.spec.template.pod
KafkaMirrorMaker2.spec.template.pod


👉 Use pod anti-affinity to avoid critical applications sharing nodes
👉 Use node affinity to schedule workloads onto specific nodes
👉 Use node affinity and tolerations for dedicated nodes


Configuring pod anti-affinity to schedule each Kafka broker on a different worker node
```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: Kafka
spec:
  kafka:
    # ...
    template:
      pod:
        affinity:
          podAntiAffinity:
            requiredDuringSchedulingIgnoredDuringExecution:
              - labelSelector:
                  matchExpressions:
                    - key: strimzi.io/name
                      operator: In
                      values:
                        - CLUSTER-NAME-kafka
                topologyKey: "kubernetes.io/hostname"
    # ...
  zookeeper:
    # ...
    template:
      pod:
        affinity:
          podAntiAffinity:
            requiredDuringSchedulingIgnoredDuringExecution:
              - labelSelector:
                  matchExpressions:
                    - key: strimzi.io/name
                      operator: In
                      values:
                        - CLUSTER-NAME-zookeeper
                topologyKey: "kubernetes.io/hostname"
    # ...
```

Where CLUSTER-NAME is the name of your Kafka custom resource.


If you even want to make sure that a Kafka broker and ZooKeeper node do not share the same worker node, use the strimzi.io/cluster label. For example:

```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: Kafka
spec:
  kafka:
    # ...
    template:
      pod:
        affinity:
          podAntiAffinity:
            requiredDuringSchedulingIgnoredDuringExecution:
              - labelSelector:
                  matchExpressions:
                    - key: strimzi.io/cluster
                      operator: In
                      values:
                        - CLUSTER-NAME
                topologyKey: "kubernetes.io/hostname"
    # ...
  zookeeper:
    # ...
    template:
      pod:
        affinity:
          podAntiAffinity:
            requiredDuringSchedulingIgnoredDuringExecution:
              - labelSelector:
                  matchExpressions:
                    - key: strimzi.io/cluster
                      operator: In
                      values:
                        - CLUSTER-NAME
                topologyKey: "kubernetes.io/hostname"
    # ...
```

Configuring pod anti-affinity in Kafka components
```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: Kafka
spec:
  kafka:
    # ...
    template:
      pod:
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
    # ...
  zookeeper:
    # ...
```


Configuring node affinity in Kafka components


Label the nodes where Strimzi components should be scheduled.
This can be done using kubectl label:
kubectl label node NAME-OF-NODE node-type=fast-network
```yml
apiVersion: kafka.strimzi.io/v1beta2
kind: Kafka
spec:
  kafka:
    # ...
    template:
      pod:
        affinity:
          nodeAffinity:
            requiredDuringSchedulingIgnoredDuringExecution:
              nodeSelectorTerms:
                - matchExpressions:
                  - key: node-type
                    operator: In
                    values:
                    - fast-network
    # ...
  zookeeper:
    # ...
```


Setting up dedicated nodes and scheduling pods on them

Select the nodes which should be used as dedicated.
Make sure there are no workloads scheduled on these nodes.
Set the taints on the selected nodes:
This can be done using kubectl taint:

kubectl taint node NAME-OF-NODE dedicated=Kafka:NoSchedule

Additionally, add a label to the selected nodes as well.
This can be done using kubectl label:

kubectl label node NAME-OF-NODE dedicated=Kafka

Configure the Kafka custom resource to use the taints and tolerations:
```yml
apiVersion: kafka.strimzi.io/v1beta2
kind: Kafka
spec:
  kafka:
    # ...
    template:
      pod:
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
    # ...
  zookeeper:
    # ...
````