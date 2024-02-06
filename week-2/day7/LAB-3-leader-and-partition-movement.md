


```bash
kubectl apply -f ./kafka.yaml -n kafka
kubectl get pods -n kafka
```

```bash
kubectl apply -f ./cruise-control/kafka-rebalance-add-brokers.yaml -n kafka
kubectl get kafkarebalance -n kafka -w
kubectl annotate kafkarebalances.kafka.strimzi.io kafka-rebalance-add-brokersv2 strimzi.io/rebalance=approve -n kafka

kubectl apply -f ./cruise-control/kafka-rebalance-remove-brokers.yaml -n kafka
kubectl get kafkarebalance -n kafka -w
kubectl annotate kafkarebalances.kafka.strimzi.io kafka-rebalance-remove-brokersv2 strimzi.io/rebalance=approve -n kafka

kubectl apply -f ./cruise-control/kafka-rebalance-full -n kafka
kubectl annotate kafkarebalances.kafka.strimzi.io my-full-rebalance strimzi.io/rebalance=approve -n kafka

```



kafka rebalance remove brokers
```bash
kubectl apply -f ./cruise-control/kafka-rebalance-remove-brokers.yaml -n kafka
kubectl get kafkarebalance -n kafka
kubectl annotate kafkarebalances.kafka.strimzi.io my-rebalance strimzi.io/rebalance=approve -n kafka
```



     






https://strimzi.io/blog/2022/09/16/reassign-partitions/
https://strimzi.io/blog/2020/06/15/cruise-control/
