
---
Deploy Kafka Connect to your Kubernetes cluster.
---

```bash
kubectl apply -f ../strimzi/examples/connect/kafka-connect.yaml -n kafka
```

Check the status of the deployment:
```bash
kubectl get pods -n kafka -w
```

---
Adding Kafka Connect connectors
---
https://strimzi.io/docs/operators/latest/deploying#using-kafka-connect-with-plug-ins-str


```bash
kubectl apply -f ../strimzi/examples/connect/source-connector.yaml -n kafka
kubectl apply -f ../strimzi/examples/connect/sink-connector.yaml -n kafka

kubectl get kctr --selector strimzi.io/cluster=my-connect-cluster -o name -n kafka
```

In the container, execute kafka-console-consumer.sh to read the messages that were written to the topic by the source connector:
```bash
kubectl exec my-cluster-kafka-0 -n kafka -i -t -- bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic my-topic --from-beginning 
```

