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
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contenttypes.ContentTypeInfo;
import org.librecms.contenttypes.ContentTypesManager;

import java.util.ResourceBundle;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ItemSearchWidget extends Widget {

    private Class<? extends ContentItem> type;

    public ItemSearchWidget(final String name) {
        super(new LongParameter(name));
    }

    @Override
    public boolean isCompound() {
        return true;
    }

    @Override
    protected String getType() {
        return "item-search-widget";
    }

    @Override
    protected String getElementTag() {
        return "cms:item-search-widget";
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
            final ContentItemRepository itemRepo = cdiUtil
                .findBean(ContentItemRepository.class);
            final ContentTypesManager typesManager = cdiUtil
                .findBean(ContentTypesManager.class);
            final GlobalizationHelper globalizationHelper = cdiUtil
                .findBean(GlobalizationHelper.class);

            final ContentItem item = itemRepo
                .findById(value)
                .orElseThrow(() -> new IllegalArgumentException(String
                .format("No ContentItem with ID %d in the database.", value)));
            
            final Element selected = widget
                .newChildElement("cms:selected-content-item", CMS.CMS_XML_NS);
            selected.addAttribute("contentItemId", 
                                  Long.toString(item.getObjectId()));
            selected.addAttribute("name", item.getDisplayName());
            selected.addAttribute(
                "title", 
                globalizationHelper.getValueFromLocalizedString(item.getTitle()));
            final ContentTypeInfo typeInfo = typesManager
            .getContentTypeInfo(item.getClass());
            final ResourceBundle bundle = ResourceBundle
                .getBundle(typeInfo.getLabelBundle(),
                           globalizationHelper.getNegotiatedLocale());
            final String typeLabel = bundle.getString(typeInfo.getLabelKey());
            selected.addAttribute("type", typeLabel);
            
            exportAttributes(widget);
            
        }

    }

}
