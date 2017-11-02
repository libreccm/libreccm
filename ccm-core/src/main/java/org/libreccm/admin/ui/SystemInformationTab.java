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
import com.arsdigita.util.SystemInformation;

import com.vaadin.data.provider.AbstractDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import org.libreccm.l10n.LocalizedTextsUtil;
import org.xml.sax.SAXException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class SystemInformationTab extends CustomComponent {

    private static final long serialVersionUID = 2060924579790730222L;

    private static final String COL_PROPERTY_NAME = "col_name";
    private static final String COL_PROPERTY_VALUE = "col_value";

    protected SystemInformationTab(final AdminViewController controller) {

        super();
        
        final LocalizedTextsUtil adminTextsUtil = controller
            .getGlobalizationHelper()
            .getLocalizedTextsUtil(AdminUiConstants.ADMIN_BUNDLE);

        final Grid<Map.Entry<String, String>> ccmSysInfoGrid = new Grid<>();
        ccmSysInfoGrid
            .setCaption(adminTextsUtil.getText("ui.admin.sysinfo.appinfo"));
        ccmSysInfoGrid
            .addColumn(Map.Entry::getKey)
            .setCaption("Property")
            .setId(COL_PROPERTY_NAME);
        ccmSysInfoGrid
            .addColumn(Map.Entry::getValue)
            .setCaption("Value")
            .setId(COL_PROPERTY_VALUE);
        ccmSysInfoGrid.setDataProvider(new CcmSysInfoDataProvider());
        ccmSysInfoGrid.setWidth("30em");

        final Grid<Map.Entry<Object, Object>> systemPropsGrid = new Grid<>();
        systemPropsGrid.setCaption(adminTextsUtil
            .getText("ui.admin.sysinfo.java_system_properties"));
        systemPropsGrid
            .addColumn(entry -> Objects.toString(entry.getKey()))
            .setCaption("Name")
            .setId(COL_PROPERTY_NAME);
        systemPropsGrid
            .addColumn(entry -> Objects.toString(entry.getValue()))
            .setCaption("Vaue")
            .setId(COL_PROPERTY_VALUE);
        systemPropsGrid.setDataProvider(new JavaSystemPropertiesDataProvider());
        systemPropsGrid.setWidth("30em");

        final Grid<Map.Entry<String, String>> xmlConfigGrid = new Grid<>();
        xmlConfigGrid
            .setCaption(adminTextsUtil.getText("ui.admin.sysinfo.xml_config"));
        xmlConfigGrid
            .addColumn(Map.Entry::getKey)
            .setCaption("Property")
            .setId(COL_PROPERTY_NAME);
        xmlConfigGrid
            .addColumn(Map.Entry::getValue)
            .setCaption("Value")
            .setId(COL_PROPERTY_VALUE);
        xmlConfigGrid.setDataProvider(new XmlConfigDataProvider());
        xmlConfigGrid.setWidth("30em");

        final VerticalLayout layout = new VerticalLayout(ccmSysInfoGrid,
                                                         systemPropsGrid,
                                                         xmlConfigGrid);

        super.setCompositionRoot(layout);
    }

    private class CcmSysInfoDataProvider
        extends AbstractDataProvider<Map.Entry<String, String>, String> {

        private static final long serialVersionUID = 958711041782982594L;

        private final SystemInformation sysInfo = SystemInformation
            .getInstance();

        @Override
        public boolean isInMemory() {
            return true;
        }

        @Override
        public int size(final Query<Map.Entry<String, String>, String> query) {
            return sysInfo.size();
        }

        @Override
        public Stream<Map.Entry<String, String>> fetch(
            final Query<Map.Entry<String, String>, String> query) {

            return sysInfo.getEntriesAsStream();
        }

    }

    private class JavaSystemPropertiesDataProvider
        extends AbstractDataProvider<Map.Entry<Object, Object>, String> {

        private static final long serialVersionUID = -6971113377859500433L;

        private final Properties systemProperties = System.getProperties();

        @Override
        public boolean isInMemory() {
            return true;
        }

        @Override
        public int size(final Query<Map.Entry<Object, Object>, String> query) {

            return systemProperties.entrySet().size();
        }

        @Override
        public Stream<Map.Entry<Object, Object>> fetch(
            final Query<Map.Entry<Object, Object>, String> query) {

            return systemProperties.entrySet().stream();
        }

    }

    private class XmlConfigDataProvider
        extends AbstractDataProvider<Map.Entry<String, String>, String> {

        private static final long serialVersionUID = 3724329522046019159L;

        @Override
        public boolean isInMemory() {
            return true;
        }

        @Override
        public int size(final Query<Map.Entry<String, String>, String> query) {
            return 6;
        }

        @Override
        public Stream<Map.Entry<String, String>> fetch(
            final Query<Map.Entry<String, String>, String> query) {

            final Map<String, String> properties = new HashMap<>();
            properties.put("XML Transformer Factory",
                           getTransformerFactory());
            properties.put("XML Transformer", getTransformer());
            properties.put("XML Document Builder Factory",
                           getDocumentBuilderFactory());
            properties.put("XML Document Builder", getDocumentBuilder());
            properties.put("SAX Parser Factory", getSaxParserFactory());
            properties.put("SAX Parser", getSaxParser());

            return properties.entrySet().stream();
        }

        private String getTransformerFactory() {
            return TransformerFactory
                .newInstance()
                .getClass()
                .getName();
        }

        private String getTransformer() {
            try {
                return TransformerFactory
                    .newInstance()
                    .newTransformer()
                    .getClass()
                    .getName();
            } catch (TransformerConfigurationException ex) {

                return "???";
            }
        }

        private String getDocumentBuilderFactory() {
            return DocumentBuilderFactory
                .newInstance()
                .getClass()
                .getName();
        }

        private String getDocumentBuilder() {
            try {
                return DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .getClass()
                    .getName();
            } catch (ParserConfigurationException ex) {

                return "???";
            }
        }

        private String getSaxParserFactory() {
            return SAXParserFactory
                .newInstance()
                .getClass()
                .getName();
        }

        private String getSaxParser() {
            try {
                return SAXParserFactory
                    .newInstance()
                    .newSAXParser()
                    .getClass()
                    .getName();
            } catch (ParserConfigurationException
                     | SAXException ex) {

                return "???";
            }
        }

    }

}
