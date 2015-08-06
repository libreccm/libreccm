/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.StringParameter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;

/**
 * Stores the configuration record for the XML functionality.
 *
 * Most important: Configuration of the XML factories: - Document Builder - Sax Parser - XSL
 * Transformer
 *
 * @version $Id: XMLConfig.java 1393 2006-11-28 09:12:32Z sskracic $
 */
public final class XMLConfig extends AbstractConfig {

    /**
     * Internal logger instance to faciliate debugging. Enable logging output by editing
     * /WEB-INF/conf/log4j.properties int hte runtime environment and set
     * com.arsdigita.xml.XMLConfig=DEBUG by uncommenting or adding the line.
     */
    private static final Logger s_log = Logger.getLogger(XMLConfig.class);

    /**
     * Private instance of this class to be returned after initialization.
     */
    private static XMLConfig s_config;

    /**
     * Returns the singleton configuration record for the XML functionality
     *
     * @return The <code>XMLConfig</code> record; it cannot be null
     */
    public static final synchronized XMLConfig getConfig() {
        if (s_config == null) {
            s_config = new XMLConfig();
            // read values from the persistent storage
            s_config.load();
        }

        return s_config;
    }

    // supported XSL transformer implementations
    private static final String RESIN = "com.caucho.xsl.Xsl";
    private static final String SAXON = "com.icl.saxon.TransformerFactoryImpl";
    private static final String SAXON_HE = "net.sf.saxon.TransformerFactoryImpl";
    private static final String XALAN = "org.apache.xalan.processor.TransformerFactoryImpl";
    private static final String XSLTC = "org.apache.xalan.xsltc.trax.TransformerFactoryImpl";

    // supported documentBuilder implementations
    private static final String DOM_XERCES = "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl";
    private static final String DOM_RESIN = "com.caucho.xml.parsers.XmlDocumentBuilderFactory";

    // supported SAX parser implementations
    private static final String SAX_XERCES = "org.apache.xerces.jaxp.SAXParserFactoryImpl";
    private static final String SAX_RESIN = "com.caucho.xml.parsers.XmlSAXParserFactory";

    private final Parameter m_xfmr = new StringParameter(
        "waf.xml.xsl_transformer",
        Parameter.REQUIRED, "saxon");
    private final Parameter m_builder = new StringParameter(
        "waf.xml.dom_builder",
        Parameter.REQUIRED, "xerces");
    private final Parameter m_parser = new StringParameter(
        "waf.xml.sax_parser",
        Parameter.REQUIRED, "xerces");

    private final Parameter m_activateFullTimeFormatter = new BooleanParameter(
        "waf.xml.activate_full_date_formatter",
        Parameter.OPTIONAL, false);

    /**
     * Constructs an empty XMLConfig object following the singleton pattern.
     *
     * They are meant as a singleton pattern (with private constructor), but it does not work with
     * the associated classes AbstractConfig and ConfigRegistry because they can currently not deal
     * with a private constructor
     */
    // private XMLConfig() {
    public XMLConfig() {

        super();

        register(m_xfmr);
        register(m_builder);
        register(m_parser);
        register(m_activateFullTimeFormatter);

        loadInfo();
    }

    /* ************     public getter / setter section          ************ */
    /**
     * Returns the XSL Transformer factory class name to use.
     *
     * The method assures that the return value is a valid class name.
     *
     * @return String XSL Transformer factory class name
     */
    public String getXSLTransformerFactoryClassname() {

        final String key = (String) get(m_xfmr);

        // Defined values: saxon (default)|jd.xslt|resin|xalan|xsltc
        if (key.equalsIgnoreCase("xsltc")) {
            return XSLTC;
        } else if (key.equalsIgnoreCase("xalan")) {
            return XALAN;
        } else if (key.equalsIgnoreCase("resin")) {
            return RESIN;
        } else if (key.equalsIgnoreCase("saxonhe")) {
            return SAXON_HE;
        } else {
            // return defaultValue
            return getDefaultXSLTransformerFactoryClassname();
        }
    }
    
    /**
     * Returns the class name of the default {@link TransformerFactory}. This method encapsulates 
     * the default value so that is easy to change. The method is only for use by the 
     * classes in the {@code com.arsdigita.xml} package, therefore the method is {@code protected}.
     * 
     * @return 
     */
    protected String getDefaultXSLTransformerFactoryClassname() {
        return SAXON;
    }

    /**
     * Returns the Document Builder factory class name to use
     *
     * The method assures that the return value is a valid class name.
     * 
     * Not used at the moment.
     *
     * @return String Document Builder factory class name
     */
    public String getDOMBuilderFactoryClassname() {

        final String key = (String) get(m_builder);

        // Defined values: xerces (default)|resin
        if (key.equalsIgnoreCase("resin")) {
            return DOM_RESIN;
        } else {
            return getDefaultDOMBuilderFactoryClassname();
        }
    }
    
    /**
     * Returns the class name of the default {@link DocumentBuilderFactory}. 
     * This method encapsulates the default value so that is easy to change. The method is only for 
     * use by the classes in the {@code com.arsdigita.xml} package, therefore the method is 
     * {@code protected}.
     * 
     * @return 
     */
    protected String getDefaultDOMBuilderFactoryClassname() {
        return DOM_XERCES;
    }

    /**
     * Returns the Sax Parser factory class name to use.
     *
     * The method assures that the return value is a valid class name.
     * 
     * Not used at the moment.
     *
     * @return String Sax Parser factory class name
     */
    public String getSAXParserFactoryClassname() {

        final String key = (String) get(m_parser);

        // Defined values: xerces (default)|resin
        if (key.equalsIgnoreCase("resin")) {
            return SAX_RESIN;
        } else {
            return getDefaultSAXParserFactoryClassname();
        }
    }

    /**
     * Returns the class name of the default {@link SAXParserFactory}. 
     * This method encapsulates the default value so that is easy to change. The method is only for 
     * use by the classes in the {@code com.arsdigita.xml} package, therefore the method is 
     * {@code protected}.
     * 
     * @return 
     */
    protected String getDefaultSAXParserFactoryClassname() {
        return SAX_XERCES;
    }

    /**
     * Returns the activateFullTimeFormatter flag.
     *
     * @return
     */
    public boolean getActivateFullTimeFormatter() {
        return (Boolean) get(m_activateFullTimeFormatter);
    }

    /**
     * Sets the activateFullTimeFormatter flag.
     *
     * @param activateFullTimeFormatter
     */
    public void setActivateFullTimeFormatter(final boolean activateFullTimeFormatter) {
        set(m_activateFullTimeFormatter, activateFullTimeFormatter);
    }
}
