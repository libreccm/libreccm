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
import com.vaadin.data.HasValue;
import com.vaadin.data.ValueProvider;
import com.vaadin.server.UserError;
import com.vaadin.ui.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Id;
import javax.persistence.PersistenceException;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class JpqlConsole extends CustomComponent {

    private static final long serialVersionUID = 2585630538827827614L;
    private static final Logger LOGGER = LogManager.getLogger(JpqlConsole.class);

    private final AdminView view;

    private final TextArea queryArea;
    private final TextField maxResults;
    private final TextField offset;
    private final Button executeQueryButton;
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
        executeQueryButton = new Button(bundle
            .getString("ui.admin.jpqlconsole.query.execute"));
        executeQueryButton.addClickListener(event -> executeQuery());
        final Button clearQueryButton = new Button(bundle
            .getString("ui.admin.jpqlconsole.query.clear"));
        clearQueryButton.addClickListener(event -> queryArea.clear());
        final HorizontalLayout queryButtonsLayout = new HorizontalLayout(
            clearQueryButton,
            executeQueryButton);
        maxResults = new TextField("Max results", "10");
        maxResults.addValueChangeListener(new NumberValidator());
        offset = new TextField("Offset", "0");
        offset.addValueChangeListener(new NumberValidator());
        final HorizontalLayout maxResultsLayout = new HorizontalLayout(
            maxResults, offset);

        final VerticalLayout queryLayout = new VerticalLayout(queryArea,
                                                              maxResultsLayout,
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
        super.setCompositionRoot(new VerticalLayout(queryLayout, resultsPanel));
    }

    @SuppressWarnings("unchecked")
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

        final List<?> result;
        try {
            result = view
                .getJpqlConsoleController()
                .executeQuery(queryStr,
                              Integer.parseInt(maxResults.getValue()),
                              Integer.parseInt(offset.getValue()));
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

        final Set<EntityPropertyDescriptor> entityProperties = new HashSet<>();
        try {
            for (final Class<?> clazz : classes) {
                final BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
                final PropertyDescriptor[] props = beanInfo
                    .getPropertyDescriptors();

                for (final PropertyDescriptor prop : props) {
                    entityProperties.add(createEntityPropertyDescriptor(clazz,
                                                                        prop));
                }

            }

            for (final Class<?> clazz : classes) {
                final BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
                final List<EntityPropertyDescriptor> props = Arrays
                    .stream(beanInfo.getPropertyDescriptors())
                    .map(prop -> createEntityPropertyDescriptor(clazz,
                                                                prop))
                    .collect(Collectors.toList());

                entityProperties.retainAll(props);
            }
        } catch (IntrospectionException ex) {
            Notification.show(
                "Error displaying result. Failed to introspect classes.",
                Notification.Type.ERROR_MESSAGE);
            return;
        }

        final List<EntityPropertyDescriptor> propertiesList = entityProperties
            .stream()
            .filter(prop -> {
                return !Collection.class
                    .isAssignableFrom(prop
                        .getPropertyDescriptor()
                        .getPropertyType());
            })
            .collect(Collectors.toList());
        Collections.sort(propertiesList);

//        final List<String> propertyNames = propertiesList
//            .stream()
//            .map(prop -> prop.getPropertyDescriptor().getName())
//            .collect(Collectors.toList());
        final Label count = new Label(String.format("Found %d results",
                                                    result.size()));
//        final Label propertiesLabel = new Label(String.join(", ",
//                                                            propertyNames));

        final Grid<Object> resultsGrid = new Grid<>(Object.class);
        resultsGrid.setWidth("100%");
        for (final EntityPropertyDescriptor property : propertiesList) {
            resultsGrid.addColumn(new ValueProvider<Object, Object>() {

                private static final long serialVersionUID
                                              = 8400673589843188514L;

                @Override
                public Object apply(final Object source) {
                    final Method readMethod = property
                        .getPropertyDescriptor()
                        .getReadMethod();
                    try {
                        return readMethod.invoke(source);
                    } catch (IllegalAccessException
                             | IllegalArgumentException
                             | InvocationTargetException ex) {
                        Notification.show("Failed to display some properties.",
                                          Notification.Type.WARNING_MESSAGE);
                        LOGGER.error("Failed to display property '{}'.",
                                     property.getPropertyDescriptor().getName());
                        LOGGER.error(ex);
                        return ex.getMessage();
                    }
                }

            })
                .setCaption(property.getPropertyDescriptor().getName());
        }
        resultsGrid.setItems((Collection<Object>) result);

//        final VerticalLayout data = new VerticalLayout(count, propertiesLabel);
        final VerticalLayout data = new VerticalLayout(count, resultsGrid);
        resultsPanel.setContent(data);

    }

    private boolean isIdProperty(final Class<?> clazz,
                                 final PropertyDescriptor property) {

        final String propertyName = property.getName();
        final Optional<Field> field = getField(clazz, propertyName);
        final Method readMethod = property.getReadMethod();

        return (field.isPresent() && field.get().isAnnotationPresent(Id.class)
                || (readMethod != null && readMethod.isAnnotationPresent(
                    Id.class)));
    }

    private Optional<Field> getField(final Class<?> clazz, final String name) {

        try {
            return Optional.of(clazz.getDeclaredField(name));
        } catch (NoSuchFieldException ex) {

            if (Object.class.equals(clazz.getSuperclass())) {
                return Optional.empty();
            } else {
                return getField(clazz.getSuperclass(), name);
            }
        }
    }

    private EntityPropertyDescriptor createEntityPropertyDescriptor(
        final Class<?> clazz,
        final PropertyDescriptor propertyDescriptor) {

        return new EntityPropertyDescriptor(
            propertyDescriptor,
            "class".equals(propertyDescriptor.getName()),
            isIdProperty(clazz, propertyDescriptor));

    }

    private class EntityPropertyDescriptor
        implements Comparable<EntityPropertyDescriptor> {

        private final PropertyDescriptor propertyDescriptor;
        private final boolean classProperty;
        private final boolean idProperty;

        public EntityPropertyDescriptor(
            final PropertyDescriptor propertyDescriptor,
            final boolean classProperty,
            final boolean idProperty) {

            this.propertyDescriptor = propertyDescriptor;
            this.classProperty = classProperty;
            this.idProperty = idProperty;
        }

        public PropertyDescriptor getPropertyDescriptor() {
            return propertyDescriptor;
        }

        public boolean isClassProperty() {
            return classProperty;
        }

        public boolean isIdProperty() {
            return idProperty;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 13 * hash + Objects.hashCode(propertyDescriptor);
            hash = 13 * hash + (classProperty ? 1 : 0);
            hash = 13 * hash + (idProperty ? 1 : 0);
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof EntityPropertyDescriptor)) {
                return false;
            }
            final EntityPropertyDescriptor other
                                               = (EntityPropertyDescriptor) obj;
            if (classProperty != other.isClassProperty()) {
                return false;
            }
            if (idProperty != other.isIdProperty()) {
                return false;
            }
            return Objects.equals(propertyDescriptor,
                                  other.getPropertyDescriptor());
        }

        @Override
        public int compareTo(final EntityPropertyDescriptor other) {

            if (isIdProperty() && other.isIdProperty()) {
                return propertyDescriptor
                    .getName()
                    .compareTo(other.getPropertyDescriptor().getName());
            } else if (isIdProperty() && other.isClassProperty()) {
                return -1;
            } else if (isClassProperty() && other.isIdProperty()) {
                return 1;
            } else if (isIdProperty()) {
                return -1;
            } else if (other.isIdProperty()) {
                return 1;
            } else if (isClassProperty()) {
                return -1;
            } else if (other.isClassProperty()) {
                return 1;
            } else {
                return propertyDescriptor
                    .getName()
                    .compareTo(other.getPropertyDescriptor().getName());
            }
        }

        @Override
        public String toString() {
            return String.format("%s{ "
                                     + "name = '%s'; "
                                     + "readMethod = '%s'; "
                                     + "writeMethod = '%s'; "
                                     + "type = '%s'; "
                                     + "isIdProperty = '%b'; "
                                     + "isClassProperty = '%b';"
                                     + " }",
                                 super.toString(),
                                 propertyDescriptor.getName(),
                                 propertyDescriptor.getReadMethod().getName(),
                                 propertyDescriptor.getWriteMethod().getName(),
                                 propertyDescriptor.getPropertyType().getName(),
                                 idProperty,
                                 classProperty);
        }

    }

    private class NumberValidator
        implements HasValue.ValueChangeListener<String> {

        private static final long serialVersionUID = -3604431972616625411L;

        @Override
        public void valueChange(
            final HasValue.ValueChangeEvent<String> event) {
            final String value = event.getValue();
            try {
                Integer.parseUnsignedInt(value);
            } catch (NumberFormatException ex) {
                executeQueryButton.setEnabled(false);
                ((AbstractComponent) event.getComponent()).setComponentError(
                    new UserError(String.format(
                        "%s is not a unsigned integer/long value.",
                        event.getComponent().getCaption())));
                return;
            }

            ((AbstractComponent) event.getComponent()).setComponentError(null);
            executeQueryButton.setEnabled(true);
        }

    }

}
