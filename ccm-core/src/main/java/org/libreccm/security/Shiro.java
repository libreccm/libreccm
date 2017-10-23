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

import com.arsdigita.kernel.KernelConfig;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;

/**
 * This application scoped CDI bean acts as bridge between CDI and Shiro. It
 * initialises the Shiro environment and provides the Shiro
 * {@link SecurityManager} and the current Shiro {@link Subject} via CDI
 * producer methods.
 *
 * This class is based on the implementation for the upcoming CDI integration of
 * Shiro discussed at https://issues.apache.org/jira/browse/SHIRO-337 and the
 * implementation which can be found at https://github.com/hwellmann/shiro
 * (commit 8a40df0).
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationScoped
public class Shiro implements Serializable {

    private static final long serialVersionUID = -3270585472267405099L;

    /**
     * Principal used for the public user if
     * {@link KernelConfig#primaryUserIdentifier} is set to
     * {@link KernelConfig#USER_IDENTIFIER_SCREEN_NAME}.
     *
     * @see #getPublicUser()
     */
    public static final String PUBLIC_USER_PRINCIPAL_SCREEN_NAME = "public-user";
    /**
     * Principal used for the public user if
     * {@link KernelConfig#primaryUserIdentifier} is set to
     * {@link KernelConfig#USER_IDENTIFIER_EMAIL}.
     *
     * @see #getPublicUser()
     */
    public static final String PUBLIC_USER_PRINCIPAL_EMAIL
                               = "public-user@localhost";

    /**
     * Principal used for the system user if
     * {@link KernelConfig#primaryUserIdentifier} is set to
     * {@link KernelConfig#USER_IDENTIFIER_SCREEN_NAME}.
     *
     * @see #getSystemUser()
     */
    public static final String SYSTEM_USER_PRINCIPAL_SCREEN_NAME = "system-user";
    /**
     * Principal used for the system user if
     * {@link KernelConfig#primaryUserIdentifier} is set to
     * {@link KernelConfig#USER_IDENTIFIER_EMAIL}.
     *
     * @see #getSystemUser()
     */
    public static final String SYSTEM_USER_PRINCIPAL_EMAIL
                               = "system-user@localhost";

    @Inject
    private UserRepository userRepository;

    /**
     * Provides access Shiro's {@link SecurityManager}.
     *
     * @return The Shiro {@link SecurityManager}.
     */
    @Produces
    @Named("securityManager")
    public SecurityManager getSecurityManager() {
        return proxy(SecurityManager.class,
                     new SecurityManagerInvocationHandler());
    }

    /**
     * Provides access the the current Shiro {@link Subject}.
     *
     * @return The current {@link Subject}.
     *
     */
    @Produces
    public Subject getSubject() {
        return proxy(Subject.class, new SubjectInvocationHandler());
    }

    @Produces
    public Session getSession() {
        return proxy(Session.class, new SessionInvocationHandler());
    }

    /**
     * Subject which is used to store the permissions for a none authenticated
     * user.
     *
     * @return
     */
    public Subject getPublicUser() {
        if (KernelConfig.getConfig().emailIsPrimaryIdentifier()) {
            return buildInternalSubject(PUBLIC_USER_PRINCIPAL_EMAIL);
        } else {
            return buildInternalSubject(PUBLIC_USER_PRINCIPAL_SCREEN_NAME);
        }
    }

    public boolean isPublicUser() {
        final Subject subject = getSubject();
        final Object principal = subject.getPrincipal();

        if (KernelConfig.getConfig().emailIsPrimaryIdentifier()) {
            return PUBLIC_USER_PRINCIPAL_EMAIL.equals(principal);
        } else {
            return PUBLIC_USER_PRINCIPAL_SCREEN_NAME.equals(principal);
        }
    }

    /**
     * A virtual user for internal processes which has all permissions.
     *
     * @return
     */
    public Subject getSystemUser() {
        if (KernelConfig.getConfig().emailIsPrimaryIdentifier()) {
            return buildInternalSubject(SYSTEM_USER_PRINCIPAL_EMAIL);
        } else {
            return buildInternalSubject(SYSTEM_USER_PRINCIPAL_SCREEN_NAME);
        }
    }

    public boolean isSystemUser() {
        final Subject subject = getSubject();
        final Object principal = subject.getPrincipal();

        if (KernelConfig.getConfig().emailIsPrimaryIdentifier()) {
            return SYSTEM_USER_PRINCIPAL_EMAIL.equals(principal);
        } else {
            return SYSTEM_USER_PRINCIPAL_SCREEN_NAME.equals(principal);
        }
    }

    /**
     * Retrieve the {@link User} entity from the database for the current
     * subject.
     *
     * @return An {@link Optional} containing the {@link User} entity for the
     *         current subject. If the current subject is a virtual user which
     *         has no representation in the database the returned
     *         {@link Optional} is empty.
     *
     * @see #getSubject()
     * @see #getSystemUser()
     * @see #getPublicUser()
     */
    public Optional<User> getUser() {
        final KernelConfig kernelConfig = KernelConfig.getConfig();
        if (kernelConfig.emailIsPrimaryIdentifier()) {
            return userRepository.findByEmailAddress((String) getSubject().
                getPrincipal());
        } else {
            return userRepository.findByName((String) getSubject().
                getPrincipal());
        }
    }

    private Subject buildInternalSubject(final String userName) {
        final PrincipalCollection principals = new SimplePrincipalCollection(
            userName, "CcmShiroRealm");
        final Subject internalUser = new Subject.Builder()
            .principals(principals)
            .authenticated(true)
            .buildSubject();

        return internalUser;
    }

    @SuppressWarnings("unchecked")
    private <T> T proxy(final Class<T> clazz, final InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(getClass().getClassLoader(),
                                          new Class<?>[]{clazz},
                                          handler);
    }

    private static abstract class Handler implements InvocationHandler {

        public abstract Object handlerInvoke(Object proxy,
                                             Method method,
                                             Object[] args) throws Throwable;

        @Override
        public Object invoke(final Object proxy,
                             final Method method,
                             Object[] args) throws Throwable {
            try {
                return handlerInvoke(proxy, method, args);
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }

    }

    private static class SubjectInvocationHandler extends Handler {

        @Override
        public Object handlerInvoke(final Object proxy,
                                    final Method method,
                                    final Object[] args) throws Throwable {
            return method.invoke(SecurityUtils.getSubject(), args);
        }

    }

    private static class SecurityManagerInvocationHandler extends Handler {

        @Override
        public Object handlerInvoke(final Object proxy,
                                    final Method method,
                                    final Object[] args) throws Throwable {
            return method.invoke(SecurityUtils.getSecurityManager(), args);
        }

    }

    private class SessionInvocationHandler extends Handler {

        @Override
        public Object handlerInvoke(final Object proxy,
                                    final Method method,
                                    final Object[] args) throws Throwable {
            return method.invoke(SecurityUtils.getSubject().getSession(), args);
        }

    }

}
