/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package org.libreccm.pagemodel.layout;

import org.libreccm.core.AbstractEntityRepository;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class FlexBoxRepository extends AbstractEntityRepository<Long, FlexBox> {

    private static final long serialVersionUID = -5321887349687319620L;

    @Override
    public Class<FlexBox> getEntityClass() {
        return FlexBox.class;
    }

    @Override
    public String getIdAttributeName() {
        return "boxId";
    }

    @Override
    public Long getIdOfEntity(final FlexBox entity) {
        return entity.getBoxId();
    }

    @Override
    public boolean isNew(final FlexBox entity) {
        return entity.getBoxId() == 0;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<FlexBox> getBoxesForLayout(final FlexLayout layout) {
        
        final TypedQuery<FlexBox> query = getEntityManager()
        .createNamedQuery("FlexBox.findBoxesForLayout", FlexBox.class);
        query.setParameter("layout", layout);
        
        return query.getResultList();
    }
}
