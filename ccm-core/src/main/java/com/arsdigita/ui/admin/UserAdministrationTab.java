/*
 * Copyright (c) 2013 Jens Pelzetter
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

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.TabbedPane;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.LayoutPanel;

import static com.arsdigita.ui.admin.AdminConstants.*;

import com.arsdigita.util.Assert;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.xml.Element;

import java.util.ArrayList;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
class UserAdministrationTab extends LayoutPanel {

    private final List sections;
    private final java.util.List<Component> components = new ArrayList<Component>();
    private final java.util.List<Label> keys = new ArrayList<Label>();    
    private final GlobalizedMessage title = USER_NAVBAR_TITLE;

    public UserAdministrationTab(final TabbedPane parent, final GroupAdministrationTab groupAdminTab) {
        super();

        setClassAttr("sidebarNavPanel");
                        
        sections = new List(new GlobalizedTabModelBuilder());
        sections.addChangeListener(new SectionChangeListener());
        sections.setClassAttr("navbar");        
        setLeft(sections);
       
        final UserBrowsePane browsePane = new UserBrowsePane();
        // ToDo final UserSummarySection summarySection = new UserSummarySection(this, browsePane);
        // ToDo final UserSearchSection searchSection = new UserSearchSection(this, browsePane);
        // ToDo final UserCreateSection createSection = new UserCreateSection(this);
        
        browsePane.setTabbedPane(parent);
        browsePane.setGroupAdministrationTab(groupAdminTab);
        
        final BoxPanel body = new BoxPanel();
        // ToDo addSection(USER_TAB_SUMMARY, summarySection, body);
        addSection(USER_TAB_BROWSE, browsePane, body);
        // ToDo addSection(USER_TAB_SEARCH, searchSection, body);
        // ToDo addSection(USER_TAB_CREATE_USER, createSection, body);              
        
        setBody(body);
    }
     
     /**
     *
     * @pre label != null && c != null
     */
    private void addSection(final Label label, final Component component, final BoxPanel panel) {
        Assert.isUnlocked(this);
        components.add(component);
        component.setClassAttr("main");        
        panel.add(component);
        keys.add(label);
    }

    /**
     * 
     * @param page
     */
    @Override
    public void register(final Page page) {
        Assert.isUnlocked(this);

        for (int i = 0; i < components.size(); i++) {
            page.setVisibleDefault(components.get(i), false);
        }
    }

    public void setSection(final int index, final PageState state) {
        sections.setSelectedKey(state, String.valueOf(index));
        for (int i = 0; i < components.size(); i++) {
            if (i == index) {
                (components.get(i)).setVisible(state, true);
                ((Resettable) components.get(i)).reset(state);
            } else {
                (components.get(i)).setVisible(state, false);
            }
        }
    }

    @Override
    public void generateXML(final PageState state, final Element parent) {
        super.generateXML(state, parent);

        /**
         * Globalized navbar title.
         * Why did I override generateXML method to globalize the title?
         * Because CMS put title bar as an attribute of element. This
         * is the only method I could come up with.
         */
        final Element child = (Element) parent.getChildren().get(0);
        child.addAttribute("navbar-title",
                           (String) title.localize(state.getRequest()));
    }

    private class SectionChangeListener implements ChangeListener {

        public SectionChangeListener() {
            //Nothing
        }

        @Override
        public void stateChanged(final ChangeEvent event) {
            final PageState state = event.getPageState();
            final int selectedIndex = Integer.parseInt((String) sections.getSelectedKey(state));
            setSection(selectedIndex, state);
        }

    }

    private class GlobalizedTabModelBuilder extends LockableImpl implements ListModelBuilder {

        public GlobalizedTabModelBuilder() {
            super();
        }

        @Override
        public ListModel makeModel(final List list, final PageState state) {
            return new TabNameListModel(state);
        }

    }

    private class TabNameListModel implements ListModel {

        private int index = -1;
        private final PageState pageState;

        public TabNameListModel(final PageState state) {
            pageState = state;
        }

        @Override
        public Object getElement() {
            return keys.get(index).getLabel(pageState);
        }

        @Override
        public String getKey() {
            return String.valueOf(index);
        }

        public boolean next() {       
            final boolean result = (index < keys.size() - 1);
            //return (index++ < keys.size() - 1);
            index++;
            return result;
        }

    }
}
