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
package org.librecms.assets;

import com.arsdigita.kernel.KernelConfig;

import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.RequiresPrivilege;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetRepository;
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
import javax.transaction.Transactional;

/**
 * Manages the language versions of an asset.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class AssetL10NManager {

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private AssetRepository assetRepo;

    @Inject
    private PermissionChecker permissionChecker;

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
        final Asset asset) {

        try {
            final PropertyDescriptor[] properties = Introspector
                .getBeanInfo(asset.getClass())
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

    private LocalizedString readLocalizedString(final Asset asset,
                                                final Method readMethod) {
        try {
            return (LocalizedString) readMethod.invoke(asset);
        } catch (IllegalAccessException
                 | IllegalArgumentException
                 | InvocationTargetException ex) {
            throw new UnexpectedErrorException(ex);
        }
    }

    private Set<Locale> collectLanguages(final Asset asset) {

        final Set<Locale> locales = new HashSet<>();

        findLocalizedStringProperties(asset)
            .stream()
            .map(property -> property.getReadMethod())
            .map(readMethod -> readLocalizedString(asset, readMethod))
            .forEach(str -> locales.addAll(str.getAvailableLocales()));

        return locales;
    }

    /**
     * Helper method for reading methods in this class for verifying that the
     * current user is permitted to read the item.
     *
     * @param asset The asset for which the read permission is verified
     */
    private void checkReadPermission(final Asset asset) {

        final String requiredPrivilege = AssetPrivileges.VIEW;

        permissionChecker.checkPermission(requiredPrivilege, asset);
    }

    /**
     * Retrieves all languages in which an asset is available.
     *
     * @param asset The asset.
     *
     * @return An (unmodifiable) {@link Set} containing all languages in which
     *         the asset is available.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public Set<Locale> availableLocales(final Asset asset) {

        checkReadPermission(asset);
        return Collections.unmodifiableSet(collectLanguages(asset));
    }

    /**
     * Checks if an asset has data for particular language.
     *
     * @param asset  The asset to check.
     * @param locale The locale to check for.
     *
     * @return {@link true} if the item has data for the language, {@code false}
     *         if not.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public boolean hasLanguage(
        @RequiresPrivilege(AssetPrivileges.VIEW)
        final Asset asset,
        final Locale locale) {

        Objects.requireNonNull(asset,
                               "Can't check if asset null has a specific locale.");
        Objects.requireNonNull(locale, "Can't check for locale null.");

        checkReadPermission(asset);

        return collectLanguages(asset).contains(locale);
    }

    /**
     * Returns a {@link Set} containing the locales for which the asset does not
     * yet have a variant.
     *
     * @param asset The asset.
     *
     * @return A {@link Set} with the locales for which the item does not have a
     *         variant.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public Set<Locale> creatableLocales(final Asset asset) {
        checkReadPermission(asset);

        return supportedLocales.stream()
            .filter(locale -> !hasLanguage(asset, locale))
            .collect(Collectors.toSet());
    }

    /**
     * Adds a language to an asset. The method will retrieve all fields of the
     * type {@link LocalizedString} from the asset and add a new entry for the
     * provided locale by coping the value for the default language configured
     * in {@link KernelConfig}. If a field does not have an entry for the
     * default language the first value found is used.
     *
     * @param asset  The asset to which the language variant is added.
     * @param locale The locale of the language to add.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void addLanguage(
        @RequiresPrivilege(AssetPrivileges.EDIT)
        final Asset asset,
        final Locale locale) {

        Objects.requireNonNull(asset, "Can't add language to asset null.");
        Objects.requireNonNull(asset, "Cant't add language null to an asset.");

        findLocalizedStringProperties(asset)
            .forEach(property -> addLanguage(asset, locale, property));
        
        assetRepo.save(asset);
    }

    private void addLanguage(final Asset asset,
                             final Locale locale,
                             final PropertyDescriptor property) {

        final Method readMethod = property.getReadMethod();
        final LocalizedString localizedStr = readLocalizedString(asset,
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
     * Removes a language variant from an asset. This method will retrieve all
     * fields of the type {@link LocalizedString} from the asset and remove the
     * entry for the provided locale if the field has an entry for that locale.
     *
     * @param asset
     * @param locale
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void removeLanguage(
        @RequiresPrivilege(AssetPrivileges.EDIT)
        final Asset asset,
        final Locale locale) {

        Objects.requireNonNull(asset, "Can't remove language to asset null.");
        Objects
            .requireNonNull(asset, "Cant't remove language null to an asset.");

        findLocalizedStringProperties(asset)
            .forEach(property -> removeLanguage(asset, locale, property));

        assetRepo.save(asset);
    }

    private void removeLanguage(final Asset asset,
                                final Locale locale,
                                final PropertyDescriptor property) {

        final Method readMethod = property.getReadMethod();

        final LocalizedString localizedStr = readLocalizedString(asset,
                                                                 readMethod);
        if (localizedStr.hasValue(locale)) {
            localizedStr.removeValue(locale);
        }
    }

    /**
     * This method normalises the values of the fields of type
     * {@link LocalizedString} of an asset. The method will first retrieve all
     * fields of the type {@link LocalizedString} from the asset and than build
     * a set with all locales provided by any of the fields. After that the
     * method will iterate over all {@link LocalizedString} fields and check if
     * the {@link LocalizedString} has an entry for every language in the set.
     * If not an entry for the language is added.
     *
     * @param asset The asset to normalise.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void normalizeLanguages(
        @RequiresPrivilege(AssetPrivileges.EDIT)
        final Asset asset) {

        Objects.requireNonNull(asset, "Can't normalise asset null");

        final Set<Locale> languages = collectLanguages(asset);

        findLocalizedStringProperties(asset)
            .stream()
            .map(property -> property.getReadMethod())
            .map(readMethod -> readLocalizedString(asset, readMethod))
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
