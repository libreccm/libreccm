/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.folder;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.CMS;

import org.librecms.contentsection.ContentSection;

import com.arsdigita.cms.ui.BaseDeleteForm;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.SelectionPanel;

import org.libreccm.categorization.CategoryRepository;
import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.contentsection.Folder;


/**
 * A pane that contains a folder tree on the left and a folder manipulator on
 * the right.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 */
public class FolderAdminPane extends SelectionPanel {

    private final FolderRequestLocal m_folder;

    private final BigDecimalParameter m_param;

    public static final String FOLDER_PARAMETER = "folder";

    public FolderAdminPane() {
        super(new Label(gz("cms.ui.folder.folders")),
              new FolderTreeModelBuilder() {

            @Override
            protected Folder getRootFolder(final PageState state) {
                final ContentSection section = CMS
                .getContext()
                .getContentSection();
                
                return section.getRootDocumentsFolder();
            }
                  
              });

        m_folder = new FolderRequestLocal(getSelectionModel());
        m_param = new BigDecimalParameter(FOLDER_PARAMETER);

        setAdd(new ActionLink(new Label(gz("cms.ui.folder.add"))),
               new FolderAddForm(getSelectionModel(), m_folder));

        setEdit(new ActionLink(new Label(gz("cms.ui.folder.edit"))),
                new FolderEditForm(m_folder));

        final BaseDeleteForm delete = new BaseDeleteForm(new Label(gz(
            "cms.ui.folder.delete_prompt"))) {

            @Override
            public final void process(final FormSectionEvent e)
                throws FormProcessException {
                final PageState state = e.getPageState();

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final CategoryRepository categoryRepo = cdiUtil.findBean(
                    CategoryRepository.class);

                categoryRepo.delete(m_folder.getFolder(state));
                getSelectionModel().clearSelection(state);
            }

        };

        setDelete(new ActionLink(new Label(gz("cms.ui.folder.delete"))),
                  delete);

        setIntroPane(new Label(gz("cms.ui.folder.intro")));
        setItemPane(new Label("item XXX"));

        addAction(getAddLink());
        addAction(getEditLink());
        addAction(getDeleteLink());
    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.addGlobalStateParam(m_param);
        page.addActionListener(new PreselectListener());
    }

    private class PreselectListener implements ActionListener {

        @Override
        public final void actionPerformed(final ActionEvent e) {
            final PageState state = e.getPageState();
            final SingleSelectionModel<Long> model = getSelectionModel();

            if (!model.isSelected(state)) {
                final Long value = (Long) state.getValue(m_param);

                if (value == null) {
                    final ContentSection section = CMS.getContext()
                        .getContentSection();

                    model.setSelectedKey(state, section.getRootDocumentsFolder()
                                         .getObjectId());
                } else {
                    model.setSelectedKey(state, value);
                }
            }
        }

    }

    private static GlobalizedMessage gz(final String key) {
        return new GlobalizedMessage(key, CmsConstants.CMS_FOLDER_BUNDLE);
    }

    private static String lz(final String key) {
        return (String) gz(key).localize();
    }

}
