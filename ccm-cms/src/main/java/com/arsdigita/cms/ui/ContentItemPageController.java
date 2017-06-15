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

import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemRepository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ContentItemPageController {
    
    @Inject
    private GlobalizationHelper globalizationHelper;
    
    @Inject
    private ContentItemRepository itemRepo;
    
    @Transactional(Transactional.TxType.REQUIRED)
    public String getContentTypeLabel(final ContentItem item) {
        
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
    
}
