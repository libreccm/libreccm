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
 */package com.arsdigita.ui.admin;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.bebop.PropertySheetModel;
import com.arsdigita.bebop.PropertySheetModelBuilder;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.util.SystemInformation;

import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class SystemInformationTab extends LayoutPanel {

    public SystemInformationTab() {
        super();

        final SegmentedPanel panel = new SegmentedPanel();

        panel.addSegment(new Label(GlobalizationUtil.globalize(
            "ui.admin.sysinfo.appinfo")),
                         new PropertySheet(
                             new CCMSysInfoPropertySheetModelBuilder()));

        panel.addSegment(new Label(GlobalizationUtil.globalize(
            "ui.admin.sysinfo.java_system_properties")),
                         new PropertySheet(
                             new JavaSystemPropertiesSheetModelBuilder()));

        panel.addSegment(new Label(GlobalizationUtil.globalize(
            "ui.admin.sysinfo.xml_config")),
                         new PropertySheet(new XMLConfigSheetModelBuilder()));

        setRight(panel);
    }

    private class CCMSysInfoPropertySheetModelBuilder
        extends LockableImpl
        implements PropertySheetModelBuilder {

        public CCMSysInfoPropertySheetModelBuilder() {
            super();
        }

        @Override
        public PropertySheetModel makeModel(final PropertySheet sheet,
                                            final PageState state) {
            return new CCMSysInfoPropertySheetModel();
        }

    }

    private class CCMSysInfoPropertySheetModel implements PropertySheetModel {

        private final SystemInformation sysInfo;
        private final Iterator<Map.Entry<String, String>> sysInfoIterator;
        private Map.Entry<String, String> currentProperty;

        public CCMSysInfoPropertySheetModel() {
            sysInfo = SystemInformation.getInstance();
            sysInfoIterator = sysInfo.iterator();
        }

        @Override
        public boolean nextRow() {
            final boolean result = sysInfoIterator.hasNext();
            if (result) {
                currentProperty = sysInfoIterator.next();
            }
            return result;

        }

        @Override
        public String getLabel() {
            if (currentProperty == null) {
                return null;
            } else {
                return currentProperty.getKey();
            }
        }

        @Override
        public GlobalizedMessage getGlobalizedLabel() {
            return GlobalizationUtil.globalize(currentProperty.getKey());
        }

        @Override
        public String getValue() {
            return currentProperty.getValue();
        }

    }

    private class JavaSystemPropertiesSheetModelBuilder
        extends LockableImpl
        implements PropertySheetModelBuilder {

        public JavaSystemPropertiesSheetModelBuilder() {
            super();
        }

        @Override
        public PropertySheetModel makeModel(final PropertySheet sheet,
                                            final PageState state) {
            return new JavaSystemPropertiesSheetModel();
        }

    }

    private class JavaSystemPropertiesSheetModel implements PropertySheetModel {

        private final Properties systemProperties;
        private final Enumeration<?> enumeration;
        private Object currentElement;

        public JavaSystemPropertiesSheetModel() {
            systemProperties = System.getProperties();
            enumeration = systemProperties.propertyNames();
        }

        @Override
        public boolean nextRow() {
            final boolean result = enumeration.hasMoreElements();
            if (result) {
                currentElement = enumeration.nextElement();
            }
            return result;
        }

        @Override
        public String getLabel() {
            return currentElement.toString();
        }

        @Override
        public GlobalizedMessage getGlobalizedLabel() {
            return GlobalizationUtil.globalize(currentElement.toString());
        }

        @Override
        public String getValue() {
            return systemProperties.getProperty(currentElement.toString());
        }

    }

    private class XMLConfigSheetModelBuilder
        extends LockableImpl
        implements PropertySheetModelBuilder {

        public XMLConfigSheetModelBuilder() {
            super();
        }

        @Override
        public PropertySheetModel makeModel(final PropertySheet sheet,
                                            final PageState state) {
            return new XMLConfigSheetModel();
        }

    }

    private class XMLConfigSheetModel implements PropertySheetModel {

        private static final int TRANSFORMER_FACTORY_INDEX = 0;
        private static final int TRANSFORMER_INDEX = 1;
        private static final int DOCUMENT_BUILDER_FACTORY_INDEX = 2;
        private static final int DOCUMENT_BUILDER_INDEX = 3;
        private static final int SAX_PARSER_FACTORY_INDEX = 4;
        private static final int SAX_PARSER_INDEX = 5;
        private int currentIndex = -1;

        public XMLConfigSheetModel() {
            //Nothing
        }

        @Override
        public boolean nextRow() {
            currentIndex++;
            return currentIndex <= SAX_PARSER_INDEX;
        }

        @Override
        public String getLabel() {
            switch (currentIndex) {
                case TRANSFORMER_FACTORY_INDEX:
                    return "XML Transformer Factory";
                case TRANSFORMER_INDEX:
                    return "XML Transformer";
                case DOCUMENT_BUILDER_FACTORY_INDEX:
                    return "XML Document Builder Factory";
                case DOCUMENT_BUILDER_INDEX:
                    return "XML Document Builder";
                case SAX_PARSER_FACTORY_INDEX:
                    return "SAX Parser Factory";
                case SAX_PARSER_INDEX:
                    return "SAX Parser";
                default:
                    return "";
            }
        }

        @Override
        public GlobalizedMessage getGlobalizedLabel() {
            switch (currentIndex) {
                case TRANSFORMER_FACTORY_INDEX:
                    return GlobalizationUtil.globalize(
                        "ui.admin.sysinfo.xml_transformer_factory");
                case TRANSFORMER_INDEX:
                    return GlobalizationUtil.globalize(
                        "ui.admin.sysinfo.xml_transformer");
                case DOCUMENT_BUILDER_FACTORY_INDEX:
                    return GlobalizationUtil.globalize(
                        "ui.admin.sysinfo.xml_document_builder_factory");
                case DOCUMENT_BUILDER_INDEX:
                    return GlobalizationUtil.globalize(
                        "ui.admin.sysinfo.xml_document_builder");
                case SAX_PARSER_FACTORY_INDEX:
                    return GlobalizationUtil.globalize(
                        "ui.admin.sysinfo.sax_parser_factory");
                case SAX_PARSER_INDEX:
                    return GlobalizationUtil.globalize(
                        "ui.admin.sysinfo.sax_parser");
                default:
                    return GlobalizationUtil.globalize("unknown");
            }
        }

        @Override
        public String getValue() {
            switch (currentIndex) {
                case TRANSFORMER_FACTORY_INDEX:
                    return TransformerFactory
                        .newInstance()
                        .getClass()
                        .getName();
                case TRANSFORMER_INDEX:
                    try {
                        return TransformerFactory
                            .newInstance()
                            .newTransformer()
                            .getClass()
                            .getName();
                    } catch (TransformerConfigurationException ex) {
                        return "???";
                    }
                case DOCUMENT_BUILDER_FACTORY_INDEX:
                    return DocumentBuilderFactory.newInstance().getClass()
                        .getName();
                case DOCUMENT_BUILDER_INDEX:
                    try {
                        return DocumentBuilderFactory
                            .newInstance()
                            .newDocumentBuilder()
                            .getClass()
                            .getName();
                    } catch (ParserConfigurationException ex) {
                        return "???";
                    }
                case SAX_PARSER_FACTORY_INDEX:
                        return SAXParserFactory
                            .newInstance()
                            .getClass()
                            .getName();
                case SAX_PARSER_INDEX:
                    try {
                        return SAXParserFactory
                            .newInstance()
                            .newSAXParser()
                            .getClass()
                            .getName();
                    } catch (ParserConfigurationException | SAXException ex) {
                        return "???";
                    } 
                default:
                    return "";
            }
        }

    }

}
