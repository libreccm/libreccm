/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.contentsection;

import org.libreccm.core.AbstractEntityRepository;

import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.Dependent;
import javax.persistence.NoResultException;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Dependent
public class AttachmentListRepository
    extends AbstractEntityRepository<Long, AttachmentList> {

    private static final long serialVersionUID = 1L;

    /**
     * Retrieves an attachment list for a specific item by the ID of the list.
     *
     * @param item   The content item.
     * @param listId The ID of the list.
     *
     * @return An {@link Optional} with the list if the item has a list with the
     *         provided ID. Otherwise an empty {@link Optional}.
     */
    public Optional<AttachmentList> findForItemAndId(
        final ContentItem item, final long listId
    ) {
        try {
            return Optional.of(
                getEntityManager()
                    .createNamedQuery(
                        "AttachmentList.findForItemAndId",
                        AttachmentList.class
                    )
                    .setParameter("listId", listId)
                    .setParameter("item", item)
                    .getSingleResult()
            );
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    /**
     * Retrieves an attachment list for a specific item by the UUID of the list.
     *
     * @param item The content item.
     * @param uuid The UUID of the list.
     *
     * @return An {@link Optional} with the list if the item has a list with the
     *         provided UUID. Otherwise an empty {@link Optional}.
     */
    public Optional<AttachmentList> findForItemAndUuid(
        final ContentItem item, final String uuid
    ) {
        try {
            return Optional.of(
                getEntityManager()
                    .createNamedQuery(
                        "AttachmentList.findForItemAndUuid",
                        AttachmentList.class
                    )
                    .setParameter("uuid", uuid)
                    .setParameter("item", item)
                    .getSingleResult()
            );
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    /**
     * Retrieves an attachment list for a specific item by the name of the list.
     *
     * @param item The content item.
     * @param name The name of the list.
     *
     * @return An {@link Optional} with the list if the item has a list with the
     *         provided name. Otherwise an empty {@link Optional}.
     */
    public Optional<AttachmentList> findForItemAndName(
        final ContentItem item, final String name
    ) {
        try {
            return Optional.of(
                getEntityManager()
                    .createNamedQuery(
                        "AttachmentList.findForItemAndName",
                        AttachmentList.class
                    )
                    .setParameter("name", name)
                    .setParameter("item", item)
                    .getSingleResult()
            );
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Override
    public Class<AttachmentList> getEntityClass() {
        return AttachmentList.class;
    }

    @Override
    public String getIdAttributeName() {
        return "listId";
    }

    @Override
    public Long getIdOfEntity(final AttachmentList entity) {
        return entity.getListId();
    }

    @Override
    public boolean isNew(final AttachmentList entity) {
        return entity.getListId() == 0;
    }

    @Override
    protected void initNewEntity(final AttachmentList entity) {
        super.initNewEntity(entity);
        entity.setUuid(UUID.randomUUID().toString());
    }

}
