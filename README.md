
### kube node environment setting
we need docker desktop and enable kubernetes

### build & run order
```shell
# all micorservices and obersavation apps
# docker image build with Google Jib added in pom.xml
<plugin>
    <groupId>com.google.cloud.tools</groupId>
    <artifactId>jib-maven-plugin</artifactId>
    <version>3.3.2</version>
    <configuration>
        <to>
            <image>lomojiki/uber-msa/${project.artifactId}:latest</image>
        </to>
    </configuration>
</plugin>

# for each microservices,
intellij tab maven -> select service -> Plugins -> jib -> jib:dockerBuild

# image name must be lomojiki/uber-msa/driver
# Pushing into hub fails if : lomojiki/uber-msa/driver
push images into hub with a docker desktop

# in values.yaml in each micro service
# default is IfNotPresent which will search on local image first and then remote.
# IfNotPresent vs Always
# https://stackoverflow.com/questions/74006353/difference-between-always-and-ifnotpresent-imagepullpolicy
image:
  repository: uber-msa/driver
  pullPolicy: IfNotPresent
  tag: ""
  
  
# helm build dependencies & install
cd environments
helm dependencies build prod-env
helm install <cluster-name> prod-env/ 
helm uninstall <cluster-name>

cd environment
helm install uber-msa /prod-env
```
<br/>

### Start order in local IDE environment for dev
1. configserver
2. eureka
3. micro services
4. gateway server
<br/>

### Helm

<b> container ports </b>   

create new chart
```shell
helm create <chart-name>
```

configserver: 8071   
driver: 8080



### Kafka Module Architecture
<b>Dependencies</b>    
- KafkaAdminClient, KafkaProducerConfig(KafkaTemplate) <- KafkaProducer/KafkaConsumer <- Apps   
  - KafkaAdminClient contains `createTopic()`
  - KafkaProducer contains `onAppCreated(ApplicationStartedEvent)` EventListener, which microservice application's spring context will fire on their app started.
- KafkaConfigData <- Apps
  - configserver will feed config data into KafkaConfigData object.
  - because of `@ConfigurationProperties(prefix = "appname")`


### Note
`@ComponentScan(basePackages={"com.example"})` necessary to use shared classes.





### Micro Service Setting
1. application apps
- pom.xml   
  - spring cloud config client
  - eureka client
  - open feign
- application.yml
  - import config server url
  - eureka server url
  - actuator setting

2. gateway server
- route rules



