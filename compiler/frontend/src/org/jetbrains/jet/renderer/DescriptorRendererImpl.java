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

package org.jetbrains.jet.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.descriptors.*;
import org.jetbrains.jet.lang.resolve.DescriptorUtils;
import org.jetbrains.jet.lang.resolve.name.FqName;
import org.jetbrains.jet.lang.resolve.name.FqNameUnsafe;
import org.jetbrains.jet.lang.resolve.name.Name;
import org.jetbrains.jet.lang.types.*;
import org.jetbrains.jet.lang.types.lang.KotlinBuiltIns;
import org.jetbrains.jet.lexer.JetKeywordToken;
import org.jetbrains.jet.lexer.JetTokens;

import java.util.*;

/**
 * @author abreslav
 */
public class DescriptorRendererImpl implements DescriptorRenderer {
    private static final Set<String> KEYWORDS = Sets.newHashSet();
    static {
        for (IElementType elementType : JetTokens.KEYWORDS.getTypes()) {
            assert elementType instanceof JetKeywordToken;
            assert !((JetKeywordToken) elementType).isSoft();
            KEYWORDS.add(((JetKeywordToken) elementType).getValue());
        }
    }

    private final boolean shortNames;
    private final boolean withDefinedIn;
    private final boolean modifiers;
    private final boolean startFromName;
    private final boolean debugMode;
    @Nullable
    private final ValueParametersHandler handler;
    @NotNull
    private final TextFormat textFormat;

    /* package */ DescriptorRendererImpl(
            boolean shortNames,
            boolean withDefinedIn,
            boolean modifiers,
            boolean startFromName,
            boolean debugMode,
            @Nullable ValueParametersHandler handler,
            @NotNull TextFormat textFormat
    ) {
        this.shortNames = shortNames;
        this.withDefinedIn = withDefinedIn;
        this.modifiers = modifiers;
        this.startFromName = startFromName;
        this.handler = handler;
        this.debugMode = debugMode;
        this.textFormat = textFormat;
    }


    /* FORMATTING */
    private String renderKeyword(String keyword) {
        switch (textFormat) {
            case PLAIN:
                return keyword;
            case HTML:
                return "<b>" + keyword + "</b>";
        }
        throw new IllegalStateException("Unexpected textFormat: " + textFormat);
    }

    private String escape(String s) {
        switch (textFormat) {
            case PLAIN:
                return s;
            case HTML:
                return s.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        }
        throw new IllegalStateException("Unexpected textFormat: " + textFormat);
    }

    private String lt() {
        return escape("<");
    }

    private String renderMessage(String message) {
        switch (textFormat) {
            case PLAIN:
                return message;
            case HTML:
                return "<i>" + message + "</i>";
        }
        throw new IllegalStateException("Unexpected textFormat: " + textFormat);
    }


    /* NAMES RENDERING */
    private String renderName(Name identifier) {
        String asString = identifier.toString();
        return escape(KEYWORDS.contains(asString) ? '`' + asString + '`' : asString);
    }

    private void renderName(DeclarationDescriptor descriptor, StringBuilder stringBuilder) {
        stringBuilder.append(renderName(descriptor.getName()));
    }

    @NotNull
    private String renderFqName(@NotNull FqNameUnsafe fqName) {
        return renderFqName(fqName.pathSegments());
    }


    @NotNull
    private String renderFqName(@NotNull List<Name> pathSegments) {
        StringBuilder buf = new StringBuilder();
        for (Name element : pathSegments) {
            if (buf.length() != 0) {
                buf.append(".");
            }
            buf.append(renderName(element));
        }
        return buf.toString();
    }


    /* TYPES RENDERING */
    @Override
    public String renderType(JetType type) {
        if (type == null) {
            return escape("[NULL]");
        }
        else if (ErrorUtils.isErrorType(type)) {
            return escape(type.toString());
        }
        else if (KotlinBuiltIns.getInstance().isUnit(type)) {
            return escape(KotlinBuiltIns.UNIT_ALIAS + (type.isNullable() ? "?" : ""));
        }
        else if (KotlinBuiltIns.getInstance().isFunctionType(type)) {
            return escape(renderFunctionType(type));
        }
        else {
            return escape(renderDefaultType(type));
        }
    }

    private String renderDefaultType(JetType type) {
        StringBuilder sb = new StringBuilder();

        sb.append(renderTypeName(type.getConstructor()));
        if (!type.getArguments().isEmpty()) {
            sb.append("<");
            appendTypeProjections(sb, type.getArguments());
            sb.append(">");
        }
        if (type.isNullable()) {
            sb.append("?");
        }
        return sb.toString();
    }

    private String renderTypeName(@NotNull TypeConstructor typeConstructor) {
        ClassifierDescriptor cd = typeConstructor.getDeclarationDescriptor();
        if (cd == null) {
            return typeConstructor.toString();
        }
        else if (cd instanceof TypeParameterDescriptor) {
            return renderName(cd.getName());
        }
        else {
            if (shortNames) {
                List<Name> qualifiedNameElements = Lists.newArrayList();

                // for nested classes qualified name should be used
                DeclarationDescriptor current = cd;
                do {
                    qualifiedNameElements.add(current.getName());
                    current = current.getContainingDeclaration();
                }
                while (current instanceof ClassDescriptor);

                Collections.reverse(qualifiedNameElements);

                return renderFqName(qualifiedNameElements);
            }
            else {
                return renderFqName(DescriptorUtils.getFQName(cd));
            }
        }
    }

    private void appendTypeProjections(StringBuilder result, List<TypeProjection> typeProjections) {
        for (Iterator<TypeProjection> iterator = typeProjections.iterator(); iterator.hasNext(); ) {
            TypeProjection typeProjection = iterator.next();
            if (typeProjection.getProjectionKind() != Variance.INVARIANT) {
                result.append(typeProjection.getProjectionKind()).append(" ");
            }
            result.append(renderType(typeProjection.getType()));
            if (iterator.hasNext()) {
                result.append(", ");
            }
        }
    }


    private String renderFunctionType(JetType type) {
        StringBuilder sb = new StringBuilder();

        JetType receiverType = KotlinBuiltIns.getInstance().getReceiverType(type);
        if (receiverType != null) {
            sb.append(renderType(receiverType));
            sb.append(".");
        }

        sb.append("(");
        appendTypeProjections(sb, KotlinBuiltIns.getInstance().getParameterTypeProjectionsFromFunctionType(type));
        sb.append(") -> ");
        sb.append(renderType(KotlinBuiltIns.getInstance().getReturnTypeFromFunctionType(type)));

        if (type.isNullable()) {
            return "(" + sb + ")?";
        }
        return sb.toString();
    }


    /* METHODS FOR ALL KINDS OF DESCRIPTORS */
    private void appendDefinedIn(DeclarationDescriptor declarationDescriptor, StringBuilder stringBuilder) {
        if (declarationDescriptor instanceof ModuleDescriptor) {
            stringBuilder.append(" is a module");
            return;
        }
        stringBuilder.append(" ").append(renderMessage("defined in")).append(" ");

        final DeclarationDescriptor containingDeclaration = declarationDescriptor.getContainingDeclaration();
        if (containingDeclaration != null) {
            FqNameUnsafe fqName = DescriptorUtils.getFQName(containingDeclaration);
            stringBuilder.append(FqName.ROOT.equalsTo(fqName) ? "root package" : renderFqName(fqName));
        }
    }

    private void renderVisibility(Visibility visibility, StringBuilder builder) {
        if (!modifiers) return;
        if ("package".equals(visibility.toString())) {
            builder.append("public/*package*/ ");
        }
        else {
            builder.append(renderKeyword(visibility.toString())).append(" ");
        }
    }

    private void renderModality(Modality modality, StringBuilder builder) {
        if (!modifiers) return;
        String keyword = "";
        switch (modality) {
            case FINAL:
                keyword = "final";
                break;
            case OPEN:
                keyword = "open";
                break;
            case ABSTRACT:
                keyword = "abstract";
                break;
        }
        builder.append(renderKeyword(keyword)).append(" ");
    }

    @NotNull
    @Override
    public String render(@NotNull DeclarationDescriptor declarationDescriptor) {
        StringBuilder stringBuilder = new StringBuilder();
        declarationDescriptor.accept(new RenderDeclarationDescriptorVisitor(), stringBuilder);

        if (withDefinedIn) {
            appendDefinedIn(declarationDescriptor, stringBuilder);
        }
        return stringBuilder.toString();
    }


    /* TYPE PARAMETERS */
    private void renderTypeParameter(TypeParameterDescriptor descriptor, StringBuilder builder, boolean topLevel) {
        if (!descriptor.isReified()) {
            String variance = descriptor.getVariance().toString();
            if (!variance.isEmpty()) {
                builder.append(renderKeyword(variance)).append(" ");
            }
        }
        else {
            builder.append(renderKeyword("reified")).append(" ");
        }
        renderName(descriptor, builder);
        if (descriptor.getUpperBounds().size() == 1) {
            JetType upperBound = descriptor.getUpperBounds().iterator().next();
            if (!KotlinBuiltIns.getInstance().getDefaultBound().equals(upperBound)) {
                builder.append(" : ").append(renderType(upperBound));
            }
        }
        else if (topLevel) {
            boolean first = true;
            for (JetType upperBound : descriptor.getUpperBounds()) {
                if (upperBound.equals(KotlinBuiltIns.getInstance().getDefaultBound())) {
                    continue;
                }
                if (first) {
                    builder.append(" : ");
                }
                else {
                    builder.append(" & ");
                }
                builder.append(renderType(upperBound));
                first = false;
            }
        }
        else {
            // rendered with "where"
        }
    }

    private boolean renderTypeParameters(List<TypeParameterDescriptor> typeParameters, StringBuilder builder) {
        if (!typeParameters.isEmpty()) {
            builder.append(lt());
            for (Iterator<TypeParameterDescriptor> iterator = typeParameters.iterator(); iterator.hasNext(); ) {
                TypeParameterDescriptor typeParameterDescriptor = iterator.next();
                renderTypeParameter(typeParameterDescriptor, builder, false);
                if (iterator.hasNext()) {
                    builder.append(", ");
                }
            }
            builder.append(">");
            return true;
        }
        return false;
    }

    private void renderTypeParameter(TypeParameterDescriptor descriptor, StringBuilder builder) {
        builder.append(lt());
        renderTypeParameter(descriptor, builder, true);
        builder.append(">");
    }


    /* FUNCTIONS */
    private void renderFunction(FunctionDescriptor descriptor, StringBuilder builder) {
        if (!startFromName) {
            renderVisibility(descriptor.getVisibility(), builder);
            renderModality(descriptor.getModality(), builder);
            builder.append(renderKeyword("fun")).append(" ");
            if (renderTypeParameters(descriptor.getTypeParameters(), builder)) {
                builder.append(" ");
            }

            ReceiverParameterDescriptor receiver = descriptor.getReceiverParameter();
            if (receiver != null) {
                builder.append(escape(renderType(receiver.getType()))).append(".");
            }
        }

        renderName(descriptor, builder);
        renderValueParameters(descriptor, builder);
        builder.append(" : ").append(escape(renderType(descriptor.getReturnType())));
        renderWhereSuffix(descriptor, builder);
    }

    private void renderConstructor(ConstructorDescriptor constructorDescriptor, StringBuilder builder) {
        renderVisibility(constructorDescriptor.getVisibility(), builder);

        builder.append(renderKeyword("ctor")).append(" ");

        ClassDescriptor classDescriptor = constructorDescriptor.getContainingDeclaration();
        builder.append(classDescriptor.getName());

        renderTypeParameters(classDescriptor.getTypeConstructor().getParameters(), builder);
        renderValueParameters(constructorDescriptor, builder);
    }


    private void renderWhereSuffix(@NotNull CallableMemberDescriptor callable, @NotNull StringBuilder builder) {
        boolean first = true;
        for (TypeParameterDescriptor typeParameter : callable.getTypeParameters()) {
            if (typeParameter.getUpperBounds().size() > 1) {
                for (JetType upperBound : typeParameter.getUpperBounds()) {
                    if (first) {
                        builder.append(" ");
                        builder.append(renderKeyword("where"));
                        builder.append(" ");
                    }
                    else {
                        builder.append(", ");
                    }
                    builder.append(typeParameter.getName());
                    builder.append(" : ");
                    builder.append(escape(renderType(upperBound)));
                    first = false;
                }
            }
        }
    }

    @Override
    public String renderFunctionParameters(@NotNull FunctionDescriptor functionDescriptor) {
        StringBuilder stringBuilder = new StringBuilder();
        renderValueParameters(functionDescriptor, stringBuilder);
        return stringBuilder.toString();
    }

    private void renderValueParameters(FunctionDescriptor function, StringBuilder builder) {
        if (handler != null) {
            handler.appendBeforeValueParameters(function, builder);
            for (ValueParameterDescriptor parameter : function.getValueParameters()) {
                handler.appendBeforeValueParameter(parameter, builder);
                renderValueParameterOfFunction(parameter, builder);
                handler.appendAfterValueParameter(parameter, builder);
            }
            handler.appendAfterValueParameters(function, builder);
        }
        else {
            builder.append("(");
            for (ValueParameterDescriptor parameter : function.getValueParameters()) {
                renderValueParameterOfFunction(parameter, builder);
                if (parameter.getIndex() != function.getValueParameters().size() - 1) {
                    builder.append(", ");
                }
            }
            builder.append(")");
        }
    }

    private void renderValueParameterOfFunction(ValueParameterDescriptor descriptor, StringBuilder builder) {
        renderVariable(descriptor, builder, true);
        boolean withDefaultValue = debugMode ? descriptor.declaresDefaultValue() : descriptor.hasDefaultValue();
        if (withDefaultValue) {
            builder.append(" = ...");
        }
    }


    /* VARIABLES */
    private void renderValueParameter(ValueParameterDescriptor descriptor, StringBuilder builder) {
        builder.append(renderKeyword("value-parameter")).append(" ");
        renderVariable(descriptor, builder);
    }

    private void renderVariable(VariableDescriptor descriptor, StringBuilder builder) {
        renderVariable(descriptor, builder, false);
    }

    private void renderVariable(VariableDescriptor descriptor, StringBuilder builder, boolean skipValVar) {
        JetType type = descriptor.getType();
        if (descriptor instanceof ValueParameterDescriptor) {
            JetType varargElementType = ((ValueParameterDescriptor) descriptor).getVarargElementType();
            if (varargElementType != null) {
                builder.append(renderKeyword("vararg")).append(" ");
                type = varargElementType;
            }
        }
        String typeString = renderPropertyPrefixAndComputeTypeString(
                builder,
                skipValVar ? null : descriptor.isVar(),
                Collections.<TypeParameterDescriptor>emptyList(),
                ReceiverParameterDescriptor.NO_RECEIVER_PARAMETER,
                type);
        renderName(descriptor, builder);
        builder.append(" : ").append(escape(typeString));
    }

    private String renderPropertyPrefixAndComputeTypeString(
            @NotNull StringBuilder builder,
            @Nullable Boolean isVar,
            @NotNull List<TypeParameterDescriptor> typeParameters,
            @Nullable ReceiverParameterDescriptor receiver,
            @Nullable JetType outType
    ) {
        String typeString = lt() + "no type>";
        if (outType != null) {
            if (isVar != null && !startFromName) {
                builder.append(renderKeyword(isVar ? "var" : "val")).append(" ");
            }
            typeString = renderType(outType);
        }

        renderTypeParameters(typeParameters, builder);

        if (receiver != null) {
            builder.append(escape(renderType(receiver.getType()))).append(".");
        }

        return typeString;
    }

    private void renderProperty(PropertyDescriptor descriptor, StringBuilder builder) {
        if (!startFromName) {
            renderVisibility(descriptor.getVisibility(), builder);
            renderModality(descriptor.getModality(), builder);
        }
        String typeString = renderPropertyPrefixAndComputeTypeString(
                builder,
                descriptor.isVar(),
                descriptor.getTypeParameters(),
                descriptor.getReceiverParameter(),
                descriptor.getType());
        renderName(descriptor, builder);
        builder.append(" : ").append(escape(typeString));
    }


    /* CLASSES */
    private void renderClass(ClassDescriptor descriptor, StringBuilder builder) {
        String keyword;
        switch (descriptor.getKind()) {
            case TRAIT:
                keyword = "trait";
                break;
            case ENUM_CLASS:
                keyword = "enum class";
                break;
            case OBJECT:
                keyword = "object";
                break;
            case ANNOTATION_CLASS:
                keyword = "annotation class";
                break;
            case CLASS_OBJECT:
                keyword = "class object";
                break;
            default:
                keyword = "class";
        }
        renderClass(descriptor, builder, keyword);
    }

    private void renderClass(ClassDescriptor descriptor, StringBuilder builder, String keyword) {
        boolean isNotClassObject = descriptor.getKind() != ClassKind.CLASS_OBJECT;
        if (!startFromName) {
            if (isNotClassObject) {
                renderVisibility(descriptor.getVisibility(), builder);
                if (descriptor.getKind() != ClassKind.TRAIT && descriptor.getKind() != ClassKind.OBJECT) {
                    renderModality(descriptor.getModality(), builder);
                }
            }
            builder.append(renderKeyword(keyword));
            if (isNotClassObject) {
                builder.append(" ");
            }
        }
        if (isNotClassObject) {
            renderName(descriptor, builder);
            renderTypeParameters(descriptor.getTypeConstructor().getParameters(), builder);
        }
        if (!descriptor.equals(KotlinBuiltIns.getInstance().getNothing())) {
            Collection<JetType> supertypes = descriptor.getTypeConstructor().getSupertypes();
            if (supertypes.isEmpty() || supertypes.size() == 1 && KotlinBuiltIns.getInstance().isAny(supertypes.iterator().next())) {
            }
            else {
                builder.append(" : ");
                for (Iterator<JetType> iterator = supertypes.iterator(); iterator.hasNext(); ) {
                    JetType supertype = iterator.next();
                    builder.append(renderType(supertype));
                    if (iterator.hasNext()) {
                        builder.append(", ");
                    }
                }
            }
        }
    }


    /* OTHER */
    private void renderModuleOrScript(DeclarationDescriptor descriptor, StringBuilder builder) {
        renderName(descriptor, builder);
    }

    private void renderNamespace(NamespaceDescriptor namespaceDescriptor, StringBuilder builder) {
        builder.append(renderKeyword(JetTokens.PACKAGE_KEYWORD.getValue())).append(" ");
        renderName(namespaceDescriptor, builder);
    }


    /* STUPID DISPATCH-ONLY VISITOR */
    private class RenderDeclarationDescriptorVisitor extends DeclarationDescriptorVisitorEmptyBodies<Void, StringBuilder> {
        @Override
        public Void visitValueParameterDescriptor(ValueParameterDescriptor descriptor, StringBuilder builder) {
            renderValueParameter(descriptor, builder);
            return null;
        }

        @Override
        public Void visitVariableDescriptor(VariableDescriptor descriptor, StringBuilder builder) {
            renderVariable(descriptor, builder);
            return null;
        }

        @Override
        public Void visitPropertyDescriptor(PropertyDescriptor descriptor, StringBuilder builder) {
            renderProperty(descriptor, builder);
            return null;
        }

        @Override
        public Void visitFunctionDescriptor(FunctionDescriptor descriptor, StringBuilder builder) {
            renderFunction(descriptor, builder);
            return null;
        }

        @Override
        public Void visitReceiverParameterDescriptor(ReceiverParameterDescriptor descriptor, StringBuilder data) {
            throw new UnsupportedOperationException("Don't render receiver parameters");
        }

        @Override
        public Void visitConstructorDescriptor(ConstructorDescriptor constructorDescriptor, StringBuilder builder) {
            renderConstructor(constructorDescriptor, builder);
            return null;
        }

        @Override
        public Void visitTypeParameterDescriptor(TypeParameterDescriptor descriptor, StringBuilder builder) {
            renderTypeParameter(descriptor, builder);
            return null;
        }

        @Override
        public Void visitNamespaceDescriptor(NamespaceDescriptor namespaceDescriptor, StringBuilder builder) {
            renderNamespace(namespaceDescriptor, builder);
            return null;
        }

        @Override
        public Void visitModuleDeclaration(ModuleDescriptor descriptor, StringBuilder builder) {
            renderModuleOrScript(descriptor, builder);
            return null;
        }

        @Override
        public Void visitScriptDescriptor(ScriptDescriptor scriptDescriptor, StringBuilder builder) {
            renderModuleOrScript(scriptDescriptor, builder);
            return null;
        }

        @Override
        public Void visitClassDescriptor(ClassDescriptor descriptor, StringBuilder builder) {
            renderClass(descriptor, builder);
            return null;
        }

   }
}