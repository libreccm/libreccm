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

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.Template;
import com.arsdigita.cms.TemplateManager;
import com.arsdigita.cms.TemplateManagerFactory;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * ------- May be outdated. TemplateResolver has been reworked.  ----------
 * Resolves the JSP template to use for dispatching an 
 * item. This replaces TemplateResolver since the latter
 * has a useless API.
 * ------------------------------------------------------------------------
 *
 * <p>In general, the process for resolving a template involves two
 * steps:</p>
 *
 * <ol>
 *
 *   <li>The template resolver examines specific properties of the
 *   item, the content section, and/or the request itself and selects
 *   an appropriate <em>context</em>.  A context is simply a token
 *   such as "plain" or "fancy".
 *
 *   <li>Based on the selected context, the template resolver
 *   identifies an appropriate template for the item.  This is a
 *   three-step process: (1) the resolver queries for an association
 *   between the item and a specific template for the selected
 *   context; (2) if no such association exists, the resolver queries
 *   the item's content type for a default template to use in the
 *   selected context; (3) if a default template is not found, return
 *   null (at which point the dispatcher should probably give up and
 *   return a 404 error).
 *
 * </ol>
 */

public class DefaultTemplateResolver extends    AbstractTemplateResolver 
                                     implements TemplateResolver {

    private static Logger s_log = Logger.getLogger(DefaultTemplateResolver.class);
    
    /**
     * Returns the JSP template filename relative to the webapp
     * root.
     *
     * @param section The ContentSection for the request
     * @param item The ContentItem for the request
     * @param request The current HttpServletRequest
     *
     * @return The path to the jsp template.
     */
    public String getTemplate(ContentSection section,
                              ContentItem item,
                              HttpServletRequest request) {
        
        String template = getItemTemplate(section, item, request);
        MimeType mimeType = MimeType.loadMimeType(Template.JSP_MIME_TYPE);

        if (template == null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("No item template, looking for content type template");
            }
            template = getTypeTemplate(section, item, request, mimeType);
        }

        if (template == null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("No content type template, looking for default template");
            }

            template = getDefaultTemplate(section, item, request);

            Assert.exists(template, "default template");
        }

        if (s_log.isInfoEnabled()) {
            s_log.info("Got template " + template + " for item " + item.getOID());        
        }

        return ContentSection.getConfig().getTemplateRoot() + template;
    }
    
    /**
     * Returns the JSP template filename relative to the webapp
     * root for a given Template reference.
     *
     * @param template The Template to resolve the URL for.
     *
     * @return The path to the jsp template.
     */
    public String getTemplatePath(Template template) {

        return ContentSection.getConfig().getTemplateRoot() + 
	    getTemplateFilename(template, template.getContentSection());
    }

    /**
     * Returns the XSL template filename relative to the webapp
     * root for a given Template reference.
     *
     * @param template The Template to resolve the URL for.
     *
     * @return The path to the xsl template.
     */
    public String getTemplateXSLPath(Template template) {

        return ContentSection.getConfig().getTemplateRoot() + 
	    getTemplateXSLFilename(template, template.getContentSection());
    }

    /**
     * Returns the template associated with the item (if any)
     */
    protected String getItemTemplate(ContentSection section,
                                     ContentItem item,
                                     HttpServletRequest request) {
        TemplateManager manager = TemplateManagerFactory.getInstance();
        String context = getTemplateContext(request);
        Template template = manager.getTemplate(item, context);
        
        return template == null ? null : getTemplateFilename(
            template, section
        );
    }

    /**
     * Returns the template associated with the type (if any)
     * @deprecated Use the version that specifies a mime type
     */
    protected String getTypeTemplate(ContentSection section,
                                     ContentItem item,
                                     HttpServletRequest request) {
        MimeType mimeType = MimeType.loadMimeType(Template.JSP_MIME_TYPE);
        return getTypeTemplate(section, item, request, mimeType);
    }        

    /**
     * Returns the template associated with the type (if any)
     */
    protected String getTypeTemplate(ContentSection section,
                                     ContentItem item,
                                     HttpServletRequest request,
                                     MimeType mimeType) {
        TemplateManager manager = TemplateManagerFactory.getInstance();
        ContentType type = item.getContentType();
        
        Template template = null;

        if (type != null ) {
            String context = getTemplateContext(request);
            template = manager.getDefaultTemplate(section, type, context, mimeType);
        } else {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Item has no content type, not looking for a " +
                            "content type specific template");
            }
        }
        
        return template == null ? null : getTemplateFilename(
            template, section
        );
    }        

    /**
     * Returns the default template
     */
    protected String getDefaultTemplate(ContentSection section,
                                        ContentItem item,
                                        HttpServletRequest request) {
        String path = (item instanceof Folder) ?
            ContentSection.getConfig().getDefaultFolderTemplatePath() :
            ContentSection.getConfig().getDefaultItemTemplatePath();
        
        return path;
    }
    
    /**
     * Returns the filename for a Template object
     */
    protected String getTemplateFilename(Template template,
                                         ContentSection section,
                                         ContentItem item,
                                         HttpServletRequest request) {
        return getTemplateFilename(template, section);
    }

    /**
     * Returns the filename for a Template object
     */
    protected String getTemplateXSLFilename(Template template,
                                            ContentSection section,
                                            ContentItem item,
                                            HttpServletRequest request) {
        return getTemplateXSLFilename(template, section);
    }

    /**
     * Returns the filename for a Template object
     */
    protected String getTemplateFilename(Template template,
                                         ContentSection section) {
        
        String templateName = template.getPath();
        String sectionURL = section.getPath();
        return sectionURL + "/" + templateName;
    }    

    /**
     * Returns the filename for a Template object
     */
    protected String getTemplateXSLFilename(Template template,
                                            ContentSection section) {
        
        String templateName = template.getPathNoJsp() + ".xsl";
        String sectionURL = section.getPath();
        
        return sectionURL + "/" + templateName;
    }    
}
