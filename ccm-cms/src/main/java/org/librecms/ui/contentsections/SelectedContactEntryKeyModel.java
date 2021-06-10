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
package org.librecms.ui.contentsections;

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
@Named("CmsSelectedContactEntryKeyModel")
public class SelectedContactEntryKeyModel {
    
    private long entryId;
    
    private String key;
    
    private List<String> unusedLabelLocales;
    
    private Map<String, String> labels;

    public long getEntryId() {
        return entryId;
    }

    protected void setEntryId(long entryId) {
        this.entryId = entryId;
    }

    public String getKey() {
        return key;
    }

    protected void setKey(String key) {
        this.key = key;
    }

    public List<String> getUnusedLabelLocales() {
        return Collections.unmodifiableList(unusedLabelLocales);
    }

    protected void setUnusedLabelLocales(final List<String> unusedLabelLocales) {
        this.unusedLabelLocales = new ArrayList<>(unusedLabelLocales);
    }
    
    public boolean getHasUnusedLabelLocales() {
        return !unusedLabelLocales.isEmpty();
    }

    public Map<String, String> getLabels() {
        return Collections.unmodifiableMap(labels);
    }

    protected void setLabels(final Map<String, String> labels) {
        this.labels = new HashMap<>(labels);
    }
    
    
    
}
