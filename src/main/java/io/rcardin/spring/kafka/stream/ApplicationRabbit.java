package io.rcardin.spring.kafka.stream;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@EnableRabbit
@SpringBootApplication
public class ApplicationRabbit {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationRabbit.class, args);
    }

    @Bean
    public TopicExchange wordCountExchange() {
        return new TopicExchange("word.count.exchange");
    }

    @Bean
    public Queue wordCountQueue() {
        return new Queue("word.count.queue", true);
    }

    @Bean
    public Binding wordCountBinding() {
        return BindingBuilder.bind(wordCountQueue()).to(wordCountExchange()).with("word.count.routing.key");
    }

    @Component
    public static class WordCountProcessor {

        private final RabbitTemplate rabbitTemplate;

        public WordCountProcessor(RabbitTemplate rabbitTemplate) {
            this.rabbitTemplate = rabbitTemplate;
        }

        @RabbitListener(queuesToDeclare = @org.springframework.amqp.rabbit.annotation.Queue(name = "word.input.queue", durable = "true"))
        public void processWord(String message) {
            String[] words = message.split(" ");
            for (String word : words) {
                rabbitTemplate.convertAndSend("word.count.exchange", "word.count.routing.key", word);
            }
        }
    }

    @Configuration
    public static class RabbitConfig {

        @Bean
        public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
            SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
            factory.setConnectionFactory(connectionFactory);
            factory.setConcurrentConsumers(3);
            factory.setMaxConcurrentConsumers(10);
            return factory;
        }
    }
}