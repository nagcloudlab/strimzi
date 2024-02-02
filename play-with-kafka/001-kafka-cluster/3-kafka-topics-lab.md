

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



-----------------------------------------------------------------


create topic with 3 partitions
```bash
kafka-topics --create --topic test --bootstrap-server localhost:9092 --replication-factor 1 --partitions 3
```


Manual Partition Assignment ( assume we have brokers 1,2,3 )
create topic with 3 partitions and 2 replicas, place partions on brokers 1 and 2 using the --replica-assignment option
```bash
kafka-topics --create --topic test --bootstrap-server localhost:9092 --replication-factor 2 --partitions 3 --replica-assignment 0:1,1:2,2:0
```

-----------------------------------------------------------------