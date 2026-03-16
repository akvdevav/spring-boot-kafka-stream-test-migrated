# Migration Plan

## Current State Summary
The project currently uses Spring Boot 2.3.0.RELEASE with Java 8, integrating Kafka Streams for stream processing. It leverages spring-kafka and kafka-streams-test-utils for testing without requiring an embedded Kafka instance. The setup includes basic configuration for Kafka Streams and unit tests.

## Target State
The upgraded project will run on Spring Boot 3.4.3 with Java 21. The Kafka Streams components will be replaced with RabbitMQ integration, maintaining similar functionality but using message queues instead of streaming. All configurations and dependencies will be updated accordingly.

## Migration Steps
1. Upgrade Maven parent to Spring Boot 3.4.3 and set Java version to 21
2. Remove all Kafka-related dependencies
3. Add RabbitMQ dependencies including spring-rabbit and spring-amqp
4. Replace Kafka Streams logic with RabbitMQ listener/consumer pattern
5. Update application configuration for RabbitMQ properties
6. Refactor test classes to use RabbitMQ mocking/testing utilities
7. Update documentation and README to reflect RabbitMQ usage

## Dependency Changes
| Action | Group ID | Artifact ID | Version |
|--------|----------|-------------|---------|
| Remove | org.springframework.kafka | spring-kafka | - |
| Remove | org.apache.kafka | kafka-streams | - |
| Remove | org.apache.kafka | kafka-streams-test-utils | 2.5.0 |
| Add | org.springframework.amqp | spring-rabbit | Latest |
| Add | org.springframework.amqp | spring-amqp | Latest |
| Add | org.springframework.boot | spring-boot-starter-amqp | 3.4.3 |

## Configuration Changes
application.properties will be replaced with:

spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
spring.rabbitmq.virtual-host=/


## Code Changes
- Replace @EnableKafkaStreams with @EnableRabbit
- Convert Kafka Streams processor logic to RabbitMQ message listeners
- Change bean definitions from Kafka-specific to RabbitMQ specific
- Update test classes to mock RabbitMQ consumers instead of Kafka Streams
- Replace Serde configurations with appropriate RabbitMQ serialization handling

## Risk Assessment
Potential issues include:
- Incompatibility between old Kafka Stream logic and new RabbitMQ consumer model
- Loss of stream processing semantics when migrating away from Kafka Streams
- Testing complexity due to need for mocking RabbitMQ exchanges/queues
- Dependency conflicts during Spring Boot upgrade
Mitigation strategies involve thorough unit testing, incremental migration approach, and ensuring equivalent business logic preservation through comprehensive test coverage.