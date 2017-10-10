/*
 * Copyright (C) 2017 LibreCCM Foundation.
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

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import org.librecms.contentsection.ContentItem;
import org.librecms.contenttypes.AuthoringKitInfo;
import org.librecms.contenttypes.AuthoringStepInfo;
import org.librecms.contenttypes.ContentTypeInfo;
import org.librecms.contenttypes.ContentTypesManager;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.ResourceBundle;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ContentItemEditor extends Window {

    private static final long serialVersionUID = 3341827053652019616L;

    private List<Component> authoringsSteps;

    public ContentItemEditor(final ContentSectionViewController controller,
                             final ContentItem item) {

        super();

        final ContentTypesManager typesManager = controller
            .getContentTypesManager();
        final ContentTypeInfo typeInfo = typesManager
            .getContentTypeInfo(item.getContentType());

        final AuthoringKitInfo authoringKitInfo = typeInfo.getAuthoringKit();
        final List<AuthoringStepInfo> authoringsStepInfos = authoringKitInfo
            .getAuthoringSteps();

        final VerticalLayout sidebar = new VerticalLayout();
        final VerticalLayout mainArea = new VerticalLayout();
        for (final AuthoringStepInfo stepInfo : authoringsStepInfos) {

            final String componentClassName;
            if (stepInfo.getComponent().getName()
                .startsWith("com.arsdigita.cms")) {
                componentClassName = stepInfo
                    .getComponent()
                    .getName()
                    .replace("com.arsdigita.cms", "org.librecms");
            } else if (stepInfo.getComponent().getName().startsWith(
                "com.arsdigita")) {
                componentClassName = stepInfo
                    .getComponent()
                    .getName()
                    .replace("com.arsdigita", "org.libreccm");
            } else {
                componentClassName = stepInfo.getComponent().getName();
            }

            final Component authoringStep = createAuthoringStep(
                controller, item, componentClassName);
            final ResourceBundle resourceBundle = ResourceBundle
                .getBundle(stepInfo.getLabelBundle(),
                           controller
                               .getGlobalizationHelper()
                               .getNegotiatedLocale());
            final Button button = new Button(resourceBundle
                .getString(stepInfo.getLabelKey()));
            button.addStyleName(ValoTheme.BUTTON_LINK);
            button.addClickListener(event-> authoringStep.setVisible(true));
            authoringStep.setVisible(false);
            sidebar.addComponent(button);
            mainArea.addComponent(authoringStep);
            authoringsSteps.add(authoringStep);
        }
        
        authoringsSteps.get(0).setVisible(true);
    }

    private Component createAuthoringStep(
        final ContentSectionViewController controller,
        final ContentItem item,
        final String componentClassName) {

        try {
            @SuppressWarnings("unchecked")
            final Class<Component> stepClass = (Class<Component>) Class
                .forName(componentClassName);

            return stepClass
                .getDeclaredConstructor(ContentSectionViewController.class,
                                        ContentItem.class)
                .newInstance(controller, item);
        } catch (ClassNotFoundException
                 | NoSuchMethodException
                 | InstantiationException
                 | IllegalAccessException
                 | InvocationTargetException ex) {

            final Label label = new Label(String
                .format("AuthoringStep \"%s\" not available",
                        componentClassName));
            label.addStyleName(ValoTheme.LABEL_FAILURE);
            return new VerticalLayout(label);
        }

    }

}
