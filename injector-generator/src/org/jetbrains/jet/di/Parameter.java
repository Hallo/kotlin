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
class Parameter {
    private final DiType type;
    private final String name;
    private final Field field;
    private final boolean required;

    Parameter(DiType type, String name, Field field, boolean required) {
        this.type = type;
        this.name = name;
        this.field = field;
        this.required = required;
    }

    public DiType getType() {
=======
/**
* @author abreslav
*/
class Parameter {
    private final Class<?> type;
    private final String name;
    private final Field field;

    Parameter(Class<?> type, String name, Field field) {
        this.type = type;
        this.name = name;
        this.field = field;
    }

    public Class<?> getType() {
>>>>>>> de95e15595ab82be4f17ca9c149aa8bc22a2174e
        return type;
    }

    public String getName() {
        return name;
    }

    public Field getField() {
        return field;
    }

<<<<<<< HEAD
    public boolean isRequired() {
        return required;
    }

=======
>>>>>>> de95e15595ab82be4f17ca9c149aa8bc22a2174e
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Parameter parameter = (Parameter) o;

        if (name != null ? !name.equals(parameter.name) : parameter.name != null) return false;
        if (type != null ? !type.equals(parameter.type) : parameter.type != null) return false;

        return true;
    }
    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }


}
