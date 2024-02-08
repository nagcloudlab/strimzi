https://www.apicur.io/registry/docs/apicurio-registry/2.5.x/index.html



-----------------------------------------------------
Installing Apicurio Registry using Docker
-----------------------------------------------------


Installing Apicurio Registry with in-memory storage

docker pull apicurio/apicurio-registry-mem:latest-release
docker run -it --rm -p 8080:8080 apicurio/apicurio-registry-mem:latest-release



-----------------------------------------------------

Registering a schema in Apicurio Registry
curl -X POST http://localhost:8080/apis/registry/v2/groups/my-group/artifacts \
   -H "Content-Type: application/json; artifactType=AVRO" \
   -H "X-Registry-ArtifactId: ItemId" \
   --data '{"namespace": "com.example.common", "type": "record", "name": "ItemId", "fields":[{"name":"id", "type":"int"}]}'

Update the schema
curl -X PUT http://localhost:8080/apis/registry/v2/groups/my-group/artifacts/ItemId \
   -H "Content-Type: application/json; artifactType=AVRO" \
   -H "X-Registry-ArtifactId: ItemId" \
   --data '{"namespace": "com.example.common", "type": "record", "name": "ItemId", "fields":[{"name":"id", "type":"int"},{"name":"foo", "type":"string","default":"bar"}]}'


------------------------------------------------------------------------------------------------------------







