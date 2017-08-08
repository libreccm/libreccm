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
package com.arsdigita.cms.ui.authoring.assets.images;

import com.arsdigita.kernel.KernelConfig;

import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.LocalizedString;
import org.librecms.assets.Image;
import org.librecms.contentsection.AssetRepository;
import org.librecms.contentsection.AttachmentList;
import org.librecms.contentsection.AttachmentListManager;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ItemAttachment;
import org.librecms.contentsection.ItemAttachmentManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

/**
 * Encapsulates all interaction between the {@link ImageStep} and associated
 * classes like the {@link ImageStepTableModelBuilder} and CDI beans.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class ImageStepController {

    @Inject
    private AssetRepository assetRepo;

    @Inject
    private AttachmentListManager attachmentListManager;

    @Inject
    private ConfigurationManager confManager;
    
    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private EntityManager entityManager;

    @Inject
    private ItemAttachmentManager attachmentManager;

    private Locale defaultLocale;
    
    @PostConstruct
    private void init() {
        
        final KernelConfig kernelConfig = confManager
            .findConfiguration(KernelConfig.class);
        defaultLocale = kernelConfig.getDefaultLocale();
    }
    
    @Transactional(Transactional.TxType.REQUIRED)
    protected List<ItemAttachment<Image>> retrieveAssignedImages(
        final ContentItem fromContentItem) {

        Objects.requireNonNull(fromContentItem);

        final ContentItem item = itemRepo
            .findById(fromContentItem.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No ContentItem with id %d in the database.",
                    fromContentItem.getObjectId())));

        final List<AttachmentList> imageLists = attachmentListManager
            .getAttachmentList(item, ImageStep.IMAGES_ATTACHMENT_LIST);

        if (imageLists.isEmpty()) {
            return Collections.emptyList();
        }

        final List<ItemAttachment<?>> attachments = new ArrayList<>();
        for (final AttachmentList imageList : imageLists) {
            for (final ItemAttachment<?> attachment : imageList.getAttachments()) {
                attachments.add(attachment);
            }
        }

        @SuppressWarnings("unchecked")
        final List<ItemAttachment<Image>> imageAttachments = attachments
            .stream()
            .sorted((attachment1, attachment2) -> {
                return attachment1.compareTo(attachment2);
            })
            .filter(attachment -> attachment.getAsset() instanceof Image)
            .map(attachment -> (ItemAttachment<Image>) attachment)
            .collect(Collectors.toList());

        return imageAttachments;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<AssignedImageTableRow> retrieveAssignedImagesRows(
        final ContentItem fromContentItem, final Locale selectedLocale) {

//        Objects.requireNonNull(fromContentItem);
//        Objects.requireNonNull(selectedLocale);
//
//        final ContentItem item = itemRepo
//            .findById(fromContentItem.getObjectId())
//            .orElseThrow(() -> new IllegalArgumentException(String
//            .format("No ContentItem with id %d in the database.",
//                    fromContentItem.getObjectId())));
//
//        final List<AttachmentList> imageLists = attachmentListManager
//            .getAttachmentList(item, ImageStep.IMAGES_ATTACHMENT_LIST);
//
//        if (imageLists.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        final List<ItemAttachment<?>> attachments = new ArrayList<>();
//        for (final AttachmentList imageList : imageLists) {
//            for (final ItemAttachment<?> attachment : imageList.getAttachments()) {
//                attachments.add(attachment);
//            }
//        }
        final List<ItemAttachment<Image>> imageAttachments
                                              = retrieveAssignedImages(
                fromContentItem);

//        @SuppressWarnings("unchecked")
//        final List<AssignedImageTableRow> rows = attachments
//            .stream()
//            .sorted((attachment1, attachment2) -> {
//                return attachment1.compareTo(attachment2);
//            })
//            .filter(attachment -> attachment.getAsset() instanceof Image)
//            .map(attachment -> (ItemAttachment<Image>) attachment)
//            .map(imageAttachment -> buildAssignedImageTableRow(imageAttachment,
//                                                               selectedLocale))
//            .collect(Collectors.toList());
//
//        return rows;
        return imageAttachments
            .stream()
            .map(imageAttachment -> buildAssignedImageTableRow(imageAttachment,
                                                               selectedLocale))
            .collect(Collectors.toList());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void moveToFirst(final ItemAttachment<?> attachmentToMove) {

        Objects.requireNonNull(attachmentToMove);

        final ItemAttachment<?> toMove = attachmentManager
            .findById(attachmentToMove.getAttachmentId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No ItemAttachment with ID %d in the database.",
                    attachmentToMove.getAttachmentId())));

        final AttachmentList attachmentList = toMove.getAttachmentList();
        final List<ItemAttachment<?>> attachments = attachmentList
            .getAttachments()
            .stream()
            .sorted((attachment1, attachment2) -> {
                return attachment1.compareTo(attachment2);
            })
            .collect(Collectors.toList());

        toMove.setSortKey(0);
        attachments
            .stream()
            .filter(current -> !current.equals(toMove))
            .forEach(current -> current.setSortKey(current.getSortKey() + 1));

        attachments
            .forEach(current -> entityManager.merge(current));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void moveAfter(final ItemAttachment<?> attachmentToMove,
                             final long destId) {

        Objects.requireNonNull(attachmentToMove);

        final ItemAttachment<?> toMove = attachmentManager
            .findById(attachmentToMove.getAttachmentId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No ItemAttachment with ID %d in the database.",
                    attachmentToMove.getAttachmentId())));

        final ItemAttachment<?> after = attachmentManager
            .findById(destId)
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No ItemAttachment with ID %d in the database.",
                    attachmentToMove.getAttachmentId())));

        final AttachmentList attachmentList = toMove.getAttachmentList();
        final List<ItemAttachment<?>> attachments = attachmentList
            .getAttachments()
            .stream()
            .sorted((attachment1, attachment2) -> {
                return attachment1.compareTo(attachment2);
            })
            .collect(Collectors.toList());

        if (!attachments.contains(toMove)) {
            throw new IllegalArgumentException(String.format(
                "ItemAttachment %d is not part of AttachmentList %d.",
                toMove.getAttachmentId(),
                attachmentList.getListId()));
        }

        if (!attachments.contains(after)) {
            throw new IllegalArgumentException(String.format(
                "ItemAttachment %d is not part of AttachmentList %d.",
                after.getAttachmentId(),
                attachmentList.getListId()));
        }

        final int afterIndex = attachments.indexOf(after);
        for (int i = afterIndex + 1; i < attachments.size(); i++) {
            final ItemAttachment<?> current = attachments.get(i);
            current.setSortKey(current.getSortKey() + 1);
            entityManager.merge(current);
        }

        toMove.setSortKey(afterIndex + 1);
        entityManager.merge(toMove);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void deleteAttachment(final ItemAttachment<?> attachment) {

        Objects.requireNonNull(attachment);

        final ItemAttachment<?> toRemove = attachmentManager
            .findById(attachment.getAttachmentId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No ItemAttachment with ID %d in the database.",
                    attachment.getAttachmentId())));

        entityManager.remove(toRemove);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected long getNumberOfAvailableImages(final ContentItem selectedItem,
                                              final String filter) {

        Objects.requireNonNull(selectedItem);

        final ContentItem item = itemRepo
            .findById(selectedItem.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No ContentItem with ID %d in the database.",
                    selectedItem.getObjectId())));

        final List<ItemAttachment<Image>> imageAttachments
                                              = retrieveAssignedImages(item);
        final List<Image> excluededImages = imageAttachments
            .stream()
            .map(imageAttachment -> imageAttachment.getAsset())
            .collect(Collectors.toList());

        final CriteriaBuilder criteriaBuilder = entityManager
            .getCriteriaBuilder();

        final CriteriaQuery<Long> query = criteriaBuilder
            .createQuery(Long.class);
        final Root<Image> from = query.from(Image.class);
        final Join<Image, LocalizedString> titleJoin = from.join("title");
        final Join<Image, String> titleValuesJoin = titleJoin.join("values");

        query
            .select(criteriaBuilder.count(from));

        if (filter == null || filter.trim().isEmpty()) {
            if (excluededImages != null && !excluededImages.isEmpty()) {
                query.where(criteriaBuilder.not(from.in(excluededImages)));
            }
        } else {
            if (excluededImages == null || excluededImages.isEmpty()) {
                query.where(criteriaBuilder.like(titleValuesJoin,
                                                 String.format("%s%%", filter)));
            } else {
                query.where(criteriaBuilder.and(
                    criteriaBuilder.not(from.in(excluededImages)),
                    criteriaBuilder.like(titleValuesJoin,
                                         String.format("%s%%", filter))));
            }
        }

        final long result = entityManager.createQuery(query)
            .getSingleResult();
        return result;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<Image> getAvailableImages(final List<Image> excludedImages,
                                             final String filter,
                                             final long firstImage,
                                             final long maxImages) {

        final CriteriaBuilder criteriaBuilder = entityManager
            .getCriteriaBuilder();

        final CriteriaQuery<Image> criteriaQuery = criteriaBuilder
            .createQuery(Image.class);
        final Root<Image> from = criteriaQuery.from(Image.class);
        final Join<Image, String> titleJoin = from.join("title");
        final Join<Image, String> titleValuesJoin = titleJoin.join("values");

        if (filter == null || filter.trim().isEmpty()) {
            if (excludedImages != null && !excludedImages.isEmpty()) {
                criteriaQuery.where(criteriaBuilder.not(from.in(
                    excludedImages)));
            }
        } else {
            if (excludedImages == null || excludedImages.isEmpty()) {
                criteriaQuery
                    .where(criteriaBuilder.like(titleValuesJoin,
                                                String.format("%s%%", filter)));
            } else {
                criteriaQuery.where(criteriaBuilder.and(
                    criteriaBuilder.not(from.in(excludedImages)),
                    criteriaBuilder.like(titleValuesJoin,
                                         String.format("%s%%", filter))));
            }
        }

        final TypedQuery<Image> query = entityManager
            .createQuery(criteriaQuery);
        query.setFirstResult((int) firstImage);
        query.setMaxResults((int) maxImages);

        final List<Image> result = query.getResultList();
        return result;

    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<AvailableImageTableRow> getAvailableImageRows(
        final List<Image> excludedImages,
        final Locale selectedLocale,
        final String filter,
        final long firstImage,
        final long lastImage
    ) {

        return getAvailableImages(excludedImages, filter, firstImage,
                                  lastImage)
            .stream()
            .map(image -> buildAvailableImageRow(image, selectedLocale))
            .collect(Collectors.toList());

    }

    private AvailableImageTableRow buildAvailableImageRow(
        final Image image, final Locale selectedLocale) {
        final AvailableImageTableRow row = new AvailableImageTableRow();
        row.setImageId(image.getObjectId());
        row.setImageUuid(image.getUuid());
        if (image.getTitle().hasValue(selectedLocale)) {
            row.setTitle(image.getTitle().getValue(selectedLocale));
        } else if(image.getTitle().hasValue(defaultLocale)){
            row.setTitle(image.getTitle().getValue(defaultLocale));
        } else {
            row.setTitle(image.getTitle().getValue());
        }
        row.setFilename(image.getFileName());
        row.setWidth(image.getWidth());
        row.setHeight(image.getHeight());
        row.setType(image.getMimeType().toString());

        return row;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void attachImage(final ContentItem contentItem,
                               final long imageId) {

        final ContentItem item = itemRepo
            .findById(contentItem.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No ContentItem with ID %d in the database.",
                    contentItem.getObjectId())));

        final List<AttachmentList> list = attachmentListManager
            .getAttachmentList(item, ImageStep.IMAGES_ATTACHMENT_LIST);

        final AttachmentList addTo;
        if (list == null || list.isEmpty()) {
            addTo = attachmentListManager
                .createAttachmentList(item, ImageStep.IMAGES_ATTACHMENT_LIST);
        } else {
            addTo = list.get(0);
        }

        final Image image = assetRepo
            .findById(imageId, Image.class)
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Image with ID %d in the database.",
                    imageId)));

        attachmentManager.attachAsset(image, addTo);
    }

    private AssignedImageTableRow buildAssignedImageTableRow(
        final ItemAttachment<Image> imageAttachment,
        final Locale selectedLocale) {

        final AssignedImageTableRow row = new AssignedImageTableRow();

        row.setAttachmentId(imageAttachment.getAttachmentId());

        final Image image = imageAttachment.getAsset();
        row.setImageUuid(image.getUuid());
        row.setFilename(image.getFileName());
        row.setWidth(image.getWidth());
        row.setHeight(image.getHeight());
        row.setType(image.getMimeType().toString());
        if (image.getTitle().hasValue(selectedLocale)) {
            row.setTitle(image.getTitle().getValue(selectedLocale));
        } else if(image.getTitle().hasValue(defaultLocale)){
            row.setTitle(image.getTitle().getValue(defaultLocale));
        } else {
            row.setTitle(image.getTitle().getValue());
        }
        row.setCaption(image.getDescription().getValue(selectedLocale));

        return row;
    }

}
