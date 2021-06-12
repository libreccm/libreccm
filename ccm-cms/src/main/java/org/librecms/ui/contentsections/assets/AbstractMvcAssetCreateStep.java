/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.ui.contentsections.assets;

import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.security.AuthorizationRequired;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetManager;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderManager;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T>
 */
public abstract class AbstractMvcAssetCreateStep<T extends Asset>
    implements MvcAssetCreateStep<T> {

    private static final String FORM_PARAMS_NAME = "name";

    private static final String FORM_PARAMS_TITLE = "title";

    private static final String FORM_PARAM_INITIAL_LOCALE = "locale";

    @Inject
    private AssetManager assetManager;

    /**
     * Provides operations for folders.
     */
    @Inject
    private FolderManager folderManager;

    /**
     * Provides functions for working with {@link LocalizedString}s.
     */
    @Inject
    private GlobalizationHelper globalizationHelper;

    private boolean canCreate;

    /**
     * The current folder.
     */
    private Folder folder;

    /**
     * The current content section.
     */
    private ContentSection section;

    /**
     * Messages to be shown to the user.
     */
    private SortedMap<String, String> messages;

    private String name;

    private String title;

    private String initialLocale;

    public AbstractMvcAssetCreateStep() {
        messages = new TreeMap<>();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public Map<String, String> getAvailableLocales() {
        return globalizationHelper
            .getAvailableLocales()
            .stream()
            .collect(
                Collectors.toMap(
                    locale -> locale.toString(),
                    locale -> locale.toString(),
                    (value1, value2) -> value1,
                    () -> new LinkedHashMap<String, String>()
                )
            );
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public ContentSection getContentSection() {
        return section;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void setContentSection(final ContentSection section) {
        this.section = section;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public String getContentSectionLabel() {
        return section.getLabel();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public String getContentSectionTitle() {
        return globalizationHelper.getValueFromLocalizedString(
            section.getTitle()
        );
    }

    @Override
    public boolean getCanCreate() {
        return canCreate;
    }

    @Override
    public Folder getFolder() {
        return folder;
    }

    @Override
    public void setFolder(final Folder folder) {
        this.folder = folder;
    }

    @Override
    public String getFolderPath() {
        if (folder.getParentFolder() == null) {
            return "";
        } else {
            return folderManager.getFolderPath(folder).substring(1);
        }
    }

    @Override
    public Map<String, String> getMessages() {
        return Collections.unmodifiableSortedMap(messages);
    }

    public void addMessage(final String context, final String message) {
        messages.put(context, message);
    }

    public void setMessages(final SortedMap<String, String> messages) {
        this.messages = new TreeMap<>(messages);
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getInitialLocale() {
        return initialLocale;
    }

    @Override
    public String getAssetType() {
        return getAssetClass().getName();
    }

    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public String createAsset(final Map<String, String[]> formParams) {
        if (!formParams.containsKey(FORM_PARAMS_NAME)
                || formParams.get(FORM_PARAMS_NAME) == null
                || formParams.get(FORM_PARAMS_NAME).length == 0) {
            addMessage(
                "danger",
                globalizationHelper
                    .getLocalizedTextsUtil(getBundle())
                    .getText("postaladdress.createstep.name.error.missing")
            );
            return showCreateStep();
        }

        name = formParams.get(FORM_PARAMS_NAME)[0];
        if (!name.matches("^([a-zA-Z0-9_-]*)$")) {
            addMessage(
                "danger",
                globalizationHelper
                    .getLocalizedTextsUtil(getBundle())
                    .getText("createstep.name.error.invalid")
            );
            return showCreateStep();
        }

        if (!formParams.containsKey(FORM_PARAMS_TITLE)
                || formParams.get(FORM_PARAMS_TITLE) == null
                || formParams.get(FORM_PARAMS_TITLE).length == 0) {
            addMessage(
                "danger",
                globalizationHelper
                    .getLocalizedTextsUtil(getBundle())
                    .getText("createstep.title.error.missing")
            );
            return showCreateStep();
        }
        title = formParams.get(FORM_PARAMS_TITLE)[0];

        if (!formParams.containsKey(FORM_PARAM_INITIAL_LOCALE)
                || formParams.get(FORM_PARAM_INITIAL_LOCALE) == null
                || formParams.get(FORM_PARAM_INITIAL_LOCALE).length == 0) {
            addMessage(
                "danger",
                globalizationHelper.getLocalizedTextsUtil(
                    getBundle()
                ).getText("createstep.initial_locale.error.missing")
            );
            return showCreateStep();
        }
        initialLocale = formParams.get(FORM_PARAM_INITIAL_LOCALE)[0];
        final Locale locale = new Locale(initialLocale);

//        final T asset = createAsset(name, title, locale, folder);
        final T asset = assetManager.createAsset(
            name,
            title,
            locale,
            folder,
            getAssetClass()
        );

        return setAssetProperties(asset, formParams);
    }

    protected abstract Class<T> getAssetClass();

    protected abstract String setAssetProperties(
        final T asset, final Map<String, String[]> formParams
    );

}
