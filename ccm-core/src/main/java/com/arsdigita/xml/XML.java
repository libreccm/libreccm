/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
 */
package com.arsdigita.xml;

import com.arsdigita.util.UncheckedWrapperException;

import com.arsdigita.xml.formatters.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


import org.apache.logging.log4j.Logger;

/**
 * Provides a set of static helper methods for dealing with XML,
 * including file parsing &amp; object -> string serialization
 */
public class XML {

    private static final Logger LOGGER = LogManager.getLogger(XML.class);

    // private static XMLConfig s_config;

    private static final Map s_formatters = new HashMap();
    static {
        LOGGER.debug("Static initalizer starting...");
        s_formatters.put(Date.class, new DateTimeFormatter());
        LOGGER.debug("Static initalizer finished.");
    }

    /**
     * Constructor. All methods are static, no initialization required.
     */
    private XML() {}

    /**
     * Retrieves the current configuration
    public static XMLConfig getConfig() {
        if (s_config == null) {
            s_config = new XMLConfig();
            s_config.load();
        }
        return s_config;
    }
     */
    
    /**
     * Registers a formatter for serializing objects of a
     * class to a String suitable for XML output.
     * @param klass
     * @param formatter
     */
    public static void registerFormatter(Class klass,
                                         Formatter formatter) {
        s_formatters.put(klass, formatter);
    }

    /**
     * Unregisters a formatter against a class.
     * @param klass
     */
    public static void unregisterFormatter(Class klass) {
        s_formatters.remove(klass);
    }

    /**
     * Gets a directly registered formatter for a class.
     * @param klass the class to find a formatter for
     * @return the formatter, or null if non is registered
     */
    public static Formatter getFormatter(Class klass) {
        return (Formatter)s_formatters.get(klass);
    }

    /**
     * Looks for the best matching formatter.
     * 
     * @param klass the class to find a formatter for
     * @return the formatter, or null if non is registered
     */
    public static Formatter findFormatter(Class klass) {
        Formatter formatter = null;
        while (formatter == null && klass != null) {
            formatter = getFormatter(klass);
            klass = klass.getSuperclass();
        }
        return formatter;
    }

    /**
     * Converts an object to a String using the closest
     * matching registered Formatter implementation. Looks
     * for a formatter registered against the object's
     * class first, then its superclass, etc. If no formatter
     * is found, uses the toString() method.
     * 
     * @param value
     * @return 
     */
    public static String format(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            return (String)value;
        }

        Formatter formatter = findFormatter(value.getClass());
        if (formatter == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No formatter for " + value.getClass());
            }
            return value.toString();
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Processing " + value.getClass() +
                        " with " + formatter.getClass());
        }
        return formatter.format(value);
    }

    /**
     * Processes an XML file with the default SAX Parser, with
     * namespace processing, schema validation & DTD validation
     * enabled.
     *
     * @param path the XML file relative to the webapp root
     * @param handler the content handler
     */
    public static final void parseResource(String path,
                                           DefaultHandler handler) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Processing resource " + path +
                        " with " + handler.getClass());
        }

        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        ClassLoader cload = Thread.currentThread().getContextClassLoader();
        InputStream stream = cload.getResourceAsStream(path);

        if (stream == null) {
            throw new IllegalArgumentException("no such resource: " + path);
        }

        parse(stream, handler);
    }

    /**
     * Processes an XML file with the default SAX Parser, with
     * namespace processing, schema validation & DTD validation
     * enabled.
     *
     * @param source the xml input stream
     * @param handler the content handler
     */
    public static final void parse(InputStream source,
                                   DefaultHandler handler) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Processing stream " + source +
                        " with " + handler.getClass());
        }

        try {
            // ToDo (pboy): We should use
            // SAXParserFactory.newInstance(String clName, ClassLoader clLoader)
            // instead to achieve independence of a JVM wide acceptable
            // configuration (affecting all CCM instances which may run in a
            // container).
            // Requires additional modifications in c.ad.util.xml.XML
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setFeature("http://xml.org/sax/features/namespaces", true);
            SAXParser parser = spf.newSAXParser();
            parser.parse(source, handler);
        } catch (ParserConfigurationException e) {
            throw new UncheckedWrapperException("error parsing stream", e);
        } catch (SAXException e) {
            if (e.getException() != null) {
                throw new UncheckedWrapperException("error parsing stream",
                                                    e.getException());
            } else {
                throw new UncheckedWrapperException("error parsing stream", e);
            }
        } catch (IOException e) {
            throw new UncheckedWrapperException("error parsing stream", e);
        }
    }

    /**
     * This visitor is called by {@link #traverse(Element, int, XML.Action)}.
     **/
    public interface Action {
        void apply(Element elem, int level);
    }

    /**
     * Prints the skeleton structure of the element to the supplied print
     * writer.
     * @param element
     * @param writer
     **/
    public static void toSkeleton(final Element element,
                                  final PrintWriter writer) {

        XML.traverse(element, 0, new Action() {
                @Override
                public void apply(Element elem, int level) {
                    final String padding = "  ";
                    for (int ii=0; ii<level; ii++) {
                        writer.print(padding);
                    }
                    writer.print(elem.getName());
                    Iterator attrs = elem.getAttributes().keySet().iterator();
                    while (attrs.hasNext()) {
                        writer.print(" @");
                        writer.print((String) attrs.next());
                    }
                    writer.println("");
                }
            });
    }

    /**
     * This is a wrapper for {@link #toSkeleton(Element, PrintWriter)}.
     * 
     * @param element
     * @return 
     **/
    public static String toSkeleton(Element element) {
        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        XML.toSkeleton(element, pw);
        pw.close();
        return writer.toString();
    }

    /**
     * Pre-order, depth-first traversal.
     * 
     * @param elem
     * @param level
     * @param action
     **/
    public static void traverse(Element elem, int level, Action action) {
        action.apply(elem, level);
        final Iterator children=elem.getChildren().iterator();
        while (children.hasNext()) {
            XML.traverse((Element) children.next(), level+1, action);
        }
    }
}
