////////////////////////////////////////////////////////////////////////////////////////////////////
// Copyright 2011 Astina AG, Zurich
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
////////////////////////////////////////////////////////////////////////////////////////////////////
package ch.astina.hesperid.web.services.springsecurity.internal;

import java.lang.reflect.Modifier;

import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.model.MutableComponentModel;
import org.apache.tapestry5.runtime.Component;
import org.apache.tapestry5.services.ClassTransformation;
import org.apache.tapestry5.services.ComponentClassTransformWorker;
import org.apache.tapestry5.services.ComponentInstanceOperation;
import org.apache.tapestry5.services.FieldAccess;
import org.apache.tapestry5.services.TransformConstants;
import org.apache.tapestry5.services.TransformField;
import org.apache.tapestry5.services.TransformMethod;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.annotation.Secured;
import org.springframework.security.intercept.InterceptorStatusToken;

/**
 * @author Ivan Dubrov
 */
public class SpringSecurityWorker implements ComponentClassTransformWorker
{
    private static final String CONFIG_ATTRIBUTE_DEFINITION_FIELD_NAME = "_$configAttributeDefinition";
    private static final String TOKEN_FIELD_NAME = "_$token";
    private static final String CHECKER_FIELD_NAME = "_$checker";
    private SecurityChecker securityChecker;

    public SpringSecurityWorker(final SecurityChecker securityChecker)
    {
        this.securityChecker = securityChecker;
    }

    public final void transform(final ClassTransformation transformation,
            final MutableComponentModel model)
    {
        // Secure methods
        for (TransformMethod method : transformation.matchMethodsWithAnnotation(Secured.class)) {
            transformMethod(transformation, method);
        }

        // Secure pages
        Secured annotation = transformation.getAnnotation(Secured.class);
        if (annotation != null) {
            transformPage(transformation, annotation);
        }

        // Added by K.S. to fix it for Tapestry 5.1.0.0
        model.addRenderPhase(BeginRender.class);
        model.addRenderPhase(SetupRender.class);
    }

    private void transformPage(final ClassTransformation transformation, final Secured annotation)
    {
        // Security checker
        final String interField = transformation.addInjectedField(SecurityChecker.class,
                CHECKER_FIELD_NAME, securityChecker);
        final FieldAccess checkerFieldAccess = transformation.getField(interField).getAccess();

        // Attribute definition
        final FieldAccess configFieldAccess = createConfigAttributeDefinitionField(transformation,
                annotation);

        // Interceptor token
        final TransformField statusTokenField = transformation.createField(Modifier.PRIVATE,
                InterceptorStatusToken.class.getCanonicalName(), TOKEN_FIELD_NAME);
        final FieldAccess statusTokenFieldAccess = statusTokenField.getAccess();

        // Extend class
        TransformMethod beginRenderMethod = transformation.getOrCreateMethod(TransformConstants.BEGIN_RENDER_SIGNATURE);
        beginRenderMethod.addOperationBefore(new ComponentInstanceOperation() {

            public void invoke(Component instance) {
                // sets the InterceptorStatusToken inside instance
                // by calling {@link
                // SecurityChecker#checkBefore(ConfigAttributeDefinition)}
                statusTokenFieldAccess.write(instance, ((SecurityChecker) checkerFieldAccess.read(instance)).checkBefore(configFieldAccess.read(instance)));
            }
        });

        TransformMethod cleanupRenderMethod = transformation.getOrCreateMethod(TransformConstants.CLEANUP_RENDER_SIGNATURE);
        cleanupRenderMethod.addOperationAfter(new ComponentInstanceOperation() {

            public void invoke(Component instance) {
                // calls {@link
                // SecurityChecker#checkAfter(InterceptorStatusToken, Object)}
                // inside instance
                ((SecurityChecker) checkerFieldAccess.read(instance)).checkAfter(
                        ((InterceptorStatusToken) statusTokenFieldAccess.read(instance)), null);
            }
        });
    }

    private void transformMethod(final ClassTransformation transformation,
            final TransformMethod method)
    {
        // Security checker
        final String interField = transformation.addInjectedField(SecurityChecker.class,
                CHECKER_FIELD_NAME, securityChecker);
        final FieldAccess checkerFieldAccess = transformation.getField(interField).getAccess();

        // Interceptor status token
        final TransformField statusTokenField = transformation.createField(Modifier.PRIVATE,
                InterceptorStatusToken.class.getCanonicalName(), TOKEN_FIELD_NAME);
        final FieldAccess statusTokenFieldAccess = statusTokenField.getAccess();

        // Attribute definition
        final Secured annotation = method.getAnnotation(Secured.class);
        final FieldAccess configFieldAccess = createConfigAttributeDefinitionField(transformation,
                annotation);

        // Prefix and extend method
        method.addOperationBefore(new ComponentInstanceOperation() {

            public void invoke(Component instance)
            {
                // sets the InterceptorStatusToken inside instance
                // by calling {@link
                // SecurityChecker#checkBefore(ConfigAttributeDefinition)}
                statusTokenFieldAccess.write(instance, ((SecurityChecker) checkerFieldAccess.read(instance)).checkBefore(configFieldAccess.read(instance)));
            }
        });
        method.addOperationAfter(new ComponentInstanceOperation() {

            public void invoke(Component instance)
            {
                // calls {@link
                // SecurityChecker#checkAfter(InterceptorStatusToken, Object)}
                // inside instance
                ((SecurityChecker) checkerFieldAccess.read(instance)).checkAfter(
                        ((InterceptorStatusToken) statusTokenFieldAccess.read(instance)), null);
            }
        });
    }

    private FieldAccess createConfigAttributeDefinitionField(
            final ClassTransformation transformation, final Secured annotation)
    {
        ConfigAttributeDefinition configAttributeDefinition = new ConfigAttributeDefinition(
                annotation.value());
        String fieldName = transformation.addInjectedField(ConfigAttributeDefinition.class,
                CONFIG_ATTRIBUTE_DEFINITION_FIELD_NAME, configAttributeDefinition);
        return transformation.getField(fieldName).getAccess();
    }
}
