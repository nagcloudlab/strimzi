

Source kafka Cluster

```bash
kubectl apply -f kafka-source.yaml -n kafka
```


Target kafka Cluster
    
```bash
kubectl apply -f kafka-target.yaml -n kafka
```

Mirror Maker

```bash
kubectl apply -f ./mirror-maker/kafka-mirror-maker-2.yaml -n kafka
```
