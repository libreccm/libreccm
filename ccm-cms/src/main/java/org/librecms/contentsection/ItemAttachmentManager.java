/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.librecms.contentsection;

import java.util.List;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.librecms.contentsection.privileges.ItemPrivileges;

import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 * Provides methods for managing the {@link Asset} of an {@link AttachmentList}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ItemAttachmentManager {

    @Inject
    private EntityManager entityManager;

    @Inject
    private AssetRepository assetRepo;

    @Inject
    private AssetManager assetManager;

    /**
     * Adds the provided {@link Asset} to the provided {@link AttachmentList}.
     *
     * @param asset The {@link Asset} to add.
     * @param attachmentList The attachment list to which the asset is added.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    public void attachAsset(
            final Asset asset,
            @RequiresPrivilege(ItemPrivileges.EDIT)
            final AttachmentList attachmentList) {

        if (asset == null) {
            throw new IllegalArgumentException("Can't attach asset null.");
        }

        if (attachmentList == null) {
            throw new IllegalArgumentException(
                    "Can't attach an asset to attachment list null.");
        }

        // For shared assets (we assume that every asset already in the database
        // is a shared one here) check of the asset is already attached.
        if (asset.getObjectId() == 0) {
            saveNonSharedAsset(asset);
        } else {
            final TypedQuery<Long> countQuery = entityManager.createNamedQuery(
                    "ItemAttachment.countByAssetIdAndList", Long.class);
            countQuery.setParameter("asset", asset);
            countQuery.setParameter("attachmentList", attachmentList);

            final long count = countQuery.getSingleResult();
            if (count > 0) {
                //Asset is already attached.
                return;
            }
        }

        final ItemAttachment<Asset> itemAttachment = new ItemAttachment<>();
        itemAttachment.setUuid(UUID.randomUUID().toString());
        itemAttachment.setAttachmentList(attachmentList);
        itemAttachment.setAsset(asset);
        itemAttachment.setSortKey(attachmentList.getAttachments().size());
        asset.addItemAttachment(itemAttachment);
        attachmentList.addAttachment(itemAttachment);

        assetRepo.save(asset);
        entityManager.merge(attachmentList);
        entityManager.persist(itemAttachment);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    private void saveNonSharedAsset(final Asset asset) {
        assetRepo.save(asset);
        entityManager.flush();
    }

    /**
     * Removes the provided {@link Asset} from the provided
     * {@link AttachmentList}. If the asset is a non shared asset the asset is
     * deleted.
     *
     * @param asset The {@link Asset} to remove.
     * @param attachmentList The attachment list to which the asset is removed
     * from.
     */
    public void unattachAsset(final Asset asset,
                              final AttachmentList attachmentList) {
        final TypedQuery<Long> countQuery = entityManager.createNamedQuery(
                "ItemAttachment.countByAssetIdAndList", Long.class);
        countQuery.setParameter("asset", asset);
        countQuery.setParameter("attachmentList", attachmentList);

        final long count = countQuery.getSingleResult();

        if (count == 0) {
            return;
        }

        final TypedQuery<ItemAttachment> query = entityManager
                .createNamedQuery("ItemAttachment.findByAssetByAndList",
                                  ItemAttachment.class);
        query.setParameter("asset", asset);
        query.setParameter("attachmentList", attachmentList);

        final List<ItemAttachment> attachments = query.getResultList();
        attachments.forEach((attachment) -> entityManager.remove(attachment));

        if (!assetManager.isShared(asset)) {
//            assetRepo.delete(asset);
            entityManager.remove(asset);
        }
    }

    /**
     * Moves the {@link Asset} one position up in the provided
     * {@link AttachmentList}.
     *
     * @param asset The asset to move up. If the asset is not part of the
     * provided {@link AttachmentList} an {@link IllegalArgumentException} is
     * thrown.
     * @param attachmentList The attachment list in which the item is moved.
     */
    public void moveUp(final Asset asset,
                       final AttachmentList attachmentList) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Moves the {@link Asset} one position down in the provided
     * {@link AttachmentList}.
     *
     * @param asset The asset to move down. If the asset is not part of the
     * provided {@link AttachmentList} an {@link IllegalArgumentException} is
     * thrown.
     * @param attachmentList The attachment list in which the item is moved.
     */
    public void moveDown(final Asset asset,
                         final AttachmentList attachmentList) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
