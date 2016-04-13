/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package com.arsdigita.ui.admin.usersgroupsroles;

import com.arsdigita.ui.admin.usersgroupsroles.users.UserAdmin;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.ui.admin.usersgroupsroles.groups.GroupAdmin;
import com.arsdigita.util.Assert;
import com.arsdigita.util.LockableImpl;

import java.util.ArrayList;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class UsersGroupsRolesTab extends LayoutPanel {

    private final List sections;
    private final java.util.List<Component> components = new ArrayList<>();
    private final java.util.List<Label> keys = new ArrayList<>();

    public UsersGroupsRolesTab() {
        super();

        setClassAttr("sidebarNavPanel");

        sections = new List(new SectionsListModelBuilder());
        sections.addChangeListener(e -> {
            final PageState state = e.getPageState();
            final int selectedIndex = Integer.parseInt((String) sections
                .getSelectedKey(state));
            setSection(selectedIndex, state);
        });
        sections.setClassAttr("navbar");

//        final BoxPanel usersPanel = new BoxPanel();
//        usersPanel.add(new Label("Users Panel"));

//        final BoxPanel groupsPanel = new BoxPanel();
//        groupsPanel.add(new Label("Groups Panel"));

        final BoxPanel rolesPanel = new BoxPanel();
        rolesPanel.add(new Label("Roles Panel"));

        final BoxPanel body = new BoxPanel();
        addSection(
            new Label(new GlobalizedMessage(
                "ui.admin.users_groups_roles.users.title",
                ADMIN_BUNDLE)),
            new UserAdmin(),
            body);
        addSection(
            new Label(new GlobalizedMessage(
                "ui.admin.users_groups_roles.groups.title",
                ADMIN_BUNDLE)),
            new GroupAdmin(),
            body);
        addSection(
            new Label(new GlobalizedMessage(
                "ui.admin.users_roles_roles.users.title",
                ADMIN_BUNDLE)),
            rolesPanel,
            body);
        
        setLeft(sections);
        setBody(body);
    }
    
    private void addSection(final Label label,
                            final Component component,
                            final BoxPanel panel) {
        Assert.isUnlocked(this);
        components.add(component);
        component.setClassAttr("main");
        panel.add(component);
        keys.add(label);
    }
    
    @Override
    public void register(final Page page) {
        Assert.isUnlocked(this);
        
        components.forEach(c -> page.setVisibleDefault(c, false));
        //page.setVisibleDefault(components.get(0), true);
        
        
    }

    public void setSection(final int index, final PageState state) {
        sections.setSelectedKey(state, String.valueOf(index));
        for(int i = 0; i < components.size(); i++) {
            if (i == index) {
                final Component component = components.get(i);
                component.setVisible(state, true);
                if (component instanceof Resettable) {
                    final Resettable resettable = (Resettable) component;
                    resettable.reset(state);
                }
            } else {
                components.get(i).setVisible(state, false);
            }
        }
    }

    private class SectionsListModelBuilder extends LockableImpl
        implements ListModelBuilder {

        @Override
        public ListModel makeModel(final List list,
                                   final PageState state) {
            if (sections.getSelectedKey(state) == null) {
                sections.setSelectedKey(state, String.valueOf(0));
            }
            
            //sections.setSelectedKey(state, String.valueOf(0));
            return new SectionsListModel(state);
        }

    }

    private class SectionsListModel implements ListModel {

        private int index = -1;
        private final PageState state;

        public SectionsListModel(final PageState state) {
            this.state = state;
        }

        @Override
        public boolean next() {
            final boolean result = (index < keys.size() - 1);
            index++;
            return result;
        }

        @Override
        public Object getElement() {
            return keys.get(index).getLabel(state);
        }

        @Override
        public String getKey() {
            return String.valueOf(index);
        }

    }

}
