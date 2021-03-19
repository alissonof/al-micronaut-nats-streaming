/*
 * Copyright 2017-2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.natsstreaming.intercept;

import io.micronaut.context.BeanContext;
import io.micronaut.context.processor.ExecutableMethodProcessor;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.bind.BoundExecutable;
import io.micronaut.core.bind.DefaultExecutableBinder;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import io.micronaut.messaging.exceptions.MessageListenerException;
import io.micronaut.natsstreaming.annotation.NatsStreamingListener;
import io.micronaut.natsstreaming.annotation.Subject;
import io.micronaut.natsstreaming.bind.NatsBinderRegistry;
import io.micronaut.natsstreaming.exception.NatsStreamingListenerException;
import io.micronaut.natsstreaming.exception.NatsStreamingListenerExceptionHandler;
import io.micronaut.natsstreaming.serdes.NatsMessageSerDesRegistry;
import io.nats.streaming.Message;
import io.nats.streaming.MessageHandler;
import io.nats.streaming.StreamingConnection;
import io.nats.streaming.SubscriptionOptions;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.Optional;

/**
 * @author alisson
 */
@Singleton
public class NatsConsumerAdvice implements ExecutableMethodProcessor<NatsStreamingListener> {

    private static final String ACK_MESSAGE = "enableAck";

    private final BeanContext beanContext;

    private final NatsBinderRegistry binderRegistry;

    private final NatsMessageSerDesRegistry serDesRegistry;

    private final NatsStreamingListenerExceptionHandler exceptionHandler;

    /**
     * Default constructor.
     * @param beanContext      The bean context
     * @param binderRegistry   The registry to bind arguments to the method
     * @param serDesRegistry   The serialization/deserialization registry
     * @param exceptionHandler The exception handler to use if the consumer isn't a handler
     */
    public NatsConsumerAdvice(BeanContext beanContext, NatsBinderRegistry binderRegistry,
                              NatsMessageSerDesRegistry serDesRegistry, NatsStreamingListenerExceptionHandler exceptionHandler) {
        this.beanContext = beanContext;
        this.binderRegistry = binderRegistry;
        this.serDesRegistry = serDesRegistry;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void process(BeanDefinition<?> beanDefinition, ExecutableMethod<?, ?> method) {
        final AnnotationValue<Subject> subjectAnn = method.getAnnotation(Subject.class);
        final AnnotationValue<NatsStreamingListener> natsStreamingListenerAnnotation = beanDefinition.getAnnotation(NatsStreamingListener.class);

        if (subjectAnn != null) {
            String subject = subjectAnn.getRequiredValue(String.class);

            /*String connectionName =
                    method.findAnnotation(NatsConnection.class).flatMap(conn -> conn.get("connection", String.class))
                            .orElse(NatsConnection.DEFAULT_CONNECTION);

            io.micronaut.context.Qualifier<Object> qualifer =
                    beanDefinition.getAnnotationTypeByStereotype(Qualifier.class)
                            .map(type -> Qualifiers.byAnnotation(beanDefinition, type)).orElse(null);*/

            Class<Object> beanType = (Class<Object>) beanDefinition.getBeanType();

            Class<?> returnTypeClass = method.getReturnType().getType();
            boolean isVoid = returnTypeClass == Void.class || returnTypeClass == void.class;

            /*Object bean = beanContext.findBean(beanType, qualifer).orElseThrow(
                    () -> new MessageListenerException("Could not find the bean to execute the method " + method));

            Connection connection = beanContext.getBean(Connection.class, Qualifiers.byName(connectionName));*/

            Object bean = beanContext.findBean(beanType).orElseThrow(
                    () -> new MessageListenerException("Could not find the bean to execute the method " + method));

            StreamingConnection connection = beanContext.getBean(StreamingConnection.class);

            DefaultExecutableBinder<Message> binder = new DefaultExecutableBinder<>();


            MessageHandler messageHandler = (msg -> {
                BoundExecutable boundExecutable = null;
                try {
                    boundExecutable = binder.bind(method, binderRegistry, msg);
                } catch (Throwable e) {
                    handleException(
                            new NatsStreamingListenerException("An error occurred binding the message to the method", e, bean,
                                    msg));
                }

                if (boundExecutable != null) {
                    boundExecutable.invoke(bean);
                }

                if (natsStreamingListenerAnnotation.isTrue(ACK_MESSAGE)) {
                    try {
                        msg.ack();
                    } catch (IOException e) {
                        handleException(
                                new NatsStreamingListenerException("An error occurred during ack message", e, bean, msg));
                    }
                }

            });

            try {
                final SubscriptionOptions.Builder builder =  new SubscriptionOptions.Builder();
                final Optional<String> durableName = natsStreamingListenerAnnotation.get("durableName", String.class);
                builder.durableName(durableName.isEmpty() ? null : durableName.get());
                if (natsStreamingListenerAnnotation.isTrue(ACK_MESSAGE)) {
                    builder.manualAcks();
                }

                Optional<String> queue = subjectAnn.get("queueGroup", String.class);
                connection.subscribe(subject,
                        queue.isEmpty() ? null : queue.get(),
                        messageHandler,
                        builder.build());
            } catch (Exception e) {
                handleException(
                        new NatsStreamingListenerException("An error occurred during ack invoque method subscribe", e, bean, null));
            }

        }
    }

    /**
     *
     * @param exception
     */
    private void handleException(NatsStreamingListenerException exception) {
        Object bean = exception.getListener();
        if (bean instanceof NatsStreamingListenerExceptionHandler) {
            ((NatsStreamingListenerExceptionHandler) bean).handle(exception);
        } else {
            exceptionHandler.handle(exception);
        }
    }

}
