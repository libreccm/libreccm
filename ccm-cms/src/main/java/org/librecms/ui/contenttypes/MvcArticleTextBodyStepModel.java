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
package org.librecms.ui.contenttypes;

import org.librecms.ui.contentsections.documents.CmsEditorLocaleVariantRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsArticleTextBodyStep")
public class MvcArticleTextBodyStepModel {
    
    private boolean canEdit;
    
    private Map<String, String> textValues;

    private List<CmsEditorLocaleVariantRow> variants;

    private List<String> unusedLocales;

    private String selectedLocale;

    /**
     * Get all localized values of the main text.
     *
     * @return The localized values of the main text.
     */
    public Map<String, String> getTextValues() {
        return Collections.unmodifiableMap(textValues);
    }

    protected void setTextValues(final Map<String, String> textValues) {
        this.textValues = new HashMap<>(textValues);
    }

       /**
     * Gets the locales for which the main text has not been defined yet.
     *
     * @return The locales for which the main text has not been defined yet.
     */
    public List<CmsEditorLocaleVariantRow> getVariants() {
        return Collections.unmodifiableList(variants);
    }

    protected void setVariants(final List<CmsEditorLocaleVariantRow> variants) {
        this.variants = new ArrayList<>(variants);
    }

    public List<String> getUnusedLocales() {
        return Collections.unmodifiableList(unusedLocales);
    }

    protected void setUnusedLocales(final List<String> unusedLocales) {
        this.unusedLocales = new ArrayList<>(unusedLocales);
    }

    public String getSelectedLocale() {
        return selectedLocale;
    }

    protected void setSelectedLocale(final String selectedLocale) {
        this.selectedLocale = selectedLocale;
    }

    public boolean getCanEdit() {
        return canEdit;
    }

    protected void setCanEdit(final boolean canEdit) {
        this.canEdit = canEdit;
    }
    
    
    
}
