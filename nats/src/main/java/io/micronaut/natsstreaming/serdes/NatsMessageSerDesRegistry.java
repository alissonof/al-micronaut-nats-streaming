/*
 * Copyright 2017-2020 original authors
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
package io.micronaut.natsstreaming.serdes;

import io.micronaut.core.type.Argument;

import java.util.Optional;

/**
 * @author alisson
 */
public interface NatsMessageSerDesRegistry {

    /**
     * Returns the serdes that supports the given type.
     *
     * @param type The type
     * @param <T> The type to be serialized/deserialized
     * @return An optional serdes
     */
    <T> Optional<NatsMessageSerDes<T>> findSerdes(Argument<T> type);
}
