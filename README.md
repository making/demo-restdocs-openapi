```
./mvnw clean package -Drestdoc.scheme=https -Drestdoc.host=demo-restdocs.apps.pcfone.io -Drestdoc.port=443

cf push demo-restdocs -p target/demo-restdocs-openapi-0.0.1-SNAPSHOT.jar -m 768m
```