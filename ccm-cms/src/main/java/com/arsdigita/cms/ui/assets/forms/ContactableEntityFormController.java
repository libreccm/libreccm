/*
 * Copyright (C) 2019 LibreCCM Foundation.
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
package com.arsdigita.cms.ui.assets.forms;

import org.librecms.assets.ContactEntry;
import org.librecms.assets.ContactableEntity;
import org.librecms.assets.ContactableEntityManager;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ContactableEntityFormController {

    @Inject
    private ContactableEntityManager contactableEntityManager;

    @Transactional(Transactional.TxType.REQUIRED)
    public void addContactEntry(final ContactEntry contactEntry,
                                final ContactableEntity toContactableEntity) {

        contactableEntityManager
            .addContactEntryToContactableEntity(contactEntry,
                                                toContactableEntity);

    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void removeContactEntry(final ContactableEntity contactableEntity,
                                   final int index) {

        if (contactableEntity.getContactEntries().size() > index) {
            
            final ContactEntry contactEntry = contactableEntity
                .getContactEntries()
                .get(index);
            contactableEntityManager.removeContactEntryFromContactableEntity(
                contactEntry, contactableEntity);
        }

    }

}
