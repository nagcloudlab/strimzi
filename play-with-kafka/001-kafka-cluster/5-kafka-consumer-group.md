

to list consumer groups
```bash
kafka-consumer-groups --bootstrap-server localhost:9092 --list
```

to describe consumer group
```bash
kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group my-first-consumer-group
```

to delete consumer group
```bash
kafka-consumer-groups --bootstrap-server localhost:9092 --delete --group my-first-consumer-group
```

to reset consumer group offset
```bash
kafka-consumer-groups --bootstrap-server localhost:9092 --reset-offsets --group my-first-consumer-group --to-earliest --execute --topic test
```

to reset consumer group offset to specific offset
```bash
kafka-consumer-groups --bootstrap-server localhost:9092 --reset-offsets --group my-first-consumer-group --to-offset 0 --execute --topic test
```

to reset consumer group offset to specific timestamp
```bash
kafka-consumer-groups --bootstrap-server localhost:9092 --reset-offsets --group my-first-consumer-group --to-datetime "2020-01-01T00:00:00.000" --execute --topic test
```

