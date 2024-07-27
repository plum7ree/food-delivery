로직

1. Order Approval Database 에 저장

-  ```
    id            uuid            NOT NULL,
    restaurant_id uuid            NOT NULL,
    order_id      uuid            NOT NULL,
    status        approval_status NOT NULL,
   ```
- Approval Status
    - APPROVED, REJECTED, PENDING

2. restaurant_approval_outbox 에 저장

-  ```
    id            uuid                                           NOT NULL,
    saga_id       uuid                                           NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE                       NOT NULL,
    processed_at  TIMESTAMP WITH TIME ZONE,
    type          character varying COLLATE pg_catalog."default" NOT NULL,
    payload       jsonb                                          NOT NULL,
    outbox_status outbox_status                                  NOT NULL,
    saga_status   saga_status                                    NOT NULL,
    order_status  order_status                                   NOT NULL,
    version       integer                                        NOT NULL,
   ```

이때 approval_status -> order_status 로 변환됨.

- PENDING, CALLEE_APPROVED, CALLEE_REJECTED

Errors

- Parameter 0 of constructor in
  com.example.restaurantapprovalservice.service.listener.kafka.RestaurantApprovalRequestKafkaConsumer required a bean of
  type 'com.example.eatsorderdataaccess.repository.OrderApprovalRepository' that could not be found.
    - solution:
    - ```
        @Configuration
        @EnableJpaRepositories(basePackages = "com.example.eatsorderdataaccess.repository")
        //@EntityScan(basePackages = "com.example.eatsorderdataaccess.entity")
        public class JpaConfig {
        } 
      ```

- required a bean named 'entityManagerFactory' that could not be found.
    - 직접 생성
    - ```
        @Configuration
        @EnableJpaRepositories(basePackages = "com.example.eatsorderdataaccess.repository")
        @EntityScan(basePackages = {"com.example.eatsorderdataaccess.entity"})
        public class JpaConfig {
        @Bean
        public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.example.eatsorderdataaccess.entity");
        
                JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
                em.setJpaVendorAdapter(vendorAdapter);
        
                return em;
            }
        
            @Bean
            public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
                JpaTransactionManager transactionManager = new JpaTransactionManager();
                transactionManager.setEntityManagerFactory(emf);
                return transactionManager;
            }
        
        }
        
        @Configuration
        public class DataSourceConfig {
        
            @Bean
            public DataSource dataSource() {
                DriverManagerDataSource dataSource = new DriverManagerDataSource();
                dataSource.setDriverClassName("org.postgresql.Driver");
                dataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
                dataSource.setUsername("postgres");
                dataSource.setPassword("admin");
                dataSource.setSchema("restaurant");
                return dataSource;
            }
        
        }
      ```