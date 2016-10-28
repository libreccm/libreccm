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
import org.librecms.contentsection.ContentItem;

import java.util.List;

import javax.enterprise.context.RequestScoped;

/**
 * Provides methods for managing the assets attached to an item.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class AttachmentManager {

    /**
     * Retrieves the names of all {@link AttachmentList}s of an
     * {@link ContentItem}.
     *
     * @param item The item from the which the names are retrieved.
     *
     * @return A list containing the names all the attachment lists of the item,
     *         in the order of the attachment lists.
     */
    public List<String> getAttachmentListNames(final ContentItem item) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Retrieves all {@link AttachmentList}s of a {@link ContentItem} with a
     * specific name.
     *
     * @param item The item from which the lists are retrieved.
     * @param name The name of the lists to retrieve.
     *
     * @return A list of the attachment list with the specified name. If no
     *         attachment list of the {@code item} does match the provided
     *         {@code name} an empty list is returned.
     */
    public List<AttachmentList> getAttachmentList(
        final ContentItem item,
        final String name) {

        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Adds a new {@link AttachmentList} to an {@link ContentItem}. The list is
     * put after the existing attachment lists.
     *
     * @param item The item to which the list is added.
     * @param name The name of the new attachment list.
     *
     * @return The new attachment list.
     */
    public AttachmentList createAttachmentList(final ContentItem item,
                                               final String name) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Adds a new {@link AttachmentList} an {@link ContentItem}. The list is put
     * after the specified position.
     *
     * @param item  The item to which the list is added.
     * @param name  The name of the new attachment list.
     * @param after The position after which the new attachment list is added.
     *              If the provided value is larger than the number of existing
     *              attachment lists the list is added after the last one.
     *
     * @return The new attachment list.
     */
    public AttachmentList createAttachmentList(final ContentItem item,
                                               final String name,
                                               final long after) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Removes an {@link AttachentList} from an item. All non shared assets
     * assigned to the {@code attachmentList} are deleted.
     *
     * @param item           The item from the attachment list is removed.
     * @param attachmentList The attachment list to remove.
     */
    public void removeAttachmentList(final ContentItem item,
                                     final AttachmentList attachmentList) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Moves an attachment list one position up.
     *
     * @param attachmentList The list to move.
     */
    public void moveUp(final AttachmentList attachmentList) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Moves an attachment list one position down.
     *
     * @param attachmentList The list to move.
     */
    public void moveDown(final AttachmentList attachmentList) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Moves an attachment list to a specific position. The attachment list with
     * the provided index is moved one position down. If the position is larger
     * than the number of attachment lists the list is moved to the last
     * position.
     *
     * @param attachmentList The list to move.
     * @param position       The position to which the list is moved.
     */
    public void moveTo(final AttachmentList attachmentList,
                       final long position) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

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
