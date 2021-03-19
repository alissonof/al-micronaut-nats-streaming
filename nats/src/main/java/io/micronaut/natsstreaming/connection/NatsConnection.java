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
package io.micronaut.natsstreaming.connection;

import io.micronaut.context.annotation.Factory;
import io.micronaut.natsstreaming.exception.NatsStreemingConnectionException;
import io.nats.streaming.Options;
import io.nats.streaming.StreamingConnection;
import io.nats.streaming.StreamingConnectionFactory;

import javax.inject.Singleton;

/**
 * @author alisson
 */
@Factory
public class NatsConnection {

    /**
     * @param natsStreamingConnectionConfig
     * @return StreamingConnection
     */
    @Singleton
    public StreamingConnection createNatConnection(final NatsStreamingConnectionConfig natsStreamingConnectionConfig) {
        final Options options = natsStreamingConnectionConfig.toOptions();
        final StreamingConnectionFactory cf = new StreamingConnectionFactory(options);
        try {
            return cf.createConnection();
        } catch (Exception e) {
            throw new NatsStreemingConnectionException("Fail to connect to the server", e);
        }
    }

}
