

rack-awareness
----------------------------

   Strimzi allows you to deploy Kafka brokers across different Kubernetes zones using the `rack-awareness` feature. 
   This feature ensures that Kafka brokers are distributed across different zones to provide high availability and fault tolerance.

   Here’s how you can configure rack-awareness for your Kafka cluster:

1. **Define the zones**:

   You can define the zones in your Kubernetes cluster using the `topology.kubernetes.io/zone` label. 
   This label is automatically applied to nodes in a Kubernetes cluster based on the zone they belong to. 
   You can use this label to define the zones in your Kafka cluster.

2. **Configure the Kafka brokers**:

    You can configure the Kafka brokers to use rack-awareness using the `rack` attribute in the Kafka custom resource. 
    Here’s an example of how you can configure rack-awareness for your Kafka cluster:
    
   ```yaml
   apiVersion: kafka.strimzi.io/v1beta2
   kind: Kafka
   metadata:
   name: my-cluster
   spec:
   kafka:
      replicas: 3
      rack:
         topologyKey: topology.kubernetes.io/zone
      ...
   ```
    
   In this example, we have configured the Kafka brokers to use the `topology.kubernetes.io/zone` label to determine the zones they belong to. 
   This ensures that the Kafka brokers are distributed across different zones in the Kubernetes cluster.

3. **Deploy the Kafka cluster**:

   Once you have configured the Kafka brokers to use rack-awareness, 
   You can deploy the Kafka cluster using the Strimzi operator. 
   The Strimzi operator will ensure that the Kafka brokers are distributed across different zones based on the configuration you have provided.

4. **Verify the deployment**:
   
   You can verify the deployment of the Kafka brokers across different zones using the `kubectl get pods -n <namespace> -o wide` command. 
   This command will show you the zones in which the Kafka brokers are deployed.


