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
package com.arsdigita.ui.admin;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.xml.Element;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class JpqlConsole extends LayoutPanel {

    private static final Logger LOGGER = LogManager.getLogger(JpqlConsole.class);

    private final Form queryForm;
    private final TextArea queryArea;

    public JpqlConsole() {
        super();

        final SegmentedPanel panel = new SegmentedPanel();

        queryForm = new Form("jpql-console-query-form");

        queryArea = new TextArea("jpql-console-query-area");
        queryArea.setCols(132);
        queryArea.setRows(42);

        final BoxPanel hpanel = new BoxPanel(BoxPanel.HORIZONTAL);
        hpanel.add(new Label(new GlobalizedMessage(
            "ui.admin.jpqlconsole.query.label",
            AdminUiConstants.ADMIN_BUNDLE)));
        hpanel.add(queryArea);

        final BoxPanel vpanel = new BoxPanel(BoxPanel.VERTICAL);
        vpanel.add(hpanel);
        final SaveCancelSection saveCancelSection = new SaveCancelSection();
        saveCancelSection.getSaveButton().setLabel(
            new GlobalizedMessage("ui.admin.jpqlconsole.query.execute",
                                  AdminUiConstants.ADMIN_BUNDLE));
        saveCancelSection.getCancelButton().setLabel(
            new GlobalizedMessage("ui.admin.jpqlconsole.query.clear",
                                  AdminUiConstants.ADMIN_BUNDLE));
        vpanel.add(saveCancelSection);

        queryForm.add(hpanel);

        panel.addSegment(new Label(new GlobalizedMessage(
            "ui.admin.jpqlconsole.query.segment.title",
            AdminUiConstants.ADMIN_BUNDLE)),
                         queryForm);

        setBody(panel);
    }

    private class QueryResults extends SimpleComponent {

        private List<?> results;

        public List<?> getResults() {
            return results;
        }

        public void setResults(final List<?> results) {
            this.results = results;
        }

        private List<PropertyDescriptor> getProperties(final List<?> results)
            throws IntrospectionException {

            final Set<PropertyDescriptor> propertyDescriptors = new HashSet<>();

            for (final Object obj : results) {
                final PropertyDescriptor[] properties = Introspector
                    .getBeanInfo(obj.getClass()).getPropertyDescriptors();

                for (final PropertyDescriptor property : properties) {
                    propertyDescriptors.add(property);
                }
            }

            final List<PropertyDescriptor> propertiesList
                                           = new ArrayList<>(propertyDescriptors);
            propertiesList.sort((prop1, prop2) -> {
                return prop1.getName().compareTo(prop2.getName());
            });

            return propertiesList;
        }

        @Override
        public void generateXML(final PageState state,
                                final Element parent) {

            final Element queryResults = parent
                .newChildElement("bebop:query-results", BEBOP_XML_NS);

            // Find all properties in the results
            final List<PropertyDescriptor> properties;
            try {
                properties = getProperties(results);
            } catch (IntrospectionException ex) {
                queryArea.addError(new GlobalizedMessage(
                    "ui.admin.jpqlconsole.failed_to_render_results",
                    AdminUiConstants.ADMIN_BUNDLE));
                LOGGER.error("Failed to render results.", ex);
                return;
            }

            final Element columns = queryResults
                .newChildElement("bebop:query-result-columns", BEBOP_XML_NS);
            for (final PropertyDescriptor property : properties) {
                final Element column = columns
                    .newChildElement("bebop:query-result-column", BEBOP_XML_NS);
                column.setText(property.getName());
            }

            final Element resultList = queryResults
                .newChildElement("bebop:query-result-list", BEBOP_XML_NS);
            for(final Object obj : results) {
                for(final PropertyDescriptor property : properties) {
                    final Method readMethod = property.getReadMethod();
                    
                    
                }
            }

        }

    }

}
