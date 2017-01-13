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
package com.arsdigita.cms.ui.item;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemL10NManager;
import org.librecms.util.LanguageUtil;

import java.util.Locale;


/**
 * Displays a list of all language instances of an item.
 *
 */
public class ItemLanguagesTable extends Table {

    public static final int COL_LANGUAGE = 0;
    public static final int COL_TITLE = 1;
    public static final int COL_DELETE = 2;

    private final ItemSelectionModel itemSelectionModel;
    private final SingleSelectionModel<String> langSelectionModel;
//    private final TableColumn deleteColumn;

    /**
     * Construct a new {@code ItemLanguagesTable} which shows all language
     * variants of an item.
     *
     * @param itemSelectionModel the ItemSelectionModel that supplies the
     *                           current item
     * @param langSelectionModel the single selection model which stores the
     *                           selected language.
     */
    public ItemLanguagesTable(final ItemSelectionModel itemSelectionModel,
                              final SingleSelectionModel<String> langSelectionModel) {
        super();
        setIdAttr("item_languages_table");

        this.itemSelectionModel = itemSelectionModel;
        this.langSelectionModel = langSelectionModel;
        setModelBuilder(new ItemLanguagesTableModelBuilder(itemSelectionModel));

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(COL_LANGUAGE,
                                        new Label(new GlobalizedMessage(
                                            "cms.ui.language.header",
                                            CmsConstants.CMS_BUNDLE))));
        columnModel.add(new TableColumn(COL_TITLE,
                                        new Label(new GlobalizedMessage(
                                            "cms.ui.language.title",
                                            CmsConstants.CMS_BUNDLE))));
        columnModel.add(new TableColumn(COL_DELETE,
                                        new Label(new GlobalizedMessage(
                                            "cms.ui.action",
                                            CmsConstants.CMS_BUNDLE))));

        columnModel.get(COL_LANGUAGE).setCellRenderer(new TableCellRenderer() {

            @Override
            public Component getComponent(final Table table,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final Object key,
                                          final int row,
                                          final int column) {

                final Locale locale = new Locale((String) value);

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final LanguageUtil langUtil = cdiUtil.findBean(
                    LanguageUtil.class);

                final Label label = new Label(langUtil.getLangFull(locale));

                if (langSelectionModel.getSelectedKey(state).equals(key)) {
                    // Current variant, no link
                    return label;
                } else {
                    final String target = ContentItemPage.getItemURL(
                        itemSelectionModel.getSelectedItem(state),
                        ContentItemPage.AUTHORING_TAB);
                    return new Link(label, target);
                }
            }

        }
        );
        columnModel.get(COL_DELETE).setCellRenderer(new TableCellRenderer() {

            @Override
            public Component getComponent(final Table table,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final Object key,
                                          final int row,
                                          final int column) {

                final ControlLink link = new ControlLink(new Label(
                    new GlobalizedMessage("cms.ui.delete",
                                          CmsConstants.CMS_BUNDLE)));
                link.setConfirmation(new GlobalizedMessage(
                    "cms.ui.delete.confirmation", CmsConstants.CMS_BUNDLE));

                return link;
            }

        });

        addTableActionListener(new TableActionListener() {

            @Override
            public void cellSelected(final TableActionEvent event)
                throws FormProcessException {

                if (event.getColumn() != COL_DELETE) {
                    //Nothing to do
                    return;
                }

                final PageState state = event.getPageState();
                final ContentItem item = itemSelectionModel
                    .getSelectedItem(state);
                final String selectedLang = (String) event.getRowKey();

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final ContentItemL10NManager l10nManager = cdiUtil.findBean(
                    ContentItemL10NManager.class);

                l10nManager.removeLanguage(item, new Locale(selectedLang));
            }

            @Override
            public void headSelected(final TableActionEvent event) {
                //Nothing
            }

        });

    }

}
