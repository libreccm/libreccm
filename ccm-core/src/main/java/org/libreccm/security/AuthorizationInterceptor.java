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

import org.libreccm.security.PermissionChecker;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@AuthorizationRequired
@Interceptor
public class AuthorizationInterceptor {

    private static final Logger LOGGER = LogManager.getLogger(
        AuthorizationInterceptor.class);

    @Inject
    private Subject subject;

    @Inject
    private PermissionChecker permissionChecker;

    @AroundInvoke
    public Object intercept(final InvocationContext context) throws Exception {
        LOGGER.debug("Intercepting method invocation");

        final Method method = context.getMethod();
        if (method == null) {
            throw new IllegalArgumentException(
                "The authoriziation interceptor can only be used for method");
        }

        if (method.isAnnotationPresent(RequiresRole.class)) {
            final String requiredRole = method.getAnnotation(RequiresRole.class)
                .value();
            subject.checkRoles(requiredRole);
        }

        if (method.isAnnotationPresent(RequiresPrivilege.class)) {
            final String requiredPrivilege = method.getAnnotation(
                RequiresPrivilege.class).value();
            permissionChecker.checkPermission(requiredPrivilege);
        }

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

    private void checkParameterPermission(final Object parameter,
                                          final Annotation[] annotations) {
        if (parameter instanceof CcmObject 
            && annotations != null 
            && annotations.length > 0) {
            final CcmObject object = (CcmObject) parameter;
            
            String requiredPrivilege = null;
            for(Annotation annotation : annotations) {
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
