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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.Template;
import com.arsdigita.cms.PageLocations;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * <p>The context bar of the content section UI.</p>
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: ContentItemContextBar.java 287 2005-02-22 00:29:02Z sskracic $
 */
class ContentItemContextBar extends ContentSectionContextBar {

    private static final Logger s_log = Logger.getLogger
        (ContentItemContextBar.class);

    private final ItemSelectionModel m_item;

    ContentItemContextBar(final ItemSelectionModel item) {
        super();

        m_item = item;
    }

    @Override
    protected final List entries(final PageState state) {
        final List entries = super.entries(state);
        final ContentItem item = (ContentItem) m_item.getSelectedObject(state);
        final ContentSection section = CMS.getContext().getContentSection();
        boolean isTemplate = 
            item.getContentType().equals(ContentType.findByAssociatedObjectType(Template.BASE_DATA_OBJECT_TYPE));

        final URL url = URL.there
            (state.getRequest(),
             section.getPath() + "/" + PageLocations.ITEM_PAGE,
             params(item));

        StringBuffer title = new StringBuffer();
        if (isTemplate) {
            title.append(localize("cms.ui.template"));
        } else {
            title.append(localize("cms.ui.content_item"));
        }
        title.append(": ")
            .append(item.getDisplayName());
        String language = item.getLanguage();
        if (language != null) {
            title.append(" (")
                .append(language)
                .append(")");
        }

        entries.add(new Entry(title.toString(), url));

        return entries;
    }

    private static ParameterMap params(final ContentItem item) {
        final ParameterMap params = new ParameterMap();

        params.setParameter(ContentItemPage.ITEM_ID, item.getID());

        return params;
    }

    private static String localize(final String key) {
        return (String) ContentSectionPage.globalize(key).localize();
    }
}
