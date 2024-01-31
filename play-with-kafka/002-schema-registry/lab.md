


------------------------------------------------------------------------------------------------------------
What is Apicurio Registry?
------------------------------------------------------------------------------------------------------------

👉 Apicurio Registry is a datastore for sharing standard event schemas and API designs 
across event-driven and API architectures.

👉 Client applications can dynamically push or pull the latest schema updates 
to or from Apicurio Registry at runtime without needing to redeploy

👉 Developer teams can query Apicurio Registry for existing schemas required for services already deployed in production, 
and can register new schemas required for new services in development.

👉 You can enable client applications to use schemas and API designs stored in Apicurio Registry by specifying the Apicurio Registry URL 
in your client application code.

👉 Apicurio Registry can store schemas used to serialize and deserialize messages, 
which are referenced from your client applications to ensure that the messages that they send and receive are compatible with those schemas.


------------------------------------------------------------------------------------------------------------
Apicurio Registry capabilities
------------------------------------------------------------------------------------------------------------

Multiple payload formats for standard event schema and API specifications such as Apache Avro, JSON Schema, Google Protobuf, AsyncAPI, OpenAPI, and more.

Pluggable Apicurio Registry storage options in Apache Kafka or PostgreSQL database.

Rules for content validation, compatibility, and integrity to govern how Apicurio Registry content evolves over time.

Apicurio Registry content management using web console, REST API, command line, Maven plug-in, or Java client.

Full Apache Kafka schema registry support, including integration with Kafka Connect for external systems.

Kafka client serializers/deserializers (SerDes) to validate message types at runtime.

Compatibility with existing Confluent schema registry client applications.

Cloud-native Quarkus Java runtime for low memory footprint and fast deployment times.

Operator-based installation of Apicurio Registry on OpenShift.

OpenID Connect (OIDC) authentication using Keycloak.


------------------------------------------------------------------------------------------------------------
Installing Apicurio Registry using Docker
------------------------------------------------------------------------------------------------------------


Installing Apicurio Registry with in-memory storage

docker pull apicurio/apicurio-registry-mem:latest-snapshot
docker run -it --rm -p 8080:8080 apicurio/apicurio-registry-mem:latest-snapshot



------------------------------------------------------------------------------------------------------------


Registering a schema in Apicurio Registry


curl -X POST http://localhost:8080/apis/registry/v2/groups/my-group/artifacts \
   -H "Content-Type: application/json; artifactType=AVRO" \
   -H "X-Registry-ArtifactId: ItemId" \
   --data '{"namespace": "com.example.common", "type": "record", "name": "ItemId", "fields":[{"name":"id", "type":"int"}]}'

curl -X POST http://my-cluster-my-registry-my-project.example.com/apis/registry/v2/groups/my-group/artifacts \
   -H "Content-type: application/json; artifactType=AVRO" \
   -H "X-Registry-ArtifactId: share-price" \ 
   --data '{
     "type":"record",
     "name":"price",
     "namespace":"com.example",
     "fields":[{"name":"symbol","type":"string"},
     {"name":"price","type":"string"}]}'
   
------------------------------------------------------------------------------------------------------------







