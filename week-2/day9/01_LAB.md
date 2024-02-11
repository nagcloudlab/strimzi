

kubectl apply -f cluster-issuer.yaml -n kafka
kubectl apply -f my-cluster-lets-encrypt.yaml -n kafka
kubectl create -f ./strimzi-0.38.0/install/cluster-operator -n kafka
kubectl apply -f ./kafka_v3.yaml -n kafka