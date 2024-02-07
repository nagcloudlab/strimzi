
Generating CA certificates: 
Strimzi can automatically generate a self-signed CA certificate, which is then used to sign broker and client certificates.

Creating broker certificates: 
Each Kafka broker in the cluster receives a TLS certificate signed by the CA, ensuring that clients can verify the broker's identity.

Issuing client certificates: 
Clients can also be issued certificates, enabling mutual TLS authentication where both the client and the broker verify each other's identity.



------------------------------------------------------------------------
demo-1: Deploy a Kafka Cluster with TLS
------------------------------------------------------------------------

Configuration of TLS Encryption

Broker configuration: 
Strimzi configures Kafka brokers to use TLS for all communication. 
This includes inter-broker communication and communication with clients.

Client configuration: 
Clients must be configured to use TLS when connecting to the Kafka cluster. 
This involves setting up the client's truststore and keystore to trust the CA certificate and present the client's certificate, respectively.


Step 1: Deploy a Certificate Authority (CA)

👉  Create a CA to sign your broker and client certificates. 
👉  Strimzi can automatically handle this for you when you deploy a Kafka cluster, 
    so there's no separate manual step required unless you opt for using   an external CA.


Step 2: Configure the Kafka Cluster for TLS

kubectl apply -f kafka-tls.yaml -n kafka


Step 4: Configure Kafka Clients for TLS
👉   To communicate with the Kafka cluster over TLS, clients must trust the CA that signed the broker's certificate and optionally use a client certificate for authentication if mutual TLS is desired.


export CLUSTER_NAME=my-cluster
kubectl get secret $CLUSTER_NAME-cluster-ca-cert -n kafka -o jsonpath='{.data.ca\.crt}' | base64 -d > ca.crt
kubectl get secret $CLUSTER_NAME-cluster-ca-cert -n kafka -o jsonpath='{.data.ca\.password}' | base64 --decode > ca.password

export CERT_FILE_PATH=ca.crt
export CERT_PASSWORD_FILE_PATH=ca.password
export PASSWORD=`cat $CERT_PASSWORD_FILE_PATH`
export CA_CERT_ALIAS=strimzi-kafka-cert
sudo keytool -importcert -alias $CA_CERT_ALIAS -file $CERT_FILE_PATH -keystore kafka-truststore.jks -keypass $PASSWORD
sudo keytool -list -alias $CA_CERT_ALIAS -keystore kafka-truststore.jks


Extract the LoadBalancer public IP for Kafka cluster from AKS

export KAFKA_CLUSTER_NAME=my-cluster
kubectl get service/${KAFKA_CLUSTER_NAME}-kafka-listener2-bootstrap --output=jsonpath={.status.loadBalancer.ingress[0].ip} -n kafka


------------------------------------------------------------------------
demo-2: mTLS with Kafka
------------------------------------------------------------------------


kubectl apply -f kafka-tls.yaml -n kafka

export CLUSTER_NAME=my-cluster
kubectl get configmap/${CLUSTER_NAME}-kafka-0 -o yaml -n kafka

kubectl get deployment $CLUSTER_NAME-entity-operator -n kafka
kubectl get pod -l=app.kubernetes.io/name=entity-operator -n kafka


👉Create Kafka User for mTLS

Create a KafkaUser resource for mTLS authentication. 
Strimzi will automatically generate a client certificate for this user.

kubectl apply -f kafka-user.yaml -n kafka



Extract and Use Client Certificates

After the KafkaUser is created, Strimzi generates a secret containing the client's certificate and key. 
You need to extract these to use them for authentication.


kubectl get secret/my-user -o yaml -n kafka

export KAFKA_USER_NAME=my-user
kubectl -n kafka get secret $KAFKA_USER_NAME -o jsonpath='{.data.user\.crt}' | base64 --decode > user.crt 
kubectl -n kafka get secret $KAFKA_USER_NAME -o jsonpath='{.data.user\.key}' | base64 --decode > user.key
kubectl -n kafka get secret $KAFKA_USER_NAME -o jsonpath='{.data.user\.p12}' | base64 --decode > user.p12
kubectl -n kafka get secret $KAFKA_USER_NAME -o jsonpath='{.data.user\.password}' | base64 --decode > user.password


Import the entry in user.p12 into another keystore

export USER_P12_FILE_PATH=user.p12
export USER_KEY_PASSWORD_FILE_PATH=user.password
export KEYSTORE_NAME=kafka-keystore.jks
export KEYSTORE_PASSWORD=foobar
export PASSWORD=`cat $USER_KEY_PASSWORD_FILE_PATH`

sudo keytool -importkeystore -deststorepass $KEYSTORE_PASSWORD -destkeystore $KEYSTORE_NAME -srckeystore $USER_P12_FILE_PATH -srcstorepass $PASSWORD -srcstoretype PKCS12

sudo keytool -list -alias $KAFKA_USER_NAME -keystore $KEYSTORE_NAME



export KAFKA_CLUSTER_NAME=my-cluster
kubectl get service/${KAFKA_CLUSTER_NAME}-kafka-tls-bootstrap --output=jsonpath={.status.loadBalancer.ingress[0].ip} -n kafka



------------------------------------------------------------------------

Security Best Practices
When using TLS encryption with Strimzi, it's important to follow best practices:

Regularly rotate certificates to reduce the risk of compromise.
Use strong cipher suites to ensure the encryption is resilient against attacks.
Keep your Strimzi and Kafka versions up to date to benefit from the latest security features and fixes.

------------------------------------------------------------------------

