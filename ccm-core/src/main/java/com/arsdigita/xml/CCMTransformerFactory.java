/*
 * Copyright (C) 2014 Jens Pelzetter
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

import javax.enterprise.context.ContextNotActiveException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;

/**
 * This class acts as a facade for the {@link TransformerFactory} implementation
 * configured in {@link XMLConfig}. The current API in the Java Standard API
 * does not allow to configure the implementation of {@link TransformerFactory}
 * to use at runtime. Therefore we are setting this facade as implementation to
 * use via {@code META-INF/services/javax.xml.transform.TransformerFactory}.
 * This class uses
 * {@link TransformerFactory#newInstance(java.lang.String, java.lang.ClassLoader)}
 * to create an instance of the configured {@link TransformerFactory}
 * implementation and delegates all calls to it.
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class CCMTransformerFactory extends TransformerFactory {

    private static final Logger LOGGER = LogManager.getLogger(
        CCMTransformerFactory.class);
    private final TransformerFactory factory;

    public CCMTransformerFactory() {
        super();

        //Get an XMLConfig instance
        final XmlConfig config = retrieveXmlConfig();
        //Get the classname
        final String classname = config.getXslTransformerFactoryClassname();
        LOGGER.warn(String.format("XSL Transformer Factory classname is %s",
                                  classname));

        if (classname == null || classname.isEmpty()) {
            //To make this class errorprone we check for null and empty string. Normally this
            //is not possible, but to be sure, we check the classname provided by XMLConfig and
            //fallback to the default value if the string is null or empty.
            LOGGER.warn(
                "XSLTransformerFactory classname provided by XMLConfig is null or empty. "
                + "This indicates a invalid configuration. Check your configuration! "
                + "Falling back to default.");
            factory = TransformerFactory.newInstance(
                config.getDefaultXslTransformerFactoryClassname(), null);
        } else {
            factory = TransformerFactory.newInstance(classname, null);
        }

    }

    private XmlConfig retrieveXmlConfig() {
        try {
            return XmlConfig.getConfig();
        } catch (IllegalStateException | ContextNotActiveException ex) {
            LOGGER.warn(
                "Failed to access registry (CDI container not available?).",
                ex);
            return new XmlConfig();
        }
    }

    @Override
    public Transformer newTransformer(final Source source)
        throws TransformerConfigurationException {
        return factory.newTransformer(source);
    }

    @Override
    public Transformer newTransformer() throws TransformerConfigurationException {
        return factory.newTransformer();
    }

    @Override
    public Templates newTemplates(final Source source) throws
        TransformerConfigurationException {
        return factory.newTemplates(source);
    }

    @Override
    public Source getAssociatedStylesheet(final Source source,
                                          final String media,
                                          final String title,
                                          final String charset)
        throws TransformerConfigurationException {
        return factory.getAssociatedStylesheet(source, media, title, charset);
    }

    @Override
    public void setURIResolver(final URIResolver resolver) {
        factory.setURIResolver(resolver);
    }

    @Override
    public URIResolver getURIResolver() {
        return factory.getURIResolver();
    }

    @Override
    public void setFeature(final String name,
                           final boolean value) throws
        TransformerConfigurationException {
        factory.setFeature(name, value);
    }

    @Override
    public boolean getFeature(final String name) {
        return factory.getFeature(name);
    }

    @Override
    public void setAttribute(final String name, final Object value) {
        factory.setAttribute(name, value);
    }

    @Override
    public Object getAttribute(final String name) {
        return factory.getAttribute(name);
    }

    @Override
    public void setErrorListener(final ErrorListener listener) {
        factory.setErrorListener(listener);
    }

    @Override
    public ErrorListener getErrorListener() {
        return factory.getErrorListener();
    }

}
