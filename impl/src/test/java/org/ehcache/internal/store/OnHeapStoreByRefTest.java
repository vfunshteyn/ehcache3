/*
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.internal.store;

import org.ehcache.config.EvictionVeto;
import org.ehcache.config.EvictionPrioritizer;
import org.ehcache.expiry.Expirations;
import org.ehcache.expiry.Expiry;
import org.ehcache.internal.SystemTimeSource;
import org.ehcache.internal.TimeSource;
import org.ehcache.spi.cache.Store;
import org.ehcache.spi.serialization.SerializationProvider;

public class OnHeapStoreByRefTest extends BaseOnHeapStoreTest {

  @Override
  protected <K, V> OnHeapStore<K, V> newStore() {
    return newStore(SystemTimeSource.INSTANCE, Expirations.noExpiration());
  }

  @Override
  protected <K, V> OnHeapStore<K, V> newStore(final TimeSource timeSource,
      final Expiry<? super K, ? super V> expiry) {
    return new OnHeapStore<K, V>(new Store.Configuration<K, V>() {
      @Override
      public Class<K> getKeyType() {
        return (Class<K>) String.class;
      }

      @Override
      public Class<V> getValueType() {
        return (Class<V>) String.class;
      }

      @Override
      public Comparable<Long> getCapacityConstraint() {
        return null;
      }

      @Override
      public EvictionVeto<? super K, ? super V> getEvictionVeto() {
        return null;
      }

      @Override
      public EvictionPrioritizer<? super K, ? super V> getEvictionPrioritizer() {
        return null;
      }

      @Override
      public SerializationProvider getSerializationProvider() {
        return null;
      }

      @Override
      public ClassLoader getClassLoader() {
        return getClass().getClassLoader();
      }

      @Override
      public Expiry<? super K, ? super V> getExpiry() {
        return expiry;
      }
    }, timeSource, false);
  }

}
