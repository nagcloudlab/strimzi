

Platform : Ubuntu 22.04


install java 8 and above
```bash
sudo apt update
sudo apt install openjdk-11-jdk
java -version
```

---

download confluent kafka community edition
```bash
wget https://packages.confluent.io/archive/7.0/confluent-community-7.0.0.tar.gz
tar -xf confluent-community-7.0.0.tar.gz
cd confluent-7.0.0
```

---

export confluent home-path in bashrc
```bash
code ~/.bashrc
export PATH=$PATH:$(pwd)/bin
```

---

start zookeeper
```bash
zookeeper-server-start etc/kafka/zookeeper.properties
```

---

# Single-broker kafka-cluster


start kafka
```bash
kafka-server-start etc/kafka/server.properties
```


---

# multi-broker kafka-cluster


create broker 1
```bash
cp etc/kafka/server.properties etc/kafka/server-1.properties
```

update broker 1
```bash
code etc/kafka/server-1.properties
```


broker.id=1
listeners=PLAINTEXT://:9093
log.dirs=/tmp/kafka-logs-1


create broker 2
```bash
cp etc/kafka/server.properties etc/kafka/server-2.properties
```

update broker 2
```bash
code etc/kafka/server-2.properties
```

broker.id=2
listeners=PLAINTEXT://:9094
log.dirs=/tmp/kafka-logs-2


create broker 3
```bash
cp etc/kafka/server.properties etc/kafka/server-3.properties
```

update broker 3
```bash
code etc/kafka/server-3.properties
``` 

broker.id=3
listeners=PLAINTEXT://:9095
log.dirs=/tmp/kafka-logs-3


start broker 1
```bash
kafka-server-start etc/kafka/server-1.properties
```

start broker 2
```bash
kafka-server-start etc/kafka/server-2.properties
```

start broker 3
```bash
kafka-server-start etc/kafka/server-3.properties
```

---
