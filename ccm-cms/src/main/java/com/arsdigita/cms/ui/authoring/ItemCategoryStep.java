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

import java.math.BigDecimal;


/**
 * 
 * 
 */
public class ItemCategoryStep extends SimpleContainer  implements Resettable{
    

    private final LongParameter rootParameter;
    private final StringParameter modeParameter;
    
    private final ItemCategorySummary itemCategorySummary;
    
    private final SimpleComponent addComponent;
    
//    private SimpleComponent[] extensionSummaries;
//    private SimpleComponent[] extensionForms;
//    private int extensionsCount;
    

    public ItemCategoryStep(final ItemSelectionModel itemSelectionModel, 
                            final AuthoringKitWizard authoringKitWizard) {
        
        super("cms:categoryStep", CMS.CMS_XML_NS);

        rootParameter = new LongParameter("root");
        modeParameter = new StringParameter("mode");

        itemCategorySummary = new ItemCategorySummary();
        itemCategorySummary.registerAction(ItemCategorySummary.ACTION_ADD,
                new AddActionListener("plain"));
        itemCategorySummary.registerAction(ItemCategorySummary.ACTION_ADD_JS,
                new AddActionListener("javascript"));

//        Class addForm = CMSConfig.getConfig().getCategoryAuthoringAddForm();
//        addComponent = (SimpleComponent)
//            Classes.newInstance(addForm,
//                                new Class[] { BigDecimalParameter.class,
//                                              StringParameter.class },
//                                new Object[] { rootParameter, modeParameter });
        addComponent = new ItemCategoryForm(rootParameter, modeParameter);
        addComponent.addCompletionListener(new ResetListener());

//        Class extensionClass = ContentSection.getConfig().getCategoryAuthoringExtension();
//        ItemCategoryExtension extension = (ItemCategoryExtension)
//            Classes.newInstance(extensionClass);
//        
//        extensionSummaries = extension.getSummary();
//        extensionForms = extension.getForm();
//        int nSummaries = extensionSummaries.length;
//        int nForms= extensionForms.length;
//        Assert.isTrue(nSummaries==nForms, "invalid CategoryStep extension");
//        extensionsCount = nForms;
//        for (int i=0;i<extensionsCount;i++) {
//            extensionSummaries[i].addCompletionListener(new ExtensionListener(i));
//            extensionForms[i].addCompletionListener(new ResetListener());
//            add(extensionSummaries[i]);
//            add(extensionForms[i]);
//        }
        add(itemCategorySummary);
        add(addComponent);
    }

    @Override
    public void register(Page p) {
        super.register(p);
        
        p.setVisibleDefault(addComponent, false);
//        for (int i=0;i<extensionsCount;i++) {
//            p.setVisibleDefault(extensionForms[i], false);    
//        }
        p.addGlobalStateParam(rootParameter);
        p.addGlobalStateParam(modeParameter);
    }

    @Override
    public void reset(PageState state) {
        state.setValue(rootParameter, null);
        state.setValue(modeParameter, null);
        
        itemCategorySummary.setVisible(state, true);
        addComponent.setVisible(state, false);
//        for (int i=0;i<extensionsCount;i++) {
//            extensionSummaries[i].setVisible(state, true);
//            extensionForms[i].setVisible(state, false);
//        }
    }

    private class AddActionListener implements ActionListener {
        private String m_mode;

        public AddActionListener(String mode) {
            m_mode = mode;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();
            
            state.setValue(rootParameter,
                           new BigDecimal(state.getControlEventValue()));

            state.setValue(ItemCategoryStep.this.modeParameter,
                           m_mode);

            itemCategorySummary.setVisible(state, false);
            addComponent.setVisible(state, true);
//            for (int i=0;i<extensionsCount;i++) {
//                extensionSummaries[i].setVisible(state, false);
//                extensionForms[i].setVisible(state, false);
//            }
        }
    }

    private class ResetListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();
            reset(state);
            throw new RedirectSignal(state.toURL(), true);
        }
    }

    private class ExtensionListener implements ActionListener {
        int extensionIndex;
        public ExtensionListener(int i) {
            extensionIndex = i;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();
            itemCategorySummary.setVisible(state, false);
            addComponent.setVisible(state, false);
//            for (int i=0;i<extensionsCount;i++) {
//                extensionSummaries[i].setVisible(state, false);
//            }
//            extensionForms[extensionIndex].setVisible(state, true);
        }
    }

}
