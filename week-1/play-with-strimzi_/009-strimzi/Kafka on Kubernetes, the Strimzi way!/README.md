
--------------------------------------------------------------------
Part 1
--------------------------------------------------------------------


Strimzi overview and setup
Kafka cluster installation
Kubernetes resources used/created behind the scenes
Test the Kafka setup using clients within the Kubernetes cluster

--------------------------------------------------------------------


What do I need to try this out?

What do I need to try this out?
kubectl - https://kubernetes.io/docs/tasks/tools/install-kubectl/

I will be using Azure Kubernetes Service (AKS) to demonstrate the concepts, but by and large it is independent of the Kubernetes provider (e.g. feel free to use a local setup such as minikube). If you want to use AKS, all you need is a Microsoft Azure accountwhich you can get for FREE if you don't have one already.

Install Helm
I will be using Helm to install Strimzi. Here is the documentation to install Helm itself - https://helm.sh/docs/intro/install/

You can also use the YAML files directly to install Strimzi. Check out the quick start guide here - https://strimzi.io/docs/quickstart/latest/#proc-install-product-str


(optional) Setup Azure Kubernetes Service
Azure Kubernetes Service (AKS) makes it simple to deploy a managed Kubernetes cluster in Azure. It reduces the complexity and operational overhead of managing Kubernetes by offloading much of that responsibility to Azure. Here are examples of how you can setup an AKS cluster using

Azure CLI, — Azure portal 
Once you setup the cluster, you can easily configure kubectl to point to it

az aks get-credentials --resource-group <CLUSTER_RESOURCE_GROUP> --name <CLUSTER_NAME>


Install Strimzi
Installing Strimzi using Helm is pretty easy:

//add helm chart repo for Strimzi
helm repo add strimzi https://strimzi.io/charts/
//install it! (I have used strimzi-kafka as the release name)
helm install strimzi-kafka strimzi/strimzi-kafka-operator

To delete, simply 
helm uninstall strimzi-kafka

kubectl get all
kubectl get crd | grep strimzi


Time to create a Kafka cluster!

A single node Kafka cluster (and Zookeeper)
Available internally to clients in the same Kubernetes cluster
No encryption, authentication or authorization
No persistence (uses emptyDir volume)

kubectl apply -f kafka.yaml
kubectl get all
kubectl get configmaps/my-kafka-cluster-kafka-0 -o yaml
kubectl get svc
kubectl get secrets

my-kafka-cluster-cluster-ca-cert - Cluster CA certificate to sign Kafka broker certificates, and is used by a connecting client to establish a TLS encrypted connection
my-kafka-cluster-clients-ca-cert - Client CA certificate for a user to sign its own client certificate to allow mutual authentication against the Kafka cluster

kubectl run kafka-producer -ti --image=quay.io/strimzi/kafka:0.39.0-kafka-3.6.1 --rm=true --restart=Never -- bin/kafka-console-producer.sh --bootstrap-server my-kafka-cluster-kafka-bootstrap:9092 --topic my-topic

kubectl run kafka-consumer -ti --image=quay.io/strimzi/kafka:0.39.0-kafka-3.6.1 --rm=true --restart=Never -- bin/kafka-console-consumer.sh --bootstrap-server my-kafka-cluster-kafka-bootstrap:9092 --topic my-topic --from-beginning



--------------------------------------------------------------------
Part 2
--------------------------------------------------------------------


This part will cover these topics:

Expose Kafka cluster to external applications
Apply TLS encryption
Explore Kubernetes resources behind the scenes


kubectl apply -f kafka-external.yaml
kubectl get all

export AKS_RESOURCE_GROUP=aks-rg
export AKS_CLUSTER_NAME=aks
export AKS_LOCATION=centralindia

az network lb list -g MC_${AKS_RESOURCE_GROUP}_${AKS_CLUSTER_NAME}_${AKS_LOCATION}

export CLUSTER_NAME=my-kafka-cluster
kubectl get configmap/${CLUSTER_NAME}-kafka-0 -o yaml

kubectl get secret $CLUSTER_NAME-cluster-ca-cert -o jsonpath='{.data.ca\.crt}' | base64 --decode > ca.crt
kubectl get secret $CLUSTER_NAME-cluster-ca-cert -o jsonpath='{.data.ca\.password}' | base64 --decode > ca.password


export CERT_FILE_PATH=ca.crt
export CERT_PASSWORD_FILE_PATH=ca.password
export PASSWORD=`cat $CERT_PASSWORD_FILE_PATH`
export CA_CERT_ALIAS=strimzi-kafka-cert
sudo keytool -importcert -alias $CA_CERT_ALIAS -file $CERT_FILE_PATH -keystore kafka_truststore.jks -keypass $PASSWORD
sudo keytool -list -alias $CA_CERT_ALIAS -keystore kafka_truststore.jks



Extract the LoadBalancer public IP for Kafka cluster

export KAFKA_CLUSTER_NAME=my-kafka-cluster
kubectl get service/${KAFKA_CLUSTER_NAME}-kafka-external-bootstrap --output=jsonpath={.status.loadBalancer.ingress[0].ip}






--------------------------------------------------------------------
Part 3
--------------------------------------------------------------------



kubectl apply -f ./kafka-tls-auth.yaml
kubectl get all

export CLUSTER_NAME=my-kafka-cluster
kubectl get configmap/${CLUSTER_NAME}-kafka-0 -o yaml

kubectl get deployment $CLUSTER_NAME-entity-operator
kubectl get pod -l=app.kubernetes.io/name=entity-operator

kubectl apply -f ./user-tls-auth.yaml

kubectl get secret/kafka-tls-client-credentials -o yaml


Extract and configure the user credentials

export KAFKA_USER_NAME=kafka-tls-client-credentials
kubectl get secret $KAFKA_USER_NAME -o jsonpath='{.data.user\.crt}' | base64 --decode > user.crt
kubectl get secret $KAFKA_USER_NAME -o jsonpath='{.data.user\.key}' | base64 --decode > user.key
kubectl get secret $KAFKA_USER_NAME -o jsonpath='{.data.user\.p12}' | base64 --decode > user.p12
kubectl get secret $KAFKA_USER_NAME -o jsonpath='{.data.user\.password}' | base64 --decode > user.password

Import the entry in user.p12 into another keystore

export USER_P12_FILE_PATH=user.p12
export USER_KEY_PASSWORD_FILE_PATH=user.password
export KEYSTORE_NAME=kafka-auth-keystore.jks
export KEYSTORE_PASSWORD=foobar
export PASSWORD=`cat $USER_KEY_PASSWORD_FILE_PATH`

sudo keytool -importkeystore -deststorepass $KEYSTORE_PASSWORD -destkeystore $KEYSTORE_NAME -srckeystore $USER_P12_FILE_PATH -srcstorepass $PASSWORD -srcstoretype PKCS12

sudo keytool -list -alias $KAFKA_USER_NAME -keystore $KEYSTORE_NAME




export KAFKA_CLUSTER_NAME=my-kafka-cluster
kubectl get service/${KAFKA_CLUSTER_NAME}-kafka-external-bootstrap --output=jsonpath={.status.loadBalancer.ingress[0].ip}


kubectl apply -f ./topic.yaml


--------------------------------------------------------------------
Part 4
--------------------------------------------------------------------


kubectl apply -f ./kafka-persistence.yaml
kubectl get pvc


kubectl get pvc
kubectl get pv

export CLUSTER_NAME=my-kafka-cluster
kubectl get configmap/${CLUSTER_NAME}-kafka-0 -o yaml


export CLUSTER_NAME=my-kafka-cluster
kubectl get pod/${CLUSTER_NAME}-kafka-0 -o yaml

export STRIMZI_BROKER_ID=0
kubectl exec -it my-kafka-cluster-kafka-0 -- ls -lrt /var/lib/kafka/data-${STRIMZI_BROKER_ID}/kafka-log${STRIMZI_BROKER_ID}

kubectl get sc
kubectl get sc/default -o yaml