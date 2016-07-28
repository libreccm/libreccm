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
package com.arsdigita.cms.dispatcher;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.CMSConfig;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentItemXMLRenderer;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.UserDefinedContentItem;
import com.arsdigita.cms.XMLDeliveryCache;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectTraversal;
import com.arsdigita.domain.SimpleDomainObjectTraversalAdapter;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.metadata.DynamicObjectType;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;
import org.apache.log4j.Logger;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>The default <tt>XMLGenerator</tt> implementation.</p>
 *
 * @author Michael Pih
 * @version $Revision: #20 $ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id: SimpleXMLGenerator.java 2167 2011-06-19 21:12:12Z pboy $
 */
public class SimpleXMLGenerator implements XMLGenerator {

    private static final Logger s_log = Logger.getLogger(SimpleXMLGenerator.class);
    public static final String ADAPTER_CONTEXT = SimpleXMLGenerator.class.getName();
    /**
     * jensp 2011-10-23: Sometimes the extra XML is not needed, for example 
     * when embedding the XML of a content item into the XML output of another
     * content item. The default value {@code true}. To change the value
     * call {@link #setUseExtraXml(booelan)} after creating an instance of 
     * your generator.
     */
    private boolean useExtraXml = true;
    /**
     * jensp 2012-04-18: This value is forwarded to this ExtraXMLGenerators
     * by calling {@link ExtraXMLGenerator#setListMode(boolean)}. The behavior
     * triggered by this value depends on the specific implementation of
     * the {@code ExtraXMLGenerator}
     */
    private boolean listMode = false;
    /**
     * Extra attributes for the cms:item element.
     */
    private final Map<String, String> itemAttributes = new LinkedHashMap<String, String>();
    /**
     * Allows to overwrite the name and the namespace of the XML element
     * used to wrap the rendered item. 
     */
    private String itemElemName = "cms:item";
    private String itemElemNs = CMS.CMS_XML_NS;

    // Register general purpose adaptor for all content items
    static {
        s_log.debug("Static initializer starting...");
        final SimpleDomainObjectTraversalAdapter adapter = new SimpleDomainObjectTraversalAdapter();
        adapter.addAssociationProperty("/object/type");
        adapter.addAssociationProperty("/object/categories");

        DomainObjectTraversal.registerAdapter(
                ContentItem.BASE_DATA_OBJECT_TYPE,
                adapter,
                ADAPTER_CONTEXT);
        s_log.debug("Static initializer finished");
    }

    public SimpleXMLGenerator() {
        super();
    }

    public void setUseExtraXml(final boolean useExtraXml) {
        this.useExtraXml = useExtraXml;
    }

    public void setListMode(final boolean listMode) {
        this.listMode = listMode;
    }

    public void addItemAttribute(final String name, final String value) {
        itemAttributes.put(name, value);
    }

    public void setItemElemName(final String itemElemName, final String itemElemNs) {
        this.itemElemName = itemElemName;
        this.itemElemNs = itemElemNs;
    }

    /**
     * Generates the XML to render the content panel.
     *
     * @param state  The page state
     * @param parent The parent DOM element
     * @param useContext The use context
     */
    @Override
    public void generateXML(final PageState state, final Element parent, final String useContext) {
        //final long start = System.nanoTime();

        //ContentSection section = CMS.getContext().getContentSection();
        ContentItem item = getContentItem(state);

        s_log.info("Generate XML for item " + item.getOID());

        Party currentParty = Kernel.getContext().getParty();
        if (currentParty == null) {
            currentParty = Kernel.getPublicUser();
        }
        // check if current user can edit the current item (nb privilege is granted on draft item, but live item
        // has draft as its permission context
        //
        // Note that the xml that is generated is only of use if you DO NOT CACHE content pages.
        // cg.
        final PermissionDescriptor edit = new PermissionDescriptor(
                PrivilegeDescriptor.get(SecurityManager.CMS_EDIT_ITEM), item, currentParty);
        if (PermissionService.checkPermission(edit)) {
            parent.addAttribute("canEdit", "true");
            final Element canEditElem = parent.newChildElement("canEdit");
            canEditElem.setText("true");

        }
        final PermissionDescriptor publish = new PermissionDescriptor(
                PrivilegeDescriptor.get(SecurityManager.CMS_PUBLISH), item, currentParty);
        if (PermissionService.checkPermission(publish)) {
            parent.addAttribute("canPublish", "true");
        }
        final String className = item.getDefaultDomainClass();

        // Ensure correct subtype of ContentItem is instantiated
        if (!item.getClass().getName().equals(className)) {
            s_log.info("Specializing item");
            try {
                item = (ContentItem) DomainObjectFactory.newInstance(
                        new OID(item.getObjectType().getQualifiedName(), item.getID()));
            } catch (DataObjectNotFoundException ex) {
                throw new UncheckedWrapperException(
                        (String) GlobalizationUtil.globalize(
                        "cms.dispatcher.cannot_find_domain_object").localize(),
                        ex);
            }
        }

        // Implementing XMLGenerator directly is now deprecated
        if (item instanceof XMLGenerator) {
            s_log.info("Item implements XMLGenerator interface");
            final XMLGenerator xitem = (XMLGenerator) item;
            xitem.generateXML(state, parent, useContext);

        } else if ("com.arsdigita.cms.UserDefinedContentItem".equals(className)) {
            s_log.info("Item is a user defined content item");
            final UserDefinedContentItem UDItem = (UserDefinedContentItem) item;
            generateUDItemXML(UDItem, state, parent, useContext);

        } else {
            s_log.info("Item is using DomainObjectXMLRenderer");

            // This is the preferred method
            //final Element content = startElement(useContext, parent);
            final Element content = startElement(useContext);
            s_log.debug("Item is not in cache, generating item.");

            final XMLDeliveryCache xmlCache = XMLDeliveryCache.getInstance();

            if (CMSConfig.getInstanceOf().getEnableXmlCache() && xmlCache.isCached(item.getOID(), useContext, listMode)) {
                xmlCache.retrieveFromCache(content, item.getOID(), useContext, listMode);
            } else {
                final ContentItemXMLRenderer renderer = new ContentItemXMLRenderer(content);

                renderer.setWrapAttributes(true);
                renderer.setWrapRoot(false);
                renderer.setWrapObjects(false);
                renderer.setRevisitFullObject(true);

                //System.out.printf("Prepared renderer in %d ms\n", (System.nanoTime() - start)
                //                                                  / 1000000);

                renderer.walk(item, ADAPTER_CONTEXT);

                //System.out.printf("Rendered standard item xml in %d ms\n", (System.nanoTime() - start)
                //                                                           / 1000000);

                //parent.addContent(content);

                //Only item XML Cache End

//            s_log.debug("Content elem content: ");
//            logElementTree(content);
//            s_log.debug("Content elem content end -- ");


                /*
                 * 2011-08-27 jensp: Introduced to remove the annoying special templates
                 * for MultiPartArticle, SiteProxy and others. The method called
                 * here was already definied but not used. 
                 * 
                 * 2011-10-23 jensp: It is now possible to disable the use of
                 * extra XML.
                 */
                //final long extraXMLStart = System.nanoTime();
                if (useExtraXml) {
                    for (ExtraXMLGenerator generator : item.getExtraXMLGenerators()) {
                        generator.setListMode(listMode);
                        generator.generateXML(item, content, state);
                    }
                }

                //Only published items
                //Only the XML of the item itself, no extra XML
                if (CMSConfig.getInstanceOf().getEnableXmlCache() && item.isLiveVersion()) {
                    xmlCache.cache(item.getOID(), item, content, useContext, listMode);
                }
            }

            if (PermissionService.checkPermission(edit)) {
                final ItemResolver resolver = item.getContentSection().getItemResolver();
                final Element editLinkElem = content.newChildElement("editLink");
                final ContentItem draftItem = item.getDraftVersion();
                editLinkElem.setText(resolver.generateItemURL(state,
                                                              draftItem,
                                                              item.getContentSection(),
                                                              draftItem.getVersion()));
            }

            parent.addContent(content);

            //System.out.printf("Rendered item in              %d ms\n\n", (System.nanoTime() - start) / 1000000);
        }
    }

    /**
     * Fetches the current content item. This method can be overridden to
     * fetch any {@link com.arsdigita.cms.ContentItem}, but by default,
     * it fetches the <code>ContentItem</code> that is set in the page state
     * by the dispatcher.
     *
     * @param state The page state
     * @return A content item
     */
    protected ContentItem getContentItem(final PageState state) {
        if (CMS.getContext().hasContentItem()) {
            return CMS.getContext().getContentItem();
        } else {
            final CMSPage page = (CMSPage) state.getPage();
            return page.getContentItem(state);
        }
    }

    protected void generateUDItemXML(final UserDefinedContentItem UDItem,
                                     final PageState state,
                                     final Element parent,
                                     final String useContext) {

        final Element element = startElement(useContext, parent);
        final Element additionalAttrs = UDItemElement(useContext);

        element.addAttribute("type", UDItem.getContentType().getName());
        element.addAttribute("id", UDItem.getID().toString());
        element.addAttribute("name", UDItem.getName());
        element.addAttribute("title", UDItem.getTitle());
        element.addAttribute("javaClass", UDItem.getContentType().getClassName());

        final DynamicObjectType dot = new DynamicObjectType(UDItem.getSpecificObjectType());
        final Iterator declaredProperties =
                       dot.getObjectType().getDeclaredProperties();
        Property currentProperty;
        Object value;
        while (declaredProperties.hasNext()) {
            currentProperty = (Property) declaredProperties.next();
            value = (Object) UDItem.get(currentProperty.getName());
            if (value != null) {
                element.addContent(
                        UDItemAttrElement(currentProperty.getName(),
                                          value.toString()));
            } else {
                element.addContent(
                        UDItemAttrElement(currentProperty.getName(),
                                          "none specified"));
            }
        }

        //element.addContent(additionalAttrs);
        //parent.addContent(element);

    }

    private Element startElement(final String useContext, final Element parent) {
        //Element element = new Element("cms:item", CMS.CMS_XML_NS);
        //final Element element = new Element(itemElemName, itemElemNs);       
        final Element element = parent.newChildElement(itemElemName, itemElemNs);
        if (useContext != null) {
            element.addAttribute("useContext", useContext);
        }

        for (Map.Entry<String, String> attr : itemAttributes.entrySet()) {
            element.addAttribute(attr.getKey(), attr.getValue());
        }

        return element;
    }

    private Element startElement(final String useContext) {
        final Element element = new Element(itemElemName, itemElemNs);

        if (useContext != null) {
            element.addAttribute("useContext", useContext);
        }

        for (Map.Entry<String, String> attr : itemAttributes.entrySet()) {
            element.addAttribute(attr.getKey(), attr.getValue());
        }

        return element;
    }

    private Element startCachedElement(final String useContext) {
        final Element element = new Element(itemElemName, itemElemNs) {
            @Override
            public Element newChildElement(Element copyFrom) {
                s_log.debug("Copy of element added to cached elem.");
                return super.newChildElement(copyFrom);
            }

            @Override
            public Element newChildElement(String name, Element copyFrom) {
                s_log.debug("Copy of element added to cached elem.");
                return super.newChildElement(name, copyFrom);
            }

            @Override
            public Element addContent(final Element newContent) {
                s_log.debug("Content added to cached element");
                return super.addContent(newContent);
            }

        };

        if (useContext != null) {
            element.addAttribute("useContext", useContext);
        }

        for (Map.Entry<String, String> attr : itemAttributes.entrySet()) {
            element.addAttribute(attr.getKey(), attr.getValue());
        }

        return element;
    }

    private void copyElement(final Element parent, final Element element) {
        final Element copy = parent.newChildElement(element.getName());
        final Iterator attrs = element.getAttributes().entrySet().iterator();
        Map.Entry attr;
        while (attrs.hasNext()) {
            attr = (Map.Entry) attrs.next();
            copy.addAttribute((String) attr.getKey(), (String) attr.getValue());
        }

        final Iterator childs = element.getChildren().iterator();
        while (childs.hasNext()) {
            copyElement(copy, (Element) childs.next());
        }

        if (element.getText() != null) {
            copy.setText(element.getText());
        }

        if (element.getCDATASection() != null) {
            copy.setCDATASection(element.getCDATASection());
        }

    }

    private Element UDItemElement(final String useContext) {
        final Element element = new Element("cms:UDItemAttributes", CMS.CMS_XML_NS);
        /*
         if ( useContext != null ) {
         element.addAttribute("useContext", useContext);
         }
         */
        return element;
    }

    private Element UDItemAttrElement(final String name, final String value) {
        final Element element = new Element("cms:UDItemAttribute", CMS.CMS_XML_NS);
        element.addAttribute("UDItemAttrName", name);
        element.addAttribute("UDItemAttrValue", value);
        return element;
    }

    private void logElementTree(final Element element) {
        s_log.debug("Tree of element" + element.getName() + ":\n");
        s_log.debug("\n" + logElementTree(element, new StringBuilder(), 0));
    }

    private String logElementTree(final Element element, final StringBuilder builder, final int depth) {
        for (int i = 0; i < depth; i++) {
            builder.append('\t');
        }
        builder.append('<').append(element.getName()).append(">\n");

        for (Object childObj : element.getChildren()) {
            final Element child = (Element) childObj;
            logElementTree(child, builder, depth + 1);
        }

        for (int i = 0; i < depth; i++) {
            builder.append('\t');
        }
        builder.append("</").append(element.getName()).append(">\n");
        return builder.toString();
    }

}
