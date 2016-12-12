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
package com.arsdigita.cms.ui.templates;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.TableActionAdapter;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.cms.CMS;

import org.librecms.contentsection.ContentItem;

import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentType;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.SecurityPropertyEditor;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.util.Assert;

/**
 * This component will eventually contain the full templates UI for
 * content items. It is just a placeholder for now.
 *
 * @author Stanislav Freidin
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ItemTemplates extends SecurityPropertyEditor {
    private ItemSelectionModel m_itemModel;

    public static final String ASSIGN_TEMPLATE = "assignTemplate";

    /**
     * Construct a new ItemTemplates component
     *
     * @param model the <code>ItemSelectionModel</code> that will supply the
     *   current content item
     */
    public ItemTemplates(ItemSelectionModel model) {
        super();
        m_itemModel = model;

        addComponent("Placeholder", new Text("Placeholder"));
        
//        ToDo
//        ItemTemplatesListingImpl l = new ItemTemplatesListingImpl(model);
//
//        final LayoutPanel layout = new LayoutPanel();
//        setDisplayComponent(layout);
//
//        SegmentedPanel st = new SegmentedPanel();
//        layout.setBody(st);
//
//        st.addSegment(new Label(GlobalizationUtil.globalize("cms.ui.templates.assigned_templates")), l);
//
//        SegmentedPanel sa = new SegmentedPanel();
//        Label assignLabel = new Label(GlobalizationUtil.globalize("cms.ui.templates.dummy"));
//        assignLabel.addPrintListener(new PrintListener() {
//                public void prepare(PrintEvent e) {
//                    PageState s = e.getPageState();
//                    Label targetLabel = (Label)e.getTarget();
//                    ContentPage item = (ContentPage)m_itemModel.getSelectedItem(s);
//                    Assert.exists(item, "item");
//                    targetLabel.setLabel( (String) GlobalizationUtil.globalize("cms.ui.templates.assign_a_template_to").localize() + item.getTitle());
//                }
//            });
//        sa.addSegment(assignLabel,
//                      new AvailableTemplatesListing(l.getRowSelectionModel()));
//        addComponent(ASSIGN_TEMPLATE, sa);
    }

    /**
     * Displays a list of templates which are currently assigned to
     * the current item
     */
//    protected class ItemTemplatesListingImpl extends ItemTemplatesListing {
//
//        private WorkflowLockedComponentAccess m_access;
//
//        public ItemTemplatesListingImpl(ItemSelectionModel model) {
//            super(model);
//            m_access = new WorkflowLockedComponentAccess(null, model);
//        }
//
//        public void assignLinkClicked(PageState s,
//                                      ContentItem item,
//                                      String useContext) {
//            showComponent(s, ASSIGN_TEMPLATE);
//        }
//
//        public void register(Page p) {
//            super.register(p);
//            // Hide action columns if user has no access
//            p.addActionListener(new ActionListener() {
//                    public void actionPerformed(ActionEvent e) {
//                        final PageState state = e.getPageState();
//
//                        if (state.isVisibleOnPage(ItemTemplates.this)) {
//                            if (m_access.canAccess
//                                (state,
//                                 CMS.getContext().getSecurityManager())) {
//                                getRemoveColumn().setVisible(state, true);
//                                getAssignColumn().setVisible(state, true);
//                            } else {
//                                getRemoveColumn().setVisible(state, false);
//                                getAssignColumn().setVisible(state, false);
//                            }
//                        }
//                    }
//                });
//        }
//    }
//
//    /**
//     * Displays a list of templates for the given content item in the
//     * given context, along with a link to select a template
//     */
//    protected class AvailableTemplatesListing extends TemplatesListing {
//
//        TableColumn m_assignCol;
//        SingleSelectionModel m_contextModel;
//
//        /**
//         * Construct a new AvailableTemplatesListing
//         *
//         * @param contextModel the SingleSelectionModel that will define the
//         *   current use context
//         */
//        public AvailableTemplatesListing(SingleSelectionModel contextModel) {
//            super();
//            m_contextModel = contextModel;
//
//            // Add the "assign" column and corresponding action listener
//            m_assignCol = addColumn("Assign",
//                                    TemplateCollection.TEMPLATE, false,
//                                    new AssignCellRenderer());
//
//            addTableActionListener(new TableActionAdapter() {
//                    public void cellSelected(TableActionEvent e) {
//                        PageState s = e.getPageState();
//                        TemplatesListing l = (TemplatesListing)e.getSource();
//                        int i = e.getColumn().intValue();
//                        TableColumn c = l.getColumnModel().get(i);
//
//                        // Safe to check pointer equality since the column is
//                        // created statically
//                        if(c == m_assignCol) {
//                            SectionTemplateMapping m =
//                                (SectionTemplateMapping)getMappingModel().getSelectedObject(s);
//                            assignTemplate(s, m.getTemplate());
//                        }
//                    }
//                });
//        }
//
//        /**
//         * Get all the templates for the given context in the current section
//         */
//        protected TemplateCollection getTemplateCollection(PageState s) {
//            ContentSection sec = CMS.getContext().getContentSection();
//
//            ContentItem item = m_itemModel.getSelectedItem(s);
//            Assert.exists(item, "item");
//
//            ContentType type = item.getContentType();
//            Assert.exists(type, "content type");
//
//            MimeType mimeType = getMimeType(s);
//            TemplateCollection c = 
//                TemplateManagerFactory.getInstance().getTemplates(sec, type);
//            if (mimeType != null) {
//                c.addEqualsFilter(TemplateCollection.TEMPLATE + "." + 
//                                  Template.MIME_TYPE + "." + 
//                                  MimeType.MIME_TYPE, mimeType.getMimeType());
//            } 
//            c.addEqualsFilter(TemplateCollection.USE_CONTEXT,
//                              getUseContext(s));
//            return c;
//        }
//
//        // TODO: this is a 100% convoluted interdependent mess that
//        // really needs to be reworked
//        /**
//         * Get the currently selected use context
//         */
//        protected String getUseContext(PageState s) {
//            String c = (String)m_contextModel.getSelectedKey(s);
//            Assert.exists(c, "use context");
//            return ItemTemplatesListing.getUseContextFromKey(c);
//        }
//
//        protected MimeType getMimeType(PageState s) {
//            String key = (String)m_contextModel.getSelectedKey(s);
//            return ItemTemplatesListing.getMimeTypeFromKey(key);
//        }
//
//        /**
//         * Assign a template to the current item
//         */
//        public void assignTemplate(PageState s, Template t) {
//            ContentItem item = m_itemModel.getSelectedItem(s);
//            Assert.exists(item, "item");
//
//            TemplateManagerFactory.getInstance()
//                .addTemplate(item, t, getUseContext(s));
//
//            showDisplayPane(s);
//        }
//
//        /**
//         * Render the "assign" link
//         */
//        protected class AssignCellRenderer implements TableCellRenderer {
//
//            private ControlLink m_link;
//
//            public AssignCellRenderer() {
//                m_link = new ControlLink
//                    (new Label(GlobalizationUtil.globalize
//                               ("cms.ui.templates.assign_this_template")));
//                m_link.setClassAttr("assignTemplateLink");
//            }
//
//            public Component getComponent(Table table, PageState state, Object value,
//                                          boolean isSelected, Object key,
//                                          int row, int column) {
//                return m_link;
//            }
//        }
//    }
}
