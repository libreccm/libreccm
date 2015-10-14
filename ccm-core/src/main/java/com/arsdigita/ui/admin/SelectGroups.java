/*
 * Copyright (C) 2006 Chris Gilbert. All Rights Reserved.
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
package com.arsdigita.ui.admin;

import java.math.BigDecimal;
import java.util.TooManyListenersException;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.Submit;

import org.libreccm.core.Group;

import java.util.List;

import static com.arsdigita.ui.admin.AdminConstants.*;

/**
 * @author cgyg9330
 *
 * contains a form that allows the user to select desired groups to be added as
 * subgroups to the currently selected group
 *
 * NB could be improved with cancel button - currently need to use browser back
 * button
 */
public class SelectGroups {

    private static final Logger s_log = Logger.getLogger(SelectGroups.class);
    private static final String GROUPS_CBG = "groups_cbg";
    private BoxPanel groupPanel;
    private CheckboxGroup groups;
    private Form form;
    private Submit save;
    private ExistingGroupAddPane parentPane;
    private GroupSearchForm searchForm;

    public SelectGroups(ExistingGroupAddPane parent, GroupSearchForm searchForm) {
        parentPane = parent;
        makeForm();
        groupPanel = new BoxPanel();
        groupPanel.add(form);
        this.searchForm = searchForm;
    }

    /**
     * Builds the form used to select groups.
     */
    private void makeForm() {
        form = new Form("ChooseGroups", new BoxPanel());
        form.setMethod(Form.POST);
        form.addSubmissionListener(new AddGroupsSubmissionListener());
        form.add(PICK_GROUPS);
        groups = new CheckboxGroup(GROUPS_CBG);
        groups.setClassAttr("vertical");

        try {
            groups.addPrintListener(new GroupSearchPrintListener());
        } catch (TooManyListenersException e) {
            throw new RuntimeException(e.getMessage());
        }
        form.add(groups);

        save = new Submit("save", SAVE_BUTTON);
        form.add(save);
    }

    public BoxPanel getPanel() {
        return groupPanel;
    }

    /**
     *
     * FormSubmissionListener that sets selected groups to be subgroups of the
     * currently selected group
     *
     */
    private class AddGroupsSubmissionListener
        implements FormSubmissionListener {

        public void submitted(FormSectionEvent e) throws FormProcessException {
            PageState state = e.getPageState();
            FormData data = e.getFormData();
            String[] selectedGroups = (String[]) data.get(GROUPS_CBG);
            BigDecimal groupID = null;
            Group child = null;
//            if (selectedGroups != null) {
//                Group parent = parentPane.getParentGroup(state);
//                for (int i = 0; i < selectedGroups.length; i++) {
//                    groupID = new BigDecimal(selectedGroups[i]);
//                    try {
//                        child = new Group(groupID);
//                    } catch (DataObjectNotFoundException e2) {
//                        s_log.warn("Non existant Group " + child
//                                   + " selected to be child of " + parent);
//                    }
//                    parent.addSubgroup(child);
//                    parent.save();
//                }
//            }

            parentPane.showSearch(state);
            parentPane.getParentPage().displayGroupInfoPanel(state);

        }

    }

    /**
     *
     * @author cgyg9330
     *
     * Printlistener retrieves query results from the groupsearch form and uses
     * them as the entries on the checkbox group
     */
    private class GroupSearchPrintListener implements PrintListener {

        public void prepare(PrintEvent e) {
            PageState state = e.getPageState();
            OptionGroup cbg = (CheckboxGroup) e.getTarget();

            List<Group> results = searchForm.getResults();

            String groupID;
            String groupName;
            Group child;
            
            for(Group group : results) {
                child = group;
                groupID = Long.toString(child.getSubjectId());
                groupName = child.getName();
                cbg.addOption(new Option(groupID, groupName));
            }
        }

    }

}
