/**
 * Copyright 2015-2016 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin.autoconfigure.storage.cassandra3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin.storage.StorageComponent;
import zipkin.storage.cassandra3.Cassandra3Storage.SessionFactory;

/**
 * This storage accepts Cassandra logs in a specified category. Each log entry is expected to contain
 * a single span, which is TBinaryProtocol big-endian, then base64 encoded. Decoded spans are stored
 * asynchronously.
 */
@Configuration
@EnableConfigurationProperties(ZipkinCassandra3StorageProperties.class)
@ConditionalOnProperty(name = "zipkin.storage.type", havingValue = "cassandra3")
@ConditionalOnMissingBean(StorageComponent.class)
// This component is named .*Cassandra3.* even though the package already says cassandra3 because
// Spring Boot configuration endpoints only printout the simple name of the class
public class ZipkinCassandra3StorageAutoConfiguration {

  @Autowired(required = false)
  @Qualifier("tracingSessionFactory")
  SessionFactory tracingSessionFactory;

  @Bean StorageComponent storage(ZipkinCassandra3StorageProperties properties) {
    return tracingSessionFactory == null
        ? properties.toBuilder().build()
        : properties.toBuilder().sessionFactory(tracingSessionFactory).build();
  }
}
