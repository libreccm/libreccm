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
@Named("CmsSideNoteEditStep")
public class SideNoteEditStepModel {

    private Map<String, String> textValues;

    private List<CmsEditorLocaleVariantRow> variants;

    private List<String> unusedTextLocales;

    public Map<String, String> getTextValues() {
        return Collections.unmodifiableMap(textValues);
    }

    protected void setTextValues(final Map<String, String> textValues) {
        this.textValues = new HashMap<>(textValues);
    }

    public List<CmsEditorLocaleVariantRow> getVariants() {
        return Collections.unmodifiableList(variants);
    }

    protected void setVariants(final List<CmsEditorLocaleVariantRow> variants) {
        this.variants = new ArrayList<>(variants);
    }

    public List<String> getUnusedTextLocales() {
        return Collections.unmodifiableList(unusedTextLocales);
    }

    protected void setUnusedTextLocales(final List<String> unusedTextLocales) {
        this.unusedTextLocales = new ArrayList<>(unusedTextLocales);
    }

}
