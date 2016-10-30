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

import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.privileges.ItemPrivileges;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 * Provides methods for managing the {@link AttachmentList}s of an
 * {@link ContentItem}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class AttachmentListManager {

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private PermissionChecker permissionChecker;

    @Inject
    private EntityManager entityManager;

    /**
     * Retrieves the names of all {@link AttachmentList}s of an
     * {@link ContentItem}.
     *
     * @param item The item from the which the names are retrieved.
     *
     * @return A list containing the names all the attachment lists of the item,
     *         in the order of the attachment lists.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public List<String> getAttachmentListNames(final ContentItem item) {
        if (item == null) {
            throw new IllegalArgumentException(
                "Can't get AttachmentList(s) from null.");
        }

        //We have to distinguish between live and draft versions, therefore
        //we can't use the CDI interceptor here.
        if (itemManager.isLive(item)) {
            permissionChecker.checkPermission(ItemPrivileges.VIEW_PUBLISHED,
                                              item);
        } else {
            permissionChecker.checkPermission(ItemPrivileges.PREVIEW, item);
        }

        final List<AttachmentList> lists = item.getAttachments();
        final List<String> names = lists.stream()
            .map(list -> list.getName())
            .collect(Collectors.toList());

        Collections.sort(names);

        return names;
    }

    /**
     * Retrieves all {@link AttachmentList}s of a {@link ContentItem} with a
     * specific name.
     *
     * @param item The item from which the lists are retrieved.
     * @param name The name of the lists to retrieve.
     *
     * @return A list of the attachment lists with the specified name. If no
     *         attachment list of the {@code item} does match the provided
     *         {@code name} an empty list is returned.
     */
    public List<AttachmentList> getAttachmentList(
        final ContentItem item,
        final String name) {

        if (item == null) {
            throw new IllegalArgumentException(
                "Can't get attachments lists from null.");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "An AttachmentList can't have an empty name.");
        }

        //We have to distinguish between live and draft versions, therefore
        //we can't use the CDI interceptor here.
        if (itemManager.isLive(item)) {
            permissionChecker.checkPermission(ItemPrivileges.VIEW_PUBLISHED,
                                              item);
        } else {
            permissionChecker.checkPermission(ItemPrivileges.PREVIEW, item);
        }

        final TypedQuery<AttachmentList> query = entityManager.createNamedQuery(
            "AttachmentList.findForItemAndName", AttachmentList.class);
        query.setParameter("name", name);
        query.setParameter("item", item);

        return query.getResultList();
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

        final List<AttachmentList> lists = item.getAttachments();
        Collections.sort(lists,
                         (list1, list2) -> Long.compare(list1.getOrder(),
                                                        list2.getOrder()));
        
        final long lastOrder = lists.get(lists.size() - 1).getOrder();
        
        final AttachmentList newList = new AttachmentList();
        newList.setItem(item);
        newList.setName(name);
        newList.setOrder(lastOrder + 1);

//        item.addAttachmentList(newList);
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
     * Removes an {@link AttachentList} from the owning item. All non shared
     * assets assigned to the {@code attachmentList} are deleted.
     *
     * @param attachmentList The attachment list to remove.
     */
    public void removeAttachmentList(final AttachmentList attachmentList) {
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

}
