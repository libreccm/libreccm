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

import org.libreccm.categorization.Categorization;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.cdi.utils.CdiUtil;

import java.util.ArrayList;
import java.util.List;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SubCategoriesTable extends Table {

    private static final int COL_NAME = 0;
    private static final int COL_UP = 1;
    private static final int COL_DOWN = 2;
    private static final int COL_EDIT = 3;
    private static final int COL_DEL = 4;

    private final CategoriesTab categoriesTab;
    private final ParameterSingleSelectionModel<String> selectedCategoryId;

    public SubCategoriesTable(
        final CategoriesTab categoriesTab,
        final ParameterSingleSelectionModel<String> selectedCategoryId) {

        super();

        this.categoriesTab = categoriesTab;
        this.selectedCategoryId = selectedCategoryId;

        setIdAttr("subCategoriesTable");

        setEmptyView(new Label(new GlobalizedMessage(
            "ui.admin.categories.category.no_subcategories",
            ADMIN_BUNDLE)));

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
            COL_NAME,
            new Label(new GlobalizedMessage(
                "ui.admin.categories.category.subcategories.col_name",
                ADMIN_BUNDLE))
        ));
        columnModel.add(new TableColumn(
            COL_UP,
            new Label(new GlobalizedMessage(
                "ui.admin.categories.category.subcategories.col_up",
                ADMIN_BUNDLE))
        ));
        columnModel.add(new TableColumn(
            COL_DOWN,
            new Label(new GlobalizedMessage(
                "ui.admin.categories.category.subcategories.col_down",
                ADMIN_BUNDLE))
        ));
        columnModel.add(new TableColumn(
            COL_EDIT,
            new Label(new GlobalizedMessage(
                "ui.admin.categories.category.subcategories.col_edit",
                ADMIN_BUNDLE))
        ));
        columnModel.add(new TableColumn(
            COL_DEL,
            new Label(new GlobalizedMessage(
                "ui.admin.categories.category.subcategories.col_del",
                ADMIN_BUNDLE))
        ));

        columnModel.get(COL_UP).setCellRenderer(new TableCellRenderer() {

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
                    return new ControlLink((Component) value);
                }
            }

        });

        columnModel.get(COL_DOWN).setCellRenderer(new TableCellRenderer() {

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
                    return new ControlLink((Component) value);
                }
            }

        });

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
                        "ui.admin.categories.category.subcategories.del_confirm",
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
                    case COL_UP: {
                        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                        final CategoryRepository categoryRepo = cdiUtil
                            .findBean(CategoryRepository.class);
                        final CategoryManager categoryManager = cdiUtil
                            .findBean(CategoryManager.class);
                        final Category parentCategory = categoryRepo.findById(
                            Long.parseLong(selectedCategoryId.getSelectedKey(
                                state)));
                        final Category subCategory = categoryRepo.findById(Long
                            .parseLong((String) event.getRowKey()));
                        categoryManager.decreaseCategoryOrder(subCategory,
                                                              parentCategory);
                        break;
                    }
                    case COL_DOWN: {
                        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                        final CategoryRepository categoryRepo = cdiUtil
                            .findBean(CategoryRepository.class);
                        final CategoryManager categoryManager = cdiUtil
                            .findBean(CategoryManager.class);
                        final Category parentCategory = categoryRepo.findById(
                            Long.parseLong(selectedCategoryId.getSelectedKey(
                                state)));
                        final Category subCategory = categoryRepo.findById(Long
                            .parseLong((String) event.getRowKey()));
                        categoryManager.increaseCategoryOrder(subCategory,
                                                              parentCategory);
                        break;
                    }
                    case COL_EDIT: {
                        selectedCategoryId.setSelectedKey(state,
                                                          event.getRowKey());
                        categoriesTab.showCategoryEditForm(state);
                        break;
                    }
                    case COL_DEL: {
                        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                        final CategoryRepository categoryRepo = cdiUtil
                            .findBean(CategoryRepository.class);
                        final Category category = categoryRepo.findById(Long
                            .parseLong((String) event.getRowKey()));
                        categoryRepo.delete(category);
                        break;
                    }
                }
            }

            @Override
            public void headSelected(final TableActionEvent event) {
                //Nothing
            }

        });

        setModelBuilder(new SubCategoriesTableModelBuilder());
    }

    private class SubCategoriesTableModelBuilder
        extends LockableImpl
        implements TableModelBuilder {

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            table.getRowSelectionModel().clearSelection(state);

            return new SubCategoriesTableModel(state);
        }

    }

    private class SubCategoriesTableModel implements TableModel {

        private final List<Category> subCategories;
        private int index = -1;

        public SubCategoriesTableModel(final PageState state) {
            final CategoryRepository categoryRepo = CdiUtil.
                createCdiUtil().findBean(CategoryRepository.class);
            final Category category = categoryRepo.findById(Long.parseLong(
                selectedCategoryId.getSelectedKey(state)));

            subCategories = new ArrayList<>(category.getSubCategories());
            subCategories.sort((c1, c2) -> {
                return Long.compare(c1.getCategoryOrder(),
                                    c2.getCategoryOrder());
            });
        }

        @Override
        public int getColumnCount() {
            return 5;
        }

        @Override
        public boolean nextRow() {
            index++;
            return index < subCategories.size();
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            final Category subCategory = subCategories.get(index);

            switch (columnIndex) {
                case COL_NAME:
                    return subCategory.getName();
                case COL_UP:
                    if (index == 0) {
                        return null;
                    } else {
                        return new Label(new GlobalizedMessage(
                            "ui.admin.categories.category.subcategories.up",
                            ADMIN_BUNDLE));
                    }
                case COL_DOWN:
                    if (index == subCategories.size() - 1) {
                        return null;
                    } else {
                        return new Label(new GlobalizedMessage(
                            "ui.admin.categories.category.subcategories.down",
                            ADMIN_BUNDLE));
                    }
                case COL_EDIT:
                    return new Label(new GlobalizedMessage(
                        "ui.admin.categories.category.subcategories.edit",
                        ADMIN_BUNDLE));
                case COL_DEL:
                    if (isDeletable(subCategory)) {
                        return new Label(new GlobalizedMessage(
                            "ui.admin.categories.category.subcategories.del",
                            ADMIN_BUNDLE));
                    } else {
                        return null;
                    }
                default:
                    throw new IllegalArgumentException(
                        "Not a valid column index");
            }
        }

        private boolean isDeletable(final Category category) {
            final List<Category> subCats = category.getSubCategories();
            final List<Categorization> objects = category.getObjects();

            return (subCats == null || subCats.isEmpty())
                       && (objects == null || objects.isEmpty());
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return subCategories.get(index).getObjectId();
        }

    }

}
