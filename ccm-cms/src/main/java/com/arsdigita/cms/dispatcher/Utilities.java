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
import com.arsdigita.cms.ContentCenter;
import com.arsdigita.cms.ContentCenterServlet;
import com.arsdigita.dispatcher.DispatcherHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.librecms.CmsConstants;
import org.librecms.assets.BinaryAsset;
import org.librecms.assets.Image;
import org.librecms.contentsection.ContentSection;

/**
 * <p>This class provides many utility functions for the Content Management
 * System.</p>
 * Specifically used by various JSP templates.
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Id$
 */
public class Utilities {

    // Used for caching util lookups
    private static HashMap m_cache = new HashMap();

    private static Date s_lastSectionRefresh = null;
    private static Map s_sectionRefreshTimes =
        Collections.synchronizedMap(new HashMap());

    public static final Logger LOG = Logger.getLogger(Utilities.class);

    /**
     * Fetch the location of the CMS ContentCenter package.
     * @return The URL of the CMS ContentCenter package
     * @deprecated use ContentCenter.getURL() instead
     */
    public static String getWorkspaceURL() {
        
            return CmsConstants.CONTENT_CENTER_URL;

    }

    /**
     * Fetch the location (URL) of the CMS Services package. Caches the result.
     * @return The URL of the CMS Services package
     * @deprecated Use Service.getURL(  instead
     */
    public static String getServiceURL() {
        String url = (String) m_cache.get(CmsConstants.SERVICE_PACKAGE_KEY);
        if ( url == null ) {
	    // chris.gilbert@westsussex.gov.uk
            // We don't want application context in this url, especially when 
            // it gets cached in a static variable - if I have a 
            // file that is maintained by a non cms application eg 
            // forum, then I can end up with a url that doesn't work
            // and so breaks file links everywhere
        //  url = getSingletonPackageURLSansContext(CMS.SERVICE_PACKAGE_KEY);
            url = CmsConstants.SERVICE_URL;
            m_cache.put(CmsConstants.SERVICE_PACKAGE_KEY, url);
        }

        return url;
    }

    /**
     * The URL to log out.
     * @return The logout URL
     */
    public static String getLogoutURL() {
      //StringBuffer buf = new StringBuffer(getServiceURL());
        StringBuilder buf = new StringBuilder(CmsConstants.SERVICE_URL );
        buf.append("logout");
        return buf.toString();
    }

    /**
     * Construct a URL which serves a binary asset.
     *
     * @param asset  The binary asset
     * @return the URL which will serve the specified binary asset
     * @deprecated Use Service.getAssetURL(BinaryAsset asset) instead
     */
    public static String getAssetURL(BinaryAsset asset) {
        return getAssetURL(asset.getAssetId());
    }

    /**
     * Constuct a URL which serves a binary asset.
     *
     * @param assetId  The asset ID
     * @return the URL which will serve the specified binary asset
     * @deprecated Use Service.getAssetURL(BigDecimal assetId) instead
     */
    public static String getAssetURL(long assetId) {
     // StringBuffer buf = new StringBuffer(getServiceURL());
        StringBuilder buf = new StringBuilder(CmsConstants.SERVICE_URL );
        buf.append("stream/asset?");
        buf.append(CmsConstants.ASSET_ID).append("=").append(assetId);
        return buf.toString();
    }



    /**
     * Constuct a URL which serves an image.
     *
     * @param asset  The image asset whose image is to be served
     * @return the URL which will serve the specified image asset
     * @deprecated Use Service.getImageURL(ImageAsset) instead!
     */
    public static String getImageURL(Image asset) {
    //  StringBuffer buf = new StringBuffer(getServiceURL());
        StringBuilder buf = new StringBuilder(CmsConstants.SERVICE_URL );
        buf.append("stream/image/?");
        buf.append(CmsConstants.IMAGE_ID).append("=").append(asset.getAssetId());
        return buf.toString();
    }

    public static String getGlobalAssetsURL() {
        return getWebappContext();
    }
    /**
     * Fetch the context path of the request. This is typically "/".
     *
     * @return The webapp context path
     */
    public static String getWebappContext() {
        return DispatcherHelper.getWebappContext();
    }


    /**
     * Check for the last refresh on authoring kits or content types in
     * a section.
     **/
    public static synchronized Date
        getLastSectionRefresh(ContentSection section) {

        // cache by URL string instead of by section object to avoid
        // holding the reference.

        String sectionURL = section.getPrimaryUrl();

        Date lastModified = (Date) s_sectionRefreshTimes.get(sectionURL);
        if (lastModified == null) {
            lastModified = new Date();
            s_lastSectionRefresh = lastModified;
            s_sectionRefreshTimes.put(sectionURL, lastModified);
        }

        return lastModified;
    }

    /**
     * Check for the last refresh on authoring kits or content types in
     * any section.
     **/
    public static Date getLastSectionRefresh() {

        // instantiate last refresh lazily to ensure that first result is
        // predictable.

        if (s_lastSectionRefresh == null) {
            s_lastSectionRefresh = new Date();
        }
        return s_lastSectionRefresh;
    }

    /**
     * Force the authoring UI to reload. This should be done every time an
     * authoring kit or a content type are updated.
     */
    public static void refreshItemUI(PageState state) {
        // Drop the authoring kit UI to force it to refresh
        // THE URL SHOULD NOT BE HARDCODED !

        ContentSection section = CMS.getContext().getContentSection();

        // OLD APPROACH: used in conjunction with CMSDispatcher.  This
        // shouldn't do any harm even if CMSDispatcher is not being used.
        CMSDispatcher.releaseResource(section, "admin/item");
        refreshAdminUI(state);

        // NEW APPROACH: used in conjunction with
        // ContentSectionDispatcher.  cache by URL string instead of by
        // section object to avoid holding the reference.  This shouldn't
        // do any harm even if ContentSectionDispatcher is not being used.
        s_lastSectionRefresh = new Date();
        s_sectionRefreshTimes.put(section.getPrimaryUrl(), 
                                  s_lastSectionRefresh);
    }

    /**
     * Force the authoring UI to reload. This should be done every time an
     * authoring kit or a content type are updated.
     */
    public static void refreshAdminUI(PageState state) {
        // Drop the admin UI to force it to refresh
        // THE URL SHOULD NOT BE HARDCODED !

        ContentSection section = CMS.getContext().getContentSection();

        CMSDispatcher.releaseResource(section, "admin");
        CMSDispatcher.releaseResource(section, "admin/index");
        CMSDispatcher.releaseResource(section, "");
    }

    /**
     * Add the "pragma: no-cache" header to the HTTP response to make sure
     * the browser does not cache tha page
     *
     * @param response The HTTP response
     * @deprecated use
     * com.arsdigita.dispatcher.DispatcherHelper.cacheDisable(HttpServletResponse)
     */
    public static void disableBrowserCache(HttpServletResponse response) {
        response.addHeader("pragma", "no-cache");
    }


}
