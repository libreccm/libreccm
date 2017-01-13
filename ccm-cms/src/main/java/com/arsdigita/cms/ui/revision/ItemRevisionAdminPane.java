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
package com.arsdigita.cms.ui.revision;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.TableActionAdapter;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.bebop.table.DefaultTableColumnModel;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;

import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentSection;

import com.arsdigita.cms.dispatcher.CMSDispatcher;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.ui.BaseItemPane;
import com.arsdigita.cms.ui.item.ContentItemRequestLocal;
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.toolbox.ui.NullComponent;
import com.arsdigita.toolbox.ui.Section;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSectionManager;

import java.math.BigInteger;

/**
 * Displays the revision history of a {@link ContentItem}
 *
 * @author unknown
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 */
public final class ItemRevisionAdminPane extends BaseItemPane {

    private final ContentItemRequestLocal itemRequestLocal;

    private final RevisionTable revisionsTable;

    private final RevisionRequestLocal fromRequestLocal;
    private final RevisionRequestLocal toRequestLocal;

    private final LayoutPanel selectPane;
    //ToDo
    //private final DifferencePane diffPane;

    public ItemRevisionAdminPane(final ContentItemRequestLocal itemRequestLocal) {
        this.itemRequestLocal = itemRequestLocal;

        final RadioGroup fromSelect = new RadioGroup("from");
        final RadioGroup toSelect = new RadioGroup("to");

        fromRequestLocal = new SelectionRequestLocal(fromSelect);
        toRequestLocal = new SelectionRequestLocal(toSelect);

        revisionsTable = new RevisionTable(fromSelect, toSelect);

        final RevisionForm form = new RevisionForm(fromSelect, toSelect,
                                                   revisionsTable);

        selectPane = new LayoutPanel();
        add(selectPane);
        setDefault(selectPane);

        selectPane.setBody(new Section(gz("cms.ui.item.revisions"), form));

        final ActionLink returnLink = new ActionLink(new Label(gz(
            "cms.ui.item.revision.return")));

// Todo
//        diffPane = new DifferencePane(itemRequestLocal,
//                                      fromRequestLocal,
//                                      toRequestLocal,
//                                      returnLink);
//        add(diffPane);
//
//        connect(form, diffPane);
        connect(returnLink, selectPane);
    }

    private class SelectionRequestLocal extends RevisionRequestLocal {

        private final RadioGroup group;

        SelectionRequestLocal(final RadioGroup group) {
            this.group = group;
        }

        @Override
        protected final Object initialValue(final PageState state) {
            final Object selected = group.getValue(state);

            if (selected == null || selected.toString().equals("")) {
                return null;
            } else {
                final long revisionNumber = Long.parseLong(selected.toString());
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final ContentItemRepository itemRepository = cdiUtil.findBean(
                    ContentItemRepository.class);
                return itemRepository.retrieveRevision(revisionNumber);
            }
        }

    }

    private class RevisionForm extends Form {

        RevisionForm(final RadioGroup fromSelect,
                     final RadioGroup toSelect,
                     final RevisionTable revisions) {
            super("revisions", new GridPanel(1));

            setMethod("get");

            add(fromSelect);
            add(toSelect);

            // Sets the 'to' revision to the dummy current revision
            // and the 'from' revision to the dummy root revision.
            fromSelect.setOptionSelected("");
            toSelect.setOptionSelected("");

            final ActionGroup group = new ActionGroup();
            add(group);

            group.setSubject(revisions);

            group.addAction(new Submit("diff",
                                       gz("cms.ui.item.revision.difference.show")));
        }

    }

    private class RevisionTable extends Table {
        // XXX Need to fix the static l18n stuff

        private final TableColumn fromCol = new TableColumn(
            RevisionTableModelBuilder.FROM, lz("cms.ui.item.revision.from"));
        private final TableColumn toCol = new TableColumn(
            RevisionTableModelBuilder.TO,
            lz("cms.ui.item.revision.to"));
        private final TableColumn timestampCol = new TableColumn(
            RevisionTableModelBuilder.TIMESTAMP, lz("cms.ui.item.revision"));
        private final TableColumn userCol = new TableColumn(
            RevisionTableModelBuilder.USER, lz("cms.ui.user"));
        private final TableColumn descriptionCol = new TableColumn(
            RevisionTableModelBuilder.DESCRIPTION, lz("cms.ui.description"));
        private final TableColumn previewCol = new TableColumn(
            RevisionTableModelBuilder.PREVIEW, lz("cms.ui.item.revision.view"));
        private final TableColumn rollbackCol = new TableColumn(
            RevisionTableModelBuilder.ROLLBACK,
            lz("cms.ui.item.revision.rollback"));

        public RevisionTable(final RadioGroup fromSelect,
                             final RadioGroup toSelect) {
            super(new RevisionTableModelBuilder(itemRequestLocal),
                  new DefaultTableColumnModel());

            final TableColumnModel columns = getColumnModel();
            columns.add(fromCol);
            columns.add(toCol);
            columns.add(timestampCol);
            columns.add(userCol);
            columns.add(descriptionCol);
            columns.add(previewCol);
            columns.add(rollbackCol);

            fromCol.setCellRenderer(new RadioCellRenderer(fromSelect));
            toCol.setCellRenderer(new RadioCellRenderer(toSelect));
            timestampCol.setCellRenderer(new DefaultTableCellRenderer(false));
            userCol.setCellRenderer(new DefaultTableCellRenderer(false));
            descriptionCol.setCellRenderer(new DefaultTableCellRenderer(false));
            previewCol.setCellRenderer(new ViewCellRenderer());
            rollbackCol.setCellRenderer(new RollbackCellRenderer());

            setEmptyView(new Label(gz("cms.ui.item.revision.none")));

            addTableActionListener(new RollbackActionListener());
        }

        class RadioCellRenderer implements TableCellRenderer {

            private final RadioGroup group;

            RadioCellRenderer(final RadioGroup group) {
                this.group = group;
            }

            @Override
            public Component getComponent(final Table table,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final Object key,
                                          final int row,
                                          final int column) {
                switch (key.toString()) {
                    case "first":
                        if (column == RevisionTableModelBuilder.FROM) {
                            return new NullComponent();
                        } else {
                            final Option option = new Option("", new Text(""));

                            option.setGroup(group);

                            return option;
                        }
                    case "last":
                        if (column == RevisionTableModelBuilder.FROM) {
                            final Option option = new Option("", "");

                            option.setGroup(group);

                            return option;
                        } else {
                            return new NullComponent();
                        }
                    default:
                        final Option option = new Option(key.toString(),
                                                         new Text(""));

                        option.setGroup(group);

                        return option;
                }
            }

        }

        class ViewCellRenderer implements TableCellRenderer {

            @Override
            public Component getComponent(final Table table,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final Object key,
                                          final int row,
                                          final int column) {
                if (key instanceof String) {
                    return new NullComponent();
                } else {
                    final BigInteger transID = (BigInteger) key;
                    final ContentItem item = itemRequestLocal.getContentItem(
                        state);
                    final ContentSection section = item.getContentType()
                        .getContentSection();

                    final ContentSectionManager sectionManager = CdiUtil
                        .createCdiUtil().findBean(ContentSectionManager.class);
                    final ItemResolver itemResolver = sectionManager
                        .getItemResolver(section);

                    final StringBuffer url = new StringBuffer(itemResolver
                        .generateItemURL(state, item, section,
                                         CMSDispatcher.PREVIEW));

                    // Cheesy code should be fixed
                    final String sep;
                    if (url.toString().indexOf('?') == -1) {
                        sep = "?";
                    } else {
                        sep = "&";
                    }

                    // TODO: fix this
                    //url.append(sep).append
                    //    (HistoryCollection.TRANS_ID).append("=");
                    url.append(sep).append("transID").append("=");
                    url.append(transID.toString());

                    final Link link = new Link(new Label(gz(
                        "cms.ui.item.revision.view")),
                                               url.toString());

                    link.setTargetFrame(lz("cms.ui.item.revision.view"));

                    return link;
                }
            }

        }

        class RollbackActionListener extends TableActionAdapter {

            @Override
            public final void cellSelected(final TableActionEvent event) {
                final PageState state = event.getPageState();

                if (event.getColumn() == RevisionTableModelBuilder.ROLLBACK) {
                    final ContentItem item = itemRequestLocal.getContentItem(
                        state);

                    //Rollback not supported yet
                    
//                    Versions.rollback(item.getOID(),
//                                      new BigInteger(event.getRowKey()
//                                          .toString()));
//
//                    item.applyTag(lz("cms.ui.item.revision.rolled_back"));
                }
            }

        }

        class RollbackCellRenderer implements TableCellRenderer {

            @Override
            public Component getComponent(final Table table,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final Object key,
                                          final int row, 
                                          final int column) {
                if (key instanceof String) {
                    return new NullComponent();
                } else {
                    return new ControlLink(new Label(gz(
                        "cms.ui.item.revision.rollback")));
                }
            }

        }

    }

}
