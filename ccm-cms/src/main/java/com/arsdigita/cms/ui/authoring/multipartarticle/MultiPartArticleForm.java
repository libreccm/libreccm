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

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Embedded;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.bebop.parameters.URLTokenValidationListener;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.SelectedLanguageUtil;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;

import java.util.Date;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.librecms.CMSConfig;
import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;
import org.librecms.contenttypes.MultiPartArticle;

/**
 * A form for editing MultiPartArticle and subclasses.
 *
 * @author <a href="mailto:dturner@arsdigita.com">Dave Turner</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class MultiPartArticleForm
    extends FormSection
    implements FormInitListener,
               FormProcessListener,
               FormValidationListener {

    private final ItemSelectionModel itemSelectionModel;
    private final StringParameter selectedLanguageParam;
    private SaveCancelSection saveCancelSection;
    /**
     * Constant property, placeholder for a JavaScript element.
     */
    private final Embedded m_script = new Embedded(
        String.format(""
                          + "<script language=\"javascript\" "
                          + "        src=\"%s/javascript/manipulate-input.js\">"
                          + "</script>",
                      Web.getWebappContextPath()),
        false);

    public static final String NAME = "name";
    public static final String TITLE = "title";
    public static final String SUMMARY = "summary";
    public static final String LAUNCH_DATE = "launch_date";
    public static final String LANGUAGE = "language";

    private static final Logger LOGGER = LogManager.getLogger(
        MultiPartArticleForm.class);

    public MultiPartArticleForm(final String formName,
                                final ItemSelectionModel itemSelectionModel,
                                final StringParameter selectedLanguageParam) {

        super(new ColumnPanel(2));

        this.itemSelectionModel = itemSelectionModel;
        this.selectedLanguageParam = selectedLanguageParam;

        ColumnPanel panel = (ColumnPanel) getPanel();
        panel.setBorder(false);
        panel.setPadColor("#FFFFFF");
        panel.setColumnWidth(1, "20%");
        panel.setColumnWidth(2, "80%");
        panel.setWidth("100%");

        addWidgets();

        addSaveCancelSection();

        addInitListener(this);
        addProcessListener(this);
        addValidationListener(this);
    }

    public void addSaveCancelSection() {
        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);
    }

    public SaveCancelSection getSaveCancelSection() {
        return saveCancelSection;
    }

    protected void addWidgets() {

        // add(new Label(GlobalizationUtil
        //     .globalize("cms.contenttypes.ui.title")));
        final TextField titleWidget = new TextField(new TrimmedStringParameter(
            TITLE));
        titleWidget.setLabel(new GlobalizedMessage("cms.contenttypes.ui.title",
                                                   CmsConstants.CMS_BUNDLE));
        titleWidget.addValidationListener(new NotNullValidationListener());
        titleWidget.setOnFocus(String.format(
            "if (this.form.%s.value == '') { "
                + " defaulting = true; this.form.%s.value = urlize(this.value); }",
            NAME, NAME));
        titleWidget.setOnKeyUp(String.format(
            "if (defaulting) { this.form.%s.value = urlize(this.value) }",
            NAME));
        add(titleWidget);

        //add(new Label(GlobalizationUtil
        //    .globalize("cms.contenttypes.ui.name")));
        final TextField nameWidget = new TextField(new TrimmedStringParameter(
            NAME));
        nameWidget.setLabel(new GlobalizedMessage("cms.contenttypes.ui.name",
                                                  CmsConstants.CMS_BUNDLE));
        nameWidget.addValidationListener(new NotNullValidationListener());
        nameWidget.addValidationListener(new URLTokenValidationListener());
        nameWidget.setOnFocus("defaulting = false");
        nameWidget.setOnBlur(String.format(
            "if (this.value == '') { "
                + "defaulting = true; this.value = urlize(this.form.%s.value) "
                + "}",
            TITLE));
        add(nameWidget);

        if (!CMSConfig.getConfig().isHideLaunchDate()) {
            //add(new Label(GlobalizationUtil
            //    .globalize("cms.ui.authoring.page_launch_date")));
            final ParameterModel launchDateParam
                                     = new DateParameter(LAUNCH_DATE);
            com.arsdigita.bebop.form.Date launchDate
                                              = new com.arsdigita.bebop.form.Date(
                    launchDateParam);
            if (CMSConfig.getConfig().isRequireLaunchDate()) {
                launchDate.addValidationListener(new NotNullValidationListener(
                    new GlobalizedMessage(
                        "cms.contenttypes.ui.mparticle.no_launch_date",
                        CmsConstants.CMS_BUNDLE)));
                // if launch date is required, help user by suggesting today's date
                launchDateParam.setDefaultValue(new Date());
            }
            launchDate.setLabel(new GlobalizedMessage(
                "cms.ui.authoring.page_launch_date",
                CmsConstants.CMS_BUNDLE));
            add(launchDate);
        }

        //add(new Label(GlobalizationUtil
        //    .globalize("cms.contenttypes.ui.summary")));
        final TextArea summaryWidget = new TextArea(
            new TrimmedStringParameter(SUMMARY));
        if (CMSConfig.getConfig().isMandatoryDescriptions()) {
            summaryWidget
                .addValidationListener(new NotEmptyValidationListener(
                    new GlobalizedMessage(
                        "cms.contenttypes.ui.description_missing",
                        CmsConstants.CMS_BUNDLE)));
        }
        summaryWidget.setLabel(new GlobalizedMessage(
            "cms.contenttypes.ui.summary",
            CmsConstants.CMS_BUNDLE));
        summaryWidget.setRows(5);
        summaryWidget.setCols(30);
        summaryWidget.setHint(new GlobalizedMessage(
            "cms.contenttypes.ui.summary_hint",
            CmsConstants.CMS_BUNDLE));
        add(summaryWidget);
    }

    @Override
    public abstract void init(final FormSectionEvent event)
        throws FormProcessException;

    @Override
    public abstract void process(final FormSectionEvent event)
        throws FormProcessException;

    @Override
    public abstract void validate(final FormSectionEvent event)
        throws FormProcessException;

    /**
     * Utility method to initialise the name/title/summary widgets.
     *
     * @param event
     *
     * @return
     */
    public MultiPartArticle initBasicWidgets(final FormSectionEvent event) {

        final FormData data = event.getFormData();
        final PageState state = event.getPageState();
        final MultiPartArticle article = (MultiPartArticle) itemSelectionModel
            .getSelectedObject(state);

        final Locale selectedLocale = SelectedLanguageUtil
            .selectedLocale(state, selectedLanguageParam);
        final MultiPartArticleFormController controller = CdiUtil
        .createCdiUtil()
        .findBean(MultiPartArticleFormController.class);

        if (article != null) {
            data.put(NAME, controller.getName(article, selectedLocale));
            data.put(TITLE, controller.getTitle(article, selectedLocale));
            if (!CMSConfig.getConfig().isHideLaunchDate()) {
                data.put(LAUNCH_DATE, article.getLaunchDate());
            }
            data.put(SUMMARY, controller.getSummary(article, selectedLocale));
        }

        return article;
    }

    /**
     * Utility method to process the name/title/summary widgets.
     *
     * @param event
     *
     * @return
     */
    public MultiPartArticle processBasicWidgets(final FormSectionEvent event) {

        final FormData data = event.getFormData();
        final PageState state = event.getPageState();
        final MultiPartArticle article = (MultiPartArticle) itemSelectionModel
            .getSelectedObject(state);

        if (article != null) {
            final Locale selectedLocale = SelectedLanguageUtil
                .selectedLocale(state, selectedLanguageParam);

            article.getName().addValue(selectedLocale,
                                       (String) data.get(NAME));
            article.getTitle().addValue(selectedLocale,
                                        (String) data.get(TITLE));
            if (!CMSConfig.getConfig().isHideLaunchDate()) {
                article.setLaunchDate((Date) data.get(LAUNCH_DATE));
            }
            article.getSummary().addValue(selectedLocale,
                                          (String) data.get(SUMMARY));
        }

        return article;
    }

    /**
     * Ensure that the name of an item is unique within a folder.
     *
     * @param folder the folder in which to check
     * @param event  the FormSectionEvent which was passed to the validation
     *               listener
     *
     * @return true if the name is not null and unique, false otherwise
     */
    public boolean validateNameUniqueness(final Folder folder,
                                          final FormSectionEvent event) {

        final FormData data = event.getFormData();
        final String name = (String) data.get(NAME);

        if (name != null) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ContentItemRepository itemRepo = cdiUtil
                .findBean(ContentItemRepository.class);

            final long result = itemRepo.countByNameInFolder(folder, name);

            return result == 0;
        }

        // false if name == null
        return false;
    }

    /**
     * Utility method to create a new MultiPartArticle and update the selected
     * model. This can be called in the process method of a ProcessListener.
     *
     * @param state   the current page state
     * @param name
     * @param section
     * @param folder
     * @param locale  Initial locale of the article.
     *
     * @return the new content item (or a proper subclass)
     *
     * @throws com.arsdigita.bebop.FormProcessException
     */
    public MultiPartArticle createArticle(final PageState state,
                                          final String name,
                                          final ContentSection section,
                                          final Folder folder,
                                          final Locale locale)
        throws FormProcessException {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ContentItemManager itemManager = cdiUtil
            .findBean(ContentItemManager.class);

        final MultiPartArticle article = itemManager
            .createContentItem(name,
                               section,
                               folder,
                               MultiPartArticle.class,
                               locale);

        if (itemSelectionModel.getSelectedKey(state) == null) {
            itemSelectionModel.setSelectedKey(state, article.getObjectId());
        }

        return article;
    }

    @Override
    public void generateXML(final PageState state, final Element parent) {

        m_script.generateXML(state, parent);
        super.generateXML(state, parent);
    }

}
