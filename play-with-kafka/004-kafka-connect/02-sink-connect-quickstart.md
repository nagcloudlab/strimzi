



create a file sink connector
```bash
curl -X POST -H "Content-Type: application/json" --data '{"name": "quickstart-file-sink", "config": {"connector.class":"FileStreamSink", "tasks.max":"1", "file":"test_1.txt", "topics":"test1"}}' http://localhost:8083/connectors | jq
```

check connector status

```bash
curl -X GET http://localhost:8083/connectors/quickstart-file-sink/status | jq
```


update connector config

```bash
curl -X PUT -H "Content-Type: application/json" --data '{"connector.class":"FileStreamSink", "tasks.max":"1", "file":"test_.txt", "topics":"test"}' http://localhost:8083/connectors/quickstart-file-sink/config | jq
```
