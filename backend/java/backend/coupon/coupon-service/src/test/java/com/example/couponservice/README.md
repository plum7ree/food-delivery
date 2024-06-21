Test tips

#### Consumer Broker address Configuration 문제

EmbeddedKafka 주소를 Consumer Config 주소로 넣을 수 가 없다.
따라서 미리 설정된 주소 49092 를 application-test.yml 에 정의하고, EmbeddedKafka 에도 넣고 Consumer Config 에도 넣음.
할 수 없는 이유

1. @DynamicPropertySource 내부에서 EmbeddedKafka 주소를 바꿀 수 없다. (바꿀수있다면 해결되긴함)
2. @BeforeAll 이 불릴때에는 이미 Application Context 가 Load 되어서 Consumer(Listener) 시작해버림.

#### Schema Registry 문제

avro 를 사용하므로 필수인데, schema.registry.url=mock://test-address 으로 설정하고,
아래 bean 을 작성

```
    @Configuration
    public class TestConfig {

        @Bean
        @Primary
        public SchemaRegistryClient schemaRegistryClient() {
            return new MockSchemaRegistryClient();
        }
    }
```

