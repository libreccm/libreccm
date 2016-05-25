/*
 * Copyright (c) 2013 Jens Pelzetter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.ui.admin.applications;

import com.arsdigita.bebop.Form;

import org.libreccm.web.CcmApplication;

/**
 * An abstract class providing a default implementation of
 * {@link ApplicationManager#getApplicationCreateForm()}.
 *
 * @param <T> Type of the application for which this ApplicationManager provides
 *            the administration forms.
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id: AbstractSingletonApplicationManager.java 3703 2015-10-21
 * 08:52:39Z jensp $
 */
public abstract class AbstractSingletonApplicationManager<T extends CcmApplication>
    implements ApplicationManager<T> {

    /**
     * Implementation of {@link ApplicationManager#getApplicationCreateForm()}
     * for singleton applications.
     *
     * @return {@code} null because it is not possible to create instances of
     *         singleton applications.
     */
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    @Override
    public final Form getApplicationCreateForm() {
        return null;
    }

}
