/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.ui.authoring.news;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.cms.ui.ItemSearch;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionChecker;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentType;
import org.librecms.contentsection.ContentTypeRepository;

import java.util.List;
import java.util.TooManyListenersException;

import org.librecms.contenttypes.ContentTypeInfo;
import org.librecms.contenttypes.ContentTypesManager;

/**
 * A form element which displays a select box of all content types available
 * under the given content section, and forwards to the item creation UI when
 * the user selects a content type to instantiate.
 *
 * @author Stanislav Freidin (sfreidin@arsdigtia.com)
 * @author <a href="jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class NewItemForm extends Form {

    public static final String TYPE_ID = "tid";

    private final SingleSelect typeSelect;
    private final Submit submit;
    private final Label emptyLabel;
    private final Label createLabel;

    public NewItemForm(final String name) {
        this(name, BoxPanel.HORIZONTAL);
    }

    /**
     * Construct a new NewItemForm. It sets a vertical BoxPanel as the component
     * container.
     *
     * @param name        the name attribute of the form.
     * @param orientation
     */
    public NewItemForm(final String name, final int orientation) {

        super(name, new BoxPanel(BoxPanel.VERTICAL));
        setIdAttr("new_item_form");

        final BoxPanel panel = new BoxPanel(orientation);
        panel.setWidth("2%");
        panel.setBorder(0);

        // create and add an "empty" component
        emptyLabel = new Label(
            new GlobalizedMessage("cms.ui.authoring.no_types_registered",
                                  CmsConstants.CMS_BUNDLE),
            false);
        emptyLabel.setIdAttr("empty_label");
        panel.add(emptyLabel);

        createLabel = new Label(
            new GlobalizedMessage("cms.ui.authoring.create_new",
                                  CmsConstants.CMS_BUNDLE),
            false);
        createLabel.setIdAttr("create_label");
        panel.add(createLabel);

        typeSelect = new SingleSelect(new LongParameter(TYPE_ID),
                                      OptionGroup.SortMode.ALPHABETICAL_ASCENDING);
        try {
            typeSelect.addPrintListener(new PrintListener() {

                // Read the content section's content types and add them as options
                @Override
                public void prepare(final PrintEvent event) {
                    final OptionGroup target = (OptionGroup) event
                        .getTarget();
                    target.clearOptions();
                    final PageState state = event.getPageState();

                    // gather the content types of this section into a list
                    final ContentSection section = getContentSection(state);
//                    final ContentType parentType;
                    final List<ContentType> typesCollection;
                    final Long singleTypeID = (Long) state.getValue(
                        new LongParameter(ItemSearch.SINGLE_TYPE_PARAM));

                    final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                    final NewItemFormController controller = cdiUtil.findBean(
                        NewItemFormController.class);
                    final ContentTypeRepository typeRepo = cdiUtil.findBean(
                        ContentTypeRepository.class);
                    final ContentTypesManager typesManager = cdiUtil.findBean(
                        ContentTypesManager.class);
                    final PermissionChecker permissionChecker = cdiUtil
                        .findBean(PermissionChecker.class);

                    typesCollection = controller.getContentTypes(section);

                    for (final ContentType type : typesCollection) {
                        final ContentTypeInfo typeInfo = typesManager
                            .getContentTypeInfo(type.getContentItemClass());
                        final String value = Long.toString(type.getObjectId());
                        final Label label = new Label(
                            new GlobalizedMessage(typeInfo.getLabelKey(),
                                                  typeInfo.getLabelBundle()));
                        target.addOption(
                            new Option(value, label));
                    }
                }

            });
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException("Too many listeners.", ex);
        }

        panel.add(typeSelect);

        submit = new Submit("new",
                            new GlobalizedMessage("cms.ui.authoring.go",
                                                  CmsConstants.CMS_BUNDLE));
        panel.add(submit);

        add(panel);
    }

    public abstract ContentSection getContentSection(PageState state);

    /**
     *
     * @param state
     *
     * @return
     */
    public Long getTypeID(final PageState state) {
        return (Long) typeSelect.getValue(state);
    }

    /**
     *
     * @return
     */
    public final SingleSelect getTypeSelect() {
        return typeSelect;
    }

    /**
     * Generate XML - show/hide labels/widgets
     *
     * @param state
     * @param parent
     */
    @Override
    public void generateXML(final PageState state, final Element parent) {

        if (isVisible(state)) {
            final ContentSection section = getContentSection(state);

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final NewItemFormController controller = cdiUtil.findBean(
                NewItemFormController.class);
            boolean isEmpty = !controller.hasContentTypes(section);

            createLabel.setVisible(state, !isEmpty);
            typeSelect.setVisible(state, !isEmpty);
            submit.setVisible(state, !isEmpty);
            emptyLabel.setVisible(state, isEmpty);

            super.generateXML(state, parent);
        }
    }

}
