


resources
---------------------------

Configuring CPU and memory resources for Strimzi components is crucial for ensuring the stability and performance of your Kafka cluster running on Kubernetes. 
Strimzi allows you to define resource requests and limits for the Kafka brokers, ZooKeeper nodes, and the Strimzi operators themselves. 

Here’s how you can configure these settings:

1. **Kafka brokers and ZooKeeper nodes**: 
You can configure the CPU and memory resources for Kafka brokers and ZooKeeper nodes using the `resources` section in the Kafka and ZooKeeper custom resources. 
   
```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: Kafka
metadata:
  name: my-cluster
spec:
  kafka:
    replicas: 3
    resources:
      requests:
        memory: "4Gi"
        cpu: "2"
      limits:
        memory: "6Gi"
        cpu: "3"
    ...

   ```

In this example, we have configured the Kafka brokers to request 2 CPU and 4Gi memory, with a limit of 3 CPU and 6Gi memory.


2. **Strimzi operators**:
You can also configure the CPU and memory resources for the Strimzi operators using the `resources` section in the operator custom resource.

```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: Kafka
metadata:
  name: my-cluster
spec:
    kafka:
        replicas: 3
        ...
    entityOperator:
        resources:
        requests:
            memory: "1Gi"
            cpu: "0.5"
        limits:
            memory: "2Gi"
            cpu: "1"
        ...
```    

In this example, we have configured the Strimzi operator to request 0.5 CPU and 1Gi memory, with a limit of 1 CPU and 2Gi memory.

By configuring the CPU and memory resources for Strimzi components, you can ensure that your Kafka cluster runs smoothly and efficiently on Kubernetes.

For more information on configuring resources for Strimzi components, refer to the official Strimzi documentation: https://strimzi.io/docs/latest/#assembly-resources-str