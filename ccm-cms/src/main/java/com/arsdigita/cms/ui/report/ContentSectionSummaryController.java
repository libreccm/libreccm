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
package com.arsdigita.cms.ui.report;

import com.arsdigita.bebop.table.RowData;

import org.libreccm.categorization.Categorization;
import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.contentsection.ContentType;
import org.librecms.contentsection.Folder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ContentSectionSummaryController {

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Transactional(Transactional.TxType.REQUIRED)
    public List<RowData<Long>> createReportData(final ContentSection section) {
        final ContentSection contentSection = sectionRepo.findById(
            section.getObjectId());

        final List<Folder> rootFolders = contentSection.getRootDocumentsFolder()
            .getSubFolders();

        final List<RowData<Long>> data = new ArrayList<>();

        for (final Folder folder : rootFolders) {
            data.addAll(createFolderData(folder));
        }

        return data;
    }

    private List<RowData<Long>> createFolderData(final Folder folder) {
        final List<RowData<Long>> data = new ArrayList<>();

        final long subFolderCount = countSubFolders(folder);
        final List<ContentTypeFolderInfo> contentTypeInfo = generateContentTypeInfoForFolder(
                folder);

        final RowData<Long> firstRow = new RowData<>(5);
        firstRow.setRowKey(-1L);
        firstRow.setColData(ContentSectionSummaryTable.COL_FOLDER_NAME,
                            folder.getDisplayName());
        firstRow.setColData(ContentSectionSummaryTable.COL_SUBFOLDER_COUNT,
                            Long.toString(subFolderCount));
        firstRow.setColData(ContentSectionSummaryTable.COL_CONTENT_TYPE, 
                            contentTypeInfo.get(0).getTypeName());
        firstRow.setColData(ContentSectionSummaryTable.COL_CONTENT_TYPE, 
                            Long.toString(contentTypeInfo.get(0).getDraftCount()));
        firstRow.setColData(ContentSectionSummaryTable.COL_CONTENT_TYPE, 
                            Long.toString(contentTypeInfo.get(0).getLiveCount()));
        data.add(firstRow);
        
        for(int i = 1; i < contentTypeInfo.size(); i++) {
            data.add(createRow(contentTypeInfo.get(i)));
        }

        return data;
    }
    
    private RowData<Long> createRow(final ContentTypeFolderInfo info) {
        final RowData<Long> row = new RowData<>(5);
        
        row.setRowKey(-1L);
        row.setColData(ContentSectionSummaryTable.COL_FOLDER_NAME, "");
        row.setColData(ContentSectionSummaryTable.COL_SUBFOLDER_COUNT, "");
        row.setColData(ContentSectionSummaryTable.COL_CONTENT_TYPE, 
                       info.getTypeClassName());
        row.setColData(ContentSectionSummaryTable.COL_DRAFT_COUNT, 
                       Long.toString(info.getDraftCount()));
        row.setColData(ContentSectionSummaryTable.COL_LIVE_COUNT, 
                       Long.toString(info.getLiveCount()));
        
        return row;
    }

    private long countSubFolders(final Folder folder) {
        long count = 0;
        for (final Folder subFolder : folder.getSubFolders()) {
            count++;
            count += countSubFolders(subFolder);
        }

        return count;
    }

    private List<ContentTypeFolderInfo> generateContentTypeInfoForFolder(
        final Folder folder) {

        final Map<String, ContentTypeFolderInfo> dataMap = new HashMap<>();
        generateContentTypeInfoForFolder(folder, dataMap);
        final List<ContentTypeFolderInfo> data = new ArrayList<>(dataMap
            .values());
        Collections.sort(
            data,
            (info1, info2) -> { 
                return info1.getTypeName().compareTo(info2.getTypeName());
            });
        return data;
    }

    private void generateContentTypeInfoForFolder(
        final Folder folder, final Map<String, ContentTypeFolderInfo> data) {

        for (final Categorization categorization : folder.getObjects()) {
            if (!(categorization.getCategorizedObject() instanceof ContentItem)) {
                continue;
            }

            final ContentItem item = (ContentItem) categorization
                .getCategorizedObject();
            final ContentType type = item.getContentType();

            final ContentTypeFolderInfo info;
            if (data.containsKey(type.getContentItemClass())) {
                info = data.get(type.getContentItemClass());
            } else {
                info = new ContentTypeFolderInfo(
                    type.getContentItemClass(),
                    type.getLabel().getValue(globalizationHelper
                        .getNegotiatedLocale()));
            }
            info.increaseDraftCount();
            if (itemManager.isLive(item)) {
                info.increaseLiveCount();
            }
        }

        for (final Folder subFolder : folder.getSubFolders()) {
            generateContentTypeInfoForFolder(subFolder, data);
        }
    }

    private class ContentTypeFolderInfo {

        private final String typeClassName;
        private final String typeName;
        private long draftCount = 0;
        private long liveCount = 0;

        public ContentTypeFolderInfo(final String typeClassName,
                                     final String typeName) {
            this.typeClassName = typeClassName;
            this.typeName = typeName;
        }

        public String getTypeClassName() {
            return typeClassName;
        }

        public String getTypeName() {
            return typeName;
        }

        public long getDraftCount() {
            return draftCount;
        }

        public void increaseDraftCount() {
            draftCount++;
        }

        public long getLiveCount() {
            return liveCount;
        }

        public void increaseLiveCount() {
            liveCount++;
        }

    }

}
