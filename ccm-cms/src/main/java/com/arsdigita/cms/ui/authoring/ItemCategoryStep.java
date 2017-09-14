/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.web.RedirectSignal;

import com.arsdigita.cms.CMS;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.util.Classes;

import org.arsdigita.cms.CMSConfig;
import org.libreccm.core.UnexpectedErrorException;
import org.librecms.CmsConstants;
import org.librecms.ui.authoring.ContentItemAuthoringStep;

import java.math.BigDecimal;

@ContentItemAuthoringStep(
    labelBundle = CmsConstants.CMS_BUNDLE,
    labelKey = "item_category_step.label",
    descriptionBundle = CmsConstants.CMS_BUNDLE,
    descriptionKey = "item_category_step.description"
)
public class ItemCategoryStep extends SimpleContainer implements Resettable {

    private final LongParameter rootParameter;
    private final StringParameter modeParameter;

    private final ItemCategorySummary itemCategorySummary;

    private final SimpleComponent addComponent;

    private final SimpleComponent[] extensionSummaries;
    private final SimpleComponent[] extensionForms;
    private int extensionsCount;

    public ItemCategoryStep(final ItemSelectionModel itemSelectionModel,
                            final AuthoringKitWizard authoringKitWizard,
                            final StringParameter selectedLanguage) {

        super("cms:categoryStep", CMS.CMS_XML_NS);

        rootParameter = new LongParameter("root");
        modeParameter = new StringParameter("mode");

        itemCategorySummary = new ItemCategorySummary();
        itemCategorySummary.registerAction(ItemCategorySummary.ACTION_ADD,
                                           new AddActionListener("plain"));
        itemCategorySummary.registerAction(ItemCategorySummary.ACTION_ADD_JS,
                                           new AddActionListener("javascript"));

        final String addFormClassName = CMSConfig
            .getConfig()
            .getCategoryAuthoringAddForm();
        final Class<?> addFormClass;
        try {
            addFormClass = Class.forName(addFormClassName);
        } catch (ClassNotFoundException ex) {
            throw new UnexpectedErrorException(ex);
        }
        addComponent = (SimpleComponent) Classes
            .newInstance(addFormClass,
                         new Class<?>[]{LongParameter.class,
                                        StringParameter.class},
                         new Object[]{rootParameter, modeParameter});
        addComponent.addCompletionListener(new ResetListener());

        final String extensionClassName = CMSConfig
            .getConfig()
            .getCategoryAuthoringExtension();
        final Class<?> extensionClass;
        try {
            extensionClass = Class.forName(extensionClassName);
        } catch (ClassNotFoundException ex) {
            throw new UnexpectedErrorException(ex);
        }
        final ItemCategoryExtension extension = (ItemCategoryExtension) Classes
            .newInstance(extensionClass);

        extensionSummaries = extension.getSummary();
        extensionForms = extension.getForm();
        int nSummaries = extensionSummaries.length;
        int nForms = extensionForms.length;
        if (nSummaries != nForms) {
            throw new UnexpectedErrorException(
                "Invalid category step extension.");
        }
        extensionsCount = nForms;
        for (int i = 0; i < extensionsCount; i++) {
            extensionSummaries[i]
                .addCompletionListener(new ExtensionListener(i));
            extensionForms[i].addCompletionListener(new ResetListener());
            super.add(extensionSummaries[i]);
            super.add(extensionForms[i]);
        }
        super.add(itemCategorySummary);
        super.add(addComponent);
    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.setVisibleDefault(addComponent, false);
        for (int i = 0; i < extensionsCount; i++) {
            page.setVisibleDefault(extensionForms[i], false);
        }
        page.addGlobalStateParam(rootParameter);
        page.addGlobalStateParam(modeParameter);
    }

    @Override
    public void reset(final PageState state) {
        state.setValue(rootParameter, null);
        state.setValue(modeParameter, null);

        itemCategorySummary.setVisible(state, true);
        addComponent.setVisible(state, false);
        for (int i = 0; i < extensionsCount; i++) {
            extensionSummaries[i].setVisible(state, true);
            extensionForms[i].setVisible(state, false);
        }
    }

    private class AddActionListener implements ActionListener {

        private final String mode;

        public AddActionListener(final String mode) {
            this.mode = mode;
        }

        @Override
        public void actionPerformed(final ActionEvent event) {
            
            final PageState state = event.getPageState();

            state.setValue(rootParameter,
                           new BigDecimal(state.getControlEventValue()));

            state.setValue(ItemCategoryStep.this.modeParameter,
                           mode);

            itemCategorySummary.setVisible(state, false);
            addComponent.setVisible(state, true);
            for (int i=0;i<extensionsCount;i++) {
                extensionSummaries[i].setVisible(state, false);
                extensionForms[i].setVisible(state, false);
            }
        }

    }

    private class ResetListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            final PageState state = e.getPageState();
            reset(state);
            throw new RedirectSignal(state.toURL(), true);
        }

    }

    private class ExtensionListener implements ActionListener {

        private final int extensionIndex;

        public ExtensionListener(int extensionIndex) {
            this.extensionIndex = extensionIndex;
        }

        @Override
        public void actionPerformed(final ActionEvent event) {
            
            final PageState state = event.getPageState();
            itemCategorySummary.setVisible(state, false);
            addComponent.setVisible(state, false);
            for (int i=0;i<extensionsCount;i++) {
                extensionSummaries[i].setVisible(state, false);
            }
            extensionForms[extensionIndex].setVisible(state, true);
        }

    }

}
