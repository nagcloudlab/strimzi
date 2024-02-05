

Download and Install Minikube Binary

curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
sudo install minikube-linux-amd64 /usr/local/bin/minikube

Install Kubectl tool
curl -LO https://storage.googleapis.com/kubernetes-release/release/`curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt`/bin/linux/amd64/kubectl

Start Minikube
minikube start --cpus=4 --memory=10240 --disk-size=30g --driver=docker

--------------------------------------------------------------------------------

kubectl get nodes
kubectl get svc

--------------------------------------------------------------------------------

Deploying the Cluster Operator

kubectl create namespace kafka
sed -i 's/namespace: .*/namespace: kafka/' ./strimzi-0.39/install/cluster-operator/*RoleBinding*.yaml

kubectl create -f ./strimzi-0.39/install/cluster-operator -n kafka
kubectl get deployment -n kafka
kubectl get pods -n kafka

kubectl logs deployment/strimzi-cluster-operator -n kafka -f

--------------------------------------------------------------------------------


Deploying the Kafka Cluster

kubectl create -f ./01-kafka.yaml -n kafka
kubectl get pod -n kafka -w
kubectl get svc -n kafka

--------------------------------------------------------------------------------

Deploying the Kafka Topic
kubectl create -f ./02-kafka-topic.yaml -n kafka

--------------------------------------------------------------------------------

Deploying the Kafka UI

helm repo add kafka-ui https://provectus.github.io/kafka-ui-charts
helm install kafka-ui kafka-ui/kafka-ui --set envs.config.KAFKA_CLUSTERS_0_NAME=local --set envs.config.KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS=my-cluster-kafka-plain-bootstrap:9092 --namespace kafka
kubectl --namespace kafka port-forward kafka-ui-7b6dbf48c4-h5c2d 8080:8080

--------------------------------------------------------------------------------

--------------------------------------------------------------------------------

Updating kafka Custom Resource for Prometheus Monitoring & kafka Exporter

kubectl apply -f ./01-kafka.yaml -n kafka

--------------------------------------------------------------------------------

Deploying the Prometheus Operator


curl -s https://raw.githubusercontent.com/coreos/prometheus-operator/master/bundle.yaml > prometheus-operator-deployment.yaml
sed -E -i '/[[:space:]]*namespace: [a-zA-Z0-9-]*$/s/namespace:[[:space:]]*[a-zA-Z0-9-]*$/namespace: kafka/' prometheus-operator-deployment.yaml
kubectl create -f prometheus-operator-deployment.yaml -n kafka

kubectl get pods -n kafka

--------------------------------------------------------------------------------

Deploying Prometheus

sed -i 's/namespace: .*/namespace: kafka/' ./metrics/prometheus-install/prometheus.yaml
kubectl apply -f ./metrics/prometheus-additional-properties/prometheus-additional.yaml -n kafka

kubectl apply -f ./metrics/prometheus-install/strimzi-pod-monitor.yaml -n kafka
kubectl apply -f ./metrics/prometheus-install/prometheus-rules.yaml -n kafka
kubectl apply -f ./metrics/prometheus-install/prometheus.yaml -n kafka

kubectl get pods -n kafka -w

--------------------------------------------------------------------------------

Deploying Grafana

kubectl apply -f ./metrics/grafana-install/grafana.yaml -n kafka
kubectl get service grafana
kubectl port-forward svc/grafana 3000:3000 -n kafka

--------------------------------------------------------------------------------