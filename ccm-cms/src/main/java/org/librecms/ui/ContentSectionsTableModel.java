/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.ui;

import org.libreccm.security.AuthorizationRequired;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;


/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("ContentSectionsTableModel")
public class ContentSectionsTableModel {

    @Inject
    private CmsController controller;
    
    @Inject
    private ContentSectionRepository sectionRepo;
    
    

    @AuthorizationRequired
    @Transactional
    public List<ContentSectionsTableRow> getContentSections() {
        return sectionRepo
            .findAll()
            .stream()
            .map(this::buildTableRow)
            .sorted()
            .collect(Collectors.toList());
    }

    private ContentSectionsTableRow buildTableRow(
        final ContentSection section
    ) {
        final ContentSectionsTableRow row = new ContentSectionsTableRow();

        row.setSectionId(section.getObjectId());
        row.setLabel(section.getLabel());
        row.setDeletable(controller.canDelete(section));

        return row;
    }

}
