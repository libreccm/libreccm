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
package org.librecms.pagemodel.assets;

import org.librecms.assets.Person;
import org.librecms.assets.PersonName;
import org.librecms.contentsection.Asset;

import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@AssetRenderer(renders = Person.class)
public class PersonRenderer extends ContactableEntityRenderer {

    @Override
    protected void renderAsset(final Asset asset,
                               final Locale language,
                               final Map<String, Object> result) {

        super.renderAsset(asset, language, result);

        final Person person;
        if (asset instanceof Person) {
            person = (Person) asset;
        } else {
            return;
        }

        final PersonName personName = person.getPersonName();
        if (personName != null) {

            if (personName.getSurname() != null
                    && !personName.getSurname().isEmpty()) {

                result.put("surname", personName.getSurname());
            }

            if (personName.getGivenName() != null
                    && !personName.getGivenName().isEmpty()) {

                result.put("givenName", personName.getGivenName());
            }

            if (personName.getPrefix() != null
                    && !personName.getPrefix().isEmpty()) {

                result.put("prefix", personName.getPrefix());
            }

            if (personName.getSuffix() != null
                    && !personName.getSuffix().isEmpty()) {

                result.put("suffix", personName.getSuffix());
            }
        }

        if (person.getBirthdate() != null) {
            result.put("birthdate", person.getBirthdate());
        }
    }

}
