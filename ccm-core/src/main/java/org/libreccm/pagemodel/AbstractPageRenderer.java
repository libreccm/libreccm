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

import com.arsdigita.ui.UI;
import com.arsdigita.ui.login.LoginConstants;
import com.arsdigita.ui.login.LoginServlet;
import com.arsdigita.web.URL;

import org.apache.shiro.subject.Subject;
import org.libreccm.security.Shiro;
import org.libreccm.security.User;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

/**
 * An abstract base class for implementations of the {@link PageRenderer}
 * interface providing some functionality needed by all implementations of the
 * {@link PageRenderer} interface.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 */
public abstract class AbstractPageRenderer implements PageRenderer {

    @Inject
    private ComponentRendererManager componentRendererManager;

    @Inject
    private HttpServletRequest request;

    @Inject
    private Shiro shiro;

    /**
     * Renders a {@code Page} based on a {@link PageModel}. This implementation
     * first calls {@link #renderPage()} to create the page object. After that
     * all {@link ComponentModel}s of the {@link PageModel} are processed and
     * the component objects created by the {@link ComponentRenderer}s are added
     * to the page.
     *
     * @param pageModel  The {@link PageModel} to render.
     * @param parameters Parameters provided by application which wants to
     *                   render a {@link PageModel}. The parameters are passed
     *                   the {@link ComponentRenderer}s.
     *
     * @return A map containing the results from rendering the components of the
     *         page model.
     */
    @Override
    public Map<String, Object> renderPage(final PageModel pageModel,
                                          final Map<String, Object> parameters) {

        final Map<String, Object> page = renderPage(parameters);

        final Map<String, Object> currentUserData = new HashMap<>();
        if (shiro.getUser().isPresent()) {
            final User currentUser = shiro.getUser().get();
            final Subject currentSubject = shiro.getSubject();
            currentUserData.put(
                "authenticated", currentSubject.isAuthenticated()
            );
            currentUserData.put("username", currentUser.getName());
            currentUserData.put(
                "email", currentUser.getPrimaryEmailAddress().toString()
            );
            currentUserData.put("familyName", currentUser.getFamilyName());
            currentUserData.put("givenName", currentUser.getGivenName());

            currentUserData.put(
                "workspaceUrl",
                URL.there(request, UI.getWorkspaceURL()).toString()
            );
            currentUserData.put(
                "loginUrl",
                URL.there(request, LoginConstants.LOGIN_PAGE_URL).toString()
            );
            currentUserData.put(
                "logoutUrl",
                URL.there(
                    request,
                    LoginServlet.getLogoutPageURL()).toString()
            );
            currentUserData.put(
                "changePasswordUrl",
                URL.there(
                    request,
                    LoginServlet.getLogoutPageURL()).toString()
            );

        } else {
            currentUserData.put("authenticated", false);
        }
        page.put("currentUser", currentUserData);

        for (final ContainerModel containerModel : pageModel.getContainers()) {

            final Map<String, Object> container = renderContainer(
                containerModel, parameters);
            page.put(containerModel.getKey(), container);
        }

        return page;
    }

    protected Map<String, Object> renderContainer(
        final ContainerModel containerModel,
        final Map<String, Object> parameters) {

        final Map<String, Object> container = new HashMap<>();

        container.put("key", containerModel.getKey());
        if (containerModel.getStyles() != null) {
            container.put("styles", containerModel.getStyles().toCss());
        }

        for (final ComponentModel componentModel : containerModel
            .getComponents()) {

            renderComponent(componentModel,
                            componentModel.getClass(),
                            parameters)
                .ifPresent(component -> container.put(componentModel.getKey(),
                                                      component));
        }

        return container;
    }

    /**
     * Helper method for rendering the components.
     *
     * @param <M>                 Generics variable for the type of rendered
     *                            component
     * @param componentModel      The {@link ComponentModel} to process.
     * @param componentModelClass The class of the {@link ComponentModel}.
     * @param parameters          Parameters provided by application which wants
     *                            to render a {@link PageModel}. The parameters
     *                            are passed the {@link ComponentRenderer}s.
     *
     * @return A map containing the results from rendering the components of the
     *         page model.
     */
    protected <M extends ComponentModel> Optional<Object> renderComponent(
        final ComponentModel componentModel,
        final Class<M> componentModelClass,
        final Map<String, Object> parameters) {

        final Optional<ComponentRenderer<M>> renderer = componentRendererManager
            .findComponentRenderer(componentModelClass);

        if (renderer.isPresent()) {
            @SuppressWarnings("unchecked")
            final M model = (M) componentModel;
            return Optional
                .of(renderer.get().renderComponent(model, parameters));
        } else {
            return Optional.empty();
        }
    }

}
