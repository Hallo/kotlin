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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

interface Expression {
    @NotNull
    String renderAsCode();

    @NotNull
    Collection<DiType> getTypesToImport();

    @Nullable
    DiType getType();
}
=======
/**
* @author abreslav
*/
interface Expression {}
>>>>>>> de95e15595ab82be4f17ca9c149aa8bc22a2174e
