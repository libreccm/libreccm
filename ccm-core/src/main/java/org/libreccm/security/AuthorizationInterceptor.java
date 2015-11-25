/*
 * Copyright (C) 2015 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.libreccm.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.subject.Subject;
import org.libreccm.core.CcmObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * A CDI interceptor which can be used to secure methods of CDI beans. To use
 * the interceptor annotation the method to secure with
 * {@link AuthorizationRequired} and one or more {@link RequiresRole} and/or
 * {@link RequiresPrivilege} annotations.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@AuthorizationRequired
@Interceptor
public class AuthorizationInterceptor {

    /**
     * A logger for providing some informations what the interceptor is doing.
     */
    private static final Logger LOGGER = LogManager.getLogger(
        AuthorizationInterceptor.class);

    /**
     * The current subject
     */
    @Inject
    private Subject subject;

    /**
     * An instance of the {@link PermissionChecker} bean. The interceptor uses
     * the methods of the {@code PermissionChecker} class to check if the
     * current subject has the required roles and permissions.
     */
    @Inject
    private PermissionChecker permissionChecker;

    /**
     * Called by the CDI container when a method annotated with
     * {@link AuthorizationRequired} is called.
     *
     * @param context The invocation context of the method.
     *
     * @return The return value of the called method.
     *
     * @throws Exception If any exception occurs.
     */
    @AroundInvoke
    public Object intercept(final InvocationContext context) throws Exception {
        LOGGER.debug("Intercepting method invocation");

        //Get the method which call was intercepted.
        final Method method = context.getMethod();
        if (method == null) {
            throw new IllegalArgumentException(
                "The authoriziation interceptor can only be used for method");
        }

        // Check if the method has a RequiresRole annotation. If yes, check
        // if the current subject is assigned to the required role.
        if (method.isAnnotationPresent(RequiresRole.class)) {
            final String requiredRole = method.getAnnotation(RequiresRole.class)
                .value();
            subject.checkRoles(requiredRole);
        }

        // Check if the RequiresPrivilege annotation is present on the
        // method level. If yes check if the current subject has a permission
        // granting the required privilege.
        if (method.isAnnotationPresent(RequiresPrivilege.class)) {
            final String requiredPrivilege = method.getAnnotation(
                RequiresPrivilege.class).value();
            permissionChecker.checkPermission(requiredPrivilege);
        }

        // Check if one or more parameters are annotated with the 
        // RequiredPrivilege annotation. If yes check if the parameter is
        // of type CcmObject and check if the current subject has a permission
        // granting the required privilege on the provided CcmObject instance.
        final Annotation[][] annotations = method.getParameterAnnotations();
        final Object[] parameters = context.getParameters();
        if (parameters != null && parameters.length > 0
                && annotations != null && annotations.length > 0) {
            for (int i = 0; i < parameters.length; i++) {
                checkParameterPermission(parameters[i], annotations[i]);
            }
        }

        return context.proceed();
    }

    /**
     * Helper method for checking the parameter permissions.
     * 
     * @param parameter The parameter to check.
     * @param annotations All annotations of the parameter.
     */
    private void checkParameterPermission(final Object parameter,
                                          final Annotation[] annotations) {
        if (parameter instanceof CcmObject
                && annotations != null
                && annotations.length > 0) {
            final CcmObject object = (CcmObject) parameter;

            String requiredPrivilege = null;
            for (Annotation annotation : annotations) {
                if (annotation instanceof RequiresPrivilege) {
                    requiredPrivilege = ((RequiresPrivilege) annotation).value();
                    break;
                }
            }

            if (requiredPrivilege != null && !requiredPrivilege.isEmpty()) {
                permissionChecker.checkPermission(requiredPrivilege, object);
            }
        }
    }

}
