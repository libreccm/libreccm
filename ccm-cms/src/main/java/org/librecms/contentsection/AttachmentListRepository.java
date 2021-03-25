/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.contentsection;

import org.libreccm.core.AbstractEntityRepository;

import java.util.UUID;

import javax.enterprise.context.Dependent;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Dependent
public class AttachmentListRepository
    extends AbstractEntityRepository<Long, AttachmentList> {

    private static final long serialVersionUID = 1L;

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
