
rm -rf ssl
mkdir ssl
export CLUSTER_NAME=my-cluster
kubectl get secret $CLUSTER_NAME-cluster-ca-cert -n kafka -o jsonpath='{.data.ca\.crt}' | base64 -d > ./ssl/ca.crt
kubectl get secret $CLUSTER_NAME-cluster-ca-cert -n kafka -o jsonpath='{.data.ca\.password}' | base64 --decode > ./ssl/ca.password
export CERT_FILE_PATH=./ssl/ca.crt
export CERT_PASSWORD_FILE_PATH=./ssl/ca.password
export PASSWORD=`cat $CERT_PASSWORD_FILE_PATH`
export CA_CERT_ALIAS=strimzi-kafka-cert
sudo keytool -importcert -alias $CA_CERT_ALIAS -file $CERT_FILE_PATH -keystore ./ssl/kafka-truststore.jks -keypass $PASSWORD
sudo keytool -list -alias $CA_CERT_ALIAS -keystore kafka-truststore.jks
