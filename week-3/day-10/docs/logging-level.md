

Configuring logging levels
--------------------------


Configure logging levels in the custom resources of Kafka components and Strimzi operators. 

- You can specify the logging levels directly in the spec.logging property of the custom resource. 
- Or you can define the logging properties in a ConfigMap that’s referenced in the custom resource using the configMapKeyRef property.


You specify a logging type in your logging specification:

- inline when specifying logging levels directly
- external when referencing a ConfigMap

Example inline logging configuration
spec:
  # ...
  logging:
    type: inline
    loggers:
      kafka.root.logger.level: INFO

Example external logging configuration
spec:
  # ...
  logging:
    type: external
    valueFrom:
      configMapKeyRef:
        name: my-config-map
        key: my-config-map-key

-----------------------------------------------------
1. Creating a ConfigMap for logging
-----------------------------------------------------

```yaml
kind: ConfigMap
apiVersion: v1
metadata:
  name: logging-configmap
data:
  log4j.properties:
    kafka.root.logger.level="INFO"
```

or

log4j.properties
----------------
kafka.root.logger.level=INFO

kubectl create configmap logging-configmap --from-file=log4j.properties

-----------------------------------------------------
2. Define external logging in the spec of the resource, setting the logging.valueFrom.configMapKeyRef.name to the name of the ConfigMap and logging.valueFrom.configMapKeyRef.key to the key in this ConfigMap.
-----------------------------------------------------

```yaml
spec:
  # ...
  logging:
    type: external
    valueFrom:
      configMapKeyRef:
        name: logging-configmap
        key: log4j.properties
```
-----------------------------------------------------
3. Create or update the resource.
-----------------------------------------------------

kubectl apply -f <kafka_configuration_file>


-----------------------------------------------------



Configuring Cluster Operator logging


