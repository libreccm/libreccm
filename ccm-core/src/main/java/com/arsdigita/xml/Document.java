/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

import java.io.UnsupportedEncodingException;

/**
 * A wrapper class that implements some functionality of
 * <code>org.jdom.Document</code> using <code>org.w3c.dom.Document</code>.
 *
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 * pboy (Jan. 09)
 * Class uses "DocumentBuilderFactory.newInstance()" to setup the parser
 * (according to the javax.xml specification). This is a simple and
 * straightforward, but rather thumb method. It requires a JVM wide acceptable
 * configuration (using a system.property or a static JRE configuration file) and
 * contrains all programms in a JVM (e.g. multiple CCM running in a container)
 * to use the same configuration.
 *
 * Other methods are available but we have to dig deeper into the CCM code.
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 *
 * @author Patrick McNeill 
 * @since ACS 4.5a
 * @version $Id$
 */
public class Document {

    private static final Logger LOGGER =
                                LogManager.getLogger(Document.class.getName());
    /**
     * this is the identity XSL stylesheet.  We need to provide the
     * identity transform as XSL explicitly because the default
     * transformer (newTransformer()) strips XML namespace attributes.
     * Also, this XSLT will strip the <bebop:structure> debugging info
     * from the XML document if present.
     */
    // XXX For some reason JD.XSLT doesn't copy xmlns: attributes
    // to the output doc with <xsl:copy>
    /*
    private final static String identityXSL =
    "<xsl:stylesheet version=\"1.0\""
    + " xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n"
    + "<xsl:output method=\"xml\"/>\n"
    + "<xsl:template match=\"*|@*|text()\">\n"
    + "  <xsl:copy><xsl:apply-templates select=\"node()|@*\"/></xsl:copy>"
    + "\n</xsl:template>\n"
    + "<xsl:template match=\"bebop:structure\" "
    + " xmlns:bebop=\"http://www.arsdigita.com/bebop/1.0\">\n"
    + "</xsl:template>\n"
    + "</xsl:stylesheet>";
     */
    // Explicitly create elements & attributes to avoid namespace
    // problems
    private final static String identityXSL =
                                "<xsl:stylesheet version=\"2.0\""
                                + " xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n"
                                + "<xsl:output method=\"xml\"/>\n"
                                + "<xsl:template match=\"text()|comment()|processing-instruction()\">\n"
                                + "  <xsl:copy/>\n"
                                + "</xsl:template>\n"
                                + "<xsl:template match=\"*\">\n"
                                + "  <xsl:element name=\"{name()}\" namespace=\"{namespace-uri()}\"><xsl:apply-templates select=\"node()|@*\"/></xsl:element>\n"
                                + "</xsl:template>\n"
                                + "<xsl:template match=\"@*\">\n"
                                + "  <xsl:attribute name=\"{name()}\" namespace=\"{namespace-uri()}\"><xsl:value-of select=\".\"/></xsl:attribute>\n"
                                + "</xsl:template>\n"
                                + "<xsl:template match=\"bebop:structure\" "
                                + " xmlns:bebop=\"http://www.arsdigita.com/bebop/1.0\">\n"
                                + "</xsl:template>\n"
                                + "</xsl:stylesheet>";
    /**
     * A single <code>DocumentBuilderFactory</code> to use for
     * creating Documents.
     */
    protected static DocumentBuilderFactory s_builder = null;
    /**
     * A single <code>DocumentBuilder</code> to use for
     * creating Documents.
     */
    protected static ThreadLocal s_db = null;

    // ToDo (pboy): we should use
    //   DocumentBuilderFactory.newInstance(className cname, classLoader cloader)
    // instead to achieve independence from a JVM wide configuration.
    // Requires additional modifications in c.ad.util.xml.XML
    static {
        LOGGER.debug("Static initalizer starting...");
        s_builder = DocumentBuilderFactory.newInstance();
        s_builder.setNamespaceAware(true);
        s_db = new ThreadLocal() {

            @Override
            public Object initialValue() {
                try {
                    return s_builder.newDocumentBuilder();
                } catch (ParserConfigurationException pce) {
                    return null;
                }
            }
        };
        LOGGER.debug("Static initalized finished.");
    }

    /* Used to build the DOM Documents that this class wraps */
    /**
     * The internal DOM document being wrapped.
     */
    protected org.w3c.dom.Document m_document;

    /**
     * Creates a new Document class with no root element.
     * 
     * @throws javax.xml.parsers.ParserConfigurationException
     */
    public Document() throws ParserConfigurationException {
        DocumentBuilder db = (DocumentBuilder) s_db.get();
        if (db == null) {
            throw new ParserConfigurationException(
                    "Unable to create a DocumentBuilder");
        }
        m_document = db.newDocument();
    }

    /**
     *
     * Creates a new Document class based on an org.w3c.dom.Document.
     *
     * @param doc the org.w3c.dom.Document
     *
     */
    public Document(org.w3c.dom.Document doc) {
        m_document = doc;
    }

    /**
     * Creates a new Document class with the given root element.
     *
     * @param rootNode the element to use as the root node
     * @throws javax.xml.parsers.ParserConfigurationException
     */
    public Document(Element rootNode) throws ParserConfigurationException {
        DocumentBuilder db = (DocumentBuilder) s_db.get();
        if (db == null) {
            throw new ParserConfigurationException(
                    "Unable to create a DocumentBuilder");
        }

        m_document = db.newDocument();
        rootNode.importInto(m_document);
        m_document.appendChild(rootNode.getInternalElement());
    }

    /**
     * Creates a document from the passed in string that should
     * be properly formatted XML
     * 
     * @param xmlString
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     */
    public Document(String xmlString)
            throws ParserConfigurationException, org.xml.sax.SAXException {
        this(new org.xml.sax.InputSource(new java.io.StringReader(xmlString)));
    }

    public Document(byte[] xmlBytes)
            throws ParserConfigurationException, org.xml.sax.SAXException {
        this(new org.xml.sax.InputSource(new java.io.ByteArrayInputStream(
                xmlBytes)));
    }

    private Document(org.xml.sax.InputSource inputSource)
            throws ParserConfigurationException, org.xml.sax.SAXException {
        DocumentBuilder db = (DocumentBuilder) s_db.get();
        if (db == null) {
            throw new ParserConfigurationException(
                    "Unable to create a DocumentBuilder");
        }

        org.w3c.dom.Document domDoc;
        try {
            domDoc = db.parse(inputSource);
        } catch (java.io.IOException e) {
            throw new com.arsdigita.util.UncheckedWrapperException(e);
        }
        m_document = domDoc;
    }

    /**
     * Sets the root element.
     *
     * @param rootNode the element to use as the root node
     * @return this document.
     */
    public Document setRootElement(Element rootNode) {
        rootNode.importInto(m_document);
        m_document.appendChild(rootNode.getInternalElement());

        return this;
    }

    /**
     * Creates a new element and sets it as the root.
     * Equivalent to
     * <pre>
     * Element root = new Element("name", NS);
     * doc.setRootElement(root);
     * </pre>
     * @param elt the element name
     * @param ns the element's namespace URI
     * @return The newly created root element.
     */
    public Element createRootElement(String elt, String ns) {
        org.w3c.dom.Element root = m_document.createElementNS(ns, elt);
        m_document.appendChild(root);
        Element wrapper = new Element();
        wrapper.m_element = root;
        return wrapper;
    }

    /**
     * Creates a new element and sets it as the root.
     * Equivalent to
     * <pre>
     * Element root = new Element("name", NS);
     * doc.setRootElement(root);
     * </pre>
     * @param elt the element name
     * @return The newly created root element.
     */
    public Element createRootElement(String elt) {
        org.w3c.dom.Element root = m_document.createElement(elt);
        m_document.appendChild(root);
        Element wrapper = new Element();
        wrapper.m_element = root;
        return wrapper;
    }

    /**
     * Returns the root element for the document.  This is the top-level
     * element (the "HTML" element in an HTML document).
     * @return the document's root element.
     */
    public Element getRootElement() {
        Element root = new Element();
        root.m_element = m_document.getDocumentElement();
        return root;
    }

    /**
     * Not a part of <code>org.jdom.Document</code>, this function returns
     * the internal DOM representation of this document.  This method should
     * only be used when passing the DOM to the translator. It will require
     * changes once JDOM replaces this class.
     *
     * @return this document.
     */
    public org.w3c.dom.Document getInternalDocument() {
        return m_document;
    }

    /**
     * General toString() method for org.w3c.domDocument.
     *  Not really related to xml.Document, but needed here.
     * Converts an XML in-memory DOM to String representation, using
     * an XSLT identity transformation.
     *
     * @param document the <code>org.w3c.dom.Document</code> object
     * to convert to a String representation
     * @param indent if <code>true</code>, try to indent elements according to normal
     * XML/SGML indentation conventions (may only work with certain
     * XSLT engines)
     * @return a String representation of <code>document</code>.
     */
    public static String toString(org.w3c.dom.Document document,
                                  boolean indent) {
        Transformer identity;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            StreamSource identitySource =
                         new StreamSource(new StringReader(identityXSL));
            identity = TransformerFactory.newInstance().newTransformer(
                    identitySource);
            identity.setOutputProperty("method", "xml");
            identity.setOutputProperty("indent", (indent ? "yes" : "no"));
            identity.setOutputProperty("encoding", "UTF-8");
            identity.transform(new DOMSource(document), new StreamResult(os));
        } catch (javax.xml.transform.TransformerException e) {
            LOGGER.error("error in toString", e);
            return document.toString();
        }

        try {
            return os.toString("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("UTF-8 encoding not supported!!!");
            return os.toString();
        }
    }

    /** Convenience wrapper for static toString(Document, boolean),
     *  without additional indenting.
     * @param document the <code>org.w3c.dom.Document</code> to output
     * @return a String representation of <code>document</code>.
     */
    public static String toString(org.w3c.dom.Document document) {
        return toString(document, false);
    }

    /**
     * Generates an XML text representation of this document.
     * @param indent if <code>true</code>, try to indent XML elements according
     * to XML/SGML convention
     * @return a String representation of <code>this</code>.
     */
    public String toString(boolean indent) {
        return toString(m_document, indent);
    }

    /** Generates an XML text representation of this document,
     *  without additional indenting.
     * @return a String representation of <code>this</code>.
     */
    @Override
    public String toString() {
        return toString(m_document, false);
    }
}
