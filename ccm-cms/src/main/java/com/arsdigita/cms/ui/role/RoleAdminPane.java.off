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
package com.arsdigita.cms.ui.role;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ui.BaseAdminPane;
import com.arsdigita.cms.ui.BaseDeleteForm;
import com.arsdigita.cms.ui.VisibilityComponent;
import com.arsdigita.kernel.Role;
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.Section;
import com.arsdigita.util.LockableImpl;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

/**
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: RoleAdminPane.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class RoleAdminPane extends BaseAdminPane {

    private static final Logger s_log = Logger.getLogger(RoleAdminPane.class);

    private final SingleSelectionModel m_model;
    private final RoleRequestLocal m_role;

    private final List m_staff;
    private final List m_viewers;

    public RoleAdminPane() {
        m_model = new ParameterSingleSelectionModel
            (new StringParameter(List.SELECTED));
        setSelectionModel(m_model);

        m_model.addChangeListener(new SelectionListener());

        m_role = new SelectionRequestLocal();

        m_staff = new List(new StaffListModelBuilder());
        m_staff.setSelectionModel(m_model);

        m_viewers = new List(new ViewerListModelBuilder());
        m_viewers.setSelectionModel(m_model);

        final SimpleContainer left = new SimpleContainer();
        setLeft(left);

        final StaffSection staff = new StaffSection();
        left.add(staff);

        final ViewerSection viewers = new ViewerSection();
        left.add(viewers);

        // XXX there need to be two edit forms with different boolean
        // args.  this is broken
        setEdit(gz("cms.ui.role.edit"), new RoleEditForm(m_role, false));
        setDelete(gz("cms.ui.role.delete"), new DeleteForm());

        setIntroPane(new Label(gz("cms.ui.role.intro")));
        setItemPane(new BaseRoleItemPane(m_model, m_role,
                                         getEditLink(), getDeleteLink()));
    }

    private class StaffSection extends Section {
        StaffSection() {
            setHeading(gz("cms.ui.role.staff"));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(m_staff);

            final ActionLink link = new ActionLink
                (new Label(gz("cms.ui.role.staff.add")));

            group.addAction(new VisibilityComponent(link, STAFF_ADMIN),
                            ActionGroup.ADD);

            final RoleAddForm form = new RoleAddForm(m_model, false);
            getBody().add(form);
            getBody().connect(link, form);
        }
    }

    private class ViewerSection extends Section {
        ViewerSection() {
            setHeading(gz("cms.ui.role.viewers"));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(m_viewers);

            final ActionLink link = new ActionLink
                (new Label(gz("cms.ui.role.viewer.add")));

            group.addAction(new VisibilityComponent(link, STAFF_ADMIN),
                            ActionGroup.ADD);

            final RoleAddForm form = new RoleAddForm(m_model, true);
            getBody().add(form);
            getBody().connect(link, form);
        }
    }

    private class SelectionListener implements ChangeListener {
        public final void stateChanged(final ChangeEvent e) {
            s_log.debug("Selection state changed; I may change " +
                        "the body's visible pane");

            final PageState state = e.getPageState();

            getBody().reset(state);

            if (m_model.isSelected(state)) {
                s_log.debug("The selection model is selected; displaying " +
                            "the item pane");

                getBody().push(state, getItemPane());
            }
        }
    }

    private class SelectionRequestLocal extends RoleRequestLocal {
        protected final Object initialValue(final PageState state) {
            final String id = m_model.getSelectedKey(state).toString();

            return new Role(new BigDecimal(id));
        }
    }

    private static class StaffListModelBuilder extends LockableImpl
            implements ListModelBuilder {
        public StaffListModelBuilder() {
            super();
        }

        public final ListModel makeModel(final List list,
                                         final PageState state) {
            final ContentSection section =
                CMS.getContext().getContentSection();

            return new RoleListModel
                (section.getStaffGroup().getOrderedRoles());
        }
    }

    private static class ViewerListModelBuilder extends LockableImpl
            implements ListModelBuilder {
        public final ListModel makeModel(final List list,
                                         final PageState state) {
            final ContentSection section =
                CMS.getContext().getContentSection();

            return new RoleListModel
                (section.getViewersGroup().getOrderedRoles());
        }
    }

    private class DeleteForm extends BaseDeleteForm {
        DeleteForm() {
            super(gz("cms.ui.role.delete_prompt"));

            addSecurityListener(STAFF_ADMIN);
        }

        public final void process(final FormSectionEvent e)
                throws FormProcessException {
            final PageState state = e.getPageState();

            m_role.getRole(state).delete();

            m_model.clearSelection(state);
        }
    }
}
