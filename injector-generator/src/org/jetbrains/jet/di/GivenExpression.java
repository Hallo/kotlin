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

<<<<<<< HEAD
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class GivenExpression implements Expression {
    private final String expression;
    private final Collection<DiType> typesToImport;


    public GivenExpression(@NotNull String expression) {
        this(expression, Collections.<DiType>emptyList());
    }

    public GivenExpression(@NotNull String expression, @NotNull DiType... typesToImport) {
        this(expression, Arrays.asList(typesToImport));
    }

    public GivenExpression(@NotNull String expression, @NotNull Class<?>... typesToImport) {
        this(expression, convertClassesToDiTypes(typesToImport));
    }

    private static Collection<DiType> convertClassesToDiTypes(Class<?>[] typesToImport) {
        Collection<DiType> types = Lists.newArrayList();
        for (Class<?> aClass : typesToImport) {
            types.add(new DiType(aClass));
        }
        return types;
    }

    public GivenExpression(@NotNull String expression, @NotNull Collection<DiType> typesToImport) {
        this.expression = expression;
        this.typesToImport = typesToImport;
=======
/**
* @author abreslav
*/
class GivenExpression implements Expression {
    private final String expression;

    GivenExpression(String expression) {
        this.expression = expression;
>>>>>>> de95e15595ab82be4f17ca9c149aa8bc22a2174e
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public String toString() {
<<<<<<< HEAD
        return "given<" + expression + ">";
    }

    public DiType getType() {
        return null;
    }

    @NotNull
    @Override
    public String renderAsCode() {
        return expression;
    }

    @NotNull
    @Override
    public Collection<DiType> getTypesToImport() {
        return typesToImport;
    }
=======
        return expression;
    }
>>>>>>> de95e15595ab82be4f17ca9c149aa8bc22a2174e
}
