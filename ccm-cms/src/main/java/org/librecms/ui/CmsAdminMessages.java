/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.ui;

import org.libreccm.ui.AbstractMessagesBean;
import org.librecms.CmsConstants;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * Provides simple access to the messages in the cms admin bundle. The make it
 * as easy as possible to access the messages this class is implemented as a map
 * a made available as named bean. For simple messages,
 * {@code CmsAdminMesssages} can be used like a map in a facelets template:
 *
 * <pre>
 * #{CmsAdminMessages['some.message.key'])
 * </pre>
 *
 * Messages with placeholders can be retrieved using
 * {@link #getMessage(java.lang.String, java.util.List)} or
 * {@link #getMessage(java.lang.String, java.lang.Object[])}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsAdminMessages")
public class CmsAdminMessages extends AbstractMessagesBean {

    @Override
    protected String getMessageBundle() {
        return CmsConstants.CMS_ADMIN_BUNDLE;
    }

}
