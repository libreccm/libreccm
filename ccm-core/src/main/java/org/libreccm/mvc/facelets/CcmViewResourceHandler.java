/*
 * Copyright (C) 2020 LibreCCM Foundation.
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
package org.libreccm.mvc.facelets;

import org.libreccm.theming.Themes;

import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;
import javax.faces.application.ViewResource;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 * A Facelets resource handler that loads Facelets templates from LibreCCM
 * themes.
 *
 * This handler only works for view resources. Only resource path which are
 * starting with {@code @themes} or {@code /@themes} are used processed. All
 * other paths are delegated to the wrapped resource handler.
 * 
 * To enable this resource handler to following snippet must be present in the
 * {@code faces-config.xml} file of the WAR (bundle):
 * 
 * <pre>
 * &lt;application&gt;
        &tl;resource-handler&gt;org.libreccm.ui.CcmFaceletsResourceHandler&lt;/resource-handler&gt;
    &lt;/application&gt;

 * </pre>
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CcmViewResourceHandler extends ResourceHandlerWrapper {

    @Inject
    private Themes themes;

    private final ResourceHandler wrapped;

    public CcmViewResourceHandler(final ResourceHandler wrapped) {
        super(wrapped);
        this.wrapped = wrapped;
    }

    @Override
    public ViewResource createViewResource(
        final FacesContext context, final String path
    ) {
        if (path.startsWith("@themes") || path.startsWith("/@themes")) {
            return new CcmThemeViewResource(themes, path);
        } else {
            return super.createViewResource(context, path);
        }
    }

}
