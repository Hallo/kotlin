/*
<<<<<<< HEAD
 * Copyright 2010-2012 JetBrains s.r.o.
=======
 * Copyright 2000-2012 JetBrains s.r.o.
>>>>>>> de95e15595ab82be4f17ca9c149aa8bc22a2174e
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

package org.jetbrains.jet.di;

import com.google.common.collect.Lists;
<<<<<<< HEAD
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ConstructorCall implements Expression {
=======

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.List;

/**
* @author abreslav
*/
class ConstructorCall implements Expression {
>>>>>>> de95e15595ab82be4f17ca9c149aa8bc22a2174e
    private final Constructor<?> constructor;
    private final List<Field> constructorArguments = Lists.newArrayList();

    ConstructorCall(Constructor<?> constructor) {
        this.constructor = constructor;
    }

    public List<Field> getConstructorArguments() {
        return constructorArguments;
    }

    @Override
    public String toString() {
<<<<<<< HEAD
        return constructor.toString();
    }

    @NotNull
    @Override
    public String renderAsCode() {
        StringBuilder builder = new StringBuilder("new " + constructor.getDeclaringClass().getSimpleName() + "(");
        for (Iterator<Field> iterator = constructorArguments.iterator(); iterator.hasNext(); ) {
            Field argument = iterator.next();
            if (argument.isPublic()) {
                builder.append(argument.getGetterName()).append("()");
            }
            else {
                builder.append(argument.getName());
            }
=======
        StringBuilder builder = new StringBuilder("new " + constructor.getDeclaringClass().getSimpleName() + "(");
        for (Iterator<Field> iterator = constructorArguments.iterator(); iterator.hasNext(); ) {
            Field argument = iterator.next();
            builder.append(argument.getGetterName() + "()");
>>>>>>> de95e15595ab82be4f17ca9c149aa8bc22a2174e
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        builder.append(")");
        return builder.toString();
    }
<<<<<<< HEAD

    @NotNull
    @Override
    public Collection<DiType> getTypesToImport() {
        return Collections.singletonList(new DiType(constructor.getDeclaringClass()));
    }

    @NotNull
    public DiType getType() {
        return new DiType(constructor.getDeclaringClass());
    }
=======
>>>>>>> de95e15595ab82be4f17ca9c149aa8bc22a2174e
}
