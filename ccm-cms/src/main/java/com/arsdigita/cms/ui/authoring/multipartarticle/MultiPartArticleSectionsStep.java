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
package com.arsdigita.cms.ui.authoring.multipartarticle;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.ResettableContainer;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SelectedLanguageUtil;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionChecker;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.privileges.ItemPrivileges;
import org.librecms.contenttypes.MultiPartArticle;
import org.librecms.contenttypes.MultiPartArticleSection;

import java.util.Locale;

/**
 * Authoring kit step to manage the sections of a MultiPartArticle. Process is
 * implemented with three main components that manipulate the currently selected
 * MultiPartArticle and sections. The visibility of these components is managed
 * by this class.
 *
 * Note: This class was originally called {@code MultiPartArticleViewSections}.
 * Starting with version 7.0.0 all authoring step classes should end with
 * {@code Step} to make them easily identifiable.
 *
 * @author <a href="mailto:dturner@arsdigita.com">Dave Turner</a>
 * * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class MultiPartArticleSectionsStep extends ResettableContainer {

    /* id keys for each editing panel */
    public static final String SECTION_TABLE = "sec_tbl";
    public static final String SECTION_EDIT = "sec_edt";
    public static final String SECTION_PREVIEW = "sec_prv";
    public static final String SECTION_DELETE = "sec_del";

    public static final String DATA_TABLE = "dataTable";
    public static final String ACTION_LINK = "actionLink";

    private final AuthoringKitWizard authoringKitWizard;
    private final ItemSelectionModel selectedArticleModel;
    private final StringParameter selectedLanguageParam;
    private SectionSelectionModel<MultiPartArticleSection> selectedSectionModel;
    private SectionSelectionModel<MultiPartArticleSection> moveSectionModel;
    private LongParameter moveSectionParam;

    private SectionTable sectionTable;
    private SectionEditForm sectionEditForm;
    private SectionPreviewPanel sectionPreviewPanel;
    private SectionDeleteForm sectionDeleteForm;

    private ActionLink beginLink;
    private Label moveSectionLabel;

    private final String typeIdStr;

    public MultiPartArticleSectionsStep(
        final ItemSelectionModel selectedArticleModel,
        final AuthoringKitWizard authoringKitWizard,
        final StringParameter selectedLanguageParam) {

        super();
        this.selectedArticleModel = selectedArticleModel;
        this.authoringKitWizard = authoringKitWizard;
        this.selectedLanguageParam = selectedLanguageParam;

        typeIdStr = authoringKitWizard
            .getContentType()
            .getContentItemClass()
            .getName();

        addWidgets();
    }

    private void addWidgets() {
        // create the components and set default visibility
        add(buildSectionTable(), true);
        add(buildSectionEdit(), false);
        add(buildSectionDelete(), false);
    }

    /**
     * Builds a {@link Container} to hold a {@link SectionTable} and a link to
     * add a new {@link MultiPartArticleSection}.
     *
     * @return A {@link Container} for the table of sections.
     */
    protected Container buildSectionTable() {

        final ColumnPanel panel = new ColumnPanel(1);
        panel.setKey(SECTION_TABLE + typeIdStr);
        panel.setBorderColor("#FFFFFF");
        panel.setPadColor("#FFFFFF");

        moveSectionParam = new LongParameter("moveSection");
        moveSectionModel = new SectionSelectionModel<>(moveSectionParam);

        sectionTable = new SectionTable(selectedArticleModel,
                                        moveSectionModel,
                                        selectedLanguageParam);
        sectionTable.setClassAttr(DATA_TABLE);

        // selected section is based on the selection in the SectionTable
        @SuppressWarnings("unchecked")
        final SingleSelectionModel<Long> rowSelectionModel = sectionTable
            .getRowSelectionModel();
        selectedSectionModel = new SectionSelectionModel<>(
            MultiPartArticleSection.class, rowSelectionModel);

        sectionTable.setSectionModel(selectedSectionModel);

        final Label emptyView = new Label(new GlobalizedMessage(
            "cms.contenttypes.ui.mparticle.no_sections_yet",
            CmsConstants.CMS_BUNDLE));
        sectionTable.setEmptyView(emptyView);

        moveSectionLabel = new Label(new GlobalizedMessage(
            "cms.contenttypes.ui.mparticle.section.title",
            CmsConstants.CMS_BUNDLE));
        moveSectionLabel.addPrintListener(event -> {
            final PageState state = event.getPageState();
            final Label target = (Label) event.getTarget();

            if (moveSectionModel.getSelectedKey(state) != null) {
                final Locale selectedLocale = SelectedLanguageUtil
                    .selectedLocale(state, selectedLanguageParam);

                final MultiPartArticleSectionStepController controller = CdiUtil
                    .createCdiUtil()
                    .findBean(MultiPartArticleSectionStepController.class);

                final Object[] parameterObj = {
                    controller.getSectionTitle(
                    moveSectionModel
                    .getSelectedSection(state),
                    selectedLocale
                    )
                };

                target.setLabel(new GlobalizedMessage(
                    "cms.contenttypes.ui.mparticle.move_section_name",
                    CmsConstants.CMS_BUNDLE,
                    parameterObj));
            }
        });
        panel.add(moveSectionLabel, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);

        beginLink = new ActionLink(new GlobalizedMessage(
            "cms.contenttypes.ui.mparticle.move_to_beginning",
            CmsConstants.CMS_BUNDLE));
        panel.add(beginLink);

        beginLink.addActionListener(event -> {
            final PageState state = event.getPageState();
            final MultiPartArticle article
                                       = (MultiPartArticle) selectedArticleModel
                    .getSelectedObject(state);
            final MultiPartArticleSection section = moveSectionModel
                .getSelectedSection(state);

            final MultiPartArticleSectionStepController controller = CdiUtil
                .createCdiUtil()
                .findBean(MultiPartArticleSectionStepController.class);

            controller.moveToFirst(article, section);
        });

        moveSectionModel.addChangeListener(event -> {
            final PageState state = event.getPageState();

            if (moveSectionModel.getSelectedKey(state) == null) {
                beginLink.setVisible(state, false);
                moveSectionLabel.setVisible(state, false);
            } else {
                beginLink.setVisible(state, true);
                moveSectionLabel.setVisible(state, true);

//                final Locale selectedLocale = SelectedLanguageUtil
//                    .selectedLocale(state, selectedLanguageParam);
//
//                final Object[] parameterObj = {
//                    moveSectionModel
//                    .getSelectedSection(state)
//                    .getTitle()
//                    .getValue(selectedLocale)
//                };
//
//                moveSectionLabel
//                    .setLabel(new GlobalizedMessage(
//                        "cms.contenttypes.ui.mparticle.move_section_name",
//                        CmsConstants.CMS_BUNDLE,
//                        parameterObj),
//                              state);
            }
        });

        // handle clicks to preview or delete a Section
        sectionTable.addTableActionListener(new TableActionListener() {

            @Override
            public void cellSelected(final TableActionEvent event) {
                final PageState state = event.getPageState();
                final TableColumn column = sectionTable
                    .getColumnModel()
                    .get(event.getColumn()
                        .intValue());

                if (column.getModelIndex() == SectionTable.COL_INDEX_DELETE) {
                    onlyShowComponent(state, SECTION_DELETE + typeIdStr);
                } else if (column.getModelIndex() == SectionTable.COL_INDEX_EDIT) {
                    onlyShowComponent(state, SECTION_EDIT + typeIdStr);
                }
            }

            @Override
            public void headSelected(final TableActionEvent event) {
                //Nothing
            }

        });

        panel.add(sectionTable);
        panel.add(buildAddLink());

        return panel;
    }

    /**
     * Builds a container to hold a SectionEditForm and a link to return to the
     * section list.
     *
     * @return
     */
    protected Container buildSectionEdit() {

        final ColumnPanel panel = new ColumnPanel(1);
        panel.setKey(SECTION_EDIT + typeIdStr);
        panel.setBorderColor("#FFFFFF");
        panel.setPadColor("#FFFFFF");

        // display an appropriate title
        panel.add(new Label(event -> {
            final PageState state = event.getPageState();
            final Label target = (Label) event.getTarget();

            if (selectedSectionModel.getSelectedKey(state) == null) {
                target.setLabel(new GlobalizedMessage(
                    "cms.contenttypes.ui.mparticle.add_section",
                    CmsConstants.CMS_BUNDLE));
            } else {
                target.setLabel(new GlobalizedMessage(
                    "cms.contenttypes.ui.mparticle.edit_section",
                    CmsConstants.CMS_BUNDLE));
            }
        }));

        sectionEditForm = new SectionEditForm(selectedArticleModel,
                                              selectedSectionModel,
                                              this,
                                              selectedLanguageParam);

        panel.add(sectionEditForm);
        panel.add(buildViewAllLink());
        panel.add(buildAddLink());

        return panel;
    }

    /**
     * Builds a container to hold the component to confirm deletion of a
     * section.
     *
     * @return
     */
    protected Container buildSectionDelete() {

        final ColumnPanel panel = new ColumnPanel(1);
        panel.setKey(SECTION_DELETE + typeIdStr);
        panel.setBorderColor("#FFFFFF");
        panel.setPadColor("#FFFFFF");

        panel.add(new Label(new GlobalizedMessage(
            "cms.contenttypes.ui.mparticle.delete_section",
            CmsConstants.CMS_BUNDLE)));
        sectionDeleteForm = new SectionDeleteForm(selectedArticleModel,
                                                  selectedSectionModel,
                                                  selectedLanguageParam);
        sectionDeleteForm.addSubmissionListener(event -> {

            final PageState state = event.getPageState();
            onlyShowComponent(state, SECTION_TABLE + typeIdStr);
        });

        panel.add(sectionDeleteForm);
        panel.add(buildViewAllLink());

        return panel;
    }

    /**
     * Utility method to create a link to display the section list.
     *
     * @return
     */
    protected ActionLink buildViewAllLink() {
        final ActionLink viewAllLink = new ActionLink(
            new GlobalizedMessage(
                "cms.contenttypes.ui.mparticle.view_all_sections",
                CmsConstants.CMS_BUNDLE));
        viewAllLink.setClassAttr(ACTION_LINK);
        viewAllLink.addActionListener(event -> {
            onlyShowComponent(event.getPageState(),
                              SECTION_TABLE + typeIdStr);
        });

        return viewAllLink;
    }

    /**
     * Utility method to create a link to display the section list.
     *
     * @return
     */
    protected ActionLink buildAddLink() {
        final ActionLink addLink = new ActionLink(
            new GlobalizedMessage(
                "cms.contenttypes.ui.mparticle.add_new_section",
                CmsConstants.CMS_BUNDLE)) {

            @Override
            public boolean isVisible(final PageState state) {

                final PermissionChecker permissionChecker = CdiUtil
                    .createCdiUtil()
                    .findBean(PermissionChecker.class);
                final ContentItem item = selectedArticleModel
                    .getSelectedItem(state);

                return super.isVisible(state)
                           && permissionChecker.isPermitted(ItemPrivileges.EDIT,
                                                            item);
            }

        };
        addLink.setClassAttr(ACTION_LINK);
        addLink.addActionListener(event -> {
            final PageState state = event.getPageState();
            selectedSectionModel.clearSelection(state);
            onlyShowComponent(state, SECTION_EDIT + typeIdStr);
        });

        return addLink;

    }

    @Override
    public void register(final Page page) {

        super.register(page);

        page.addGlobalStateParam(moveSectionParam);
        page.setVisibleDefault(beginLink, false);
        page.setVisibleDefault(moveSectionLabel, false);
    }

    public String getTypeIDStr() {
        return typeIdStr;
    }

}
