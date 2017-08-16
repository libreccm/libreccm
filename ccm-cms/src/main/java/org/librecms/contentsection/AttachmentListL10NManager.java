/*
 * Copyright (C) 2017 LibreCCM Foundation.
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

import com.arsdigita.kernel.KernelConfig;

import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.librecms.contentsection.privileges.AssetPrivileges;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

/**
 * Manages the language variants of an {@link AttachmentList}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class AttachmentListL10NManager {

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private AttachmentListManager listManager;

    @Inject
    private EntityManager entityManager;

    private Locale defaultLocale;
    private List<Locale> supportedLocales;

    @PostConstruct
    private void init() {
        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class);
        defaultLocale = kernelConfig.getDefaultLocale();
        supportedLocales = kernelConfig.getSupportedLanguages()
            .stream()
            .map(language -> new Locale(language))
            .collect(Collectors.toList());
    }

    private List<PropertyDescriptor> findLocalizedStringProperties(
        final AttachmentList attachmentList) {

        try {
            final PropertyDescriptor[] properties = Introspector
                .getBeanInfo(attachmentList.getClass())
                .getPropertyDescriptors();

            return Arrays.stream(properties)
                .filter(property -> {
                    return property
                        .getPropertyType()
                        .isAssignableFrom(LocalizedString.class);
                })
                .collect(Collectors.toList());
        } catch (IntrospectionException ex) {
            throw new UnexpectedErrorException(ex);
        }

    }

    private LocalizedString readLocalizedString(
        final AttachmentList attachmentList,
        final Method readMethod) {

        try {
            return (LocalizedString) readMethod.invoke(attachmentList);
        } catch (IllegalAccessException
                 | IllegalArgumentException
                 | InvocationTargetException ex) {
            throw new UnexpectedErrorException(ex);
        }
    }

    private Set<Locale> collectLanguages(final AttachmentList attachmentList) {

        final Set<Locale> locales = new HashSet<>();

        findLocalizedStringProperties(attachmentList)
            .stream()
            .map(property -> property.getReadMethod())
            .map(readMethod -> readLocalizedString(attachmentList, readMethod))
            .forEach(str -> locales.addAll(str.getAvailableLocales()));

        return locales;
    }

    /**
     * Retrieves all languages in which the describing properties of an {@link AttachmentList) are available.
     *
     * @param attachmentList The {@link AttachmentList}
     *
     * @return An (unmodifiable) {@link Set} containing all languages in which
     * the attachment list is available.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public Set<Locale> availableLocales(final AttachmentList attachmentList) {

        return Collections.unmodifiableSet(collectLanguages(attachmentList));
    }

    /**
     * Checks if an {@link AttachmentList} has data for the particular language.
     *
     * @param attachmentList The AttachmentList to check.
     * @param locale         The locale to check for.
     *
     * @return {@link true} if the attachment list has data for the language,
     *         {@code false} if not.
     */
    public boolean hasLanguage(final AttachmentList attachmentList,
                               final Locale locale) {

        Objects.requireNonNull(
            attachmentList,
            "Can't check if AttachmentList null has a specific locale.");
        Objects.requireNonNull(locale, "Can't check for locale null.");

        return collectLanguages(attachmentList).contains(locale);
    }

    /**
     * Returns a {@link Set} containing the locales for which the
     * {@link AttachmentList} does not yet have a variant.
     *
     * @param attachmentList The {@link AttachmentList}.
     *
     * @return A {@link Set} with the locales for which the
     *         {@link AttachmentList} does not have a variant.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public Set<Locale> creatableLocales(final AttachmentList attachmentList) {

        return supportedLocales.stream()
            .filter(locale -> !hasLanguage(attachmentList, locale))
            .collect(Collectors.toSet());
    }

    /**
     * Adds a language to an {@link AttachmentList}. The method will retrieve
     * all fields of the type {@link LocalizedString} from the
     * {@link AttachmentList} and add a new entry for the provided locale by
     * coping the value for the default language configured in
     * {@link KernelConfig}. If a field does not have an entry for the default
     * language the first value found is used.
     *
     * @param attachmentList The {@link AttachmentList} to which the language
     *                       variant is added.
     * @param locale         The locale of the language to add.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void addLanguage(
        @RequiresPrivilege(AssetPrivileges.EDIT)
        final AttachmentList attachmentList,
        final Locale locale) {

        Objects.requireNonNull(attachmentList,
                               "Can't add language to asset null.");
        Objects.requireNonNull(attachmentList,
                               "Cant't add language null to an asset.");

        findLocalizedStringProperties(attachmentList)
            .forEach(property -> addLanguage(attachmentList, locale, property));

        entityManager.merge(attachmentList);
    }

    private void addLanguage(final AttachmentList attachmentList,
                             final Locale locale,
                             final PropertyDescriptor property) {

        final Method readMethod = property.getReadMethod();
        final LocalizedString localizedStr = readLocalizedString(attachmentList,
                                                                 readMethod);
        addLanguage(localizedStr, locale);
    }

    private void addLanguage(final LocalizedString localizedString,
                             final Locale locale) {
        if (localizedString.hasValue(locale)) {
            //Nothing to do
            return;
        }

        final String value;
        if (localizedString.hasValue(defaultLocale)) {
            value = localizedString.getValue(defaultLocale);
        } else {
            value = findValue(localizedString);
        }

        localizedString.addValue(locale, value);
    }

    private String findValue(final LocalizedString localizedStr) {
        final Optional<Locale> locale = supportedLocales
            .stream()
            .filter(current -> localizedStr.hasValue(current))
            .findAny();

        if (locale.isPresent()) {
            return localizedStr.getValue(locale.get());
        } else {
            return "Lorem ipsum";
        }
    }

    /**
     * Removes a language variant from an {@link AttachmentList}. This method
     * will retrieve all fields of the type {@link LocalizedString} from the
     * {@link AttachmentList} and remove the entry for the provided locale if
     * the field has an entry for that locale.
     *
     * @param attachmentList
     * @param locale
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void removeLanguage(
        @RequiresPrivilege(AssetPrivileges.EDIT)
        final AttachmentList attachmentList,
        final Locale locale) {

        Objects.requireNonNull(attachmentList,
                               "Can't remove language to attachmentList null.");
        Objects
            .requireNonNull(attachmentList,
                            "Cant't remove language null to an attachmentList.");

        findLocalizedStringProperties(attachmentList)
            .forEach(
                property -> removeLanguage(attachmentList, locale, property));

        entityManager.merge(attachmentList);
    }

    private void removeLanguage(final AttachmentList attachmentList,
                                final Locale locale,
                                final PropertyDescriptor property) {

        final Method readMethod = property.getReadMethod();

        final LocalizedString localizedStr = readLocalizedString(attachmentList,
                                                                 readMethod);
        if (localizedStr.hasValue(locale)) {
            localizedStr.removeValue(locale);
        }
    }

    /**
     * This method normalises the values of the fields of type
     * {@link LocalizedString} of an {@link AttachmentList}. The method will
     * first retrieve all fields of the type {@link LocalizedString} from the
     * {@link AttachmentList} and than build a set with all locales provided by
     * any of the fields. After that the method will iterate over all
     * {@link LocalizedString} fields and check if the {@link LocalizedString}
     * has an entry for every language in the set. If not an entry for the
     * language is added.
     *
     * @param attachmentList The attachmentList to normalise.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void normalizeLanguages(
        final AttachmentList attachmentList) {

        Objects.requireNonNull(attachmentList,
                               "Can't normalise attachmentList null");

        final Set<Locale> languages = collectLanguages(attachmentList);

        findLocalizedStringProperties(attachmentList)
            .stream()
            .map(property -> property.getReadMethod())
            .map(readMethod -> readLocalizedString(attachmentList, readMethod))
            .forEach(str -> normalize(str, languages));
    }

    private void normalize(final LocalizedString localizedString,
                           final Set<Locale> languages) {

        final List<Locale> missingLangs = languages.stream()
            .filter(lang -> !localizedString.hasValue(lang))
            .collect(Collectors.toList());

        if (!missingLangs.isEmpty()) {
            missingLangs.stream()
                .forEach(lang -> addLanguage(localizedString, lang));
        }
    }

}
