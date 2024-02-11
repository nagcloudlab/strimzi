

Strimzi Mirror Maker 2.0
------------------------

Strimzi Mirror Maker 2.0 is a new replication engine that is part of the Strimzi project. 
It is a complete rewrite of the original Mirror Maker, and it is designed to be more scalable, more reliable, and more flexible. 
It is built on top of the Apache Kafka Connect framework, and it is designed to be used with Strimzi Kafka Connect. 
It is also designed to be used with Strimzi Kafka Bridge, which is a new component that is part of the Strimzi project.

Strimzi Mirror Maker 2.0 is designed to be used in a variety of different scenarios, including:

- Replicating data between different Kafka clusters
- Replicating data between different data centers
- Replicating data between different cloud providers
- Replicating data between different versions of Kafka
- Replicating data between different topics
- Replicating data between different message formats
- Replicating data between different security configurations
- Replicating data between different storage configurations
- Replicating data between different network configurations


The configuration must specify:

👉 Each Kafka cluster
👉 Connection information for each cluster, including authentication
👉 The replication flow and direction
👉 Cluster to cluster
👉 Topic to topic

Default configuration

```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: KafkaMirrorMaker2
metadata:
  name: my-mirror-maker2
spec:
  version: 3.6.1
  connectCluster: "my-cluster-target"
  clusters:
  - alias: "my-cluster-source"
    bootstrapServers: my-cluster-source-kafka-bootstrap:9092
  - alias: "my-cluster-target"
    bootstrapServers: my-cluster-target-kafka-bootstrap:9092
  mirrors:
  - sourceCluster: "my-cluster-source"
    targetCluster: "my-cluster-target"
    sourceConnector: {}
```

👉 You can configure access control for source and target clusters using mTLS or SASL authentication.

👉 You can specify the topics and consumer groups you wish to replicate from a source cluster in the KafkaMirrorMaker2 resource. 
👉 You use the topicsPattern and groupsPattern properties to do this. You can provide a list of names or use a regular expression. 
👉 By default, all topics and consumer groups are replicated if you do not set the topicsPattern and groupsPattern properties. 
👉 You can also replicate all topics and consumer groups by using ".*" as a regular expression. 
👉 However, try to specify only the topics and consumer groups you need to avoid causing any unnecessary extra load on the cluster


Example KafkaMirrorMaker2 custom resource configuration



```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: KafkaMirrorMaker2
metadata:
  name: my-mirror-maker2
spec:
  version: 3.6.1 # (1)
  replicas: 3 # (2)
  connectCluster: "my-cluster-target" # (3)
  clusters: # (4)
  - alias: "my-cluster-source" # (5)
    authentication: # (6)
      certificateAndKey:
        certificate: source.crt
        key: source.key
        secretName: my-user-source
      type: tls
    bootstrapServers: my-cluster-source-kafka-bootstrap:9092 # (7)
    tls: # (8)
      trustedCertificates:
      - certificate: ca.crt
        secretName: my-cluster-source-cluster-ca-cert
  - alias: "my-cluster-target" # (9)
    authentication: # (10)
      certificateAndKey:
        certificate: target.crt
        key: target.key
        secretName: my-user-target
      type: tls
    bootstrapServers: my-cluster-target-kafka-bootstrap:9092 # (11)
    config: # (12)
      config.storage.replication.factor: 1
      offset.storage.replication.factor: 1
      status.storage.replication.factor: 1
    tls: # (13)
      trustedCertificates:
      - certificate: ca.crt
        secretName: my-cluster-target-cluster-ca-cert
  mirrors: # (14)
  - sourceCluster: "my-cluster-source" # (15)
    targetCluster: "my-cluster-target" # (16)
    sourceConnector: # (17)
      tasksMax: 10 # (18)
      autoRestart: # (19)
        enabled: true
      config
        replication.factor: 1 # (20)
        offset-syncs.topic.replication.factor: 1 # (21)
        sync.topic.acls.enabled: "false" # (22)
        refresh.topics.interval.seconds: 60 # (23)
        replication.policy.class: "org.apache.kafka.connect.mirror.IdentityReplicationPolicy" # (24)
    heartbeatConnector: # (25)
      autoRestart:
        enabled: true
      config:
        heartbeats.topic.replication.factor: 1 # (26)
        replication.policy.class: "org.apache.kafka.connect.mirror.IdentityReplicationPolicy"
    checkpointConnector: # (27)
      autoRestart:
        enabled: true
      config:
        checkpoints.topic.replication.factor: 1 # (28)
        refresh.groups.interval.seconds: 600 # (29)
        sync.group.offsets.enabled: true # (30)
        sync.group.offsets.interval.seconds: 60 # (31)
        emit.checkpoints.interval.seconds: 60 # (32)
        replication.policy.class: "org.apache.kafka.connect.mirror.IdentityReplicationPolicy"
    topicsPattern: "topic1|topic2|topic3" # (33)
    groupsPattern: "group1|group2|group3" # (34)
  resources: # (35)
    requests:
      cpu: "1"
      memory: 2Gi
    limits:
      cpu: "2"
      memory: 2Gi
  logging: # (36)
    type: inline
    loggers:
      connect.root.logger.level: INFO
  readinessProbe: # (37)
    initialDelaySeconds: 15
    timeoutSeconds: 5
  livenessProbe:
    initialDelaySeconds: 15
    timeoutSeconds: 5
  jvmOptions: # (38)
    "-Xmx": "1g"
    "-Xms": "1g"
  rack:
    topologyKey: topology.kubernetes.io/zone # (40)
  template: # (41)
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
    connectContainer: # (42)
      env:
        - name: OTEL_SERVICE_NAME
          value: my-otel-service
        - name: OTEL_EXPORTER_OTLP_ENDPOINT
          value: "http://otlp-host:4317"
  tracing:
    type: opentelemetry # (43)
  externalConfiguration: # (44)
    env:
      - name: AWS_ACCESS_KEY_ID
        valueFrom:
          secretKeyRef:
            name: aws-creds
            key: awsAccessKey
      - name: AWS_SECRET_ACCESS_KEY
        valueFrom:
          secretKeyRef:
            name: aws-creds
            key: awsSecretAccessKey
```


Configuring active/active or active/passive modes


active/active cluster configuration

An active/active configuration has two active clusters replicating data bidirectionally. Applications can use either cluster. Each cluster can provide the same data. In this way, you can make the same data available in different geographical locations. As consumer groups are active in both clusters, consumer offsets for replicated topics are not synchronized back to the source cluster.

active/passive cluster configuration

An active/passive configuration has an active cluster replicating data to a passive cluster. The passive cluster remains on standby. You might use the passive cluster for data recovery in the event of system failure.
