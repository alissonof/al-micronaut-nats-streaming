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

import io.micronaut.context.annotation.ConfigurationProperties;
import io.nats.streaming.Options;

import java.util.Objects;

/**
 * @author alisson
 */
@ConfigurationProperties("nats")
public class NatsStreamingConnectionConfig {

    public static final String NATS_PROTOCOL = "nats://";

    private String host = "localhost";

    private int port = 4222;

    private String  user;

    private String  password;

    private String  clientId;

    private String  clusterId;

    /**
     *
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     *
     * @param host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     *
     * @return port
     */
    public int getPort() {
        return port;
    }

    /**
     *
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     *
     * @return user
     */
    public String getUser() {
        return user;
    }

    /**
     *
     * @param user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     *
     * @return clientId
     */
    public String getClientId() {
        return clientId;
    }

    /**
     *
     * @param clientId
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /***
     *
     * @return clusterId
     */
    public String getClusterId() {
        return clusterId;
    }

    /**
     *
     * @param clusterId
     */
    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    /**
     *
     * @return Options
     */
    public Options toOptions() {

        return  new Options.Builder()
                .clusterId(clusterId)
                .clientId(clientId)
                .natsUrl(getUrl())
                .build();
    }

    /**
     *
     * @return url
     */
    private String getUrl() {
        String url = null;
        if (Objects.nonNull(user) || Objects.nonNull(password)) {
            url = NATS_PROTOCOL + user + ":" + password + "@" + host + ":" + port;
        } else {
            url = NATS_PROTOCOL + host + ":" + port;
        }
        return url;
    }
}
