/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.permissions;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.cms.ui.UserSearchForm;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.ui.CcmObjectSelectionModel;
import com.arsdigita.xml.Element;

import org.libreccm.core.CcmObject;
import org.librecms.CmsConstants;

/**
 * <p>
 * This panel allows a staff administrator to search for users and add them to a
 * staff role for the content section.</p>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ObjectAddSearchAdmin extends SimpleContainer {

    private final CcmObjectSelectionModel<CcmObject> objectSelectionModel;

    private final UserSearchForm searchForm;
    private final ObjectAddAdmin addPanel;
    private final ActionLink returnLink;

    public ObjectAddSearchAdmin(
        final CcmObjectSelectionModel<CcmObject> objectSelectionModel) {

        super();

        this.objectSelectionModel = objectSelectionModel;

        searchForm = new UserSearchForm("ObjectAdminSearch");
        add(searchForm);

        addPanel = getObjectAddAdmin(objectSelectionModel, searchForm);
        add(addPanel);

        addPanel.addCompletionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                fireCompletionEvent(event.getPageState());
            }

        });

        returnLink = new ActionLink(new GlobalizedMessage(
            "cms.ui.permissions.return_to_object_info", CmsConstants.CMS_BUNDLE));
        returnLink.setClassAttr("actionLink");
        returnLink.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                fireCompletionEvent(event.getPageState());
            }

        });
        add(returnLink);
    }

    /**
     * Displays the appropriate form(s).
     *
     * @param parent
     */
    @Override
    public void generateXML(final PageState state, final Element parent) {
        final FormData data = searchForm.getFormData(state);
        final FormData data2 = addPanel.getForm().getFormData(state);

        if (data != null && (data.isSubmission() || data2.isSubmission())) {
            addPanel.setVisible(state, true);
        } else {
            addPanel.setVisible(state, false);
        }
        super.generateXML(state, parent);
    }

    /**
     * This returns the form for adding object administrators
     *
     * @param model
     * @param searchForm
     * @return 
     */
    protected ObjectAddAdmin getObjectAddAdmin(
        final CcmObjectSelectionModel<CcmObject> model,
        final UserSearchForm searchForm) {
        return new ObjectAddAdmin(model, searchForm.getSearchWidget());
    }

}
