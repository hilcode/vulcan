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
package org.apache.lib_0.sublib_7.subsublib_5.concurrent;

import java.util.concurrent.ExecutionException;

import org.apache.lib_0.sublib_7.subsublib_5.exception.UncheckedException;

/**
 * Unchecked {@link ExecutionException}.
 *
 * @since 3.13.0
 */
public class UncheckedExecutionException extends UncheckedException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs an instance initialized to the given {@code cause}.
     *
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method). (A @{code null} value
     *        is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public UncheckedExecutionException(final Throwable cause) {
        super(cause);
    }

}