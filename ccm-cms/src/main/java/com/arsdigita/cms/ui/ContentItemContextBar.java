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
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.CMS;

import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentSection;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.PageLocations;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.contentsection.ContentItemL10NManager;

import java.util.List;
import java.util.Locale;

/**
 * <p>
 * The context bar of the content section UI.</p>
 *
 * @author Justin Ross
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class ContentItemContextBar extends ContentSectionContextBar {

    private final ItemSelectionModel itemSelectionModel;
    private final StringParameter selectedLanguageParam;

    ContentItemContextBar(final ItemSelectionModel itemSelectionModel,
                          final StringParameter selectedLanguageParam) {
        super();

        this.itemSelectionModel = itemSelectionModel;
        this.selectedLanguageParam = selectedLanguageParam;
    }

    @Override
    protected final List<Entry> entries(final PageState state) {
        final List<Entry> entries = super.entries(state);
        final ContentItem item = itemSelectionModel.getSelectedObject(state);
        final ContentSection section = CMS.getContext().getContentSection();

        final URL url = URL.there(state.getRequest(),
                                  section.getPrimaryUrl() + "/"
                                      + PageLocations.ITEM_PAGE,
                                  params(item));

        final StringBuilder title = new StringBuilder();
        title.append(localize("cms.ui.content_item"));
        title.append(": ")
            .append(item.getDisplayName());

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ContentItemL10NManager l10nManager = cdiUtil
            .findBean(ContentItemL10NManager.class);
        final String selectedLanguage = (String) state
            .getValue(selectedLanguageParam);
        final Locale selectedLocale;
        if (selectedLanguage == null
                || selectedLanguage.isEmpty()) {
            selectedLocale = KernelConfig.getConfig().getDefaultLocale();
        } else {
            selectedLocale = new Locale(selectedLanguage);
        }
        
        final String language;
        if (l10nManager.hasLanguage(item, selectedLocale)) {
            language = selectedLanguage;
        } else {
            state.setValue(selectedLanguageParam,
                           KernelConfig.getConfig().getDefaultLanguage());
            language = KernelConfig.getConfig().getDefaultLanguage();
        }
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

        params.setParameter(ContentItemPage.ITEM_ID, item.getObjectId());

        return params;
    }

    private static String localize(final String key) {
        return (String) ContentSectionPage.globalize(key).localize();
    }

}
