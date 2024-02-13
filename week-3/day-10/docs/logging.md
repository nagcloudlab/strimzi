


 1. Configure your Kafka pods for logging
 2. Enable log forwarding:
    - Using a sidecar container
    - Using Kubernetes logging mechanisms
 3. Configure the external logging system:



Elastic stack (ELK)
--------------------------

Step 1: Configure Elasticsearch and Kibana
Ensure Elasticsearch and Kibana are properly set up and accessible. 
You might need to configure them to accept data from Filebeat, but by default, Elasticsearch is ready to receive data on port 9200.

Step 2: Deploy Filebeat in Kubernetes
Filebeat will be deployed as a DaemonSet to ensure that you have a Filebeat instance running on each node of your Kubernetes cluster. This setup allows Filebeat to collect logs from each node.

Create a Filebeat configuration file (filebeat.yml) that specifies how to collect logs, including logs from Kafka pods if you're focusing on Kafka logging:

```yaml
filebeat.inputs:
- type: container
  paths:
    - /var/lib/docker/containers/*/*.log
  processors:
    - add_kubernetes_metadata:
        in_cluster: true

output.elasticsearch:
  hosts: ["http://elasticsearch:9200"]
```

Create a ConfigMap in Kubernetes to store the Filebeat configuration:
```bash
kubectl create configmap filebeat-config --from-file=filebeat.yml
```


Create a Filebeat DaemonSet in Kubernetes to deploy Filebeat to each node in the cluster:
```yaml
apiVersion: apps/v1
kind: DaemonSet
metadata:
  name: filebeat
  namespace: kube-system
spec:
  selector:
    matchLabels:
      k8s-app: filebeat
  template:
    metadata:
      labels:
        k8s-app: filebeat
    spec:
      containers:
      - name: filebeat
        image: docker.elastic.co/beats/filebeat:7.10.0
        args: [
          "-c", "/etc/filebeat.yml",
          "-e",
        ]
        env:
        - name: ELASTICSEARCH_HOST
          value: "elasticsearch"
        - name: ELASTICSEARCH_PORT
          value: "9200"
        - name: KIBANA_HOST
          value: "kibana"
        volumeMounts:
        - name: config
          mountPath: /etc/filebeat.yml
          readOnly: true
          subPath: filebeat.yml
        - name: varlibdockercontainers
          mountPath: /var/lib/docker/containers
          readOnly: true
      volumes:
      - name: config
        configMap:
          defaultMode: 0600
          name: filebeat-config
      - name: varlibdockercontainers
        hostPath:
          path: /var/lib/docker/containers
```


Audit Logs


```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: Kafka
metadata:
  name: my-cluster
spec:
  kafka:
    version: 2.8.0
    logging:
      type: inline
      loggers:
        kafka.root.logger.level: "INFO"
        kafka.authorizer.logger.level: "INFO"
        kafka.admin.client: "DEBUG"
        kafka.client: "DEBUG"
        kafka.request.logger: "DEBUG"
        org.apache.kafka.clients.admin: "DEBUG"
        org.apache.kafka.clients.consumer: "DEBUG"
        org.apache.kafka.clients.producer: "DEBUG"
    # Other Kafka spec configurations...
```




