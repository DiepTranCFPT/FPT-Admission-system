package com.sba.configs;

import com.sba.chatboxes.dto.ChatMessageDTO;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.UUID;

@Configuration
public class RabbitMQConfig {
    // Queues
    public static final String PYTHON_TITLE_REQUEST_QUEUE = "python-title-request";
    public static final String PYTHON_TITLE_RESPONSE_QUEUE = "python-title-response";
    public static final String PYTHON_MESSAGE_REQUEST_QUEUE = "python-processing";
    public static final String PYTHON_MESSAGE_RESPONSE_QUEUE = "bot-responses";
    public static final String PYTHON_CANCEL_QUEUE = "python-cancel";

    // Exchange for Python communication
    public static final String PYTHON_EXCHANGE = "python-exchange";

    @Bean(name = "replyQueue")
    public Queue replyQueue() {
        return QueueBuilder.nonDurable()
                .autoDelete()
                .exclusive()
                .build();
    }

    @Bean
    public Queue pythonTitleRequestQueue() {
        return new Queue(PYTHON_TITLE_REQUEST_QUEUE, true);
    }

    @Bean
    public Queue pythonTitleResponseQueue() {
        return new Queue(PYTHON_TITLE_RESPONSE_QUEUE, true);
    }

    @Bean
    public Queue pythonMessageResponseQueue() {
        return new Queue(PYTHON_MESSAGE_RESPONSE_QUEUE, true);
    }

    @Bean
    public Queue pythonMessageRequestQueue() {
        return new Queue(PYTHON_MESSAGE_REQUEST_QUEUE, true);
    }

    @Bean
    public Queue pythonCancelQueue() {
        return new Queue(PYTHON_CANCEL_QUEUE, true);
    }

    @Bean
    public DirectExchange pythonExchange() {
        return new DirectExchange(PYTHON_EXCHANGE, false, false);
    }

    @Bean
    public Binding pythonTitleRequestBinding() {
        return BindingBuilder.bind(pythonTitleRequestQueue())
                .to(pythonExchange())
                .with("generate-title");
    }

    @Bean
    public Binding pythonTitleResponseBinding() {
        return BindingBuilder.bind(pythonTitleResponseQueue())
                .to(pythonExchange())
                .with("title-response");
    }

    @Bean
    public Binding pythonMessageRequestBinding() {
        return BindingBuilder.bind(pythonMessageRequestQueue())
                .to(pythonExchange())
                .with("message-request");
    }

    @Bean
    public Binding pythonMessageResponseBinding() {
        return BindingBuilder.bind(pythonMessageResponseQueue())
                .to(pythonExchange())
                .with("message-response");
    }

    @Bean
    public Binding pythonCancelBinding() {
        return BindingBuilder.bind(pythonCancelQueue())
                .to(pythonExchange())
                .with("cancel");
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter jsonMessageConverter,
                                         Queue replyQueue) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        rabbitTemplate.setDefaultReceiveQueue(replyQueue.getName());

        // Add message post processor to ensure message properties are set
        rabbitTemplate.setBeforePublishPostProcessors(message -> {
            MessageProperties props = message.getMessageProperties();
            props.setReplyTo(replyQueue.getName());
            props.setType(determineMessageType(message, jsonMessageConverter));
            return message;
        });

        return rabbitTemplate;
    }

    private String determineMessageType(Message message, Jackson2JsonMessageConverter messageConverter) {
        Object payload = messageConverter.fromMessage(message);
        if (payload instanceof ChatMessageDTO) {
            return "chat-response";
        } else if (payload instanceof Map) {
            return "title-response";
        }
        return "unknown";
    }
}