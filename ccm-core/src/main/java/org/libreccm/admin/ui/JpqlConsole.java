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
package org.libreccm.admin.ui;

import com.arsdigita.ui.admin.AdminUiConstants;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class JpqlConsole extends CustomComponent {

    private static final long serialVersionUID = 2585630538827827614L;

    private AdminView view;

    private final TextArea queryArea;
//    private final FormLayout queryForm;
//    private final VerticalLayout resultsLayout;
    private final Label noResultsLabel;
    private final Panel resultsPanel;

    public JpqlConsole(final AdminView view) {

        this.view = view;

        final ResourceBundle bundle = ResourceBundle.getBundle(
            AdminUiConstants.ADMIN_BUNDLE, UI.getCurrent().getLocale());

        queryArea = new TextArea(bundle.getString("ui.admin.jpqlconsole.query"));
        queryArea.setWidth("100%");
        final Button executeQueryButton = new Button(bundle
            .getString("ui.admin.jpqlconsole.query.execute"));
        executeQueryButton.addClickListener(event -> executeQuery());
        final Button clearQueryButton = new Button(bundle
            .getString("ui.admin.jpqlconsole.query.clear"));
        clearQueryButton.addClickListener(event -> queryArea.clear());
//        queryForm = new FormLayout(queryArea
//                                   executeQueryButton,
//                                   clearQueryButton);

        final HorizontalLayout queryButtonsLayout = new HorizontalLayout(
            clearQueryButton,
            executeQueryButton);

        final VerticalLayout queryLayout = new VerticalLayout(queryArea,
                                                              queryButtonsLayout);

        noResultsLabel = new Label(bundle
            .getString("ui.admin.jpqlconsole.results.none"));
        resultsPanel = new Panel("Query results",
                                 noResultsLabel);

//        resultsLayout = new VerticalLayout(noResultsLabel);
//        final VerticalSplitPanel splitPanel = new VerticalSplitPanel();
////        splitPanel.setSizeFull();
//       splitPanel.setHeight("100%");
//        splitPanel.setSplitPosition(33.3f, Unit.PERCENTAGE);
//        splitPanel.setFirstComponent(queryForm);
//        splitPanel.setSecondComponent(resultsLayout);
        setCompositionRoot(new VerticalLayout(queryLayout, resultsPanel));
    }

    private void executeQuery() {
        final String queryStr = queryArea.getValue();

        if (queryStr == null || queryStr.trim().isEmpty()) {
            return;
        }

        if (!queryStr.toLowerCase().startsWith("select")) {
            Notification.show("Only SELECT queries",
                              "Only SELECT queries are supported",
                              Notification.Type.WARNING_MESSAGE);
            return;
        }

//        final Query query;
//        try {
//            query = entityManager.createQuery(queryStr);
//        } catch (IllegalArgumentException ex) {
//            Notification.show("Query is malformed.",
//                              ex.getMessage(),
//                              Notification.Type.ERROR_MESSAGE);
//            return;
//        }
        final List<?> result;
        try {
            result = view.getJpqlConsoleController().executeQuery(queryStr);
        } catch (IllegalArgumentException ex) {
            Notification.show("Query is malformed.",
                              ex.getMessage(),
                              Notification.Type.ERROR_MESSAGE);
            return;
        } catch (PersistenceException ex) {
            Notification.show("Failed to execute query",
                              ex.getMessage(),
                              Notification.Type.ERROR_MESSAGE);
            return;
        }

        Set<Class<?>> classes = result
            .stream()
            .map(Object::getClass)
            .collect(Collectors.toSet());

        final Set<PropertyDescriptor> properties = new HashSet<>();
        try {
            for (final Class<?> clazz : classes) {
                final BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
                final PropertyDescriptor[] props = beanInfo
                    .getPropertyDescriptors();
                properties.addAll(Arrays.asList(props));
            }

            for (final Class<?> clazz : classes) {
                final BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
                final PropertyDescriptor[] props = beanInfo
                    .getPropertyDescriptors();
                properties.retainAll(Arrays.asList(props));
            }
        } catch (IntrospectionException ex) {
            Notification.show(
                "Error displaying result. Failed to introspect classes.",
                Notification.Type.ERROR_MESSAGE);
            return;
        }

        final List<PropertyDescriptor> propertiesList = properties
            .stream()
            .filter(prop -> {
                return !Collection.class
                    .isAssignableFrom(prop.getPropertyType());
            })
            .collect(Collectors.toList());
        propertiesList.sort((prop1, prop2) -> {
            return prop1.getName().compareTo(prop2.getName());
        });
        final List<String> propertyNames = propertiesList
            .stream()
            .map(PropertyDescriptor::getName)
            .collect(Collectors.toList());

        final Label count = new Label(String.format("Found %d results",
                                                    result.size()));
        final Label propertiesLabel = new Label(String.join(", ",
                                                            propertyNames));

        final VerticalLayout data = new VerticalLayout(count, propertiesLabel);
        resultsPanel.setContent(data);

//        while(classes.size() > 1) {
//            final Set<Class<?>> oldClasses = classes;
//            classes = oldClasses
//                .stream()
//                .map(clazz -> getSuperClass(clazz))
//                .collect(Collectors.toSet());
//        }
//
//        final Class<?> baseClass = classes.iterator().next();
//        
//        final Label count = new Label(String.format("Found %d results",
//                                                    result.size()));
//        final Label baseClassLabel;
//        if (baseClass == null) {
//            baseClassLabel = new Label("Base class is null");
//        } else {
//            baseClassLabel = new Label(String.format("Base class is '%s'.",
//                                                     baseClass.getName()));
//        }
//        final VerticalLayout data = new VerticalLayout(count, baseClassLabel);
//        resultsPanel.setContent(data);
    }

    private Class<?> getSuperClass(final Class<?> clazz) {
        if (Object.class.equals(clazz.getSuperclass())) {
            return clazz;
        } else {
            return clazz.getSuperclass();
        }
    }

}
