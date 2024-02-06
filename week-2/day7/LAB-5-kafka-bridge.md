


deploy the kafka bridge to the cluster
```bash
kubectl apply -f ./strimzi-0.39/examples/bridge/kafka-bridge.yaml -n kafka
kubectl get kafkabridge -n kafka
kubectl get svc -n kafka
```

Create a Kubernetes Ingress
```bash
kubectl apply -f ./ingress/ingress.yaml -n kafka
```
