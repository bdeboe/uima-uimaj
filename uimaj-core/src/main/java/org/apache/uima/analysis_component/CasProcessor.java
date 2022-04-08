/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.uima.analysis_component;

import java.util.Objects;

import org.apache.uima.cas.CAS;

/**
 * A functional interface for a CAS processor.
 *
 * @param <E>
 *          Thrown exception.
 */
@FunctionalInterface
public interface CasProcessor<E extends Throwable> {

  /**
   * Accepts the processor.
   *
   * @param aCas
   *          the CAS to process.
   * @throws E
   *           Thrown when the processor fails.
   */
  void process(CAS aCas) throws E;

  /**
   * Returns a composed {@code CasProcessor} like {@link CasProcessor#andThen(CasProcessor)}.
   *
   * @param after
   *          the operation to perform after this operation
   * @return a composed {@code CasProcessor} like {@link CasProcessor#andThen(CasProcessor)}.
   * @throws NullPointerException
   *           when {@code after} is null
   */
  default CasProcessor<E> andThen(final CasProcessor<E> after) {
    Objects.requireNonNull(after);
    return (final CAS t) -> {
      process(t);
      after.process(t);
    };
  }
}
