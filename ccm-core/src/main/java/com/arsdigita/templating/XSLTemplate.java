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
package com.arsdigita.templating;

import com.arsdigita.util.Assert;
import com.arsdigita.util.IO;

import java.io.File;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.IOException;

import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.w3c.dom.Document;

/**
 * A class for loading, caching and generally managing XSL templates and
 * transformers.
 *
 * @author Dan Berrange
 */
public final class XSLTemplate {

    /**
     * Internal logger instance to faciliate debugging. Enable logging output by
     * editing /WEB-INF/conf/log4j.properties int hte runtime environment and
     * set com.arsdigita.templating.XSLTemplate=DEBUG by uncommenting or adding
     * the line.
     */
    private static final Logger LOGGER = LogManager.getLogger(XSLTemplate.class);

    /**
     * Property containing the URL to the XSL source file or create this
     * instance
     */
    private final URL m_source;
    private final Templates m_templates;
    private final List m_dependents;
    private final Date m_created;

    /**
     * Creates and loads a new template from <code>source</code>, using
     * <code>listener</code> to handle any errors.
     *
     * @param source   A <code>URL</code> pointing to the template source text
     * @param listener A <code>ErrorListener</code> to customize behavior on
     *                 error
     */
    public XSLTemplate(final URL source,
                       final ErrorListener listener) {
        if (Assert.isEnabled()) {
            Assert.exists(source, URL.class);
            Assert.exists(listener, ErrorListener.class);
        }

        m_source = source;

        final SimpleURIResolver resolver = new SimpleURIResolver();

        try {
            LOGGER.debug("Getting new templates object");

            final TransformerFactory factory = TransformerFactory.newInstance();
            factory.setURIResolver(resolver);
            factory.setErrorListener(listener);

            m_templates = factory.newTemplates(resolver.resolve(m_source.
                toString(), null));

            LOGGER.debug("Done getting new templates");
        } catch (TransformerConfigurationException ex) {
            throw new WrappedTransformerException(ex);
        } catch (TransformerException ex) {
            throw new WrappedTransformerException(ex);
        }

        // List contains each include/import URL found in the style sheet
        // recursively(!) (i.e. scanning each style sheet whose URL has been
        // found in a style sheet, etc.
        // In case of Mandalay (single entry stylesheet) about 250 URL's, all
        // resolved when found Mandalay's start.xml in one go.
        m_dependents = resolver.getStylesheetURIs();
        m_created = new Date();
    }

    /**
     * Creates and loads a new template from <code>source</code> using the
     * default <code>ErrorListener</code>.
     *
     * @param source A <code>URL</code> pointing to the template source text
     */
    public XSLTemplate(final URL source) {
        this(source, new Log4JErrorListener());
    }

    /**
     * Gets the <code>URL</code> of the template source.
     *
     * @return The <code>URL</code> location of the template source; it cannot
     *         be null
     */
    public final URL getSource() {
        return m_source;
    }

    /**
     * Gets a list of all dependent stylesheet files.
     *
     * @return A <code>List</code> of <code>URL</code>s to dependent stylesheet
     *         files; it cannot be null
     */
    public final List getDependents() {
        return m_dependents;
    }

    /**
     * Generates a new <code>Transformer</code> from the internal
     * <code>Templates</code> object.
     *
     * @return The new <code>Transformer</code>; it cannot be null
     */
    public final synchronized Transformer newTransformer() {
        LOGGER.debug("Generating new transformer");

        try {
            return m_templates.newTransformer();
        } catch (TransformerConfigurationException tce) {
            throw new WrappedTransformerException(tce);
        }
    }

    /**
     * Transforms the <code>source</code> document and sends it to
     * <code>result</code>. If there are errors, <code>listener</code> handles
     * them. This method internally creates and uses a new
     * <code>Transformer</code>.
     *
     * @param source   The <code>Source</code> to be transformed; it cannot be
     *                 null
     * @param result   The <code>Result</code> to capture the transformed
     *                 product; it cannot be null
     * @param listener A <code>ErrorListener</code> to handle transformation
     *                 errors; it cannot be null
     */
    public final void transform(final Source source,
                                final Result result,
                                final ErrorListener listener) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Transforming " + source + " and sending it to "
                             + result + " using error listener " + listener);
        }

        if (Assert.isEnabled()) {
            Assert.exists(source, Source.class);
            Assert.exists(result, Result.class);
            Assert.exists(listener, ErrorListener.class);
        }

        try {
            final Transformer transformer = newTransformer();
            transformer.setErrorListener(listener);

            LOGGER.debug("Transforming the XML source document");

            transformer.transform(source, result);

            LOGGER.debug("Finished transforming");
        } catch (TransformerConfigurationException tce) {
            throw new WrappedTransformerException(tce);
        } catch (TransformerException te) {
            throw new WrappedTransformerException(te);
        }
    }

    /**
     * Transforms the <code>source</code> document and sends it to
     * <code>result</code>. This method internally creates and uses a new
     * <code>Transformer</code>.
     *
     * @param source The <code>Source</code> to be transformed; it cannot be
     *               null
     * @param result The <code>Result</code> to capture the transformed product;
     *               it cannot be null
     */
    public final void transform(final Source source,
                                final Result result) {
        transform(source, result, new Log4JErrorListener());
    }

    /**
     * Transforms <code>doc</code> and streams the result to
     * <code>writer</code>. If there are errors, <code>listener</code> handles
     * them.
     *
     * @param doc      The <code>Document</code> to transform; it cannot be null
     * @param writer   The <code>PrintWriter</code> to receive the transformed
     *                 result; it cannot be null
     * @param listener A <code>ErrorListener</code> to handle any errors; it
     *                 cannot be null
     */
    public final void transform(final Document doc,
                                final PrintWriter writer,
                                final ErrorListener listener) {
        if (Assert.isEnabled()) {
            Assert.exists(doc, Document.class);
            Assert.exists(writer, PrintWriter.class);
            Assert.exists(listener, ErrorListener.class);
        }

        final DOMSource source = new DOMSource(doc);
        final StreamResult result = new StreamResult(writer);

        transform(source, result, listener);
    }

    /**
     * Transforms <code>doc</code> and streams the result to
     * <code>writer</code>.
     *
     * @param doc    The <code>Document</code> to transform; it cannot be null
     * @param writer The <code>PrintWriter</code> to receive the transformed
     *               result; it cannot be null
     */
    public final void transform(final Document doc,
                                final PrintWriter writer) {
        transform(doc, writer, new Log4JErrorListener());
    }

    /**
     * Checks whether the XSL files associated with the template have been
     * modified.
     *
     * @return <code>true</code> if any dependent files have been modified,
     *         otherwise <code>false</code>
     */
    public final boolean isModified() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Checking if the XSL files for " + this.getSource()
                .toString() + " "
                             + "have been modified and need to be re-read");
        }

        final Iterator iter = m_dependents.iterator();

        while (iter.hasNext()) {
            final URL url = Templating.transformURL((URL) iter.next());
            Assert.exists(url, URL.class);

            if (url.getProtocol().equals("file")) {
                final File file = new File(url.getPath());

                if (file.lastModified() > m_created.getTime()) {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("File " + file + " was modified " + file.
                            lastModified());
                    }

                    return true;
                }
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("The URL is not to a file; assuming " + url
                                     + " is not modified");
                }
            }
        }

        LOGGER.debug("No files were modified");

        return false;
    }

    /**
     * Creates a ZIP file containing this stylesheet and all dependant's. NB,
     * this method assumes that all stylesheets live in the same URL protocol.
     * If the protocol a file is different from the protocol of the top level,
     * then this file will be excluded from the ZIP. In practice this limitation
     * is not critical, because XSL files should always use relative imports,
     * which implies all imported files will be in the same URL space.
     *
     * @param os   the output stream to write the ZIP to
     * @param base the base directory in which the files will extract
     *
     * @throws java.io.IOException
     */
    public void toZIP(OutputStream os,
                      String base)
        throws IOException {

        final ZipOutputStream zos = new ZipOutputStream(os);

        URL src = getSource();
        String srcProto = src.getProtocol();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Outputting files for " + src);
        }

        final Iterator sheets = getDependents().iterator();
        while (sheets.hasNext()) {
            URL xsl = (URL) sheets.next();
            if (xsl.getProtocol().equals(srcProto)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Outputting file " + xsl);
                }
                String path = xsl.getPath();
                if (path.startsWith("/")) {
                    path = path.substring(1);
                }

                zos.putNextEntry(new ZipEntry(base + "/" + path));

                IO.copy(xsl.openStream(), zos);
            } else {
                LOGGER.warn("Not outputting file " + xsl
                                + " because its not under protocol " + srcProto);
            }
        }
        zos.finish();
    }

    private static class Log4JErrorListener implements ErrorListener {

        @Override
        public void warning(TransformerException e) throws TransformerException {
            log(Level.WARN, e);
        }

        @Override
        public void error(TransformerException e) throws TransformerException {
            log(Level.ERROR, e);
        }

        @Override
        public void fatalError(TransformerException e) throws
            TransformerException {
            log(Level.FATAL, e);
        }

        private static void log(Level level, TransformerException ex) {
            LOGGER.log(level, "Transformer " + level + ": " + ex.
                       getLocationAsString() + ": " + ex.getMessage(),
                       ex);
        }

    }

}
