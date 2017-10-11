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

import java.util.Map;

import javax.enterprise.context.RequestScoped;

/**
 * Interface for page builders. A page builder is invoked to build a page a
 * specific type. An implementation must be a CDI bean which is annotated with
 * the qualifier {@link PageModelType}. The recommended scope is
 * {@link RequestScoped}.
 *
 * An implementation should add all default components which have to be present
 * in page. The {@link PageModel} should only specify
 * <strong>additional</strong> components.
 *
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public interface PageBuilder {

    /**
     * Build a page for the view technology supported by this page builder
     * without any additional components.
     * {@link #buildPage(org.libreccm.pagemodel.PageModel)} should use this
     * method for creating the default page.
     *
     * @param parameters Parameters provided by application which wants to
     *                   render a {@link PageModel}. The parameters are passed
     *                   the {@link ComponentBuilder}s.
     *
     * @return A page with the default components.
     */
    Map<String, Object> buildPage(Map<String, Object> parameters);

    /**
     * Build a page of type {@code P} using the provided {@link PageModel}.
     * Implementations should call the implementation of {@link #buildPage()}
     * for creating the basic page with the default components.
     *
     * @param pageModel  The {@link PageModel} from which the page is generated.
     * @param parameters Parameters provided by application which wants to
     *                   render a {@link PageModel}. The parameters are passed
     *                   the {@link ComponentBuilder}s.
     *
     * @return The page generated from the provided {@link PageModel}.
     */
    Map<String, Object> buildPage(PageModel pageModel,
                                  Map<String, Object> parameters);

}
