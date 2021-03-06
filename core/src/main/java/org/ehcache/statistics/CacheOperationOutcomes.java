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
 
package org.ehcache.statistics;

/**
 * 
 * @author Hung Huynh
 *
 */
public interface CacheOperationOutcomes {

  /**
   * Outcomes for cache Get operations.
   */
  enum GetOutcome {
    /** hit, no loader. */
    HIT_NO_LOADER,
    /** miss, no loader */
    MISS_NO_LOADER,
    /** hit. */
    HIT_WITH_LOADER,
    /** miss */
    MISS_WITH_LOADER,    
    /** failure */
    FAILURE
  };

  /**
   * The outcomes for Put Outcomes.
   */
  enum PutOutcome {
    /** added. */
    ADDED,
    /** failure */
    FAILURE
  };

  /**
   * The outcomes for remove operations.
   */
  enum RemoveOutcome {
    /** success. */
    SUCCESS,
    /** failure */
    FAILURE
  };

  /**
   * The eviction outcomes.
   */
  enum EvictionOutcome {
    /** success. */
    SUCCESS,
    /** failure */
    FAILURE
  };
  
  /**
   * The cacheloader outcomes.
   */
  enum CacheLoaderOutcome {
    /** success. */
    SUCCESS,
    /** failure */
    FAILURE
  };
  
  /**
   * The putIfAbsent outcomes.
   */
  enum PutIfAbsentOutcome {
    PUT,
    HIT,
    FAILURE
  };
  
  /**
   * The replace outcomes.
   */
  enum ReplaceOutcome {
    HIT,
    MISS,
    FAILURE
  };  
}
