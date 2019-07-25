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

import com.arsdigita.cms.ui.assets.IsControllerForAssetType;

import org.librecms.assets.Person;
import org.librecms.assets.PersonManager;
import org.librecms.assets.PersonName;
import org.librecms.assets.PersonRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@IsControllerForAssetType(Person.class)
public class PersonFormController
    extends AbstractContactableEntityFormController<Person> {

    protected static final String SUFFIX = "suffix";
    protected static final String PREFIX = "prefix";
    protected static final String GIVENNAME = "givenName";
    protected static final String SURNAME = "surname";
    protected static final String BIRTHDATE = "birthdate";
    protected static final String PERSON_NAMES = "personNames";

    protected static final int SURNAME_INDEX = 0;
    protected static final int GIVENNAME_INDEX = 1;
    protected static final int PREFIX_INDEX = 2;
    protected static final int SUFFIX_INDEX = 3;

    @Inject
    private PersonRepository personRepository;

    @Inject
    private PersonManager personManager;

    @Override
    protected Map<String, Object> getAssetData(final Person asset,
                                               final Locale selectedLocale) {

        final Map<String, Object> data = super.getAssetData(asset,
                                                            selectedLocale);

        final PersonName personName = asset.getPersonName();
        data.put(SURNAME, personName.getSurname());
        data.put(GIVENNAME, personName.getGivenName());
        data.put(PREFIX, personName.getPrefix());
        data.put(SUFFIX, personName.getSuffix());

        final List<String[]> names = asset
            .getPersonNames()
            .subList(0, asset.getPersonNames().size() - 1)
            .stream()
            .map(this::convertPersonName)
            .collect(Collectors.toList());
        data.put(PERSON_NAMES, names);

        data.put(BIRTHDATE, asset.getBirthdate());

        return data;
    }

    protected List<String[]> getPersonNames(final Long personId) {

        final Person person = personRepository
            .findById(personId)
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No Person with ID %d found.", personId)));

        return person
            .getPersonNames()
            .subList(0, person.getPersonNames().size() - 1)
            .stream()
            .map(this::convertPersonName)
            .collect(Collectors.toList());
    }

    private String[] convertPersonName(final PersonName name) {

        final String[] result = new String[4];

        result[SURNAME_INDEX] = name.getSurname();
        result[GIVENNAME_INDEX] = name.getGivenName();
        result[PREFIX_INDEX] = name.getPrefix();
        result[SUFFIX_INDEX] = name.getSuffix();

        return result;
    }

    @Override
    public void updateAssetProperties(final Person asset,
                                      final Locale selectedLocale,
                                      final Map<String, Object> data) {

        super.updateAssetProperties(asset, selectedLocale, data);

        if (data.containsKey(BIRTHDATE)) {

            asset.setBirthdate((LocalDate) data.get(BIRTHDATE));
        }

        final String surname = (String) data.get(SURNAME);
        final String givenName = (String) data.get(GIVENNAME);
        final String prefix = (String) data.get(PREFIX);
        final String suffix = (String) data.get(SUFFIX);

        asset.getPersonName().setSurname(surname);
        asset.getPersonName().setGivenName(givenName);
        asset.getPersonName().setPrefix(prefix);
        asset.getPersonName().setSuffix(suffix);
    }

    public void addPersonName(final long personId) {

        final Person person = personRepository
            .findById(personId)
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No Person with ID %d found.", personId)));

        personManager.addPersonName(person);
    }

    @Override
    public Person createAsset() {
        return new Person();
    }

}
