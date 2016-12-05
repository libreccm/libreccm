/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.libreccm.pagemodel;

import org.libreccm.web.CcmApplication;

import java.util.Iterator;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class PageBuilderManager {

    @Inject
    private Instance<PageBuilder<?>> pageBuilders;

    public PageBuilder<?> findPageBuilder(
        final String type,
        final Class<? extends CcmApplication> applicationType) {

        final PageModelTypeLiteral literal = new PageModelTypeLiteral(
            type, applicationType);

        final Instance<PageBuilder<?>> instance = pageBuilders.select(literal);
        if (instance.isUnsatisfied()) {
            throw new IllegalArgumentException(String.format(
                "No PageBuilder for type \"%s\" and application type \"%s\" "
                    + "available.",
                type,
                applicationType));
        } else if (instance.isAmbiguous()) {
            throw new IllegalArgumentException(String.format(
                "Multiple PageBuilders for type \"%s\" and "
                    + "application type \"%s\" avilable. Something is wrong.",
                type,
                applicationType));
        } else {
            final Iterator<PageBuilder<?>> iterator = instance.iterator();
            final PageBuilder<?> pageBuilder = iterator.next();

            return pageBuilder;
        }
    }

    private class PageModelTypeLiteral
        extends AnnotationLiteral<PageModelType>
        implements PageModelType {

        private static final long serialVersionUID = 5919950993273871601L;

        private final String type;
        private final Class<? extends CcmApplication> applicationType;

        public PageModelTypeLiteral(
            final String type,
            final Class<? extends CcmApplication> applicationType) {

            this.type = type;
            this.applicationType = applicationType;
        }

        @Override
        public String type() {
            return type;
        }

        @Override
        public Class<? extends CcmApplication> applicationType() {
            return applicationType;
        }

    }

}
