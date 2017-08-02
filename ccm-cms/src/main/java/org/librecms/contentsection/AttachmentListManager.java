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

import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.RequiresPrivilege;
import org.librecms.contentsection.privileges.ItemPrivileges;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
    private ContentItemRepository itemRepo;

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private AssetManager assetManager;

    @Inject
    private PermissionChecker permissionChecker;

    @Inject
    private EntityManager entityManager;

    /**
     * Helper method to normalise the order columns for an list of
     * {@link AttachmentList}s. After this method has been applied the values of
     * the order attribute/column are the same as the position index in the
     * list.
     *
     * @param lists The list of attachment lists to normalise.
     */
    private void normalizeOrder(final List<AttachmentList> lists) {
        for (int i = 0; i < lists.size(); i++) {
            lists.get(i).setOrder(i);
            entityManager.merge(lists.get(i));
        }
    }

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

    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<AttachmentList> getAttachmentList(final long listId) {

        final TypedQuery<AttachmentList> query = entityManager
            .createNamedQuery("AttachmentList.findById", AttachmentList.class);
        query.setParameter("listId", listId);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
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
    @Transactional(Transactional.TxType.REQUIRED)
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
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    public AttachmentList createAttachmentList(
        @RequiresPrivilege(ItemPrivileges.EDIT)
        final ContentItem item,
        final String name) {

        if (item == null) {
            throw new IllegalArgumentException(
                "Can't add an attachment list to null.");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "The name of an attachment list can't be null or empty.");
        }

        final ContentItem draft = itemManager.getDraftVersion(item,
                                                              item.getClass());

        final List<AttachmentList> lists = draft.getAttachments();
        final long lastOrder = lists.get(lists.size() - 1).getOrder();

        final AttachmentList list = new AttachmentList();
        list.setItem(draft);
        list.setName(name);
        list.setUuid(UUID.randomUUID().toString());
        list.setOrder(lastOrder + 1);

        draft.addAttachmentList(list);

        entityManager.persist(list);
        itemRepo.save(draft);

        normalizeOrder(lists);

        return list;
    }

    /**
     * Adds a new {@link AttachmentList} an {@link ContentItem}. The list is put
     * after the specified position.
     *
     * @param item     The item to which the list is added.
     * @param name     The name of the new attachment list.
     * @param position The position at which the new attachment list is added.
     *                 If the provided value is larger than the number of
     *                 existing attachment lists the list is added after the
     *                 last one.
     *
     * @return The new attachment list.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    public AttachmentList createAttachmentList(
        @RequiresPrivilege(ItemPrivileges.EDIT)
        final ContentItem item,
        final String name,
        final long position) {

        if (item == null) {
            throw new IllegalArgumentException(
                "Can't add an attachment list to null.");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "The name of an attachment list can't be null or empty.");
        }

        final ContentItem draft = itemManager.getDraftVersion(item,
                                                              item.getClass());

        final List<AttachmentList> lists = draft.getAttachments();
        final long listPos;
        if (position < 0) {
            listPos = 0;
        } else if (position >= lists.size()) {
            return createAttachmentList(draft, name);
        } else {
            listPos = position;
        }

        normalizeOrder(lists);

        final AttachmentList list = new AttachmentList();
        list.setItem(draft);
        list.setName(name);
        list.setUuid(UUID.randomUUID().toString());
        list.setOrder(listPos);

        for (long i = listPos; i < lists.size(); i++) {
            lists.get((int) i).setOrder(i + 1);
            entityManager.merge(lists.get((int) i));
        }

        draft.addAttachmentList(list);

        entityManager.persist(list);
        itemRepo.save(draft);

        return list;
    }

    /**
     * Removes an {@link AttachentList} from the owning item. All non shared
     * assets assigned to the {@code attachmentList} are deleted.
     *
     * @param attachmentList The attachment list to remove.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    public void removeAttachmentList(
        @RequiresPrivilege(ItemPrivileges.EDIT)
        final AttachmentList attachmentList) {

        if (attachmentList == null) {
            throw new IllegalArgumentException("Can't delete null.");
        }

        final ContentItem item = attachmentList.getItem();

        for (ItemAttachment<?> attachment : attachmentList.getAttachments()) {
            if (!assetManager.isShared(attachment.getAsset())) {
                entityManager.remove(attachment.getAsset());
            }
        }

        for (ItemAttachment<?> attachment : attachmentList.getAttachments()) {
            entityManager.remove(attachment);
        }

        entityManager.remove(attachmentList);
    }

    /**
     * Moves an attachment list one position up. If the list is already one the
     * last position does nothing.
     *
     * @param attachmentList The list to move.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    public void moveUp(
        @RequiresPrivilege(ItemPrivileges.EDIT)
        final AttachmentList attachmentList) {

        if (attachmentList == null) {
            throw new IllegalArgumentException("Can't move null.");
        }

        final List<AttachmentList> lists = attachmentList.getItem()
            .getAttachments();

        final Optional<AttachmentList> list1 = lists.stream()
            .filter(list -> list.getOrder() == attachmentList.getOrder())
            .findFirst();
        final Optional<AttachmentList> list2 = lists.stream()
            .filter(list -> list.getOrder() >= attachmentList.getOrder() + 1)
            .findFirst();

        if (!list2.isPresent()) {
            return;
        }

        final long order1 = list1.get().getOrder();
        final long order2 = list2.get().getOrder();

        list1.get().setOrder(order2);
        list2.get().setOrder(order1);

        entityManager.merge(list1.get());
        entityManager.merge(list2.get());
    }

    /**
     * Moves an attachment list one position down.
     *
     * @param attachmentList The list to move.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public void moveDown(
        @RequiresPrivilege(ItemPrivileges.EDIT)
        final AttachmentList attachmentList) {

        if (attachmentList == null) {
            throw new IllegalArgumentException("Can't move null.");
        }

        final List<AttachmentList> lists = attachmentList.getItem()
            .getAttachments();

        final Optional<AttachmentList> list1 = lists.stream()
            .filter(list -> list.getOrder() == attachmentList.getOrder())
            .findFirst();
        final List<AttachmentList> lower = lists.stream()
            .filter(list -> list.getOrder() <= attachmentList.getOrder() - 1)
            .collect(Collectors.toList());
        Collections.sort(lower);

        final Optional<AttachmentList> list2;
        if (lower.isEmpty()) {
            list2 = Optional.empty();
        } else {
            list2 = Optional.of(lower.get(lower.size() - 1));
        }

        if (!list2.isPresent()) {
            return;
        }

        final long order1 = list1.get().getOrder();
        final long order2 = list2.get().getOrder();

        list1.get().setOrder(order2);
        list2.get().setOrder(order1);

        entityManager.merge(list1.get());
        entityManager.merge(list2.get());
    }

}
