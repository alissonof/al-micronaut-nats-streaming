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
package io.micronaut.natsstreaming.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.natsstreaming.exception.NatsStreamingClientException;
import io.nats.streaming.AckHandler;
import io.nats.streaming.StreamingConnection;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author alisson
 */
@Singleton
public class NatsStreamingPublisher {

    @Inject
    private StreamingConnection connection;

    @Inject
    private ObjectMapper mapper;

    /**
     *
     * @param subject
     * @param paylod
     * @param akHandler
     * @param <T>
     */
    public <T> void publish(final String subject, final T paylod, final AckHandler akHandler) {
        try {
            final String json = mapper.writeValueAsString(paylod);
            System.out.println("Json gerado payload=" + json);
            connection.publish(subject, json.getBytes(), akHandler);
        } catch (Exception e) {
            throw new NatsStreamingClientException("Faill to send message", e);
        }
    }

    /**
     *
     * @param subject
     * @param paylod
     * @param <T>
     */
    public <T> void  publish(final String subject, final T paylod) {
        publish(subject, paylod, null);
    }
}


