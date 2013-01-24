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
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

<<<<<<< HEAD
class Field {

    public static Field create(boolean isPublic, DiType type, String name, @Nullable Expression init) {
        Field field = new Field(isPublic, type, name);
        if (init != null) {
            field.setInitialization(init);
        }
        return field;
    }

    private final DiType type;
    private final String name;
    private final boolean isPublic;

    @NotNull
=======
/**
* @author abreslav
*/
class Field {

    public static Field create(boolean isPublic, Class<?> type, String name, @Nullable Expression init) {
        Field field = new Field(isPublic, type, name);
        field.initialization = init;
        return field;
    }

    private final Class<?> type;
    private final String name;
    private final boolean isPublic;

    @Nullable
>>>>>>> de95e15595ab82be4f17ca9c149aa8bc22a2174e
    private Expression initialization;

    private final List<SetterDependency> dependencies = Lists.newArrayList();

<<<<<<< HEAD
    Field(boolean isPublic, DiType type, String name) {
        this.isPublic = isPublic;
        this.type = type;
        this.name = name;
        this.initialization = new InstantiateType(type);
    }

    public DiType getType() {
=======
    Field(boolean isPublic, Class<?> type, String name) {
        this.isPublic = isPublic;
        this.type = type;
        this.name = name;
    }

    public Class<?> getType() {
>>>>>>> de95e15595ab82be4f17ca9c149aa8bc22a2174e
        return type;
    }

    public String getName() {
        return name;
    }

    public String getTypeName() {
<<<<<<< HEAD
        return type.getClazz().getSimpleName();
=======
        return type.getSimpleName();
>>>>>>> de95e15595ab82be4f17ca9c149aa8bc22a2174e
    }

    public String getGetterName() {
        String prefix;
<<<<<<< HEAD
        if (getType().getClazz() == boolean.class || getType().getClazz() == Boolean.class) {
=======
        if (getType() == boolean.class || getType() == Boolean.class) {
>>>>>>> de95e15595ab82be4f17ca9c149aa8bc22a2174e
            prefix = "is";
        }
        else {
            prefix = "get";
        }
        return prefix + StringUtil.capitalize(getName());
    }

<<<<<<< HEAD
    @NotNull
=======
    @Nullable
>>>>>>> de95e15595ab82be4f17ca9c149aa8bc22a2174e
    public Expression getInitialization() {
        return initialization;
    }

    public void setInitialization(@NotNull Expression initialization) {
        this.initialization = initialization;
    }

    public List<SetterDependency> getDependencies() {
        return dependencies;
    }

    public boolean isPublic() {
        return isPublic;
    }

    @Override
    public String toString() {
        return getTypeName() + " " + getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Field field = (Field) o;

        if (!name.equals(field.name)) return false;
        if (!type.equals(field.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
