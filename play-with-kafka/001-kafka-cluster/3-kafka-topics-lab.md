


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