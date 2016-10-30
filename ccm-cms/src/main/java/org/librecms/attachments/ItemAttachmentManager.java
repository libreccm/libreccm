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
package org.librecms.attachments;

import org.librecms.assets.Asset;

import javax.enterprise.context.RequestScoped;

/**
 * Provides methods for managing the {@link Asset} of an {@link AttachmentList}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ItemAttachmentManager {

    /**
     * Adds the provided {@link Asset} to the provided {@link AttachmentList}.
     *
     * @param asset          The {@link Asset} to add.
     * @param attachmentList The attachment list to which the asset is added.
     */
    public void attachAsset(final Asset asset,
                            final AttachmentList attachmentList) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Removes the provided {@link Asset} from the provided
     * {@link AttachmentList}. If the asset is a non shared asset the asset is
     * deleted.
     *
     * @param asset          The {@link Asset} to remove.
     * @param attachmentList The attachment list to which the asset is removed
     *                       from.
     */
    public void unattachAsset(final Asset asset,
                              final AttachmentList attachmentList) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Moves the {@link Asset} one position up in the provided
     * {@link AttachmentList}.
     *
     * @param asset          The asset to move up. If the asset is not part of
     *                       the provided {@link AttachmentList} an
     *                       {@link IllegalArgumentException} is thrown.
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
     * @param asset          The asset to move down. If the asset is not part of
     *                       the provided {@link AttachmentList} an
     *                       {@link IllegalArgumentException} is thrown.
     * @param attachmentList The attachment list in which the item is moved.
     */
    public void moveDown(final Asset asset,
                         final AttachmentList attachmentList) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Moves the {@link Asset} to the specified position in the provided
     * {@link AttachmentList}.
     *
     * @param asset          The asset to move. If the asset is not part of the
     *                       provided {@link AttachmentList} an
     *                       {@link IllegalArgumentException} is thrown.
     * @param attachmentList The attachment list in which the item is moved.
     * @param position       The position to which the asset is moved. The asset
     *                       occupying the provided index is moved down. If the
     *                       provided position is larger than the size of the
     *                       attachment list the item is moved to the end of the
     *                       list.
     */
    public void moveTo(final Asset asset,
                       final AttachmentList attachmentList,
                       final long position) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
