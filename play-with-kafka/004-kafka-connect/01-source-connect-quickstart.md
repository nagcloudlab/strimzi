



get all connectors

```bash
curl http://localhost:8083/connectors | jq
```


create a file source connector
```bash

curl -X POST -H "Content-Type: application/json" --data '{"name": "quickstart-file-source", "config": {"connector.class":"FileStreamSource", "tasks.max":"1", "file":"test1.txt", "topic":"test1"}}' http://localhost:8083/connectors | jq


curl -X POST -H "Content-Type: application/json" --data '{"name": "quickstart-file-source-2", "config": {"connector.class":"FileStreamSource", "tasks.max":"1", "file":"test2.txt", "topic":"test2"}}' http://localhost:8083/connectors | jq

```


check the connector

```bash
curl http://localhost:8083/connectors/quickstart-file-source-2/status | jq
```


stop the connector

```bash
curl -X DELETE http://localhost:8083/connectors/quickstart-file-source-2
```


Get connector config

```bash
curl http://localhost:8083/connectors/quickstart-file-source/config | jq
```

Set Connector Configurations

```bash
curl -X PUT -H "Content-Type: application/json" --data '{"connector.class":"FileStreamSource", "tasks.max":"0", "file":"test.txt", "topic":"test"}' http://localhost:8083/connectors/quickstart-file-source/config | jq
```

Pause the connector

```bash
curl -X PUT http://localhost:8083/connectors/quickstart-file-source/pause
```

Resume the connector

```bash
curl -X PUT http://localhost:8083/connectors/quickstart-file-source/resume
```

Restart the connector

```bash
curl -X POST http://localhost:8083/connectors/quickstart-file-source/restart
```

Get connector tasks

```bash
curl http://localhost:8083/connectors/quickstart-file-source/tasks | jq
```

Get connector task status

```bash
curl http://localhost:8083/connectors/quickstart-file-source/tasks/0/status | jq
```

Get connector task config

```bash
curl http://localhost:8083/connectors/quickstart-file-source/tasks/0/config | jq
```

Restart connector task

```bash
curl -X POST http://localhost:8083/connectors/quickstart-file-source/tasks/0/restart
```

---

List connector Plugins
    
```bash
curl http://localhost:8083/connector-plugins | jq
```

Validate connector config

```bash
curl -X PUT http://localhost:8083/connector-plugins/quickstart-file-source/config/validate
```