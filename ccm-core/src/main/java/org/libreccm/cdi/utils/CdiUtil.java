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
package org.libreccm.cdi.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Iterator;
import java.util.Set;

/**
 * Utility class providing access the CDI beans in classes not eligible for
 * injection.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CdiUtil {

    private final static Logger LOGGER = LogManager.getLogger(CdiUtil.class);

    private final BeanManager beanManager;

    private CdiUtil(final BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    /**
     * Get a new instance of {@code CdiUtil}.
     *
     * This factory method looks up the {@link BeanManager} using JNDI and
     * passes the {@link BeanManager} to the constructor of {@code CdiUtil}.
     *
     * @return A new instance of {@code CdiUtil}.
     */
    public static CdiUtil createCdiUtil() {
        try {
            final InitialContext context = new InitialContext();
            final BeanManager beanManager = (BeanManager) context.lookup(
                "java:comp/BeanManager"
            );
            return new CdiUtil(beanManager);
        } catch (NamingException ex) {
            throw new IllegalStateException("Unable to lookup BeanManager.", ex);
        }
    }

    /**
     * Get an instance of the CDI bean of the provided class.
     *
     * @param <T>      Type of the CDI bean.
     * @param beanType The type of the bean to retrieve.
     *
     * @return An instance of {@code beanType}.
     *
     * @throws IllegalStateException if no CDI bean of the type {@code beanType}
     *                               is available.
     */
    @SuppressWarnings("unchecked")
    public <T> T findBean(final Class<T> beanType) {
        final Set<Bean<?>> beans = beanManager.getBeans(beanType);
        final Iterator<Bean<?>> iterator = beans.iterator();
        if (iterator.hasNext()) {
            @SuppressWarnings("unchecked")
            final Bean<T> bean = (Bean<T>) iterator.next();
            final CreationalContext<T> ctx = beanManager
                .createCreationalContext(bean);

            return (T) beanManager.getReference(bean, beanType, ctx);
        } else {
            LOGGER.error(new ParameterizedMessage(
                "No CDI Bean for type {0} found.", beanType.getName()));
            throw new IllegalStateException(String.format(
                "No CDI Bean for type \"%s\" found", beanType.getName()));
        }
    }

}
