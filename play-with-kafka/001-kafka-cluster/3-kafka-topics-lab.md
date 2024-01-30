

create topic
```bash
kafka-topics --create --topic test --bootstrap-server localhost:9092 --replication-factor 1 --partitions 3
```

list topic
```bash
kafka-topics --list --bootstrap-server localhost:9092
```

describe topic
```bash
kafka-topics --describe --topic test --bootstrap-server localhost:9092
```

delete topic
```bash
kafka-topics --delete --topic test --bootstrap-server localhost:9092
```

alter topic
```bash
kafka-topics --alter --topic test --bootstrap-server localhost:9092 --partitions 4
```

override topic config
```bash
kafka-topics --alter --topic test --bootstrap-server localhost:9092 --config retention.ms=1000
```

reset topic config
```bash
kafka-configs --alter --entity-type topics --entity-name test --bootstrap-server localhost:9092 --delete-config retention.ms
```
