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
import com.arsdigita.cms.ui.BaseAdminPane;
import com.arsdigita.cms.ui.BaseDeleteForm;
import com.arsdigita.cms.ui.VisibilityComponent;
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.Section;
import com.arsdigita.util.LockableImpl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.privileges.AdminPrivileges;

/**
 * Provides the logic to administer {@link Role roles}.
 *
 * NOTE: Prior, this class managed two {@link ListModelBuilder}. The reason
 * being, that roles where differentiated between Viewer and Member groups.
 * Since this is no longer the case, there exists only the
 * {@link RoleListModelBuilder} now.
 *
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 * @authr  <a href="mailto:jens.pelzetter@googemail.com">Jens Pelzetter</a>
 *
 */
public class RoleAdminPane extends BaseAdminPane<String> {

    private static final Logger LOGGER = LogManager.getLogger(
        RoleAdminPane.class);

    private final SingleSelectionModel<String> selectionModel;

    private final List rolesList;

    public RoleAdminPane() {
        selectionModel = new ParameterSingleSelectionModel<>(
            new StringParameter(List.SELECTED));
        setSelectionModel(selectionModel);

        selectionModel.addChangeListener(new SelectionListener());

        RoleRequestLocal m_role = new SelectionRequestLocal();

        rolesList = new List(new RoleListModelBuilder());
        rolesList.setSelectionModel(selectionModel);

        final SimpleContainer left = new SimpleContainer();
        setLeft(left);

        final RoleSection roleSection = new RoleSection();
        left.add(roleSection);

        setEdit(gz("cms.ui.role.edit"), new RoleEditForm(m_role));
        setDelete(gz("cms.ui.role.delete"), new DeleteForm());

        setIntroPane(new Label(gz("cms.ui.role.intro")));
        setItemPane(new BaseRoleItemPane(selectionModel, m_role,
                                         getEditLink(), getDeleteLink()));
    }

    private class RoleSection extends Section {

        RoleSection() {
            setHeading(gz("cms.ui.role.staff"));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(rolesList);

            final ActionLink link = new ActionLink(new Label(gz(
                "cms.ui.role.staff.add")));

            group.addAction(
                new VisibilityComponent(link,
                                        AdminPrivileges.ADMINISTER_ROLES),
                ActionGroup.ADD);

            final RoleAddForm form = new RoleAddForm(selectionModel);
            getBody().add(form);
            getBody().connect(link, form);
        }

    }

    private class SelectionListener implements ChangeListener {

        @Override
        public final void stateChanged(final ChangeEvent event) {
            LOGGER.debug("Selection state changed; I may change "
                             + "the body's visible pane");

            final PageState state = event.getPageState();

            getBody().reset(state);

            if (selectionModel.isSelected(state)) {
                LOGGER.debug("The selection model is selected; displaying "
                                 + "the item pane");

                getBody().push(state, getItemPane());
            }
        }

    }

    private class SelectionRequestLocal extends RoleRequestLocal {

        @Override
        protected final Object initialValue(final PageState state) {
            final Long id = Long.parseLong(selectionModel.getSelectedKey(state));
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final RoleRepository roleRepository = cdiUtil.findBean(
                RoleRepository.class);

            return roleRepository.findById(id);
        }

    }

    /**
     * This builder provides a list model of the {@link Role roles} which
     * correspond to the {@link ContentSection} in this context.
     */
    private static class RoleListModelBuilder extends LockableImpl implements
        ListModelBuilder {

        RoleListModelBuilder() {
            super();
        }

        @Override
        public final ListModel makeModel(final List list, final PageState state) {
            final ContentSection section = CMS.getContext().getContentSection();

            return new RoleListModel(section.getRoles());
        }

    }

    /**
     * Provides a simple delete form to remove a {@link Role}.
     */
    private class DeleteForm extends BaseDeleteForm {

        DeleteForm() {
            super(gz("cms.ui.role.delete_prompt"));

            addSecurityListener(AdminPrivileges.ADMINISTER_ROLES);
        }

        @Override
        public final void process(final FormSectionEvent event)
            throws FormProcessException {
            final PageState state = event.getPageState();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final RoleRepository roleRepository = cdiUtil.findBean(
                RoleRepository.class);
            final Long id = Long.parseLong(selectionModel.getSelectedKey(state));
            final Role role = roleRepository.findById(id).get();

            roleRepository.delete(role);

            selectionModel.clearSelection(state);
        }

    }

}
