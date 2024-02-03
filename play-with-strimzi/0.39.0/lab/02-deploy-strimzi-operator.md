
---
Deploy Strimzi using installation files
---

```bash
kubectl create namespace kafka
sed -i 's/namespace: .*/namespace: kafka/' install/cluster-operator/*RoleBinding*.yaml
kubectl apply -f ../strimzi/install/cluster-operator -n kafka
kubectl get pod -n kafka -w
kubectl logs deployment/strimzi-cluster-operator -n kafka
```

---
Deleting the Strimzi cluster operator
---

```bash
kubectl delete -f ../strimzi/install/cluster-operator -n kafka
```


--------------------------------------------------------------------------------

