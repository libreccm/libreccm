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
package com.arsdigita.docrepo.ui;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;

import java.util.ArrayList;

/**
 * This component shows all the properties of a file with links
 * to administrative actions to change those.
 *
 * @author <a href="mailto:StefanDeusch@computer.org">Stefan Deusch</a>
 */
public class FileInfoPropertiesPane extends SimpleContainer implements
        Constants {

    private ArrayList m_componentList;

    private Component  m_properties;
    private Component  m_upload;
    private Component  m_sendColleague;
    private Component  m_edit;
    private Component  m_action;

    private BasePage m_page;

    /**
     * Constructor. Creates different panes and adds them to a list of
     * components.
     *
     * @param page The BasePage
     */
    public FileInfoPropertiesPane(BasePage page) {
        m_page = page;

        SegmentedPanel main = new SegmentedPanel();
        main.setClassAttr("main");

        m_componentList = new ArrayList<>();

        m_properties = makePropertiesPane(main);
        m_componentList.add(m_properties);

        m_edit = makeEditPane(main);
        m_componentList.add(m_edit);

        m_action = makeActionPane(main);
        m_componentList.add(m_action);

        m_upload = makeUploadPane(main);
        m_componentList.add(m_upload);

        m_sendColleague = makeSendColleaguePane(main);
        m_componentList.add(m_sendColleague);

        add(main);
    }

    /**
     * Makes the properties pane
     *
     * @param main The main segment panel
     *
     * @return A component with the properties pane
     */
    private Component makePropertiesPane(SegmentedPanel main) {
        SimpleContainer container= new SimpleContainer();

        container.add(new FilePropertiesPanel());
        ActionLink link = new ActionLink(new Label(FILE_EDIT_LINK));
        link.setClassAttr("actionLink");
        link.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PageState state = e.getPageState();
                displayEditForm(state);
            }
        });
        container.add(link);
        return main.addSegment(FILE_PROPERTIES_HEADER, container);
    }

    /**
     * Makes the edit pane
     *
     * @param main The main segment panel
     *
     * @return A component with the edit pane
     */
    private Component makeEditPane(SegmentedPanel main) {
        return main.addSegment(FILE_EDIT_HEADER,
                new FileEditForm(this));
    }

    /**
     * Makes the action pane
     *
     * @param main The main segment panel
     *
     * @return A component with the action pane
     */
    private Component makeActionPane(SegmentedPanel main) {
        return  main.addSegment(FILE_ACTION_HEADER,
                new FileActionPane(this));
    }

    /**
     * Makes the upload pane
     *
     * @param main The main segment panel
     *
     * @return A component with the upload pane
     */
    private Component makeUploadPane(SegmentedPanel main) {
        return  main.addSegment(FILE_UPLOAD_HEADER,
                new VersionUploadForm(this));
    }

    /**
     * Makes the send to colleagues pane
     *
     * @param main The main segment panel
     *
     * @return A component with the send to colleague pane
     */
    private Component makeSendColleaguePane(SegmentedPanel main) {
        return main.addSegment(FILE_SEND_COLLEAGUE_HEADER,
                new FileSendColleagueForm(this));
    }

    /**
     * Registers the page with the properties of a file
     *
     * @param p The page
     */
    public void register(Page p) {
        for (Object aM_componentList : m_componentList) {
            p.setVisibleDefault((Component) aM_componentList, false);
        }
        p.setVisibleDefault( m_properties, true);
        p.setVisibleDefault( m_action, true);

        p.addGlobalStateParam(FILE_ID_PARAM);

        super.register(p);
    }


    /**
     * Visibility of components management methods
     *
     * @param state The page state
     */
    private void hideAll(PageState state) {
        for (Object aM_componentList : m_componentList) {
            ((Component) aM_componentList).setVisible(state, false);
        }
    }

    /**
     * Displays the properties and actions of a file
     *
     * @param state The page state
     */
    public void displayPropertiesAndActions(PageState state) {
        m_page.goUnmodal(state);
        hideAll(state);
        m_properties.setVisible(state, true);
        m_action.setVisible(state, true);
    }

    /**
     * Displays the edit form for the file
     *
     * @param state The page state
     */
    public void displayEditForm(PageState state) {
        m_page.goModal(state, m_edit);
    }

    /**
     * Displays the upload form for a file
     *
     * @param state The page state
     */
    public void displayUploadForm(PageState state) {
        m_page.goModal(state, m_upload);
    }

    /**
     * Displays the send to colleague form
     *
     * @param state The page state
     */
    public void displaySendColleagueForm(PageState state) {
        m_page.goModal(state, m_sendColleague);
    }
}
