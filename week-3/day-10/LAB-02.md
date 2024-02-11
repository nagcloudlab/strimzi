
-----------------------------------------------------
AKS setup
-----------------------------------------------------

az group create --name nag-rg2 --location southindia

az aks create \
    --resource-group nag-rg2 \
    --name nag-aks2 \
    --generate-ssh-keys \
    --node-count 3 

az aks delete -n nag-aks2 --resource-group nag-rg2 --yes
az group delete -n nag-rg2 --yes

kubectl get nodes -o wide
kubectl get nodes --show-labels

--------------------------------------------------------------


-----------------------------------------------------
Deploy Strimzi's kafka Cluster-Operator
-----------------------------------------------------

Deploying the Cluster Operator

kubectl create namespace kafka
sed -i 's/namespace: .*/namespace: kafka/' ./strimzi-0.38.0/install/cluster-operator/*RoleBinding*.yaml
kubectl apply -f ./strimzi-0.38.0/install/cluster-operator -n kafka
kubectl get deployment -n kafka
kubectl get pods -n kafka -o wide
kubectl logs deployment/strimzi-cluster-operator -n kafka 


-----------------------------------------------------
Deploy Kafka
-----------------------------------------------------

kubectl apply -f ./kafka_2.yaml -n kafka


-----------------------------------------------------
Deploy MirrorMaker 2
-----------------------------------------------------

kubectl create secret generic my-cluster-ca-cert --from-file=ca.crt=client-truststore/ca.crt -n kafka
kubectl get secret my-cluster-ca-cert -n kafka -o yaml

kubectl delete -f ./mirror-maker2.yaml -n kafka
kubectl apply -f ./mirror-maker2.yaml -n kafka


kubectl -n kafka run kafka-topic -ti --image=quay.io/strimzi/kafka:0.39.0-kafka-3.6.1 --rm=true --restart=Never -- bin/kafka-topics.sh --bootstrap-server your-cluster-kafka-bootstrap:9092 --list

kubectl -n kafka run kafka-consumer -ti --image=quay.io/strimzi/kafka:0.39.0-kafka-3.6.1 --rm=true --restart=Never -- bin/kafka-console-consumer.sh --bootstrap-server your-cluster-kafka-bootstrap:9092 --topic cluster-a.my-topic --from-beginning