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
package com.arsdigita.cms.ui.assets;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.cms.CMS;
import com.arsdigita.xml.Element;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.assets.AssetTypeInfo;
import org.librecms.assets.AssetTypesManager;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetRepository;
import org.librecms.contentsection.ContentSection;

import java.util.Map;
import java.util.ResourceBundle;

/**
 * A widget for selecting an asset. The widget does not contain any other
 * widgets, only the information required to create an HTML/JavaScript dialog
 * for selecting an asset. To create the dialog the
 * {@link org.librecms.contentsection.rs.Assets} class can be used which
 * provides several methods for getting the assets of an content section.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class AssetSearchWidget extends Widget {

    private Class<? extends Asset> type;

    public AssetSearchWidget(final String name) {
        super(new LongParameter(name));
    }

    public AssetSearchWidget(final String name,
                             final Class<? extends Asset> type) {
        this(name);
        this.type = type;
    }

    @Override
    public boolean isCompound() {
        return true;
    }

    @Override
    protected String getType() {
        return "asset-search-widget";
    }

    @Override
    protected String getElementTag() {
        return "cms:asset-search-widget";
    }

    @Override
    public void generateWidget(final PageState state,
                               final Element parent) {

        final Element widget = parent.newChildElement(getElementTag(),
                                                      CMS.CMS_XML_NS);

        widget.addAttribute("name", getName());

        if (type != null) {
            widget.addAttribute("asset-type", type.getName());
        }

        final ContentSection section = CMS.getContext().getContentSection();
        widget.addAttribute("content-section", section.getLabel());

        final Long value = (Long) getValue(state);
        if (value != null) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final AssetSearchWidgetController controller = cdiUtil
                .findBean(AssetSearchWidgetController.class);

            final Map<String, String> data = controller.getData(value);

            final Element selected = widget
                .newChildElement("cms:selected-asset", CMS.CMS_XML_NS);
            selected.addAttribute(
                "assetId", data.get(AssetSearchWidgetController.OBJECT_ID)
            );
            selected.addAttribute("title",
                                  data.get(AssetSearchWidgetController.TITLE));
            selected.addAttribute("type",
                                  data.get(AssetSearchWidgetController.TYPE));

            exportAttributes(widget);
        }
    }

}
