/*
 * Copyright (C) 2017 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.dispatcher.CMSDispatcher;

import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionManager;
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.dispatcher.ItemResolver;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class ContentItemPageController {

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private ContentSectionManager sectionManager;
    @Inject
    private ContentItemRepository itemRepo;

    @Transactional(Transactional.TxType.REQUIRED)
    protected String getContentTypeLabel(final ContentItem item) {

        final ContentItem theItem = itemRepo
            .findById(item.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No ContentItem with ID %d in the database.",
                    item.getObjectId())));

        return theItem
            .getContentType()
            .getLabel()
            .getValue(globalizationHelper.getNegotiatedLocale());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected String getDefaultPreviewLink(final ContentSection section,
                                           final ContentItem item,
                                           final PageState state) {

        final ContentSection contentSection = sectionRepo
            .findById(section.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No ContentSectio with ID %d in the database.",
                    section.getObjectId())));

        final ContentItem contentItem = itemRepo
            .findById(item.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No ContentItem with ID %d in the database.",
                    item.getObjectId())));
        
        final ItemResolver itemResolver = sectionManager
            .getItemResolver(contentSection);
        
        return itemResolver.generateItemURL(state, 
                                            contentItem, 
                                            contentSection, 
                                            CMSDispatcher.PREVIEW);

    }

}
