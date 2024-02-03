
---
Deploy a ZooKeeper-based Kafka cluster.
---

To deploy an ephemeral cluster:
```bash
kubectl apply -f ../strimzi/examples/kafka/kafka-ephemeral.yaml -n kafka
```

To deploy a persistent cluster:
```bash
kubectl apply -f ../strimzi/examples/kafka/kafka-persistent.yaml -n kafka
```

Check the status of the deployment:
```bash
kubectl get pods -n kafka -w
```

---
List of Kafka cluster resources
https://strimzi.io/docs/operators/latest/deploying#ref-list-of-kafka-cluster-resources-str
---

