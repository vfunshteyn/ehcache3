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

import org.ehcache.Cache;
import org.ehcache.config.StoreConfigurationImpl;
import org.ehcache.exceptions.CacheAccessException;
import org.ehcache.expiry.Expirations;
import org.ehcache.function.Function;
import org.ehcache.function.Predicate;
import org.ehcache.function.Predicates;
import org.ehcache.spi.cache.Store;
import org.ehcache.spi.test.Ignore;
import org.ehcache.spi.test.SPITest;
import org.hamcrest.Matchers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.lang.Long;
import java.util.Set;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Test the {@link org.ehcache.spi.cache.Store#bulkCompute(Iterable, org.ehcache.function.Function)} contract of the
 * {@link org.ehcache.spi.cache.Store Store} interface.
 * <p/>
 *
 * @author Gaurav Mangalick
 */

public class StoreBulkComputeIfAbsentTest<K, V> extends SPIStoreTester<K, V> {

  public StoreBulkComputeIfAbsentTest(final StoreFactory<K, V> factory) {
    super(factory);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @SPITest
  public void mappingFunctionReturnsIterableOfEntriesForEachInputEntry() throws Exception {
    final Store<K, V> kvStore = factory.newStore(factory.newConfiguration(factory.getKeyType(), factory.getValueType(), null, null, null));
    final K k1 = factory.createKey(1L);
    final V v1 = factory.createValue(1L);

    Set<K> set = new HashSet<K>();
    set.add(k1);

    kvStore.put(k1, v1);

    try {
      kvStore.bulkComputeIfAbsent(Arrays.asList((K[]) set.toArray()), new Function<Iterable<? extends K>, Iterable<? extends Map.Entry<? extends K, ? extends V>>>() {
          @Override
          public Iterable<? extends Map.Entry<? extends K, ? extends V>> apply(Iterable<? extends K> entries) {
            assertThat(entries.iterator().hasNext(), is(false));
            return new HashMap<K, V>().entrySet();
          }
        }
      );
    } catch (CacheAccessException e) {
      System.err.println("Warning, an exception is thrown due to the SPI test");
      e.printStackTrace();
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @SPITest
  public void missingIterableEntriesAreIgnoredByTheStore() throws Exception {
    final Store<K, V> kvStore = factory.newStore(factory.newConfiguration(factory.getKeyType(), factory.getValueType(), null, null, null));
    final K k1 = factory.createKey(1L);
    final V v1 = factory.createValue(1L);

    Set<K> set = new HashSet<K>();
    set.add(k1);

    kvStore.put(k1, v1);

    try {
      kvStore.bulkComputeIfAbsent(Arrays.asList((K[]) set.toArray()), new Function<Iterable<? extends K>, Iterable<? extends Map.Entry<? extends K, ? extends V>>>() {
            @Override
            public Iterable<? extends Map.Entry<? extends K, ? extends V>> apply(Iterable<? extends K> entries) {
              return new HashMap<K, V>().entrySet();
            }
          }
      );
      assertThat(kvStore.get(k1).value(), is(v1));
    } catch (CacheAccessException e) {
      System.err.println("Warning, an exception is thrown due to the SPI test");
      e.printStackTrace();
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @SPITest
  public void mappingIsSameInTheStoreForEntriesReturnedWithDifferentValueFromMappingFunction() throws Exception {
    final Store<K, V> kvStore = factory.newStore(factory.newConfiguration(factory.getKeyType(), factory.getValueType(), null, null, null));
    final K k1 = factory.createKey(1L);
    final V v1 = factory.createValue(1L);
    final V v10 = factory.createValue(10L);

    Set<K> set = new HashSet<K>();
    set.add(k1);

    kvStore.put(k1, v1);

    try {
      kvStore.bulkComputeIfAbsent(Arrays.asList((K[]) set.toArray()), new Function<Iterable<? extends K>, Iterable<? extends Map.Entry<? extends K, ? extends V>>>() {
        @Override
        public Iterable<? extends Map.Entry<? extends K, ? extends V>> apply(Iterable<? extends K> entries) {
          Map<K, V> map = new HashMap<K, V>();
          map.put(k1, v10);
          return map.entrySet();
        }
      });
      assertThat(kvStore.get(k1).value(), is(v1));
    } catch (CacheAccessException e) {
      System.err.println("Warning, an exception is thrown due to the SPI test");
      e.printStackTrace();
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @SPITest
  public void testWrongKeyType() throws Exception {
    final Store<K, V> kvStore = factory.newStore(factory.newConfiguration(factory.getKeyType(), factory.getValueType(), null, null, null));
    Set<K> set = new HashSet<K>();
    if (factory.getKeyType() == String.class) {
      set.add((K) new Object());
    } else {
      set.add((K) "WrongKeyType");
    }
    try {
      kvStore.bulkComputeIfAbsent(Arrays.asList((K[]) set.toArray()), new Function<Iterable<? extends K>, Iterable<? extends Map.Entry<? extends K, ? extends V>>>() {
        @Override
        public Iterable<? extends Map.Entry<? extends K, ? extends V>> apply(Iterable<? extends K> entries) {
          throw new AssertionError("Expected ClassCastException because the key is of the wrong type");
        }
      });
      throw new AssertionError("Expected ClassCastException because the key is of the wrong type");
    } catch (ClassCastException e) {
      // expected
    } catch (CacheAccessException e) {
      System.err.println("Warning, an exception is thrown due to the SPI test");
      e.printStackTrace();
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @SPITest
  public void computeValuesForEveryKeyUsingAMappingFunction() throws Exception {
    final Store<K, V> kvStore = factory.newStore(factory.newConfiguration(factory.getKeyType(), factory.getValueType(), null, null, null));
    final K k1 = factory.createKey(1L);
    final V v1 = factory.createValue(1L);

    Set<K> set = new HashSet<K>();
    set.add(k1);

    try {
      kvStore.bulkComputeIfAbsent(Arrays.asList((K[]) set.toArray()), new Function<Iterable<? extends K>, Iterable<? extends Map.Entry<? extends K, ? extends V>>>() {
        @Override
        public Iterable<? extends Map.Entry<? extends K, ? extends V>> apply(Iterable<? extends K> entries) {
          Map<K, V> map = new HashMap<K, V>();
          map.put(k1, v1);
          return map.entrySet();
        }
      });
      assertThat(kvStore.get(k1).value(), is(v1));
    } catch (CacheAccessException e) {
      System.err.println("Warning, an exception is thrown due to the SPI test");
      e.printStackTrace();
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @SPITest
  public void testMappingFunctionProducesWrongKeyType() throws Exception {
    final Store<K, V> kvStore = factory.newStore(factory.newConfiguration(factory.getKeyType(), factory.getValueType(), null, null, null));
    final K k1 = factory.createKey(1L);
    final V v1 = factory.createValue(1L);
    Set<K> set = new HashSet<K>();
    set.add(k1);
    try {
      kvStore.bulkComputeIfAbsent(Arrays.asList((K[]) set.toArray()), new Function<Iterable<? extends K>, Iterable<? extends Map.Entry<? extends K, ? extends V>>>() {
        @Override
        public Iterable<? extends Map.Entry<? extends K, ? extends V>> apply(Iterable<? extends K> entries) {
          Map<K, V> map = new HashMap<K, V>();
          if (factory.getKeyType() == String.class) {
            map.put((K)new Object(), v1);
          } else {
            map.put((K)"WrongKeyType", v1);
          }
          return map.entrySet();
        }
      });
      throw new AssertionError("Expected ClassCastException because the key is of the wrong type");
    } catch (ClassCastException cce) {
      //expected
    } catch (CacheAccessException e) {
      System.err.println("Warning, an exception is thrown due to the SPI test");
      e.printStackTrace();
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @SPITest
  public void testMappingFunctionProducesWrongValueType() throws Exception {
    final Store<K, V> kvStore = factory.newStore(factory.newConfiguration(factory.getKeyType(), factory.getValueType(), null, null, null));
    final K k1 = factory.createKey(1L);
    Set<K> set = new HashSet<K>();
    set.add(k1);
    try {
      kvStore.bulkComputeIfAbsent(Arrays.asList((K[]) set.toArray()), new Function<Iterable<? extends K>, Iterable<? extends Map.Entry<? extends K, ? extends V>>>() {
        @Override
        public Iterable<? extends Map.Entry<? extends K, ? extends V>> apply(Iterable<? extends K> entries) {
          Map<K, V> map = new HashMap<K, V>();
          if (factory.getValueType() == String.class) {
            map.put(k1, (V) new Object());
          } else {
            map.put(k1, (V) "WrongValueType");
          }
          return map.entrySet();
        }
      });
      throw new AssertionError("Expected ClassCastException because the value is of the wrong type");
    } catch (ClassCastException cce) {
      //expected
    } catch (CacheAccessException e) {
      System.err.println("Warning, an exception is thrown due to the SPI test");
      e.printStackTrace();
    }
  }
}
