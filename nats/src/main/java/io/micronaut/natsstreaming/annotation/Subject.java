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
package io.micronaut.natsstreaming.annotation;

import io.micronaut.context.annotation.AliasFor;
import io.micronaut.core.bind.annotation.Bindable;
import io.micronaut.messaging.annotation.MessageMapping;

import java.lang.annotation.*;

/**
 * @author alisson
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Bindable
public @interface Subject {

    /**
     * @return The subject to subscribe to.
     */
    @AliasFor(annotation = MessageMapping.class, member = "value")
    String value() default "";


    /**
     * @return the queue of the consumer
     */
    @AliasFor(annotation = NatsStreamingListener.class, member = "queue")
    String queueGroup() default "";

}
