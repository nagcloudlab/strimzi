


deploy the kafka bridge to the cluster
```bash
kubectl apply -f ./bridge/kafka-bridge.yaml -n kafka
kubectl get kafkabridge -n kafka
kubectl get svc -n kafka
```

Create a Kubernetes Ingress
```bash

```

or use port-forward

```bash
kubectl port-forward svc/my-bridge-bridge-service 8181:8080 -n kafka
```


---------------------------------------------------------------------------
```bash
curl -X GET http://localhost:8181/topics | jq
curl -X GET http://localhost:8181/topics/topic1 | jq
```

---------------------------------------------------------------------------

Producing messages
```bash
curl -X POST \
  http://localhost:8181/topics/topic1 \
  -H 'content-type: application/vnd.kafka.json.v2+json' \
  -d '{
    "records": [
        {
            "partition": 0,
            "value": "value-1"
        },
        {
            "partition": 1,
            "value": "value-2"
        }
    ]
}'
```

---------------------------------------------------------------------------

Creating a consumer

```bash
curl -X POST http://localhost:8181/consumers/my-group2 \
  -H 'content-type: application/vnd.kafka.v2+json' \
  -d '{
    "name": "my-consumer2",
    "format": "json",
    "auto.offset.reset": "earliest",
    "enable.auto.commit": false
  }'
```


Subscribing to the topic
```bash
curl -X POST http://localhost:8181/consumers/my-group2/instances/my-consumer2/subscription \
  -H 'content-type: application/vnd.kafka.v2+json' \
  -d '{
    "topics": [
        "topic1"
    ]
}'
```

Consuming messages

```bash
curl -X GET http://localhost:8181/consumers/my-group2/instances/my-consumer2/records \
  -H 'accept: application/vnd.kafka.json.v2+json' | jq
```

Committing offsets
```bash
curl -X POST http://localhost:8181/consumers/my-group2/instances/my-consumer2/offsets \
  -H 'content-type: application/vnd.kafka.v2+json' \
  -d '{
    "offsets": [
        {
            "topic": "topic1",
            "partition": 0,
            "offset": 13
        }
    ]
}'
```

Deleting a consumer
```bash
curl -X DELETE http://localhost:8181/consumers/my-group2/instances/my-consumer2
```


https://strimzi.io/blog/2019/11/05/exposing-http-bridge/
https://strimzi.io/blog/2019/07/19/http-bridge-intro/