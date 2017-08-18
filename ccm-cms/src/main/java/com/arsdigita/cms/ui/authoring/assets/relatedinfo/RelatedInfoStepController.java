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
package com.arsdigita.cms.ui.authoring.assets.relatedinfo;

import com.arsdigita.kernel.KernelConfig;

import org.libreccm.configuration.ConfigurationManager;
import org.librecms.contentsection.AttachmentList;
import org.librecms.contentsection.AttachmentListManager;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemRepository;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class RelatedInfoStepController {

    @Inject
    private AttachmentListManager attachmentListManager;

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private EntityManager entityManager;

    private Locale defaultLocale;

    @PostConstruct
    private void init() {

        final KernelConfig kernelConfig = confManager
            .findConfiguration(KernelConfig.class);
        defaultLocale = kernelConfig.getDefaultLocale();

    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void saveAttachmentList(final AttachmentList attachmentList) {

        if (attachmentList.getListId() == 0) {
            entityManager.persist(attachmentList);
        } else {
            entityManager.merge(attachmentList);
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    void moveToFirst(final ContentItem selectedItem,
                     final AttachmentList listToMove) {

        Objects.requireNonNull(selectedItem);
        Objects.requireNonNull(listToMove);

        final ContentItem item = itemRepo
            .findById(selectedItem.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No ContentItem with ID %d in the database.",
                    selectedItem.getObjectId())));

        final AttachmentList toMove = attachmentListManager
            .getAttachmentList(listToMove.getListId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No AttachmentList with ID %d in the database.",
                    listToMove.getListId())));

        final List<AttachmentList> lists = item
            .getAttachments()
            .stream()
            .sorted((list1, list2) -> list1.compareTo(list2))
            .collect(Collectors.toList());

        toMove.setOrder(0);
        lists
            .stream()
            .filter(current -> !current.equals(toMove))
            .forEach(current -> current.setOrder(current.getOrder() + 1));

        lists.forEach(entityManager::merge);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void moveAfter(final ContentItem selectedItem,
                             final AttachmentList listToMove,
                             final Long destId) {

        Objects.requireNonNull(selectedItem);
        Objects.requireNonNull(listToMove);

        final ContentItem item = itemRepo
            .findById(selectedItem.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No ContentItem with ID %d in the database.",
                    selectedItem.getObjectId())));

        final AttachmentList toMove = attachmentListManager
            .getAttachmentList(listToMove.getListId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No AttachmentList with ID %d in the database.",
                    listToMove.getListId())));

        final AttachmentList after = attachmentListManager
            .getAttachmentList(destId)
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No AttachmentList with ID %d in the database.", destId)));

        final List<AttachmentList> lists = item
            .getAttachments()
            .stream()
            .sorted((list1, list2) -> list1.compareTo(list2))
            .collect(Collectors.toList());

        if (!lists.contains(toMove)) {
            throw new IllegalArgumentException(String
                .format("AttachmentList %d is not part of ContentItem %d.",
                        toMove.getListId(),
                        item.getObjectId()));
        }

        if (!lists.contains(after)) {
            throw new IllegalArgumentException(String
                .format("AttachmentList %d is not part of ContentItem %d.",
                        after.getListId(),
                        item.getObjectId()));
        }

        final int afterIndex = lists.indexOf(after);
        for (int i = afterIndex + 1; i < lists.size(); i++) {
            final AttachmentList current = lists.get(i);
            current.setOrder(current.getOrder() + 1);
            entityManager.merge(current);
        }

        toMove.setOrder(afterIndex + 1);
        entityManager.merge(toMove);
    }

    protected void deleteList(final Long listId) {

        final AttachmentList list = attachmentListManager
            .getAttachmentList(listId)
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No AttachmentList with ID %d in the database.",
                    listId)));

        entityManager.remove(list);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    List<AttachmentListTableRow> retrieveAttachmentLists(
        final ContentItem forContentItem,
        final Locale selectedLocale) {

        final ContentItem item = itemRepo
            .findById(forContentItem.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No ContentItem with ID %d in the database.",
                    forContentItem.getObjectId())));

        return item.getAttachments()
            .stream()
            .filter(list -> !list.getName().startsWith("."))
            .map(list -> buildAttachmentListTableRow(list, selectedLocale))
            .collect(Collectors.toList());

    }

    private AttachmentListTableRow buildAttachmentListTableRow(
        final AttachmentList attachmentList,
        final Locale selectedLocale) {

        final AttachmentListTableRow row = new AttachmentListTableRow();

        row.setListId(attachmentList.getListId());

        row.setName(attachmentList.getName());
        if (attachmentList.getTitle().hasValue(selectedLocale)) {
            row.setTitle(attachmentList.getTitle().getValue(selectedLocale));
        } else if (attachmentList.getTitle().hasValue(defaultLocale)) {
            row.setTitle(attachmentList.getTitle().getValue(defaultLocale));
        } else {
            row.setTitle(attachmentList.getTitle().getValue());
        }

        if (attachmentList.getDescription().hasValue(selectedLocale)) {
            row.setTitle(shortenDescription(attachmentList
                .getDescription()
                .getValue(selectedLocale)));
        } else if (attachmentList.getDescription().hasValue(defaultLocale)) {
            row.setTitle(shortenDescription(attachmentList
                .getDescription()
                .getValue(defaultLocale)));
        } else {
            row.setTitle(shortenDescription(attachmentList
                .getDescription()
                .getValue()));
        }

        return row;
    }

    private String shortenDescription(final String description) {

        if (description.trim().length() < 140) {
            return description.trim();
        } else {
            final String tmp = description.trim().substring(0, 140);

            return String
                .format("%s...",
                        tmp.substring(0, tmp.lastIndexOf(" ")));
        }

    }

}
