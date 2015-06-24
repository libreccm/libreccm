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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;

/**
 * A wrapper class that implements some functionality of
 * <code>org.jdom.Element</code> using <code>org.w3c.dom.Element</code>.
 *
 * @author Patrick McNeill 
 * @since ACS 4.5a
 * @version $Revision$ $Date$
 * @version $Id$
 */
public class Element {

    private static final Logger s_log = Logger.getLogger(Element.class.getName());
    protected org.w3c.dom.Element m_element;
    /* DOM element that is being wrapped */
    /**
     * owner document
     */
    private org.w3c.dom.Document m_doc;

    private static ThreadLocal s_localDocument = new ThreadLocal() {
        @Override
        public Object initialValue() {
            try {
                DocumentBuilderFactory builder =
                                       DocumentBuilderFactory.newInstance();
                builder.setNamespaceAware(true);
                return builder.newDocumentBuilder().newDocument();
            } catch (ParserConfigurationException e) {
                s_log.error(e);
                throw new UncheckedWrapperException(
                        "INTERNAL: Could not create thread local DOM document.", 
                        e);
            }
        }

    };

    private static org.w3c.dom.Document getDocument() {
        return (org.w3c.dom.Document) s_localDocument.get();
    }

//    public org.w3c.dom.Document getOwnerDocument() {
//        if (null == m_doc) {
//            m_doc = (org.w3c.dom.Document) s_localDocument.get();
//        }
//        
//        return m_doc;
//    }
//    public void importElement(final Element element) {
//        element.m_element = (org.w3c.dom.Element) this.m_element
//                            .getOwnerDocument().importNode(element.m_element, 
//                            true);
//    }

    public void syncDocs() {
        if (m_doc == null) {
            m_doc = (org.w3c.dom.Document) s_localDocument.get();
        }

        if (!m_element.getOwnerDocument().equals(m_doc)) {
            m_element = (org.w3c.dom.Element) m_doc.importNode(m_element, true);
        }
    }

    /**
     * Protected constructor to set up factories, etc. Does not actually
     * create a new element.  Used if we are programatically setting the
     * m_element field later.
     */
    protected Element() {
    }

    /**
     * Creates a new element with the given name and no assigned namespace.
     *
     * @param name the name of the element
     */
    public Element(String name) {
        this();
        Assert.exists(name, String.class);

        m_element = getDocument().createElement(name);
    }

    /**
     * Creates a new element with the given name, and assigns it to the
     * namespace defined at <code>uri</code>.  The namespace prefix is
     * automatically determined.
     *
     * @param name the name of the element
     * @param uri the URI for the namespace definition
     */
    public Element(String name, String uri) {
        Assert.exists(name, String.class);
        Assert.exists(uri, String.class);

        m_element = getDocument().createElementNS(uri, name);
    }

    /**
     * Creates a new element and adds it as a child to this
     * element.  <code>elt.newChildElement("newElt")</code> is
     *  equivalent to
     * <pre>
     * Element newElt = new Element("newElt");
     * elt.addChild(newElt);
     * </pre>
     *
     * @param name the name of the element
     * @return the created child element.
     * @pre m_element != null
     */
    public Element newChildElement(String name) {
        Assert.exists(name, String.class);

        if (m_doc == null) {
            m_doc = this.m_element.getOwnerDocument();
        }

        Element result = new Element();
        result.m_element = m_doc.createElement(name);
        this.m_element.appendChild(result.m_element);
        return result;
    }

    /**
     * Creates a new element. Adds it as a child to this element
     * element and assigns it to the namespace defined at <code>uri</code>.
     *  <code>elt.newChildElement("newElt", namespace)</code> is
     *  equivalent to
     * <pre>
     * Element newElt = new Element("newElt", namespace);
     * elt.addChild(newElt);
     * </pre>
     *
     * @param name the name of the Element
     * @param uri the URI for the namespace definition
     * @return the created child element.
     * @pre m_element != null
     */
    public Element newChildElement(String name, String uri) {
        Assert.exists(name, String.class);
        Assert.exists(uri, String.class);

        if (m_doc == null) {
            m_doc = this.m_element.getOwnerDocument();
        }

        Element result = new Element();
        result.m_element = m_doc.createElementNS(uri, name);
        this.m_element.appendChild(result.m_element);
        return result;
    }

    /**
     * Copies the passed in element and all of its children to a new
     * Element.
     * 
     * @param copyFrom
     * @return 
     */
    public Element newChildElement(Element copyFrom) {
        Assert.exists(copyFrom, Element.class);

        if (m_doc == null) {
            m_doc = this.m_element.getOwnerDocument();
        }

        Element copyTo = new Element();
        copyTo.m_element = m_doc.createElementNS(copyFrom.m_element.getNamespaceURI(), copyFrom.getName());
        this.m_element.appendChild(copyTo.m_element);
        newChildElementHelper(copyFrom, copyTo);
        return copyTo;
    }

    /**
     * Copies the passed in element and all of its children to a new
     * Element using the passed-in name.
     * 
     * @param name
     * @param copyFrom
     * @return 
     */
    public Element newChildElement(String name, Element copyFrom) {
        if (m_doc == null) {
            m_doc = this.m_element.getOwnerDocument();
        }

        Element copyTo = new Element();
        copyTo.m_element = m_doc.createElement(name);
        this.m_element.appendChild(copyTo.m_element);
        newChildElementHelper(copyFrom, copyTo);
        return copyTo;
    }

    /**
     * Copies the passed in element and all of its children to a new
     * Element using the passed-in name.
     * 
     * @param name
     * @param uri
     * @param copyFrom
     * @return 
     */
    public Element newChildElement(String name, String uri, Element copyFrom) {
        if (m_doc == null) {
            m_doc = this.m_element.getOwnerDocument();
        }

        Element copyTo = new Element();
        copyTo.m_element = m_doc.createElementNS(uri, name);
        this.m_element.appendChild(copyTo.m_element);
        newChildElementHelper(copyFrom, copyTo);
        return copyTo;
    }

    private void newChildElementHelper(Element copyFrom, Element copyTo) {
        copyTo.setText(copyFrom.getText());


        NamedNodeMap nnm = copyFrom.m_element.getAttributes();

        if (nnm != null) {
            for (int i = 0; i < nnm.getLength(); i++) {
                Attr attr = (org.w3c.dom.Attr) nnm.item(i);
                copyTo.addAttribute(attr.getName(), attr.getValue());
            }
        }

        Iterator iter = copyFrom.getChildren().iterator();

        while (iter.hasNext()) {
            Element child = (Element) iter.next();
            copyTo.newChildElement(child);
        }

    }

    /**
     * Adds an attribute to the element.
     *
     * @param name the name of the attribute
     * @param value the value of the attribute
     * @return this element.
     */
    public Element addAttribute(String name, String value) {
        Assert.exists(name, String.class);

        m_element.setAttribute(name, value);

        return this;
    }

    public Element addAttribute(String name,
                                String value,
                                String ns) {
        Assert.exists(name, String.class);
        Assert.exists(ns, String.class);

        m_element.setAttributeNS(ns, name, value);

        return this;
    }

    /**
     * Adds a child element to this element.
     *
     * @param newContent the new child element
     * @return this element.
     */
    public Element addContent(Element newContent) {
        Assert.exists(newContent, Element.class);

        newContent.importInto(m_element.getOwnerDocument());
        m_element.appendChild(newContent.getInternalElement());

        return this;
    }

    /**
     * Sets the text value of the current element (the part between the
     * tags).  If the passed in text is null then it is converted to
     * the empty string.
     *
     * @param text the text to include
     * @return this element.
     */
    public Element setText(String text) {
        if (text == null) {
            // This converts the null to the empty string because
            // org.w3c.dom does not like null and HTML does not
            // differentiate between "" and null.  The other option
            // is to throw the NPE which causes other problems
            text = "";
        }
        org.w3c.dom.Text textElem =
                         m_element.getOwnerDocument().createTextNode(text);
        m_element.appendChild(textElem);

        return this;
    }

    /**
     * Returns the concatenation of all the text in all child nodes
     * of the current element.
     * 
     * @return 
     */
    public String getText() {
        StringBuilder result = new StringBuilder();

        org.w3c.dom.NodeList nl = m_element.getChildNodes();

        for (int i = 0; i < nl.getLength(); i++) {
            org.w3c.dom.Node n = nl.item(i);

            if (n.getNodeType() == org.w3c.dom.Node.TEXT_NODE) {
                result.append(((org.w3c.dom.Text) n).getData());
            }
        }

        return result.toString();
    }

    public Element setCDATASection(String cdata) {
        s_log.debug("Setting CDATA section to '" + cdata + "'.");

        if (cdata == null) {
            cdata = "";
        }

        org.w3c.dom.CDATASection cdataSection =
                                 m_element.getOwnerDocument().createCDATASection(cdata);

        m_element.appendChild(cdataSection);

        return this;
    }

    public String getCDATASection() {
        StringBuilder result = new StringBuilder();

        org.w3c.dom.NodeList nl = m_element.getChildNodes();

        for (int i = 0; i < nl.getLength(); i++) {
            org.w3c.dom.Node n = nl.item(i);

            if (n.getNodeType() == org.w3c.dom.Node.CDATA_SECTION_NODE) {
                result.append(((org.w3c.dom.CDATASection) n).getData());
            }
        }

        String str = result.toString();

        s_log.debug("Fetched this from CDATA section: " + str);

        return str;
    }

    /**
     * Returns a <code>List</code> of all the child elements nested
     * directly (one level deep) within this element, as <code>Element</code>
     * objects. If this target element has no nested elements, an empty
     * <code>List</code> is returned. The returned list is "live", so
     * changes to it affect the element's actual contents.
     * <p>
     *
     * This performs no recursion, so elements nested two levels deep would
     * have to be obtained with:
     * <pre>
     * Iterator itr = currentElement.getChildren().iterator();
     * while (itr.hasNext()) {
     *    Element oneLevelDeep = (Element)nestedElements.next();
     *    List twoLevelsDeep = oneLevelDeep.getChildren();
     *      // Do something with these children
     *    }
     * </pre>
     * @return list of child <code>Element</code> objects for this element.
     */
    public java.util.List getChildren() {
        java.util.List retval = new java.util.ArrayList();
        org.w3c.dom.NodeList nl = m_element.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            org.w3c.dom.Node n = nl.item(i);
            if (n instanceof org.w3c.dom.Element) {
                Element elt = new Element();
                elt.m_element = (org.w3c.dom.Element) n;
                retval.add(elt);
            }
        }
        return retval;
    }

    public java.util.Map getAttributes() {
        // Retrieve the attributes of the DOM Element
        org.w3c.dom.NamedNodeMap attributeNodeMap =
                                 m_element.getAttributes();

        // Create the HashMap that we will return the attributes
        // in
        java.util.HashMap returnMap = new java.util.HashMap();

        // Copy the attribute values in the NamedNodeMap to the
        // HashMap
        for (int i = 0; i < attributeNodeMap.getLength(); ++i) {
            // Get the Node
            org.w3c.dom.Node attributeNode = attributeNodeMap.item(i);
            // Copy the name and value to the map
            returnMap.put(attributeNode.getNodeName(),
                          attributeNode.getNodeValue());
        }

        // Return the HashMap
        return returnMap;
    }

    /**
     * Retrieves an attribute value by name.
     * @param name The name of the attribute to retrieve
     * @return The Attr value as a string,
     * or the empty string if that attribute does not have a specified
     * or default value.
     */
    public String getAttribute(String name) {
        return m_element.getAttribute(name);
    }

    public boolean hasAttribute(String name) {
        return m_element.hasAttribute(name);
    }

    public String getName() {
        return m_element.getTagName();
    }

    /**
     * Functions to allow this class to interact appropriately with the
     * Document class (for example, allows nodes to be moved around,
     * and so on).
     *
     * @return the internal DOM Element.
     */
    protected final org.w3c.dom.Element getInternalElement() {
        return m_element;
    }

    /**
     * Imports the internal node into another document.
     * This could also be done with a combination of getInternalElement
     * and a setInternalElement function.
     *
     * @param doc the org.w3c.dom.Document to import into
     */
    protected void importInto(org.w3c.dom.Document doc) {
        /*
         Exception e = new Exception();
         java.io.StringWriter sw = new java.io.StringWriter();
         e.printStackTrace(new java.io.PrintWriter(sw));
         System.out.println(sw.toString().substring(0, 300));
         */

        m_element = (org.w3c.dom.Element) doc.importNode(m_element, true);
    }

    /**
     * Workaround for bug in some versions of Xerces.
     * For some reason, importNode doesn't also copy attribute
     * values unless you call getValue() on them first.  This may
     * be fixed in a later version of Xerces.  In the meantime,
     * calling visitAllAttributes(node) before importNode should
     * help.
     *
     * @param node the org.w3c.dom.Node about to be imported
     * @deprecated with no replacement, 1 May 2003
     */
    public static void visitAllAttributes(org.w3c.dom.Node node) {
        org.w3c.dom.NamedNodeMap nnm = node.getAttributes();
        if (nnm != null) {
            for (int i = 0; i < nnm.getLength(); i++) {
                org.w3c.dom.Attr attr = (org.w3c.dom.Attr) nnm.item(i);
                attr.getValue();
            }
        }
        org.w3c.dom.NodeList nl = node.getChildNodes();
        if (nl != null) {
            for (int i = 0; i < nl.getLength(); i++) {
                visitAllAttributes(nl.item(i));
            }
        }
    }

    /**
     * retrieve an unordered list of strings relating to node tree including
     * and below the current element. Strings include element names, attribute names,
     * attribute values, text and CData sections
     * @return
     */
    private List getXMLFragments() {

        List unsortedList = new ArrayList();
        unsortedList.add(getName());
        unsortedList.add(getText());
        // CData sections are not included in getChildren()
        unsortedList.add(getCDATASection());
        Iterator it = getAttributes().entrySet().iterator();
        while (it.hasNext()) {
            java.util.Map.Entry entry = (java.util.Map.Entry) it.next();
            unsortedList.add(entry.getKey());
            unsortedList.add(entry.getValue());
        }
        Iterator childElements = getChildren().iterator();
        while (childElements.hasNext()) {
            Element el = (Element) childElements.next();
            unsortedList.addAll(el.getXMLFragments());
        }
        return unsortedList;

    }

    /**
     * retrieve a string that is an ordered concatenation of all information describing
     * this node and its subnodes, suitable as the basis of a hashCode or equals
     * implementation.
     * @return
     */
    protected String getXMLHashString() {
        // attributes and child nodes are retrieved as HashMap and List
        // respectively. These make no guarantees about the order of
        // iteration, and so we sort here to make sure the same element
        // will return the same XMLHash
        List sortedList = getXMLFragments();
        Collections.sort(sortedList);
        StringBuilder xml = new StringBuilder();
        Iterator xmlFragments = sortedList.iterator();
        while (xmlFragments.hasNext()) {
            xml.append(xmlFragments.next());
        }
        s_log.debug("getXMLHashString: " + xml.toString());
        return xml.toString();
    }

    @Override
    public int hashCode() {
        Date start = new Date();
        String hashString = getXMLHashString();
        s_log.debug(
                "hashCode: getXMLString took "
                + (new Date().getTime() - start.getTime())
                + " millisecs");

        return hashString.hashCode();

    }

    @Override
    public boolean equals(Object other) {
        s_log.debug("equals invoked");
        Date start = new Date();
        if (other == null) {
            return false;
        }
        if (!other.getClass().equals(Element.class)) {
            return false;
        }
        Element otherElement = (Element) other;
        String thisXML = getXMLHashString();
        String otherXML = otherElement.getXMLHashString();
        s_log.debug(
                "Equals: getXMLString twice took "
                + (new Date().getTime() - start.getTime())
                + " millisecs");
        return thisXML.equals(otherXML);

    }

}
