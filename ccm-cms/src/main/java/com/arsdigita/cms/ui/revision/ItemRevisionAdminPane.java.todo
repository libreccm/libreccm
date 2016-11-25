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
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.dispatcher.CMSDispatcher;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.ui.BaseItemPane;
import com.arsdigita.cms.ui.item.ContentItemRequestLocal;
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.toolbox.ui.NullComponent;
import com.arsdigita.toolbox.ui.Section;
import com.arsdigita.versioning.Transaction;
import com.arsdigita.versioning.Versions;

import java.math.BigInteger;

/** 
 * 
 * @version $Id: ItemRevisionAdminPane.java 287 2005-02-22 00:29:02Z sskracic $
 */
public final class ItemRevisionAdminPane extends BaseItemPane {

    private final ContentItemRequestLocal m_item;

    private final RevisionTable m_revisions;

    private final TransactionRequestLocal m_from;
    private final TransactionRequestLocal m_to;

    private final LayoutPanel m_selectPane;
    private final DifferencePane m_diffPane;

    public ItemRevisionAdminPane(final ContentItemRequestLocal item) {
        m_item = item;

        final RadioGroup fromSelect = new RadioGroup("from");
        final RadioGroup toSelect = new RadioGroup("to");

        m_from = new SelectionRequestLocal(fromSelect);
        m_to = new SelectionRequestLocal(toSelect);

        m_revisions = new RevisionTable(fromSelect, toSelect);

        final RevisionForm form = new RevisionForm
            (fromSelect, toSelect, m_revisions);

        m_selectPane = new LayoutPanel();
        add(m_selectPane);
        setDefault(m_selectPane);

        m_selectPane.setBody
            (new Section(gz("cms.ui.item.revisions"), form));

        final ActionLink returnLink = new ActionLink
            (new Label(gz("cms.ui.item.revision.return")));

        m_diffPane = new DifferencePane(m_item, m_from, m_to, returnLink);
        add(m_diffPane);

        connect(form, m_diffPane);
        connect(returnLink, m_selectPane);
    }

    private class SelectionRequestLocal extends TransactionRequestLocal {
        private final RadioGroup m_group;

        SelectionRequestLocal(final RadioGroup group) {
            m_group = group;
        }

        protected final Object initialValue(final PageState state) {
            final Object id = m_group.getValue(state);

            if (id == null || id.toString().equals("")) {
                return null;
            } else {
                return Transaction.retrieve(new BigInteger(id.toString()));
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

            group.addAction
                (new Submit("diff",
                            gz("cms.ui.item.revision.difference.show")));
        }
    }

    private class RevisionTable extends Table {
        // XXX Need to fix the static l18n stuff

        private TableColumn m_from = new TableColumn
            (RevisionTableModelBuilder.FROM, lz("cms.ui.item.revision.from"));
        private TableColumn m_to = new TableColumn
            (RevisionTableModelBuilder.TO, lz("cms.ui.item.revision.to"));
        private TableColumn m_timestamp = new TableColumn
            (RevisionTableModelBuilder.TIMESTAMP, lz("cms.ui.item.revision"));
        private TableColumn m_user = new TableColumn
            (RevisionTableModelBuilder.USER, lz("cms.ui.user"));
        private TableColumn m_description = new TableColumn
            (RevisionTableModelBuilder.DESCRIPTION, lz("cms.ui.description"));
        private TableColumn m_preview = new TableColumn
            (RevisionTableModelBuilder.PREVIEW, lz("cms.ui.item.revision.view"));
        private TableColumn m_rollback = new TableColumn
            (RevisionTableModelBuilder.ROLLBACK,
             lz("cms.ui.item.revision.rollback"));

        public RevisionTable(final RadioGroup fromSelect,
                             final RadioGroup toSelect) {
            super(new RevisionTableModelBuilder(m_item),
                  new DefaultTableColumnModel());

            final TableColumnModel columns = getColumnModel();
            columns.add(m_from);
            columns.add(m_to);
            columns.add(m_timestamp);
            columns.add(m_user);
            columns.add(m_description);
            columns.add(m_preview);
            columns.add(m_rollback);

            m_from.setCellRenderer(new RadioCellRenderer(fromSelect));
            m_to.setCellRenderer(new RadioCellRenderer(toSelect));
            m_timestamp.setCellRenderer(new DefaultTableCellRenderer(false));
            m_user.setCellRenderer(new DefaultTableCellRenderer(false));
            m_description.setCellRenderer(new DefaultTableCellRenderer(false));
            m_preview.setCellRenderer(new ViewCellRenderer());
            m_rollback.setCellRenderer(new RollbackCellRenderer());

            setEmptyView(new Label(gz("cms.ui.item.revision.none")));

            addTableActionListener(new RollbackActionListener());
        }

        class RadioCellRenderer implements TableCellRenderer {
            private final RadioGroup m_group;

            RadioCellRenderer(final RadioGroup group) {
                m_group = group;
            }

            public Component getComponent(final Table table,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final Object key,
                                          final int row, final int column) {
                if (key.toString().equals("first")) {
                    if (column == RevisionTableModelBuilder.FROM) {
                        return new NullComponent();
                    } else {
                        final Option option = new Option("", "");

                        option.setGroup(m_group);

                        return option;
                    }
                } else if (key.toString().equals("last")) {
                    if (column == RevisionTableModelBuilder.FROM) {
                        final Option option = new Option("", "");

                        option.setGroup(m_group);

                        return option;
                    } else {
                        return new NullComponent();
                    }
                } else {
                    final Option option = new Option(key.toString(), "");

                    option.setGroup(m_group);

                    return option;
                }
            }
        }

        class ViewCellRenderer implements TableCellRenderer {
            public Component getComponent(final Table table,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final Object key,
                                          final int row, final int column) {
                if (key instanceof String) {
                    return new NullComponent();
                } else {
                    final BigInteger transID = (BigInteger) key;
                    final ContentItem item = m_item.getContentItem(state);
                    final ContentSection section = item.getContentSection();

                    final ItemResolver itemResolver =
                        section.getItemResolver();

                    final StringBuffer url = new StringBuffer
                        (itemResolver.generateItemURL
                         (state, item, section, CMSDispatcher.PREVIEW));

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

                    final Link link = new Link
                        (new Label(gz("cms.ui.item.revision.view")),
                         url.toString());

                    link.setTargetFrame(lz("cms.ui.item.revision.view"));

                    return link;
                }
            }
        }

        class RollbackActionListener extends TableActionAdapter {
            public final void cellSelected(final TableActionEvent e) {
                final PageState state = e.getPageState();

                if (e.getColumn().intValue()
                        == RevisionTableModelBuilder.ROLLBACK) {
                    final ContentItem item = m_item.getContentItem(state);

                    Versions.rollback
                        (item.getOID(),
                         new BigInteger(e.getRowKey().toString()));

                    item.applyTag(lz("cms.ui.item.revision.rolled_back"));
                }
            }
        }

        class RollbackCellRenderer implements TableCellRenderer {
            public Component getComponent(final Table table,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final Object key,
                                          final int row, final int column) {
                if (key instanceof String) {
                    return new NullComponent();
                } else {
                    return new ControlLink
                        (new Label(gz("cms.ui.item.revision.rollback")));
                }
            }
        }
    }
}
