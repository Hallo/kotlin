/*
 * Copyright 2010-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.jet.lang.resolve.java;

/**
 * We convert Java types differently, depending on where they occur in the Java code
 * This enum encodes the kinds of occurrences
 */
public enum TypeUsage {
    // Type T occurs somewhere as a generic argument, e.g.: List<T> or List<? extends T>
    TYPE_ARGUMENT,
    UPPER_BOUND,
    MEMBER_SIGNATURE_COVARIANT,
    MEMBER_SIGNATURE_CONTRAVARIANT,
    MEMBER_SIGNATURE_INVARIANT,
    SUPERTYPE,
    SUPERTYPE_ARGUMENT
}
