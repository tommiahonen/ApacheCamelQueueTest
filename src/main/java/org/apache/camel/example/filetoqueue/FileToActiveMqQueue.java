/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.example.jmstofile;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;


import static java.util.Collections.singletonList;

/**
 * An example class for demonstrating some basics behind Camel. This
 * example sends some text messages on to a JMS Queue, consumes them and
 * persists them to disk
 */
public final class FileToActiveMqQueue {

    private FileToActiveMqQueue() {
    }

    public static void main(String[] args) throws Exception {

        try (CamelContext context = new DefaultCamelContext()) {

            // Configure connection to ActiveMQ server
            ActiveMQConnectionFactory connectionFactory = createActiveMQConnectionFactory("admin", "admin", "tcp://localhost:61616");
            JmsComponent jms = new JmsComponent();
            jms.setConnectionFactory(connectionFactory);

            context.addComponent("replyTo-jms", jms);
            context.addRoutes(new MyRouteBuilder());
            context.start();

            Thread.sleep(4000);
        }

    }

    static ActiveMQConnectionFactory createActiveMQConnectionFactory(String username, String password, String brokerURI) {

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(username, password, brokerURI);
        connectionFactory.setTrustAllPackages(false);
        connectionFactory.setTrustedPackages(singletonList("org.apache.camel.example.jmstofile"));
        return connectionFactory;
    }

    static class MyRouteBuilder extends RouteBuilder {

        @Override
        public void configure() {
            from("file://messages")
                    .log("${body}")
                    .to("replyTo-jms:queue:replyTo");
        }
    }
}
