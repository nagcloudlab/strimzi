


--------------------------------------------------------------------------------
Minikube
--------------------------------------------------------------------------------

curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
sudo install minikube-linux-amd64 /usr/local/bin/minikube

Install Kubectl tool
curl -LO https://storage.googleapis.com/kubernetes-release/release/`curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt`/bin/linux/amd64/kubectl

Start Minikube
minikube start --cpus=4 --memory=10240 --disk-size=30g --driver=docker

--------------------------------------------------------------------------------
- or -
--------------------------------------------------------------------------------
Azure Aks
--------------------------------------------------------------------------------

az group create -l centralindia -n nag-rg
az aks create -g nag-rg -n nag-aks --node-count 1 --generate-ssh-keys
az group delete -n nag-rg -y

--------------------------------------------------------------------------------
install kubectl with az aks install-cli
https://learn.microsoft.com/en-us/cli/azure/aks?view=azure-cli-latest#az-aks-install-cli
--------------------------------------------------------------------------------

kubectl get nodes
kubectl get svc

--------------------------------------------------------------------------------

Deploying the Cluster Operator

kubectl create namespace kafka
sed -i 's/namespace: .*/namespace: kafka/' ../strimzi-0.39/install/cluster-operator/*RoleBinding*.yaml

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
kubectl --namespace kafka port-forward kafka-ui-7b6dbf48c4-tp4rp 8080:8080

--------------------------------------------------------------------------------

--------------------------------------------------------------------------------

Updating kafka Custom Resource for Prometheus Monitoring & kafka Exporter

kubectl apply -f ./kafka.yaml -n kafka

--------------------------------------------------------------------------------

Deploying the Prometheus Operator


curl -s https://raw.githubusercontent.com/coreos/prometheus-operator/master/bundle.yaml > prometheus-operator-deployment.yaml
sed -E -i '/[[:space:]]*namespace: [a-zA-Z0-9-]*$/s/namespace:[[:space:]]*[a-zA-Z0-9-]*$/namespace: kafka/' prometheus-operator-deployment.yaml
kubectl create -f ./metrics/prometheus-operator-deployment.yaml -n kafka

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
Send and receive messages  ( Producer and Consumer )
--------------------------------------------------------------------------------

kubectl -n kafka get svc

kubectl -n kafka run kafka-producer -ti --image=quay.io/strimzi/kafka:0.39.0-kafka-3.6.1 --rm=true --restart=Never -- bin/kafka-console-producer.sh --bootstrap-server my-cluster-kafka-plain-bootstrap:9092 --topic my-topic

kubectl -n kafka run kafka-consumer -ti --image=quay.io/strimzi/kafka:0.39.0-kafka-3.6.1 --rm=true --restart=Never -- bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka-plain-bootstrap:9092 --topic my-topic --from-beginning

Java Clients

 ==> Refer code given in Repo



--------------------------------------------------------------------------------
Deploying Connectors in Strimzi
--------------------------------------------------------------------------------


Deploying MySQL

docker run -it --rm --name mysql -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=debezium -e MYSQL_USER=mysqluser \
  -e MYSQL_PASSWORD=mysqlpw debezium/example-mysql:1.0

docker run -it --rm --name mysqlterm --link mysql --rm mysql:5.7 sh \
  -c 'exec mysql -h"$MYSQL_PORT_3306_TCP_ADDR" \
  -P"$MYSQL_PORT_3306_TCP_PORT" \
  -uroot -p"$MYSQL_ENV_MYSQL_ROOT_PASSWORD"'

--------------------------------------
Deploy kafkaConnect in Distributed mode (i,e workers)
--------------------------------------

Kafka Connect image

First download and extract the Debezium MySQL connector archive
curl https://repo1.maven.org/maven2/io/debezium/debezium-connector-mysql/1.0.0.Final/debezium-connector-mysql-1.0.0.Final-plugin.tar.gz \
| tar xvz

cat <<EOF >Dockerfile
FROM quay.io/strimzi/kafka:0.39.0-kafka-3.6.1
USER root:root
RUN mkdir -p /opt/kafka/plugins/debezium
COPY ./debezium-connector-mysql/ /opt/kafka/plugins/debezium/
USER 1001
EOF

# You can use your own dockerhub organization
export DOCKER_ORG=tjbentley
docker build . -t ${DOCKER_ORG}/connect-debezium
docker push ${DOCKER_ORG}/connect-debezium


Secure the database credentials
cat <<EOF > debezium-mysql-credentials.properties
mysql_username: debezium
mysql_password: dbz
EOF
kubectl -n kafka create secret generic my-sql-credentials \
  --from-file=debezium-mysql-credentials.properties
rm debezium-mysql-credentials.properties


Create the Connect cluster
cat <<EOF | kubectl -n kafka apply -f -
apiVersion: kafka.strimzi.io/v1beta1
kind: KafkaConnect
metadata:
  name: my-connect-cluster
  annotations:
  # use-connector-resources configures this KafkaConnect
  # to use KafkaConnector resources to avoid
  # needing to call the Connect REST API directly
    strimzi.io/use-connector-resources: "true"
spec:
  image: ${DOCKER_ORG}/connect-debezium
  replicas: 1
  bootstrapServers: my-cluster-kafka-plain-bootstrap:9093
  tls:
    trustedCertificates:
      - secretName: my-cluster-cluster-ca-cert
        certificate: ca.crt
  config:
    config.storage.replication.factor: 1
    offset.storage.replication.factor: 1
    status.storage.replication.factor: 1
    config.providers: file
    config.providers.file.class: org.apache.kafka.common.config.provider.FileConfigProvider
  externalConfiguration:
    volumes:
      - name: connector-config
        secret:
          secretName: my-sql-credentials

EOF


kubectl -n kafka apply -f ./kafka-connect.yaml

-------------------------------------
Deploy the Debezium MySQL connector
-------------------------------------

kubectl -n kafka apply -f ./kafka-connector.yaml

check connectors status
kubectl -n kafka get kctr mysql-inventory-source-connector -o yaml


kubectl -n kafka exec my-cluster-kafka-0 -c kafka -i -t -- bin/kafka-topics.sh --bootstrap-server my-cluster-kafka-plain-bootstrap:9092:9092 --list

--------------------------------------------------------------------------------
Consumer 
--------------------------------------------------------------------------------

kubectl -n kafka exec my-cluster-pool-a-0 -c kafka -i -t -- \
  bin/kafka-console-consumer.sh \
    --bootstrap-server my-cluster-kafka-plain-bootstrap:9092 \
    --topic dbserver1.inventory.customers 

--------------------------------------------------------------------------------
play with mysql database
--------------------------------------------------------------------------------

SELECT * FROM customers;
UPDATE customers SET first_name='Anne Marie' WHERE id=1004;