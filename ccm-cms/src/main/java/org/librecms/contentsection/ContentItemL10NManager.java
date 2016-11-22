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

import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.librecms.CmsConstants;
import org.librecms.contentsection.privileges.ItemPrivileges;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * Manages the language versions of a content item.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ContentItemL10NManager {

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private ContentItemRepository itemRepo;

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
        final ContentItem item) {

        try {
            return Arrays.stream(
                Introspector.getBeanInfo(item.getClass())
                    .getPropertyDescriptors())
                .filter(property -> property.getPropertyType().isAssignableFrom(
                LocalizedString.class))
                .collect(Collectors.toList());
        } catch (IntrospectionException ex) {
            throw new UncheckedWrapperException(ex);
        }
    }

    private LocalizedString readLocalizedString(final ContentItem item,
                                                final Method readMethod) {
        try {
            return (LocalizedString) readMethod.invoke(item);
        } catch (IllegalAccessException
                 | IllegalArgumentException
                 | InvocationTargetException ex) {
            throw new UncheckedWrapperException(ex);
        }
    }

    private Set<Locale> collectLanguages(final ContentItem item) {
        final Set<Locale> locales = new HashSet<>();

        findLocalizedStringProperties(item)
            .stream()
            .map(property -> property.getReadMethod())
            .map(readMethod -> readLocalizedString(item, readMethod))
            .forEach(str -> locales.addAll(str.getAvailableLocales()));

        return locales;
    }

    /**
     * Checks if an content item has data for particular language.
     *
     * @param item   The item to check.
     * @param locale The locale to check for.
     *
     * @return {@link true} if the item has data for the language, {@code false}
     *         if not.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public boolean hasLanguage(final ContentItem item, final Locale locale) {
        if (item == null) {
            throw new IllegalArgumentException("Can't check if item null has a"
                                                   + "specific locale.");
        }

        if (locale == null) {
            throw new IllegalArgumentException("Can't check for locale null.");
        }

        return collectLanguages(item).contains(locale);
    }
    
    @Transactional(Transactional.TxType.REQUIRED)
    public Set<Locale> creatableLocales(final ContentItem item) {
        return supportedLocales.stream()
                .filter(locale -> hasLanguage(item, locale))
                .collect(Collectors.toSet());
    }

    /**
     * Adds a language to a content item. The method will retrieve all fields of
     * the type {@link LocalizedString} from the item and add a new entry for
     * the provided locale by coping the value for the default language
     * configured in {@link KernelConfig}. If a field does not have an entry for
     * the default language the first value found is used.
     *
     * @param item   The item to which the language variant is added.
     * @param locale The locale of the language variant to add.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void addLanguage(
        @RequiresPrivilege(ItemPrivileges.EDIT)
        final ContentItem item,
        final Locale locale) {

        if (item == null) {
            throw new IllegalArgumentException("Can't add language to item null.");
        }

        if (locale == null) {
            throw new IllegalArgumentException(
                "Can't add language null to an item.");
        }

        findLocalizedStringProperties(item)
            .forEach(property -> addLanguage(item, locale, property));

        itemRepo.save(item);
    }

    private void addLanguage(final ContentItem item,
                             final Locale locale,
                             final PropertyDescriptor property) {

        final Method readMethod = property.getReadMethod();
        final LocalizedString localizedStr = readLocalizedString(item,
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
     * Removes a language variant from a content item. This method will retrieve
     * all fields of the type {@link LocalizedString} from the item and remove
     * the entry for the provided locale if the field has an entry for that
     * locale.
     *
     * @param item   The item from which the language variant is removed.
     * @param locale The locale of the language variant to remove.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void removeLangauge(
        @RequiresPrivilege(ItemPrivileges.EDIT)
        final ContentItem item,
        final Locale locale) {

        if (item == null) {
            throw new IllegalArgumentException(
                "Can't remove language from item null.");
        }

        if (locale == null) {
            throw new IllegalArgumentException(
                "Can't remove language null from an item.");
        }

        findLocalizedStringProperties(item)
            .forEach(property -> removeLanguage(item, locale, property));

        itemRepo.save(item);
    }

    private void removeLanguage(final ContentItem item,
                                final Locale locale,
                                final PropertyDescriptor property) {

        final Method readMethod = property.getReadMethod();

        final LocalizedString localizedStr = readLocalizedString(item,
                                                                 readMethod);
        if (localizedStr.hasValue(locale)) {
            localizedStr.removeValue(locale);
        }
    }

    /**
     * This method normalises the values of the fields of type
     * {@link LocalizedString} of an item. The method will first retrieve all
     * fields of the type {@link LocalizedString} from the item and than build a
     * set with all locales provided by any of the fields. After that the method
     * will iterate over all {@link LocalizedString} fields and check if the
     * {@link LocalizedString} has an entry for every language in the set. If
     * not an entry for the language is added.
     *
     * @param item The item to normalise.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void normalizedLanguages(
        @RequiresPrivilege(ItemPrivileges.EDIT)
        final ContentItem item) {

        if (item == null) {
            throw new IllegalArgumentException("Can't normalise item null.");
        }

        final Set<Locale> languages = collectLanguages(item);

        findLocalizedStringProperties(item)
            .stream()
            .map(property -> property.getReadMethod())
            .map(readMethod -> readLocalizedString(item, readMethod))
            .forEach(str -> normalize(str, languages));

        itemRepo.save(item);
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
