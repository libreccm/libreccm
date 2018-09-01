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
package com.arsdigita.bebop;

import com.arsdigita.bebop.page.PageTransformer;
import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.ui.SimplePage;
import com.arsdigita.util.UncheckedWrapperException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.Configuration;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.configuration.Setting;

import java.util.stream.Collectors;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Configuration(
    descKey = "bebop.config.description")
public final class BebopConfig {

    @Setting
    private String presenterClassName = PageTransformer.class.getName();

    @Setting
    private String basePageClassName = SimplePage.class.getName();

    @Setting
    private String tidyConfigFile
                       = "com/arsdigita/bebop/parameters/tidy.properties";

    @Setting
    private Boolean fancyErrors = false;

    @Setting
    private Boolean dcpOnButtons = true;

    @Setting
    private Boolean dcpOnLinks = false;

    @Setting
    private Boolean treeSelectEnabled = false;

    @Setting
    private Set<String> dhtmlEditors = new HashSet<>(
        Arrays.asList(new String[]{BebopConstants.BEBOP_XINHAEDITOR,
                                   BebopConstants.BEBOP_FCKEDITOR,
                                   BebopConstants.BEBOP_DHTMLEDITOR,
                                   BebopConstants.BEBOP_CCMEDITOR,
                                   BebopConstants.BEBOP_TINYMCE_EDITOR}));

    @Setting
    private String defaultDhtmlEditor = BebopConstants.BEBOP_TINYMCE_EDITOR;

    @Setting
//    private String dhtmlEditorSrcFile = "/ccm-editor/ccm-editor-loader.js";
    private String dhtmlEditorSrcFile = "/webjars/tinymce/4.8.2/tinymce.js";

    @Setting
    private Boolean showClassName = false;

    public static BebopConfig getConfig() {
        final ConfigurationManager confManager = CdiUtil.createCdiUtil()
            .findBean(ConfigurationManager.class);
        return confManager.findConfiguration(BebopConfig.class);
    }

    public String getPresenterClassName() {
        return presenterClassName;
    }

    @SuppressWarnings("unchecked")
    public Class<PresentationManager> getPresenterClass() {
        try {
            return (Class<PresentationManager>) Class.
                forName(presenterClassName);
        } catch (ClassNotFoundException ex) {
            throw new UncheckedWrapperException(ex);
        }
    }

    public void setPresenterClassName(final String presenterClassName) {
        this.presenterClassName = presenterClassName;
    }

    public void setPresenterClass(
        final Class<PresentationManager> presenterClass) {
        setPresenterClassName(presenterClass.getName());
    }

    public String getBasePageClassName() {
        return basePageClassName;
    }

    @SuppressWarnings("unchecked")
    public Class<BasePage> getBasePageClass() {
        try {
            return (Class<BasePage>) Class.forName(basePageClassName);
        } catch (ClassNotFoundException ex) {
            throw new UncheckedWrapperException(ex);
        }
    }

    public void setBasePageClassName(final String basePageClassName) {
        this.basePageClassName = basePageClassName;
    }

    public void setBasePageClass(final Class<BasePage> basePageClass) {
        setBasePageClassName(basePageClass.getName());
    }

    public String getTidyConfigFile() {
        return tidyConfigFile;
    }

    public void setTidyConfigFile(final String tidyConfigFile) {
        this.tidyConfigFile = tidyConfigFile;
    }

    public Boolean getFancyErrors() {
        return fancyErrors;
    }

    public void setFancyErrors(final Boolean fancyErrors) {
        this.fancyErrors = fancyErrors;
    }

    public Boolean getDcpOnButtons() {
        return dcpOnButtons;
    }

    public void setDcpOnButtons(final Boolean dcpOnButtons) {
        this.dcpOnButtons = dcpOnButtons;
    }

    public Boolean getDcpOnLinks() {
        return dcpOnLinks;
    }

    public void setDcpOnLinks(final Boolean dcpOnLinks) {
        this.dcpOnLinks = dcpOnLinks;
    }

    public Boolean isTreeSelectEnabled() {
        return treeSelectEnabled;
    }

    public void setTreeSelectEnabled(final Boolean treeSelectEnabled) {
        this.treeSelectEnabled = treeSelectEnabled;
    }

    public Set<String> getDhtmlEditors() {
        return new HashSet<>(dhtmlEditors);
    }

    public void setDhtmlEditors(final Set<String> dhtmlEditors) {
        this.dhtmlEditors = dhtmlEditors;
    }

    public String getDefaultDhtmlEditor() {
        return defaultDhtmlEditor;
    }

    public void setDefaultDhtmlEditor(final String defaultDhtmlEditor) {
        this.defaultDhtmlEditor = defaultDhtmlEditor;
    }

    public String getDhtmlEditorSrcFile() {
        return dhtmlEditorSrcFile;
    }

    public void setDhtmlEditorSrcFile(final String dhtmlEditorSrcFile) {
        this.dhtmlEditorSrcFile = dhtmlEditorSrcFile;
    }

    public Boolean getShowClassName() {
        return showClassName;
    }

    public void setShowClassName(final Boolean showClassName) {
        this.showClassName = showClassName;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(tidyConfigFile);
        hash = 89 * hash + Objects.hashCode(fancyErrors);
        hash = 89 * hash + Objects.hashCode(dcpOnButtons);
        hash = 89 * hash + Objects.hashCode(dcpOnLinks);
        hash = 89 * hash + Objects.hashCode(treeSelectEnabled);
        hash = 89 * hash + Objects.hashCode(dhtmlEditors);
        hash = 89 * hash + Objects.hashCode(defaultDhtmlEditor);
        hash = 89 * hash + Objects.hashCode(dhtmlEditorSrcFile);
        hash = 89 * hash + Objects.hashCode(showClassName);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof BebopConfig)) {
            return false;
        }
        final BebopConfig other = (BebopConfig) obj;
        if (!Objects.equals(tidyConfigFile, other.getTidyConfigFile())) {
            return false;
        }
        if (!Objects.equals(defaultDhtmlEditor, other.getDefaultDhtmlEditor())) {
            return false;
        }
        if (!Objects.equals(dhtmlEditorSrcFile, other.getDhtmlEditorSrcFile())) {
            return false;
        }
        if (!Objects.equals(fancyErrors, other.getFancyErrors())) {
            return false;
        }
        if (!Objects.equals(dcpOnButtons, other.getDcpOnButtons())) {
            return false;
        }
        if (!Objects.equals(dcpOnLinks, other.getDcpOnLinks())) {
            return false;
        }
        if (!Objects.equals(treeSelectEnabled, other.isTreeSelectEnabled())) {
            return false;
        }
        if (!Objects.equals(dhtmlEditors, other.getDhtmlEditors())) {
            return false;
        }
        return Objects.equals(showClassName, other.getShowClassName());
    }

    @Override
    public String toString() {
        return String.format(
            "%s{ "
                + "tidyConfigFile = %s, "
                + "fancyErrors = %b, "
                + "dcpOnButtons = %b, "
                + "dcpOnLinks = %b, "
                + "treeSelectEnabled = %b, "
                + "dhtmlEditors = { %s }, "
                + "defaultDhtmlEditor = %s, "
                + "dhtmlEditorSrcFile = %s, "
                + "showClassName = %b"
                + " }",
            super.toString(),
            tidyConfigFile,
            fancyErrors,
            dcpOnButtons,
            dcpOnLinks,
            treeSelectEnabled,
            dhtmlEditors.stream().collect(Collectors.joining(", ")),
            defaultDhtmlEditor,
            dhtmlEditorSrcFile,
            showClassName);
    }

}
