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
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ui.BaseDeleteForm;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.SelectionPanel;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

/**
 * A pane that contains a folder tree on the left and a folder
 * manipulator on the right.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: FolderAdminPane.java 1942 2009-05-29 07:53:23Z terry $
 */
public class FolderAdminPane extends SelectionPanel {

    private static final Logger s_log = Logger.getLogger
        (FolderAdminPane.class);

    private final FolderRequestLocal m_folder;

    private final BigDecimalParameter m_param;

    public static final String FOLDER_PARAMETER = "folder";

    public FolderAdminPane() {
        super(new Label(gz("cms.ui.folder.folders")),
              new FolderTreeModelBuilder());

        m_folder = new FolderRequestLocal(getSelectionModel());
        m_param = new BigDecimalParameter(FOLDER_PARAMETER);

        setAdd(new ActionLink(new Label(gz("cms.ui.folder.add"))),
               new FolderAddForm(getSelectionModel(), m_folder));

        setEdit(new ActionLink(new Label(gz("cms.ui.folder.edit"))),
                new FolderEditForm(m_folder));

        final BaseDeleteForm delete = new BaseDeleteForm
            (new Label(gz("cms.ui.folder.delete_prompt"))) {
                public final void process(final FormSectionEvent e)
                        throws FormProcessException {
                    final PageState state = e.getPageState();

                    m_folder.getFolder(state).delete();
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

    public void register(final Page page) {
        super.register(page);

        page.addGlobalStateParam(m_param);
        page.addActionListener(new PreselectListener());
    }

    private class PreselectListener implements ActionListener {
        public final void actionPerformed(final ActionEvent e) {
            final PageState state = e.getPageState();
            final SingleSelectionModel model = getSelectionModel();

            if (!model.isSelected(state)) {
                final BigDecimal value = (BigDecimal) state.getValue(m_param);

                if (value == null) {
                    final ContentSection section =
                        CMS.getContext().getContentSection();

                    model.setSelectedKey
                        (state, section.getRootFolder().getID());
                } else {
                    model.setSelectedKey(state, value);
                }
            }
        }
    }

    private static GlobalizedMessage gz(final String key) {
        return GlobalizationUtil.globalize(key);
    }

    private static String lz(final String key) {
        return (String) gz(key).localize();
    }
}
