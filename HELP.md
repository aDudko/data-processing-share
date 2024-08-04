### work dir:
    cd infrastructure/docker-compose 

### Start kafka-cluster
    docker-compose -f common.yml -f kafka_cluster.yml up
[kafka_manager](http://localhost:9000/)  
(don't forget to add the cluster manually)

#### Test cluster attributes
    echo ruok | nc localhost 2181
    kafkacat -L -b localhost:19092

### Start elastic-cluster
    docker-compose -f common.yml -f elastic_cluster.yml up

### Start auth-server
    1. docker-compose -f common.yml -f storage.yml up
    2. init-keycloak_schema.sql
    3. docker-compose -f common.yml -f keycloak_postgres.yml up

[keycloak_admin_console](http://localhost:9091/auth/admin/master/console)   
(don't forget to configure the server manually or import from dump)

### Start redis-cluster
    docker-compose -f common.yml -f redis_cluster.yml up

### Start monitoring
    docker-compose -f common.yml -f monitoring.yml up
[prometeus](http://localhost:9090/targets)  
[grafana](http://localhost:3000/datasources) (don't forget to configure manually)

### Start zipkin
    docker-compose -f common.yml -f zipkin.yml up