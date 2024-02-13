

👉 strimzi-0.38.0

kubectl delete namespace kafka
kubectl create namespace kafka

sed -i 's/namespace: .*/namespace: kafka/' ./strimzi-0.38.0/install/cluster-operator/*RoleBinding*.yaml
kubectl apply -f ./strimzi-0.38.0/install/cluster-operator -n kafka
kubectl logs deployment/strimzi-cluster-operator -n kafka 

kubectl delete -f ./kafka.yaml -n kafka
kubectl apply -f ./kafka.yaml -n kafka
kubectl get pods my-cluster-kafka-0 -o jsonpath='{.spec.containers[0].image}' -n kafka


👉 strimzi-0.39.0

kubectl get kafkatopics -n kafka -o yaml > kafkatopics-backup.yaml
sed -i 's/namespace: .*/namespace: kafka/' ./strimzi-0.39.0/install/cluster-operator/*RoleBinding*.yaml
kubectl replace -f ./strimzi-0.39.0/install/cluster-operator -n kafka

kubectl get deployment strimzi-cluster-operator -n kafka
kubectl logs deployment/strimzi-cluster-operator -n kafka --since=10m

kubectl apply -f ./kafka.yaml -n kafka
kubectl get pods my-cluster-kafka-0 -o jsonpath='{.spec.containers[0].image}' -n kafka
