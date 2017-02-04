/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms;

import com.arsdigita.util.Assert;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentSection;


/**
 * <p>The entry point into all the global state that CCM CMS code expects to
 * have available to it when running, e.g. the current content section,
 * current item
 *
 * <p>This is a session object that provides an environment in which
 * code can execute. The CMSContext contains all session-specific
 * variables.  One session object is maintained per thread.</p>
 *
 * <p>Accessors of this class will assert that the item it returned is
 * not null. If the caller wants to handle the case where an item is
 * null explicitly, then use the hasContentItem and hasContentSection
 * methods first.
 * 
 * @see com.arsdigita.kernel.KernelContext
 * @see com.arsdigita.cms.CMS
 *
 * @author Daniel Berrange
 */
public final class CMSContext {

    private static final Logger LOGGER = LogManager.getLogger(CMSContext.class);

    private ContentSection m_section = null;
    private ContentItem m_item = null;
    private SecurityManager m_security = null;

    CMSContext() {
        // Empty
    }

    public final String getDebugInfo() {
        final String info = "Current state of " + this + ":\n" +
            "           getContentSection() -> " + getContentSection() + "\n" +
            "              getContentItem() -> " + getContentItem() + "\n" +
            "          getSecurityManager() -> " + getSecurityManager();

        return info;
    }

    final CMSContext copy() {
        final CMSContext result = new CMSContext();

        result.m_section = m_section;
        result.m_item = m_item;
        result.m_security = m_security;

        return result;
    }

    /**
     * Checks if a content section is available
     * @return true if a content section is available
     */
    public final boolean hasContentSection() {
        return m_section != null;
    }

    /**
     * Gets the current content section
     * not anymore: hasContentSection() == true
     * @return the currently selected content section
     */
    public final ContentSection getContentSection() {
        // removing this which is not true when viewing category pages
        //Assert.exists(m_section, "section");
        return m_section;
    }

    /**
     * Sets the current content section
     * @param section the new content section
     */
    public final void setContentSection(final ContentSection section) {
        m_section = section;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Content section set to " + section);
        }
    }

    /**
     * Checks if a content item is available
     * @return true if a content item is available
     */
    public final boolean hasContentItem() {
        return m_item != null;
    }

    /**
     * Returns the current content item
     * @pre hasContentItem() == true
     * @return the current content item
     */
    public final ContentItem getContentItem() {
        // removing this which is necessarily true in ContentList
        //Assert.exists(m_item, "item");
        if (LOGGER.isDebugEnabled() && m_item == null) {
            LOGGER.debug("Content item is null");
        }
        return m_item;
    }

    /**
     * Sets the current content item
     * @param item the new content item
     */
    public final void setContentItem(final ContentItem item) {
        m_item = item;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Content item set to " + item);
        }
    }

    /**
     * Checks if there is a CMS <code>SecurityManager</code> for this
     * session.
     *
     * @see com.arsdigita.cms.SecurityManager
     * @return <code>true</code> if a security manager is available
     */
    public final boolean hasSecurityManager() {
        return m_security != null;
    }

    /**
     * Returns the current security manager.
     *
     * @return the current security manager
     */
    public final SecurityManager getSecurityManager() {
        Assert.exists(m_security, SecurityManager.class);

        return m_security;
    }

    public final void setSecurityManager(final SecurityManager security) {
        m_security = security;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Security manager set to " + security);
        }
    }
}
