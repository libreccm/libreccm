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
package com.arsdigita.cms.ui.authoring.multipartarticle;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;

import org.librecms.contentsection.ContentItem;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.globalization.GlobalizedMessage;

import org.librecms.contenttypes.MultiPartArticle;

import com.arsdigita.util.LockableImpl;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionChecker;
import org.librecms.CmsConstants;
import org.librecms.contentsection.privileges.ItemPrivileges;
import org.librecms.contenttypes.MultiPartArticleSection;
import org.librecms.contenttypes.MultiPartArticleSectionRepository;

import java.util.Objects;

/**
 * A table that displays the sections for the currently selected
 * MultiPartArticle.
 *
 * @author <a href="mailto:dturner@arsdigita.com">Dave Turner</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SectionTable extends Table {

    /**
     * Index of the title column
     */
    public static final int COL_INDEX_TITLE = 0;
    /**
     * Index of the edit column.
     */
    public static final int COL_INDEX_EDIT = 1;
    /**
     * Index of the move column
     */
    public static final int COL_INDEX_MOVE = 2;
    /**
     * Index of the delete column
     */
    public static final int COL_INDEX_DELETE = 3;

    private ItemSelectionModel selectedArticleModel;
    private SectionSelectionModel<? extends MultiPartArticleSection> selectedSectionModel;
    private SectionSelectionModel<? extends MultiPartArticleSection> moveSectionModel;

    /**
     * Constructor. Create an instance of this class.
     *
     * @param selectedArticleModel a selection model that returns the
     *                             MultiPartArticle which holds the sections to
     *                             display.
     * @param moveSectionModel
     */
    public SectionTable(
        final ItemSelectionModel selectedArticleModel,
        final SectionSelectionModel<? extends MultiPartArticleSection> moveSectionModel) {

        super();
        this.selectedArticleModel = selectedArticleModel;
        this.moveSectionModel = moveSectionModel;

        final TableColumnModel model = getColumnModel();
        model.add(new TableColumn(
            COL_INDEX_TITLE,
            new Label(new GlobalizedMessage(
                "cms.contenttypes.ui.mparticle.section_table.header_section",
                CmsConstants.CMS_BUNDLE))));
        model.add(new TableColumn(
            COL_INDEX_EDIT,
            new Label(new GlobalizedMessage(
                "cms.contenttypes.ui.mparticle.section_table.header_edit",
                CmsConstants.CMS_BUNDLE))));
        model.add(new TableColumn(
            COL_INDEX_MOVE,
            new Label(new GlobalizedMessage(
                "cms.contenttypes.ui.mparticle.section_table.header_move",
                CmsConstants.CMS_BUNDLE))));
        model.add(new TableColumn(
            COL_INDEX_DELETE,
            new Label(new GlobalizedMessage(
                "cms.contenttypes.ui.mparticle.section_table.header_delete",
                CmsConstants.CMS_BUNDLE))));

        model.get(1).setCellRenderer(new SectionTableCellRenderer(true));
        model.get(2).setCellRenderer(new SectionTableCellRenderer(true));
        model.get(3).setCellRenderer(new SectionTableCellRenderer(true));

        super.setModelBuilder(
            new SectionTableModelBuilder(selectedArticleModel, moveSectionModel));

        super.addTableActionListener(new TableActionListener() {

            @Override
            public void cellSelected(final TableActionEvent event) {

                final PageState state = event.getPageState();

                final TableColumn column = getColumnModel()
                    .get(event.getColumn());

                if (column.getModelIndex() == COL_INDEX_MOVE) {

                    if (moveSectionModel.getSelectedKey(state) == null) {
                        moveSectionModel.setSelectedKey(
                            state,
                            selectedSectionModel.getSelectedKey(state));
                    } else {
                        final MultiPartArticle article
                                                   = (MultiPartArticle) selectedArticleModel
                                .getSelectedObject(state);

                        final Long sectionId = moveSectionModel
                            .getSelectedKey(state);

                        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                        final MultiPartArticleSectionRepository sectionRepo
                                                                    = cdiUtil
                                .findBean(
                                    MultiPartArticleSectionRepository.class);
                        final MultiPartArticleSectionStepController controller
                                                                        = cdiUtil
                                .findBean(
                                    MultiPartArticleSectionStepController.class);
                        final MultiPartArticleSection section = sectionRepo
                            .findById(sectionId)
                            .orElseThrow(() -> new IllegalArgumentException(
                            String
                                .format(
                                    "No MultiPartArticleSection with ID %d in "
                                        + "the database.",
                                    sectionId)));

                        final Long destId = Long
                            .parseLong((String) event.getRowKey());
                        final MultiPartArticleSection destSection = sectionRepo
                            .findById(destId)
                            .orElseThrow(() -> new IllegalArgumentException(
                            String.format(
                                "No MultiPartArticleSection with ID %d in "
                                    + "the database.",
                                destId)));

                        // if sect is lower in rank than the dest
                        // then move below is default behavior
                        int rank = destSection.getRank();
                        if (section.getRank() > rank) {
                            // otherwise, add one to get "move below"
                            rank++;
                        }

                        section.setRank(rank);
                        sectionRepo.save(section);
                        moveSectionModel.setSelectedKey(state, null);
                    }
                }
            }

            @Override
            public void headSelected(final TableActionEvent event) {
                // do nothing
            }

        });
    }

    public void setSectionModel(
        final SectionSelectionModel<? extends MultiPartArticleSection> selectedSectionModel) {

        Objects.requireNonNull(selectedSectionModel);

        this.selectedSectionModel = selectedSectionModel;
    }

    private class SectionTableCellRenderer
        extends LockableImpl
        implements TableCellRenderer {

        private boolean active;

        public SectionTableCellRenderer() {
            this(false);
        }

        public SectionTableCellRenderer(final boolean active) {
            this.active = active;
        }

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

            final ContentItem article = selectedArticleModel.getSelectedObject(
                state);

            boolean createLink = active
                                     && permissionChecker
                    .isPermitted(ItemPrivileges.EDIT, article);

            final Component ret;
            if (value instanceof Label) {
                if (createLink) {
                    ret = new ControlLink((Component) value);
                } else {
                    ret = (Component) value;
                }
            } else if (value instanceof String) {
                // Backwards compatibility, should be removed asap!
                if (createLink) {
                    ret = new ControlLink(value.toString());
                } else {
                    ret = new Text(value.toString());
                }
            } else {
                ret = new Label(new GlobalizedMessage(
                    "cms.contenttypes.ui.mparticle.section_table.link_not_defined",
                    CmsConstants.CMS_BUNDLE),
                                false);
            }

            return ret;
        }

    }

}
