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
package org.libreccm.admin.ui.sysinfo;

import org.xml.sax.SAXException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named
public class SysInfoController {

    public List<SysInfoProperty> getSystemInformation() {
        final Properties properties = new Properties();

        try (final InputStream stream = getClass().getResourceAsStream(
            "systeminformation.properties")) {
            if (stream == null) {
                properties.put("version", "");
                properties.put("appname", "LibreCCM");
                properties.put("apphomepage", "http://www.libreccm.org");
            } else {
                properties.load(stream);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        final List<SysInfoProperty> sysInfo = new ArrayList<>();
        properties.stringPropertyNames().forEach(propName -> sysInfo.add(
            new SysInfoProperty(propName,
                                properties.getProperty(propName))));
        return sysInfo;
    }

    public List<SysInfoProperty> getJavaSystemProperties() {
        final Properties systemProperties = System.getProperties();
        final List<SysInfoProperty> javaSysProps = new ArrayList<>();
        systemProperties.stringPropertyNames().forEach(propName -> javaSysProps
            .add(new SysInfoProperty(propName, systemProperties.getProperty(
                                     propName))));
        return javaSysProps;
    }

    public List<SysInfoProperty> getXmlConfig() {
        final List<SysInfoProperty> xmlProps = new ArrayList<>();

        final ResourceBundle texts = ResourceBundle.getBundle(
            "com.arsdigita.ui.admin.AdminResources");
        xmlProps.add(new SysInfoProperty(
            texts.getString("ui.admin.sysinfo.xml_transformer_factory"),
            TransformerFactory
                .newInstance()
                .getClass()
                .getName()));
        try {
            xmlProps.add(new SysInfoProperty(
                texts.getString("ui.admin.sysinfo.xml_transformer"),
                TransformerFactory.newInstance()
                    .newTransformer()
                    .getClass()
                    .getName()));
        } catch (TransformerConfigurationException ex) {
            xmlProps.add(new SysInfoProperty(
                texts.getString("ui.admin.sysinfo.xml_transformer"), "???"));
            xmlProps.add(new SysInfoProperty(
                texts.getString("ui.admin.sysinfo.xml_document_builder_factory"),
                DocumentBuilderFactory.newInstance().getClass().getName()));
        }
        try {
            xmlProps.add(new SysInfoProperty(
                texts.getString("ui.admin.sysinfo.xml_document_builder"),
                DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .getClass()
                    .getName()));
        } catch (ParserConfigurationException ex) {
            xmlProps.add(new SysInfoProperty(
                texts.getString("ui.admin.sysinfo.xml_document_builder"),
                "???"));
        }
        xmlProps.add(new SysInfoProperty(
            texts.getString("ui.admin.sysinfo.sax_parser_factory"),
            SAXParserFactory
                .newInstance()
                .getClass()
                .getName()));
        try {
            xmlProps.add(new SysInfoProperty(
                texts.getString("ui.admin.sysinfo.sax_parser"),
                SAXParserFactory
                    .newInstance()
                    .newSAXParser()
                    .getClass()
                    .getName()));
        } catch (ParserConfigurationException | SAXException ex) {
            xmlProps.add(new SysInfoProperty(
                texts.getString("ui.admin.sysinfo.sax_parser"), "???"));
        }
        
        return xmlProps;
    }

}
