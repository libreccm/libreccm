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
package com.arsdigita.cms.ui.type;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.cms.CMS;

import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentType;
//ToDo import com.arsdigita.cms.SectionTemplateMapping;

import com.arsdigita.cms.ui.BaseAdminPane;
import com.arsdigita.cms.ui.BaseDeleteForm;
import com.arsdigita.globalization.GlobalizedMessage;
//ToDo insert later import com.arsdigita.cms.ui.ContentSectionPage;

import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.Cancellable;
import com.arsdigita.toolbox.ui.Section;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.UnexpectedErrorException;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentSectionManager;
import org.librecms.contentsection.ContentTypeManager;
import org.librecms.contenttypes.ContentTypeInfo;

import java.util.List;
import java.util.TooManyListenersException;

/**
 * This class contains the split pane for the ContentType administration
 * interface.
 *
 * @author Jack Chung
 * @author Michael Pih
 * @author Stanislav Freidin
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public final class ContentTypeAdminPane extends BaseAdminPane<String> {

    private static final Logger LOGGER = LogManager.getLogger(
        ContentTypeAdminPane.class);

    private final ACSObjectSelectionModel m_model;
    private final ContentTypeRequestLocal m_type;

    /**
     * Constructs an admin pane. It is containing (a) a list of available
     * content types in a given content section and adds a link to make
     * additional content types available (out of a list of installed, but
     * available in a given content section).
     */
    public ContentTypeAdminPane() {

        // 
        super(new Label(gz("cms.ui.types")),
              new ContentTypeListModelBuilder());  //list with all Types avail.

        m_model = new ACSObjectSelectionModel(getSelectionModel());
        m_type = new SelectionRequestLocal();

        ActionLink addTypeLink
                       = new ActionLink(new Label(gz("cms.ui.type.add")));

        AddTypeContainer addTypeContainer = new AddTypeContainer();
        getBody().add(addTypeContainer);
        getBody().connect(addTypeLink, addTypeContainer);
        addTypeLink.addActionListener(addTypeContainer);

        setEdit(new ActionLink(new Label(gz("cms.ui.type.edit"))),
                new EditType(m_model));

        setDelete(new ActionLink(new Label(gz("cms.ui.type.delete"))),
                  new DeleteForm());

        setIntroPane(new Label(gz("cms.ui.type.intro")));
        setItemPane(new ContentTypeItemPane(m_model,
                                            m_type,
                                            getEditLink(),
                                            getDeleteLink()));

        addAction(new TypeSecurityContainer(addTypeLink), ActionGroup.ADD);
    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.addActionListener(new ActionListener() {

            /**
             *
             * @param event
             */
            @Override
            public void actionPerformed(final ActionEvent event) {
//                ToDo
//                final PageState state = event.getPageState();
//                ContentType contentType = (ContentType) m_model.getSelectedObject(state);
//                ContentSection section = CMS.getContext().getContentSection();
//                if (contentType == null) {
//                    final String template = state.getRequest()
//                                            .getParameter(ContentSectionPage
//                                                          .SET_TEMPLATE);
//                    if (template != null) {
//                        DataCollection da = SessionManager.getSession().retrieve(SectionTemplateMapping.BASE_DATA_OBJECT_TYPE);
//                        DomainCollection c = new DomainCollection(da);
//                        c.addEqualsFilter(SectionTemplateMapping.SECTION + "." + ACSObject.ID,
//                                section.getID());
//                        c.addEqualsFilter(SectionTemplateMapping.TEMPLATE + "." + ACSObject.ID,
//                                new BigDecimal(template));
//                        c.addOrder(SectionTemplateMapping.CONTENT_TYPE + "." + ContentType.LABEL);
//                        if (c.next()) {
//                            SectionTemplateMapping mapping =
//                                    (SectionTemplateMapping) c.getDomainObject();
//                            contentType = mapping.getContentType();
//                        }
//                        c.close();
//                    }
//                    if (contentType == null) {
//                        ContentTypeCollection contentTypes = section.getContentTypes();
//                        contentTypes.addOrder("label asc");
//                        try {
//                            if (contentTypes.next()) {
//                                contentType = contentTypes.getContentType();
//                            }
//                        } finally {
//                            contentTypes.close();
//                        }
//                    }
//                    if (contentType != null) {
//                        m_model.setSelectedObject(state, contentType);
//                        getBody().push(state, getItemPane());
//                    }
//
//
//                }
            }

        });
    }

//    ToDo (?) User Definied Content Types    
//    private class AddTypeContainer extends GridPanel implements ActionListener, 
//                                                                FormProcessListener {
//
//        private Label m_noTypesAvailable =
//                new Label(gz("cms.ui.type.select.none"));
//        private SelectType m_selectType;
////        User Definied Content Types private CreateType m_createType;
//
//        /**
//         * 
//         */
//        AddTypeContainer() {
//            super(1);
//            Section selectSection = new Section();
//            selectSection.setHeading(new Label(gz("cms.ui.type.select")));
//            add(selectSection);
//
//            GridPanel container = new GridPanel(1);
//            container.add(m_noTypesAvailable);
//            m_selectType = new SelectType();
//            m_selectType.addSubmissionListener(new CancelListener(m_selectType));
//            m_selectType.addProcessListener(this);
//            container.add(m_selectType);
//            selectSection.setBody(container);
//
//            Section addSection = new Section() {
//
//                @Override
//                public final boolean isVisible(final PageState state) {
//                    return super.isVisible(state)
//                            && !ContentSection.getConfig().getHideUDCTUI();
//                }
//            };
//            addSection.setHeading(new Label(gz("cms.ui.type.define")));
//            m_createType = new CreateType(m_model);
//            m_createType.addSubmissionListener(new CancelListener(m_createType));
//            m_createType.addProcessListener(this);
//            addSection.setBody(m_createType);
//            add(addSection);
//        }
//
//        /**
//         * 
//         * @param e 
//         */
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            PageState s = e.getPageState();
//            ContentSection section = CMS.getContext().getContentSection();
//            ContentTypeCollection contentTypes =
//                    section.getNotAssociatedContentTypes();
//            boolean hasAvailableTypes = !contentTypes.isEmpty();
//            m_selectType.setVisible(s, hasAvailableTypes);
//            m_noTypesAvailable.setVisible(s, !hasAvailableTypes);
//        }
//
//        public final void process(final FormSectionEvent e)
//                throws FormProcessException {
//            final PageState state = e.getPageState();
//            resetPane(state);
//        }
//    }
    /**
     * This class is essentially a copy of the CancelListener inside of
     * ModalPanel. We could not use the one in ModalPanel because it was
     * protected
     */
    private final class CancelListener implements FormSubmissionListener {

        Cancellable m_form;

        CancelListener(final Cancellable form) {
            m_form = form;
        }

        @Override
        public void submitted(FormSectionEvent event)
            throws FormProcessException {
            PageState state = event.getPageState();
            if (m_form.isCancelled(state)) {
                getBody().pop(state);
                throw new FormProcessException(new GlobalizedMessage(
                    "cms.ui.type.cancelled", CmsConstants.CMS_BUNDLE));
            }
        }

    }  // end private class 

    private void resetPane(final PageState state) {
        getBody().reset(state);
        if (getSelectionModel().isSelected(state)) {
            LOGGER.debug("The selection model is selected; displaying "
                             + "the item pane");
            getBody().push(state, getItemPane());
        }
    }

    private class SelectionRequestLocal extends ContentTypeRequestLocal {

        @Override
        protected final Object initialValue(final PageState state) {
            ContentType contentType = (ContentType) m_model.getSelectedObject(
                state);
            return contentType;
        }

    }

    private class DeleteForm extends BaseDeleteForm {

        DeleteForm() {
            super(new Label(gz("cms.ui.type.delete_prompt")));

            addSubmissionListener(new TypeSecurityListener());
        }

        @Override
        public final void process(final FormSectionEvent e)
            throws FormProcessException {
            final PageState state = e.getPageState();
            final ContentSection section = CMS.getContext().getContentSection();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ContentSectionManager sectionManager = cdiUtil.findBean(
                ContentSectionManager.class);
            final ContentTypeManager typeManager = cdiUtil.findBean(
                ContentTypeManager.class);

            m_type.getContentType(state).getContentItemClass();

            sectionManager.removeContentTypeFromSection(
                typeManager.classNameToClass(m_type.getContentType(state)
                    .getContentItemClass()),
                section);

            getSelectionModel().clearSelection(state);
        }

    }

    private class AddTypeContainer
        extends GridPanel
        implements ActionListener, FormProcessListener {

        private final Label noTypesAvailableLabel
                                = new Label(gz("cms.ui.type.select.none"));
        private final SelectType selectType;

        public AddTypeContainer() {
            super(1);

            final Section selectSection = new Section();
            selectSection.setHeading(new Label(gz("cms.ui.type.select")));
            add(selectSection);

            final GridPanel container = new GridPanel(1);
            container.add(noTypesAvailableLabel);
            selectType = new SelectType();
            selectType.addSubmissionListener(new CancelListener(selectType));
            selectType.addProcessListener(this);
            container.add(selectType);
            selectSection.setBody(container);
        }

        @Override
        public void actionPerformed(final ActionEvent event) {

            final PageState state = event.getPageState();
            final ContentSection section = CMS.getContext().getContentSection();
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ContentTypeAdminPaneController controller = cdiUtil
                .findBean(ContentTypeAdminPaneController.class);
            final List<ContentTypeInfo> contentTypes = controller
                .getNotAssociatedContentTypes(section);
            final boolean hasAvailableTypes = !contentTypes.isEmpty();
            selectType.setVisible(state, hasAvailableTypes);
            noTypesAvailableLabel.setVisible(state, !hasAvailableTypes);
        }

        @Override
        public void process(final FormSectionEvent event)
            throws FormProcessException {

            final PageState state = event.getPageState();
            resetPane(state);
        }

    }

}
