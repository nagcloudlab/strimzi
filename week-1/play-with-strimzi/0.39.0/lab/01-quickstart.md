

---
Deploy Strimzi using installation files
---

```bash
kubectl create namespace kafka
kubectl apply -f 'https://strimzi.io/install/latest?namespace=kafka' -n kafka
kubectl get pod -n kafka -w
kubectl logs deployment/strimzi-cluster-operator -n kafka
```

---
Create An Apache Kafka Cluster
---

```bash
kubectl apply -f ../strimzi/examples/kafka/kafka-ephemeral-single.yaml -n kafka
kubectl get pod -n kafka -w
kubectl wait kafka/my-cluster --for=condition=Ready --timeout=300s -n kafka 
````

---
Send and Receive Messages
---

```bash
kubectl -n kafka run kafka-producer -ti --image=quay.io/strimzi/kafka:0.39.0-kafka-3.6.1 --rm=true --restart=Never -- bin/kafka-console-producer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic my-topic
```

```bash
kubectl -n kafka run kafka-consumer -ti --image=quay.io/strimzi/kafka:0.39.0-kafka-3.6.1 --rm=true --restart=Never -- bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic my-topic --from-beginning
```


---
Deleting Your Apache Kafka Cluster
---

```bash
kubectl -n kafka delete $(kubectl get strimzi -o name -n kafka)
```

---
Deleting the Strimzi cluster operator
---

```bash
kubectl delete -f 'https://strimzi.io/install/latest?namespace=kafka' -n kafka
```


--------------------------------------------------------------------------------

