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
package com.arsdigita.cms.ui.pagemodel;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ui.assets.ItemSearchWidget;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.ui.admin.pagemodels.PageModelsTab;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.pagemodel.FixedContentItemComponent;

/**
 * Form for creating/editing a {@link FixedContentItemComponent}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class FixedContentItemComponentForm
    extends AbstractContentItemComponentForm<FixedContentItemComponent> {

    private final static String ITEM_SEARCH = "itemSearch";

    private ItemSearchWidget itemSearchWidget;

    public FixedContentItemComponentForm(
        final PageModelsTab pageModelTab,
        final ParameterSingleSelectionModel<String> selectedModelId,
        final ParameterSingleSelectionModel<String> selectedComponentId) {

        super("FixedContentItemComponentForm",
              pageModelTab,
              selectedModelId,
              selectedComponentId);
    }

    @Override
    protected void addWidgets() {

        itemSearchWidget = new ItemSearchWidget(ITEM_SEARCH);
        itemSearchWidget.setLabel(new GlobalizedMessage(
            "cms.ui.pagemodel.fixed_contentitem_component_form.itemsearch.label",
            CmsConstants.CMS_BUNDLE));
        add(itemSearchWidget);
    }

    @Override
    protected FixedContentItemComponent createComponentModel() {
        return new FixedContentItemComponent();
    }

    @Override
    protected void updateComponentModel(
        final FixedContentItemComponent component,
        final PageState state,
        final FormData data) {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ContentItemRepository itemRepo = cdiUtil
            .findBean(ContentItemRepository.class);

        final long itemId = (long) itemSearchWidget.getValue(state);

        final ContentItem item = itemRepo
            .findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException());

        component.setContentItem(item);
    }

    @Override
    public void init(final FormSectionEvent event)
        throws FormProcessException {

        super.init(event);

        final PageState state = event.getPageState();
        final FixedContentItemComponent component = getComponentModel();

        if (component != null) {
            itemSearchWidget.setValue(state, component.getContentItem());
        }
    }

    @Override
    public void validate(final FormSectionEvent event)
        throws FormProcessException {

        super.validate(event);

        final FormData data = event.getFormData();
        final Object value = data.get(ITEM_SEARCH);

        if (value == null) {
            data.addError(new GlobalizedMessage(
                "cms.ui.pagemodel.fixed_contentitem_component_form.error.no_item_selected",
                CmsConstants.CMS_BUNDLE));
        }
    }

}
