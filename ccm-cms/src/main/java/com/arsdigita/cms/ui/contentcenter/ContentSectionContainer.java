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
package com.arsdigita.cms.ui.contentcenter;


import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Embedded;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ui.CMSContainer;
import com.arsdigita.ui.admin.GlobalizationUtil;
import com.arsdigita.util.LockableImpl;

import org.libreccm.categorization.Category;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.contentsection.privileges.ItemPrivileges;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Displays all the content sections in table, with links to the admin (and in
 * legacy mode to legacy public pages as well). Also displays a form for each
 * content section to create an object of a given type (configurable). The list
 * of available types retrieved for each content section.
 *
 * <p>
 * This class is a container for two other components: a form and a table. The
 * form represents the drop down list of the content types available in a
 * particular content section. It is an extension of the
 * {@link com.arsdigita.cms.ui.authoring.NewItemForm}. The table displays each
 * content section in one row, along with the specified form. The same form is
 * reused in every row of the table.
 *
 * @author <a href="mailto:mbryzek@arsdigita.com">Michael Bryzek</a>
 * @version $Id: ContentSectionContainer.java 287 2005-02-22 00:29:02Z sskracic$
 */
public class ContentSectionContainer extends CMSContainer {

    private static final String CONTENT_SECTION_CLASS = "contentSections";

    private final ContentSectionTable m_table;
    private final FormContainer m_formContainer;
    private final SingleSelectionModel m_typeSel;
    private final SingleSelectionModel m_sectionSel;

    /**
     * Constructs a new ContentSectionContainer which containts:
     *
     * <ul>
     * <li> SimpleContainer (to contain the form)
     * <ul>
     * <li> Form (for creating a new content item in each section)
     * </ul>
     * <li> Table (Displays all content sections)
     * </ul>
     *
     * @param typeSel    passthrough to {@link NewItemForm}
     * @param sectionSel passthrough to {@link NewItemForm}
     */
    public ContentSectionContainer(SingleSelectionModel typeSel,
                                   SingleSelectionModel sectionSel) {
        super();
        setClassAttr(CONTENT_SECTION_CLASS);

        m_typeSel = typeSel;
        m_sectionSel = sectionSel;

        m_formContainer = new FormContainer();
        add(m_formContainer);
        m_table = new ContentSectionTable();
        add(m_table);
    }

    /**
     *
     * @param p
     */
    @Override
    public void register(Page p) {
        super.register(p);
        p.setVisibleDefault(m_formContainer, false);
    }

    /**
     *
     */
    private class FormContainer extends CMSContainer {

//        private final StaticNewItemForm m_form;
        private final BigDecimalParameter m_sectionIdParam;

        /**
         * Constructor
         */
        private FormContainer() {
            super();
            m_sectionIdParam = new BigDecimalParameter("sectionId");
//            m_form = new StaticNewItemForm(m_sectionIdParam);

//            m_form.addSubmissionListener(new FormSubmissionListener() {
//
//                /**
//                 * Cancels the form if the user lacks the "create new items"
//                 * privilege.
//                 */
//                @Override
//                public void submitted(FormSectionEvent event)
//                    throws FormProcessException {
//                    PageState state = event.getPageState();
//                    StaticNewItemForm form = (StaticNewItemForm) event
//                        .getSource();
//
//                    ContentSection section = form.getContentSection(state);
//                    final PermissionChecker permissionChecker = CdiUtil
//                        .createCdiUtil().findBean(PermissionChecker.class);
//                    Category folder = null;
//                    //ToDo
////                    User user = Web.getWebContext().getUser();
////                    if (user != null) {
////                        folder = Folder.getUserHomeFolder(user, section);
////                    }
////                    if (folder == null) {
////                        folder = section.getRootFolder();
////                    }
////ToDo End
//                    folder = section.getRootDocumentsFolder();
//
//                    if (!permissionChecker.isPermitted(
//                        ItemPrivileges.CREATE_NEW, folder)) {
//                        throw new FormProcessException(
//                            (GlobalizationUtil.globalize(
//                             "cms.ui.insufficient_privileges")));
//                    }
//                }
//
//            });
//
//            m_form.addProcessListener(new FormProcessListener() {
//
//                /**
//                 * Process listener: redirects to the authoring kit to create a
//                 * new item.
//                 */
//                @Override
//                public void process(FormSectionEvent e) throws
//                    FormProcessException {
//                    StaticNewItemForm form = (StaticNewItemForm) e.getSource();
//                    PageState state = e.getPageState();
//
//                    BigDecimal typeId = form.getTypeID(state);
//                    if (typeId != null) {
//                        Long sectionId = form.getContentSectionID(state);
//                        m_sectionSel.setSelectedKey(state, sectionId);
//                        m_typeSel.setSelectedKey(state, typeId);
//                    }
//                }
//
//            });
//
//            add(m_form);
        }

        @Override
        public void register(Page p) {
            super.register(p);
            p.addComponentStateParam(this, m_sectionIdParam);
        }

//        public StaticNewItemForm getNewItemForm() {
////            return m_form;
//        }

    }

//    private static class StaticNewItemForm extends NewItemForm {
//
//        private final Hidden m_sectionIDParamWidget;
//
//        public StaticNewItemForm(BigDecimalParameter sectionParam) {
//            super("StaticNewItemForm", BoxPanel.VERTICAL);
//            setClassAttr("static-new-item-form");
//            m_sectionIDParamWidget = new Hidden(sectionParam);
//            add(m_sectionIDParamWidget);
//            setProcessInvisible(true);
//        }
//
//        /**
//         * Sets the id of the content section in this form. This ID is used to
//         * generate a list of available content types in the section.
//         *
//         * @param state The current page state.
//         * @param id    The id of the ContentSection for which this form should
//         *              display a list of content types
//         *
//         * @pre ( state != null && id != null )
//         */
//        public void setSectionId(PageState state, BigDecimal id) {
//            Assert.exists(id);
//            m_sectionIDParamWidget.setValue(state, id);
//        }
//
//        /**
//         * Retrieves the content section for this form given the specified page
//         * state. This method will return null if there is no content section.
//         *
//         * @param state The current page state.
//         *
//         * @return The current content section or null if the section does not
//         *         exist
//         *
//         * @pre ( state != null )
//         */
//        @Override
//        public ContentSection getContentSection(PageState state) {
//            Long id = getContentSectionID(state);
//            Assert.exists(id);
//            ContentSection section;
//            section = CdiUtil.createCdiUtil().findBean(
//                ContentSectionRepository.class).findById(id);
//            return section;
//        }
//
//        /**
//         * Retrieves the ID of the content section for this form given the
//         * specified page state. This method will return null if no content
//         * section id has been set.
//         *
//         * @param state The current page state.
//         *
//         * @return The id of the content section or null if it has not been set.
//         *
//         * @pre ( state != null )
//         */
//        private Long getContentSectionID(PageState state) {
//            return (Long) Long.parseLong((String) m_sectionIDParamWidget
//                .getValue(state));
//        }
//
//    }

    /**
     * A table that displays all content sections, with links to their locations
     * and admin pages and a {@link NewItemForm} next to each section.
     *
     * @author <a href="mailto:mbryzek@arsdigita.com">Michael Bryzek</a>
     * @version $Revision$ $DateTime: 2004/08/17 23:15:09 $
     *
     */
    private class ContentSectionTable extends Table {

        // We will use a (symboloc) headerKey to match columns. Because the
        // number of columns depends on configuration for the llocation column,
        // the index varies and con not be used.
        private static final String COLUMN_SECTION = "Section";
        private static final String COLUMN_LOCATION = "Public Site";
        private static final String COLUMN_ACTION = "Action";

        /**
         * Constructs a new ContentSectionTable, using a default table model
         * builder.
         */
        private ContentSectionTable() {
            super();

            // we must use symbolic keys (instead of symbolic column index as
            // usual) to identify a column because their number is dynamic 
            // depending on configuration of the location column!
            Integer colNo = 0;

            Label emptyView = new Label(GlobalizationUtil
                .globalize("cms.ui.contentcenter.section"));
            emptyView.setFontWeight(Label.ITALIC);
            setEmptyView(emptyView);

            setClassAttr("dataTable");

            // add columns to the table
            TableColumnModel columnModel = getColumnModel();

            // prepare column headers
            Label sectionHead = new Label(GlobalizationUtil
                .globalize("cms.ui.contentcenter.section"));
            sectionHead.setHint(GlobalizationUtil
                .globalize("cms.ui.contentcenter.section_hint"));
            Label locationHead = new Label(GlobalizationUtil
                .globalize("cms.ui.contentcenter.location"));
            locationHead.setHint(GlobalizationUtil
                .globalize("cms.ui.contentcenter.location_hint"));
            Label actionHead = new Label(GlobalizationUtil
                .globalize("cms.ui.contentcenter.action"));
            actionHead.setHint(GlobalizationUtil
                .globalize("cms.ui.contentcenter.action_hint"));

            //TableColumn contentSectionColumn = new TableColumn(colNo, COLUMN_SECTION);
            TableColumn contentSectionColumn = new TableColumn(
                colNo,
                sectionHead,
                COLUMN_SECTION);
            contentSectionColumn
                .setCellRenderer(new AdminURLTableCellRenderer());
            columnModel.add(contentSectionColumn);

            TableColumn actionColumn = new TableColumn(
                colNo++,
                actionHead,
                COLUMN_ACTION);
            actionColumn.setCellRenderer(new ActionTableCellRenderer());
            columnModel.add(actionColumn);

            setModelBuilder(new ContentSectionTableModelBuilder());
        }

        /**
         * An ContentSections table model builder
         *
         * @author <a href="mailto:mbryzek@arsdigita.com">Michael Bryzek</a>
         *
         */
        private class ContentSectionTableModelBuilder extends LockableImpl
            implements TableModelBuilder {

            @Override
            public TableModel makeModel(Table table, PageState state) {
                table.getRowSelectionModel().clearSelection(state);
                return new ContentSectionTableModel((ContentSectionTable) table,
                                                    state);
            }

        }

        /**
         * An ContentSections table model
         *
         * @author <a href="mailto:mbryzek@arsdigita.com">Michael Bryzek</a>
         *
         */
        private class ContentSectionTableModel implements TableModel {

            private final ContentSectionTable m_table;
            private final TableColumnModel m_columnModel;
            private final PageState m_state;
            private final List<ContentSection> m_contentSections;
            private ContentSection m_section;
            private int index = -1;

            private ContentSectionTableModel(ContentSectionTable table,
                                             PageState state) {
                m_table = table;
                m_columnModel = table.getColumnModel();
                m_state = state;

                // retrieve all Content Sections
                m_contentSections = getContentSectionCollection();
            }

            /**
             * Returns a collection of ContentSections to display in this table.
             * This implementation orders the content sections by
             * <code>lower(label)</code>. They are also already filtered for the
             * sections to which the current user has no access.
             *
             */
            private List<ContentSection> getContentSectionCollection() {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final PermissionChecker permissionChecker = cdiUtil.findBean(
                    PermissionChecker.class);
                final List<ContentSection> allSections = cdiUtil.findBean(
                    ContentSectionRepository.class).findAll();
                return allSections
                    .stream()
                    .filter(section -> permissionChecker
                        .isPermitted(ItemPrivileges.VIEW_PUBLISHED,
                                     section))
                    .collect(Collectors.toList());
            }

            @Override
            public int getColumnCount() {
                return m_columnModel.size();
            }

            @Override
            public boolean nextRow() {
                index++;
                if (index < m_contentSections.size()) {
                    m_section = m_contentSections.get(index);
                    return true;
                } else {
                    return false;
                }
            }

            /**
             * By default, we return null. For the section, location, and action
             * columns, we return the current Content Section if there is one.
             *
             * @param columnIndex The index of the current column
             */
            @Override
            public Object getElementAt(int columnIndex) {
                if (m_columnModel == null || m_section == null) {
                    return null;
                }

                TableColumn tc = m_columnModel.get(columnIndex);
                String columnKey = (String) tc.getHeaderKey();

                Object result = m_section;
                if (columnKey.equals(COLUMN_SECTION)
                        || columnKey.equals(COLUMN_LOCATION)
                        || columnKey.equals(
                        COLUMN_ACTION)) {
                    result = m_section;
                }
                return result;
            }

            @Override
            public Object getKeyAt(int columnIndex) {
                return m_section.getObjectId();
            }

            /**
             * Returns the table associated with this table model.
             *
             */
            protected Table getTable() {
                return m_table;
            }

            /**
             * Returns the current page state
             *
             */
            protected PageState getPageState() {
                return m_state;
            }

        }

        /**
         * Sets the hidden parameter in the form containers form to the id of
         * the current section. Then returns the form for display, but only if
         * the user has permission to create new items in the current section.
         *
         * @author <a href="mailto:mbryzek@arsdigita.com">Michael Bryzek</a>
         *
         */
        private class ActionTableCellRenderer implements TableCellRenderer {

            @Override
            public Component getComponent(Table table, PageState state,
                                          Object value,
                                          boolean isSelected, Object key,
                                          int row, int column) {
                ContentSection section = (ContentSection) value;
                Category folder = null;
                //ToDo
//                User user = Web.getWebContext().getUser();
//                if (user != null) {
//                    folder = Folder.getUserHomeFolder(user, section);
//                }
//                if (folder == null) {
//                    folder = section.getRootFolder();
//                }

                folder = section.getRootDocumentsFolder();
                // If the user has no access, return an empty Label

//                
//                SecurityManager sm = new SecurityManager(section);
//
//                if (!sm.canAccess(state.getRequest(), SecurityManager.NEW_ITEM,
//                                  folder)
//                        || !ContentSection.getConfig()
//                    .getAllowContentCreateInSectionListing()) {
//                    // return null; //  produces NPE here but works somewhere else.
//                    // It's a kind of a hack. Label is supposed not to accept
//                    // not-gloabalized data. Usually aou will return null here
//                    // and xmlgenerator takes care of it. Doesn't work here.
//                    return new Embedded(
//                        "&nbsp;&nbsp;&nbsp;-&nbsp;-&nbsp;&nbsp;&nbsp;");
//                } else {
//                    // set the value of the sectionIdParameter in the form
//                    // to this section
//                    m_formContainer.getNewItemForm().setSectionId(state, section
//                                                                  .getID());
//                    return m_formContainer.getNewItemForm();
//                }
                //ToDo End
                return new Embedded(
                    "&nbsp;&nbsp;&nbsp;-&nbsp;-&nbsp;&nbsp;&nbsp;");

            }

        }

    }

    /**
     * Generates the correct URL to the public pages for a content section.
     *
     * @author <a href="mailto:mbryzek@arsdigita.com">Michael Bryzek</a>
     *
     */
    public static class URLTableCellRenderer implements TableCellRenderer {

        /**
         * The object passed in is the current content section. This returns a
         * Link whose name and target are the url to the public pages.
         *
         * @return Link whose name and target are the url to the public pages of
         *         the current (passed in) content section or a Label if current
         *         use does not habe acces priviledge for the content section
         */
        @Override
        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int column) {

            /* cast to ContentSection for further processing                  */
            ContentSection section = (ContentSection) value;
            String name = section.getLabel();
            String path = section.getPrimaryUrl(); // from Application

            // If the user has no access, return a Label instead of a Link
            // Kind of a hack because Label is supposed not to accept 
            // "un-globalized" display data. Label had been abused here to
            // to display a DataValue
            return new Embedded("/" + name + "/", false);
            // return null;  // produces NPE here

        }

    }

    /**
     * Generates the correct URL to the admin pages for a content section.
     *
     * @author <a href="mailto:mbryzek@arsdigita.com">Michael Bryzek</a>
     *
     */
    public static class AdminURLTableCellRenderer extends URLTableCellRenderer {

        /**
         * The object passed in is the current content section
         *
         * @param table
         * @param state
         * @param row
         * @param value
         * @param column
         * @param isSelected
         * @param key
         *
         * @return
         *
         */
        @Override
        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {
            ContentSection section = (ContentSection) value;

            final PermissionChecker permissionChecker = CdiUtil.createCdiUtil()
                .findBean(PermissionChecker.class);

            // If the user has no access, return a Label instead of a Link
            if (permissionChecker.isPermitted(
                ItemPrivileges.EDIT,
                section.getRootDocumentsFolder())) {

                return new Link(section.getLabel(),
                                generateURL(section.getPrimaryUrl() + "/"));
            } else {
                //return new Label(section.getName(), false);
                // return null;  // Produces a NPE although it shouldn't and
                // indeed doesn't elsewhere
                // Kind of a hack because Label is supposed not to accept 
                // "un-globalized" display data. Label had been abused here to
                // to display a DataValue
                return new Embedded(section.getLabel(), false);
            }
        }

        /**
         * Generates the admin url for the specified prefix. Always returns
         * something that does not start with a forward slash.
         *
         * @param prefix The prefix of the URL
         *
         * @return
         */
        protected String generateURL(String prefix) {
            return prefix;// + PageLocations.SECTION_PAGE;
        }

    }

}
