/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package com.arsdigita.cms.ui.contentcenter;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ui.CMSContainer;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.LockableImpl;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.security.PermissionChecker;
import org.librecms.CmsConstants;
import org.librecms.pages.Pages;
import org.librecms.pages.PagesPrivileges;
import org.librecms.pages.PagesRepository;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class PagesPane extends CMSContainer {

    private final ParameterSingleSelectionModel<String> selectedPages;

    private final ActionLink addPagesLink;
    private final PagesTable pagesTable;
    private final PagesForm pagesForm;

    PagesPane() {

        super();

        selectedPages = new ParameterSingleSelectionModel<>(
            new StringParameter("selectedPages"));

        addPagesLink = new ActionLink(new GlobalizedMessage(
            "cms.ui.contentcenter.pages.add_link",
            CmsConstants.CMS_BUNDLE));
        addPagesLink.addActionListener(event -> {
            showPagesForm(event.getPageState());
        });
        pagesTable = new PagesTable();
        pagesForm = new PagesForm(this, selectedPages);

        final BoxPanel panel = new BoxPanel(BoxPanel.VERTICAL);
        panel.add(addPagesLink);
        panel.add(pagesTable);
        panel.add(pagesForm);
        
        super.add(panel);
    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.addGlobalStateParam(selectedPages.getStateParameter());

        page.setVisibleDefault(addPagesLink, true);
        page.setVisibleDefault(pagesTable, true);
        page.setVisibleDefault(pagesForm, false);
    }

    protected void showPagesForm(final PageState state) {

        addPagesLink.setVisible(state, false);
        pagesTable.setVisible(state, false);
        pagesForm.setVisible(state, true);
    }

    protected void showPagesTable(final PageState state) {

        addPagesLink.setVisible(state, true);
        pagesTable.setVisible(state, true);
        pagesForm.setVisible(state, false);

        selectedPages.clearSelection(state);
    }

    private class PagesTable extends Table {

        public static final int COL_SITE = 0;
        public static final int COL_PAGES_INSTANCE = 1;
        public static final int COL_EDIT = 2;
        public static final int COL_DELETE = 3;

        public PagesTable() {

            super();

            final TableColumnModel columnModel = super.getColumnModel();

            columnModel
                .add(new TableColumn(
                    COL_SITE,
                    new Label(new GlobalizedMessage(
                        "cms.ui.contentcenter.pagestable.columns.site.header",
                        CmsConstants.CMS_BUNDLE))));
            columnModel
                .add(new TableColumn(
                    COL_SITE,
                    new Label(new GlobalizedMessage(
                        "cms.ui.contentcenter.pagestable.columns.pages_instance.header",
                        CmsConstants.CMS_BUNDLE))));
            columnModel
                .add(new TableColumn(
                    COL_EDIT,
                    new Label(new GlobalizedMessage(
                        "cms.ui.contentcenter.pagestable.columns.edit.header",
                        CmsConstants.CMS_BUNDLE))));
            columnModel
                .add(new TableColumn(
                    COL_DELETE,
                    new Label(new GlobalizedMessage(
                        "cms.ui.contentcenter.pagestable.columns.delete.header",
                        CmsConstants.CMS_BUNDLE))));

            columnModel
                .get(COL_SITE)
                .setCellRenderer(new TableCellRenderer() {

                    @Override
                    public Component getComponent(final Table table,
                                                  final PageState state,
                                                  final Object value,
                                                  final boolean isSelected,
                                                  final Object key,
                                                  final int row,
                                                  final int column) {

                        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                        final PermissionChecker permissionChecker = cdiUtil
                            .findBean(PermissionChecker.class);

//                        return new ControlLink((String) value);
                        final Pages pages = (Pages) value;
                        if (permissionChecker.isPermitted(
                            PagesPrivileges.ADMINISTER_PAGES)) {
                            return new Link(pages.getSite().getDomainOfSite(),
                                            pages.getPrimaryUrl());
                        } else {
                            return new Text(pages.getSite().getDomainOfSite());
                        }
                    }
                });
            
            columnModel
            .get(COL_PAGES_INSTANCE)
                .setCellRenderer(new TableCellRenderer() {

                @Override
                public Component getComponent(final Table table, 
                                              final PageState state,
                                              final Object value, 
                                              final boolean isSelected,
                                              final Object key, 
                                              final int row, 
                                              final int column) {
                    
                    final Pages pages = (Pages) value;
                    return new Text(pages.getPrimaryUrl());
                }
            });

            columnModel
                .get(COL_EDIT)
                .setCellRenderer(new TableCellRenderer() {

                    @Override
                    public Component getComponent(final Table table,
                                                  final PageState state,
                                                  final Object value,
                                                  final boolean isSelected,
                                                  final Object key,
                                                  final int row,
                                                  final int column) {

                        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                        final PermissionChecker permissionChecker = cdiUtil
                            .findBean(PermissionChecker.class);

                        if (permissionChecker.isPermitted(
                            PagesPrivileges.ADMINISTER_PAGES)) {

                            final ControlLink link = new ControlLink(
                                (Component) value);
                            return link;
                        } else {
                            return new Text("");
                        }
                    }

                });

            columnModel
                .get(COL_DELETE)
                .setCellRenderer(new TableCellRenderer() {

                    @Override
                    public Component getComponent(final Table table,
                                                  final PageState state,
                                                  final Object value,
                                                  final boolean isSelected,
                                                  final Object key,
                                                  final int row,
                                                  final int column) {

                        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                        final PermissionChecker permissionChecker = cdiUtil
                            .findBean(PermissionChecker.class);

                        if (permissionChecker.isPermitted(
                            PagesPrivileges.ADMINISTER_PAGES)) {

                            final ControlLink link = new ControlLink(
                                (Component) value);
                            link.setConfirmation(new GlobalizedMessage(
                                "cms.ui.contentcenter.pages.delete.confirm",
                                CmsConstants.CMS_BUNDLE));
                            return link;
                        } else {
                            return new Text("");
                        }
                    }

                });

            super.addTableActionListener(new TableActionListener() {

                @Override
                public void cellSelected(final TableActionEvent event)
                    throws FormProcessException {

                    final PageState state = event.getPageState();

                    final int column = event.getColumn();
                    final String key = (String) event.getRowKey();

                    switch (column) {
                        case COL_EDIT:
                            selectedPages.setSelectedKey(state, key);
                            showPagesForm(state);
                            break;
                        case COL_DELETE: {
                            deletePages(key);
                            break;
                        }
                        default:
                            throw new UnexpectedErrorException(
                                "Illegal column index.");
                    }

                }

                @Override
                public void headSelected(final TableActionEvent event) {

                    //Nothing
                }

            });

            super.setModelBuilder(new PagesTableModelBuilder());

            super.setEmptyView(new Label(new GlobalizedMessage(
                "cms.ui.contentcenter.pages.none", CmsConstants.CMS_BUNDLE)));
        }

    }

    private void deletePages(final String pagesId) {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PagesRepository pagesRepo = cdiUtil
            .findBean(PagesRepository.class);

        final Pages pages = pagesRepo
            .findById(Long.parseLong(pagesId))
            .orElseThrow(() -> new UnexpectedErrorException(String
            .format("No Pages with ID %d in the database.",
                    pagesId)));

        pagesRepo.delete(pages);
    }

    private class PagesTableModelBuilder
        extends LockableImpl
        implements TableModelBuilder {

        @Override
        public TableModel makeModel(final Table table, final PageState state) {

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PagesRepository pagesRepo = cdiUtil
                .findBean(PagesRepository.class);

            final List<Pages> pages = pagesRepo.findAll();
            return new PagesTableModel(pages);
        }

    }

    private class PagesTableModel implements TableModel {

        private final Iterator<Pages> iterator;
        private Pages current;

        public PagesTableModel(final List<Pages> pages) {

            iterator = pages.iterator();

        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public boolean nextRow() {

            if (iterator.hasNext()) {
                current = iterator.next();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Object getElementAt(final int columnIndex) {

            switch (columnIndex) {
                case PagesTable.COL_SITE:
                    return current;
                case PagesTable.COL_PAGES_INSTANCE:
                    return current;
                case PagesTable.COL_EDIT:
                    return new Label(new GlobalizedMessage(
                        "cms.ui.contentcenter.pages.edit.label",
                        CmsConstants.CMS_BUNDLE));
                case PagesTable.COL_DELETE:
                    return new Label(new GlobalizedMessage(
                        "cms.ui.contentcenter.pages.delete.label",
                        CmsConstants.CMS_BUNDLE));
                default:
                    throw new IllegalArgumentException("Illegal column index.");
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {

            return current.getObjectId();
        }

    }

}
