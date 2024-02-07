https://medium.com/@sushil240289/strimzi-kafka-oauth-using-keycloak-3a987fb90bba



kubectl create ns kafka
kubectl apply -f ./keycloak.yaml -n kafka
kubectl get all -n kafka 


--- Import realm in keycloak 

--- Install Strimzi

kubectl create -f 'https://strimzi.io/install/latest?namespace=kafka' -n kafka

-- Install Kafka cluster

kubectl apply -f ./kafka.yaml -n kafka

kubectl get all -n kafka

kubectl get pods -n kafka
kubectl exec -n kafka --stdin --tty my-cluster-kafka-0 -- /bin/bash

cat > ~/team-a-client.properties << EOF
security.protocol=SASL_PLAINTEXT
sasl.mechanism=OAUTHBEARER
sasl.jaas.config=org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required \
  oauth.client.id="team-a-client" \
  oauth.client.secret="team-a-client-secret" \
  oauth.token.endpoint.uri="http://keycloak:8080/auth/realms/kafka-authz/protocol/openid-connect/token" ;
sasl.login.callback.handler.class=io.strimzi.kafka.oauth.client.JaasClientOauthLoginCallbackHandler
EOF


cd /opt/kafka/bin
ls

./kafka-console-producer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic my-topic --producer.config=/home/kafka/team-a-client.properties
./kafka-console-producer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic a_messages --producer.config=/home/kafka/team-a-client.properties


./kafka-console-consumer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic a_messages --from-beginning --consumer.config=/home/kafka/team-a-client.properties
./kafka-console-consumer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic a_messages --from-beginning --consumer.config=/home/kafka/team-a-client.properties --group a_consumer_group_1