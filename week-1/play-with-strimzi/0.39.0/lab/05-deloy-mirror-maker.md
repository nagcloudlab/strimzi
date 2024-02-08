

Deploy Source and Target Kafka Clusters

```bash
kubectl apply -f ../strimzi/examples/kafka/kafka-ephemeral-single-source.yaml -n kafka
kubectl apply -f ../strimzi/examples/kafka/kafka-ephemeral-single-target.yaml -n kafka
kubectl get pods -n kafka -w
```



Deploy Kafka MirrorMaker to your Kubernetes cluster:

```bash
kubectl apply -f ../strimzi/examples/mirror-maker/kafka-mirror-maker-2.yaml -n kafka
kubectl get pods -n kafka -w
```
