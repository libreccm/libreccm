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
package com.arsdigita.xml;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.Configuration;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.configuration.Setting;

import java.util.Objects;

/**
 * Stores the configuration record for the XML functionality.
 *
 * Most important: Configuration of the XML factories: - Document Builder - Sax
 * Parser - XSL Transformer
 *
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Configuration
public final class XmlConfig {

    // supported XSL transformer implementations
    private static final String RESIN = "com.caucho.xsl.Xsl";
    private static final String SAXON = "com.icl.saxon.TransformerFactoryImpl";
    private static final String SAXON_HE = "net.sf.saxon.TransformerFactoryImpl";
    private static final String XALAN
                                    = "org.apache.xalan.processor.TransformerFactoryImpl";
    private static final String XSLTC
                                    = "org.apache.xalan.xsltc.trax.TransformerFactoryImpl";

    // supported documentBuilder implementations
    private static final String DOM_XERCES
                                    = "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl";
    private static final String DOM_RESIN
                                    = "com.caucho.xml.parsers.XmlDocumentBuilderFactory";

    // supported SAX saxParser implementations
    private static final String SAX_XERCES
                                    = "org.apache.xerces.jaxp.SAXParserFactoryImpl";
    private static final String SAX_RESIN
                                    = "com.caucho.xml.parsers.XmlSAXParserFactory";

    @Setting
    private String xslTransformer = "saxonhe";

    @Setting
    private String domBuilder = "xerces";

    @Setting
    private String saxParser = "xerces";

    @Setting
    @SuppressWarnings("PMD.LongVariable")
    private Boolean fullTimeFormatterEnabled = false;

    public static XmlConfig getConfig() {
        final CdiUtil cdiUtil = new CdiUtil();
        final ConfigurationManager confManager = cdiUtil.findBean(
            ConfigurationManager.class);
        return confManager.findConfiguration(XmlConfig.class);
    }

    public String getXslTransformer() {
        return xslTransformer;
    }

    public String getXslTransformerFactoryClassname() {
        switch (xslTransformer) {
            case "xsltc":
                return XSLTC;
            case "xalan":
                return XALAN;
            case "resin":
                return RESIN;
            case "saxonhe":
                return SAXON_HE;
            default:
                return getDefaultXslTransformerFactoryClassname();
        }
    }

    /**
     * Returns the class name of the default {@link TransformerFactory}. This
     * method encapsulates the default value so that is easy to change. The
     * method is only for use by the classes in the {@code com.arsdigita.xml}
     * package, therefore the method is visible for the package.
     *
     * @return
     */
    String getDefaultXslTransformerFactoryClassname() {
        return SAXON;
    }

    public void setXslTransformer(final String xslTransformer) {
        this.xslTransformer = xslTransformer;
    }

    public String getDomBuilder() {
        return domBuilder;
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
    public String getDomBuilderFactoryClassname() {

        if ("resin".equals(domBuilder)) {
            return DOM_RESIN;
        } else {
            return getDefaultDomBuilderFactoryClassname();
        }
    }
    
    /**
     * Returns the class name of the default {@link DocumentBuilderFactory}. 
     * This method encapsulates the default value so that is easy to change. The method is only for 
     * use by the classes in the {@code com.arsdigita.xml} package, therefore the method is 
     * only accessible from the package.
     * 
     * @return 
     */
    String getDefaultDomBuilderFactoryClassname() {
        return DOM_XERCES;
    }

    
    public void setDomBuilder(final String domBuilder) {
        this.domBuilder = domBuilder;
    }

    public String getSaxParser() {
        return saxParser;
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
    public String getSaxParserFactoryClassname() {

        if ("resin".equals(saxParser)) {
            return SAX_RESIN;
        } else {
            return getDefaultSaxParserFactoryClassname();
        }
    }

    /**
     * Returns the class name of the default {@link SAXParserFactory}. 
     * This method encapsulates the default value so that is easy to change. The method is only for 
     * use by the classes in the {@code com.arsdigita.xml} package, therefore the method is 
     * only visible in the package.
     * 
     * @return 
     */
    String getDefaultSaxParserFactoryClassname() {
        return SAX_XERCES;
    }

    
    public void setSaxParser(final String saxParser) {
        this.saxParser = saxParser;
    }

    public Boolean isFullTimeFormatterEnabled() {
        return fullTimeFormatterEnabled;
    }

    @SuppressWarnings("PMD.LongVariable")
    public void setFullTimeFormatterEnabled(
        final Boolean fullTimeFormatterEnabled) {
        this.fullTimeFormatterEnabled = fullTimeFormatterEnabled;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + Objects.hashCode(xslTransformer);
        hash = 19 * hash + Objects.hashCode(domBuilder);
        hash = 19 * hash + Objects.hashCode(saxParser);
        hash = 19 * hash + Objects.hashCode(fullTimeFormatterEnabled);
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
        if (!(obj instanceof XmlConfig)) {
            return false;
        }
        final XmlConfig other = (XmlConfig) obj;
        if (!Objects.equals(xslTransformer, other.getXslTransformer())) {
            return false;
        }
        if (!Objects.equals(domBuilder, other.getDomBuilder())) {
            return false;
        }
        if (!Objects.equals(saxParser, other.getSaxParser())) {
            return false;
        }
        return Objects.equals(fullTimeFormatterEnabled,
                              other.isFullTimeFormatterEnabled());
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "xslTransformer = \"%s\", "
                                 + "domBuilder = \"%s\", "
                                 + "parser = \"%s\", "
                                 + "fullTimeFormatterEnabled = %b"
                                 + " }",
                             super.toString(),
                             xslTransformer,
                             domBuilder,
                             saxParser,
                             fullTimeFormatterEnabled);
    }

}
