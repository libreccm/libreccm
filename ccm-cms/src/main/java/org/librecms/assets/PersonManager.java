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
package org.librecms.assets;

import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class PersonManager {

    @Inject
    private PersonRepository personRepository;

    @Transactional(Transactional.TxType.REQUIRED)
    public void addPersonName(final Person toPerson) {

        final PersonName current = Objects
            .requireNonNull(toPerson, "Can't add a name to Person null.")
            .getPersonName();

        if (current == null) {
            toPerson.addPersonName(new PersonName());
        } else {
            toPerson.addPersonName(current);
        }

        personRepository.save(toPerson);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void removePersonName(final Person person,
                                 final PersonName personName) {

        person.removePersonName(personName);

        personRepository.save(person);
    }

}
