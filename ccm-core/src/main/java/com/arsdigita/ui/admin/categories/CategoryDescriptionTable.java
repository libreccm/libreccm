/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package com.arsdigita.ui.admin.categories;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.LockableImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.cdi.utils.CdiUtil;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategoryDescriptionTable extends Table {

    private static final int COL_LOCALE = 0;
    private static final int COL_VALUE = 1;
    private static final int COL_EDIT = 2;
    private static final int COL_DEL = 3;

    private final CategoriesTab categoriesTab;
    private final ParameterSingleSelectionModel<String> selectedCategoryId;
    private final ParameterSingleSelectionModel<String> selectedLanguage;

    public CategoryDescriptionTable(
        final CategoriesTab categoriesTab,
        final ParameterSingleSelectionModel<String> selectedCategoryId,
        final ParameterSingleSelectionModel<String> selectedLanguage) {

        super();

        this.categoriesTab = categoriesTab;
        this.selectedCategoryId = selectedCategoryId;
        this.selectedLanguage = selectedLanguage;

        setIdAttr("categoryDescriptionTable");

        setEmptyView(new Label(new GlobalizedMessage(
            "ui.admin.categories.category_details.description.none",
            ADMIN_BUNDLE)));

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
            COL_LOCALE,
            new Label(new GlobalizedMessage(
                "ui.admin.categories.category_details.description.col_lang",
                ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_VALUE,
            new Label(new GlobalizedMessage(
                "ui.admin.categories.category_details.description.col_value",
                ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_EDIT,
            new Label(new GlobalizedMessage(
                "ui.admin.categories.category_details.description.col_edit",
                ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_DEL,
            new Label(new GlobalizedMessage(
                "ui.admin.categories.category_details.description.col_del",
                ADMIN_BUNDLE))));

        columnModel.get(COL_EDIT).setCellRenderer(new TableCellRenderer() {

            @Override
            public Component getComponent(final Table table,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final Object key,
                                          final int row,
                                          final int column) {
                return new ControlLink((Component) value);
            }

        });

        columnModel.get(COL_DEL).setCellRenderer(new TableCellRenderer() {

            @Override
            public Component getComponent(final Table table,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final Object key,
                                          final int row,
                                          final int column) {
                if (value == null) {
                    return new Text("");
                } else {
                    final ControlLink link = new ControlLink((Component) value);
                    link.setConfirmation(new GlobalizedMessage(
                        "ui.admin.categories.category_details.description"
                            + ".del_confirm",
                        ADMIN_BUNDLE));
                    return link;
                }
            }

        });

        addTableActionListener(new TableActionListener() {

            @Override
            public void cellSelected(final TableActionEvent event) {
                final PageState state = event.getPageState();

                switch (event.getColumn()) {
                    case COL_EDIT:
                        selectedLanguage.setSelectedKey(state,
                                                        event.getRowKey());
                        categoriesTab.showCategoryDescriptionForm(state);
                        break;
                    case COL_DEL:
                        final Locale locale = new Locale((String) event
                            .getRowKey());
                        final CategoryRepository categoryRepository = CdiUtil.
                            createCdiUtil().findBean(
                                CategoryRepository.class);
                        final Category category = categoryRepository.findById(
                            Long.parseLong(selectedCategoryId.
                                getSelectedKey(state)));
                        category.getDescription().removeValue(locale);

                        categoryRepository.save(category);

                        break;
                }
            }

            @Override
            public void headSelected(final TableActionEvent event) {
                //Nothing
            }

        });

        setModelBuilder(new CategoryDescriptionTableModelBuilder());
    }

    private class CategoryDescriptionTableModelBuilder
        extends LockableImpl
        implements TableModelBuilder {

        @Override
        public TableModel makeModel(final Table table,
                                    final PageState state) {
            table.getRowSelectionModel().clearSelection(state);

            return new CategoryDescriptionTableModel(state);
        }

    }

    private class CategoryDescriptionTableModel implements TableModel {

        private final Category selectedCategory;
        private final List<Locale> locales;
        private int index = -1;

        public CategoryDescriptionTableModel(final PageState state) {
            final CategoryRepository categoryRepository = CdiUtil.
                createCdiUtil().findBean(CategoryRepository.class);
            selectedCategory = categoryRepository.findById(Long.parseLong(
                selectedCategoryId.getSelectedKey(state)));

            locales = new ArrayList<>();
            if (selectedCategory.getDescription() != null) {
                locales.addAll(
                    selectedCategory.getDescription().getAvailableLocales());
            }
            locales.sort((l1, l2) -> {
                return l1.toString().compareTo(l2.toString());
            });

        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public boolean nextRow() {
            index++;
            return index < locales.size();
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            final Locale locale = locales.get(index);

            switch (columnIndex) {
                case COL_LOCALE:
                    return locale.toString();
                case COL_VALUE:
                    return selectedCategory.getDescription().getValue(locale);
                case COL_EDIT:
                    return new Label(new GlobalizedMessage(
                        "ui.admin.categories.category_details.description.edit",
                        ADMIN_BUNDLE));
                case COL_DEL:
                    return new Label(new GlobalizedMessage(
                        "ui.admin.categories.category_details.description.del",
                        ADMIN_BUNDLE));
                default:
                    throw new IllegalArgumentException(
                        "Not a valid column index");
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return locales.get(index);
        }

    }

}
