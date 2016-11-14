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
package com.arsdigita.cms.dispatcher;

import com.arsdigita.dispatcher.Dispatcher;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Web;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionConfig;

/**
 * This is the dispatcher for content-sections. It maintains a
 * ContentItem-to-Template cache Code that modifies a published ContentItem's
 * template must update the cache in this class by calling the appropriate cache
 * methods.
 *
 * @author bche@redhat.com
 */
public class ContentItemDispatcher implements Dispatcher {

        /** cache for the template resolver                                       */
    public static Map s_templateResolverCache = Collections
                                                .synchronizedMap(new HashMap());

    /**                                                                       */
    protected ItemXML m_itemXML;

    
    /**
     * Private Logger instance for debugging purpose.
     */
    private static final Logger s_log = Logger.getLogger(
        ContentItemDispatcher.class.getName());

    /**
     *     */
    public ContentItemDispatcher() {
        m_itemXML = new ItemXML();
    }

    /**
     * @see com.arsdigita.dispatcher.Dispatcher#dispatch (HttpServletRequest,
     * HttpServletResponse, RequestContext)
     */
    public void dispatch(final HttpServletRequest request,
                         final HttpServletResponse response,
                         final RequestContext actx)
        throws IOException, ServletException {

        Boolean bXMLMode = (Boolean) request
            .getAttribute("xmlMode");
        if (bXMLMode != null && bXMLMode.booleanValue()) {
            //if this is XML mode, then use itemXML
            m_itemXML.dispatch(request, response, actx);
        } else {
            //this is normal dispatching

            //get the Content Item
            //final ContentItem item = (ContentItem) request.getAttribute
            //    (ContentSectionServlet.CONTENT_ITEM);
            final ContentItem item = getContentItem(request);
            //get the Content Section
            final ContentSection section = (ContentSection) Web.getWebContext()
                .getApplication();

            Assert.exists(item);

            //get the item's template
//            final String sTemplateURL = getTemplatePath(item, request, actx);

            //dispatch to the template
            DispatcherHelper.setRequestContext(request, actx);
            DispatcherHelper.forwardRequestByPath(null, request,
                                                  response);

        }
    }

    /**
     * Fetches the content item from the request attributes.
     *
     * @param request The HTTP request
     *
     * @return The content item
     *
     * @pre ( request != null )
     */
    public static ContentItem getContentItem(HttpServletRequest request) {
        return (ContentItem) request.getAttribute(
            "com.arsdigita.cms.dispatcher.item");
    }

//    //synchronize access to the cache
//    private static synchronized void cachePut(BigDecimal contentItemID,
//                                              String sTemplatePath) {
//        s_cache.put(contentItemID, sTemplatePath);
//    }
//
//    private static synchronized void cacheRemove(BigDecimal contentItemID) {
//        s_cache.remove(contentItemID);
//    }
//
//    /**
//     * Method cacheRemove. Removes the cached template path for the contentItem
//     * item
//     *
//     * @param item
//     */
//    public static void cacheRemove(ContentItem item) {
//        if (item == null) {
//            return;
//        }
//        if (s_log.isDebugEnabled()) {
//            s_log.debug("removing cached entry for item " + item.getName()
//                        + " with ID " + item.getID());
//        }
//        s_cache.remove(item.getID());
//    }

    /**
     * Method cachePut. Maps the ContentItem item to the template t in the cache
     *
     * @param item
     * @param t
     */
//    public static void cachePut(ContentItem item, Template t) {
//        ContentSection section = item.getContentSection();
//        String sPath = getTemplatePath(section, t);
//
//        //only cache live items
//        if (item == null || item.getVersion().compareTo(ContentItem.LIVE) != 0) {
//            return;
//        }
//
//        if (s_log.isDebugEnabled()) {
//            s_log.debug("updating mapping for item " + item.getName()
//                        + " with ID " + item.getID() + " in section " + section
//                .getName() + " of type " + item.getContentType().getName()
//                        + " to template " + sPath);
//        }
//
//        cachePut(item.getID(), sPath);
//    }

    /**
     * Method cachePut. Maps all the content items of ContentType type and in
     * ContentSection section that don't have their own templates to the
     * template t in the cache
     *
     * @param section
     * @param type
     * @param t
     */
//    public static void cachePut(ContentSection section,
//                                ContentType type,
//                                Template t) {
//        s_log.debug("updating cache for section " + section.getName()
//                    + " and type " + type.getName());
//
//        //get all the items in the section
//        ItemCollection items = section.getItems();
//
//        //filter items by content type
//        BigDecimal typeID = type.getID();
//        Filter filter = items.addFilter("type.id = :typeID");
//        filter.set("typeID", typeID);
//
//        //get only live items
//        Filter liveFilter = items.addFilter("version = '" + ContentItem.LIVE
//                                            + "'");
//
//        //filter out content items in ContentSection section of
//        //ContentType type with a template for the "public" context
//        Filter itemsFilter = items.addNotInSubqueryFilter("id",
//                                                          "com.arsdigita.cms.ItemsWithTemplateMapping");
//        itemsFilter.set("sectionId", section.getID());
//        itemsFilter.set("typeId", type.getID());
//
//        //TODO: FILTER OUT CONTENT ITEMS IN THIS SECTION OF THIS TYPE
//        //WITH A TEMPLATE FOR THE "PUBLIC" CONTEXT
//        /*
//         * select items.item_id
//         * from cms_items items, cms_item_template_map map
//         * where items.item_id = map.item_id
//         * and use_context = 'public'
//         * and items.version = 'live'
//         * and items.section_id = :section_id
//         * and items.type_id = :type_id
//         */
//        synchronized (s_cache) {
//            //update the cache for all items
//            while (items.next()) {
//                cachePut(items.getContentItem(), t);
//            }
//        }
//    }

//    private static String getTemplatePath(ContentSection section,
//                                          Template template) {
//        //the template path is
//        //    TEMPLATE_ROOT/[content-section-name]/[template-path]
//        final String sep = java.io.File.separator;
//        String sPath = ContentSectionConfig.getConfig().getTemplateRoot() + sep
//                       + section.getName() + sep + template.getPath();
//        return sPath;
//    }
//
//    private static void updateTemplateCache(ContentSection section,
//                                            ContentItem item,
//                                            String sTemplatePath) {
//        //use the live version of the item for the cache
//        item = item.getLiveVersion();
//        s_log.debug("updating mapping for item " + item.getName() + " with ID "
//                    + item.getID() + " in section " + item.getContentSection()
//            .getName() + " of type " + item.getContentType().getName()
//                    + " to template " + sTemplatePath);
//        cachePut(item.getID(), sTemplatePath);
//    }
//
//    private String cacheGet(BigDecimal key) {
//        return (String) s_cache.get(key);
//    }

//    private String getTemplatePath(ContentItem item,
//                                   HttpServletRequest req,
//                                   RequestContext ctx) {
//
//        //check if the template path is cached
//        //BigDecimal id = item.getID();
//        //String sPath = cacheGet(id);
//        //return from cache
//        // current cache scheme doesn't work when there are
//        //multiple templates per item, as would happen with
//        // multiple template contexts or in the case of
//        //category item resolution, more than one category for
//        //the item.
//        //if (sPath != null) {
//        //s_log.debug("returning template path from cache");
//        //	return sPath;
//        //}
//        //s_log.debug("template path not in cache, so fecthing");
//        //template is not in the cache, so retrieve it and place it in
//        //the cache
//        String sPath = fetchTemplateURL(item, req, ctx);
//        //cachePut(id, sPath);
//
//        return sPath;
//    }

    /**
     * Fetches the URL of a template for an item. The returned URL is relative
     * to the webapp context.
     */
//    public String fetchTemplateURL(ContentItem item,
//                                   HttpServletRequest request,
//                                   RequestContext actx) {
//        if (s_log.isDebugEnabled()) {
//            s_log.debug("fetching URL for item " + item.getName() + " with ID "
//                        + item.getID());
//        }
//
//        ContentSection section = item.getContentSection();
//        String templateURL = getTemplateResolver(section).getTemplate(section,
//                                                                      item,
//                                                                      request);
//
//        if (s_log.isDebugEnabled()) {
//            s_log.debug("templateURL is " + templateURL);
//        }
//        return templateURL;
//
//    }
//
//    /**
//     * Fetches the TemplateResolver for a content section. Checks cache first.
//     *
//     * @param section The content section
//     *
//     * @return The TemplateResolver associated with the content section
//     */
//    public TemplateResolver getTemplateResolver(ContentSection section) {
//
//        String name = section.getName();
//        TemplateResolver ir = (TemplateResolver) s_templateResolverCache.get(
//            name);
//
//        if (ir == null) {
//            ir = section.getTemplateResolver();
//            s_templateResolverCache.put(name, ir);
//        }
//
//        return ir;
//    }

}
