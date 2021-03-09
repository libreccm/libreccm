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
import org.libreccm.ui.admin.contentsections.ContentSectionTableRow;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

/**
 * Model for table of content sections.
 *
 * @see CmsController#getContentSections()
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("ContentSectionsTableModel")
public class ContentSectionsTableModel {

    /**
     * The controller.
     */
    @Inject
    private CmsController controller;

    /**
     * Repository for content sections.
     */
    @Inject
    private ContentSectionRepository sectionRepo;

    /**
     * Retrieves all available content sections and builds a
     * {@link ContentSectionTableRow} for each content sections.
     *
     * @return A list of {@link ContentSectionTableRow}s.
     */
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

    /**
     * Helper method for building a {@link ContentSectionTableRow} for a
     * {@link ContentSection}.
     *
     * @param section The content section.
     *
     * @return A {@link ContentSectionTableRow} for the section.
     */
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
