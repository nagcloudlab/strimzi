
export CLUSTER_NAME=my-cluster
kubectl get secret $CLUSTER_NAME-cluster-ca-cert -n kafka -o jsonpath='{.data.ca\.crt}' | base64 -d > ./ca.crt
kubectl get secret $CLUSTER_NAME-cluster-ca-cert -n kafka -o jsonpath='{.data.ca\.password}' | base64 --decode > ./ca.password
export CERT_FILE_PATH=./ca.crt
export CERT_PASSWORD_FILE_PATH=./ca.password
export PASSWORD=`cat $CERT_PASSWORD_FILE_PATH`
export CA_CERT_ALIAS=strimzi-kafka-cert
sudo keytool -importcert -alias $CA_CERT_ALIAS -file $CERT_FILE_PATH -keystore ./kafka-truststore.jks -storepass $PASSWORD -noprompt
# sudo keytool -list -alias $CA_CERT_ALIAS -keystore ./SSL/kafka-truststore.jks
