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
package com.arsdigita.cms.contenttypes.ui.mparticle;


import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.cms.ReusableImageAsset;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.TextAsset;
import com.arsdigita.cms.contenttypes.ArticleSection;
import com.arsdigita.cms.contenttypes.MultiPartArticle;
import com.arsdigita.cms.ui.CMSDHTMLEditor;
import com.arsdigita.cms.contenttypes.util.MPArticleGlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;

import java.math.BigDecimal;


/**
 * Form to edit an ArticleSection for a MultiPartArticle.
 *
 * @author <a href="mailto:dturner@arsdigita.com">Dave Turner</a>
 * @version $Id: SectionEditForm.java 1423 2006-12-19 22:08:04Z apevec $
 */
public class SectionEditForm extends Form {

    private final static Logger log = Logger.getLogger(SectionEditForm.class);

    private ItemSelectionModel m_selArticle;
    private ItemSelectionModel m_selSection;

    private BigDecimalParameter m_imageParam;
    private ItemSelectionModel m_selImage;

    private BigDecimalParameter m_textParam;
    private ItemSelectionModel m_selText;
    private MultiPartArticleViewSections m_container;

    private SaveCancelSection m_saveCancelSection;
    private ImageUploadSection m_imageUpload;


    public static final String TITLE        = "title";
    public static final String TEXT         = "text";
    public static final String IMAGE        = "image";
    public static final String PAGE_BREAK   = "pageBreak";

    private static final String TEXT_PARAM  = "textParam";
    private static final String IMAGE_PARAM = "imageParam";

    /**
     * Constructor.
     *
     * @param selArticle the current article
     * @param selSection the current section
     */
    public SectionEditForm(ItemSelectionModel selArticle,
                           ItemSelectionModel selSection) {
        this(selArticle, selSection, null);
    }
    /**
     * Constructor.
     *
     * @param selArticle the current article
     * @param selSection the current section
     * @param container container which this form is added to
     */
    public SectionEditForm(ItemSelectionModel selArticle,
                           ItemSelectionModel selSection,
                           MultiPartArticleViewSections container) {
        super("SectionEditForm", new ColumnPanel(2));
        m_selArticle = selArticle;
        m_selSection = selSection;
        m_container = container;

        m_imageParam = new BigDecimalParameter(IMAGE_PARAM);
        m_selImage = new ItemSelectionModel(ReusableImageAsset.class.getName(),
                                            ReusableImageAsset.BASE_DATA_OBJECT_TYPE,
                                            m_imageParam);

        m_textParam = new BigDecimalParameter(TEXT_PARAM);
        m_selText = new ItemSelectionModel(TextAsset.class.getName(),
                                           TextAsset.BASE_DATA_OBJECT_TYPE,
                                           m_textParam);

        setMethod(Form.POST);
        setEncType("multipart/form-data");

        ColumnPanel panel = (ColumnPanel)getPanel();
        panel.setBorder(false);
        panel.setPadColor("#FFFFFF");
        panel.setColumnWidth(1, "20%");
        panel.setColumnWidth(2, "80%");
        panel.setWidth("100%");

        addWidgets();
        addSaveCancelSection();

        addInitListener(new SectionInitListener());
        addSubmissionListener(new SectionSubmissionListener());
        addProcessListener(new SectionProcessListener());
    }

    /**
     * Instantiate and add a save/cancel section to the form.
     *
     * @return the SaveCancelSection that was added
     */
    protected SaveCancelSection addSaveCancelSection() {
        m_saveCancelSection = new SaveCancelSection();
        add(m_saveCancelSection, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);
        return m_saveCancelSection;
    }

    /**
     * Returns the save/cancel section from this form.
     */
    public SaveCancelSection getSaveCancelSection() {
        return m_saveCancelSection;
    }

    /**
     * Add form widgets for a Section.
     */
    protected void addWidgets() {

        //add(new Label(MPArticleGlobalizationUtil
        //              .globalize("cms.contenttypes.ui.mparticle.section.title")));
        TextField titleWidget = new TextField(
                                    new TrimmedStringParameter(TITLE));
        titleWidget.addValidationListener(new NotNullValidationListener());
        titleWidget.setLabel(MPArticleGlobalizationUtil
                      .globalize("cms.contenttypes.ui.mparticle.section.title"));
        add(titleWidget);

        //add(new Label(MPArticleGlobalizationUtil
        //              .globalize("cms.contenttypes.ui.mparticle.section.text")),
        //    ColumnPanel.LEFT | ColumnPanel.FULL_WIDTH);
        CMSDHTMLEditor textWidget =
            new CMSDHTMLEditor(new TrimmedStringParameter(TEXT));
        textWidget.setLabel(MPArticleGlobalizationUtil
                      .globalize("cms.contenttypes.ui.mparticle.section.text"));
        textWidget.setRows(40);
        textWidget.setCols(70);
        textWidget.setWrap(CMSDHTMLEditor.SOFT);
        add(textWidget,
            ColumnPanel.LEFT | ColumnPanel.FULL_WIDTH);

        //add(new Label(MPArticleGlobalizationUtil
        //            .globalize("cms.contenttypes.ui.mparticle.section.image")),
        //  ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);
        m_imageUpload = new ImageUploadSection("image", m_selImage);
        m_imageUpload.setLabel(MPArticleGlobalizationUtil
                      .globalize("cms.contenttypes.ui.mparticle.section.image"));
        add(m_imageUpload, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);

        //add(new Label());
        CheckboxGroup pageBreak = new CheckboxGroup(PAGE_BREAK);
        pageBreak.addOption(new Option("true", 
                new Label(MPArticleGlobalizationUtil
                .globalize("cms.contenttypes.ui.mparticle.section.create_break")) ));
        add(pageBreak);
    }

    /**
     * Utility method to create a Section from the form data supplied.
     * 
     * @param event
     * @param article
     * @return      
     */
    protected ArticleSection createSection(FormSectionEvent event,
                                           MultiPartArticle article) {

        PageState state = event.getPageState();
        FormData data = event.getFormData();

        ArticleSection section = new ArticleSection();

        section.setTitle((String)data.get(TITLE));
        section.setName(article.getName() + ": " + (String)data.get(TITLE));
        section.setContentSection(article.getContentSection());

        return section;
    }

    /**
     * 
     * @param p 
     */
    @Override
    public void register(Page p) {
        super.register(p);
        p.addGlobalStateParam(m_imageParam);
        p.addGlobalStateParam(m_textParam);
    }


    /**
     * Initialize the form.  If there is a selected section, ie. this
     * is an 'edit' step rather than a 'create new' step, load the data
     * into the form fields.
     */
    private class SectionInitListener implements FormInitListener {

        @Override
        public void init( FormSectionEvent event )
            throws FormProcessException {
            PageState state = event.getPageState();
            FormData data = event.getFormData();
            m_selImage.setSelectedObject(state, null);
            m_selText.setSelectedObject(state,null);


            if ( m_selSection.getSelectedKey(state) != null ) {
                BigDecimal id = new BigDecimal(m_selSection
                                               .getSelectedKey(state).toString());
                try {
                    // retrieve the selected Section from the persistence layer
                    ArticleSection section = new ArticleSection(id);

                    data.put(TITLE, section.getTitle());

                    TextAsset t = section.getText();
                    if ( t != null ) {
                        m_selText.setSelectedObject(state, t);
                        data.put(TEXT, t.getText());
                    }

                    ReusableImageAsset img = section.getImage();
                    if (img != null) {
                        m_selImage.setSelectedObject(state, img);
                    }

                    if (section.isPageBreak()) {
                        data.put(PAGE_BREAK, new Object[] { "true" });
                    }

                } catch ( DataObjectNotFoundException ex ) {
                    log.error("Section(" + id + ") could not be found");
                }
            }

            // Wait until the image selection model is updated before
            // initializing the image section
            m_imageUpload.initImageUpload(event);
        }
    }


    /**
     * Called on form submission.  Check to see if the user clicked the
     * cancel button.  If they did, don't continue with the form.
     */
    private class SectionSubmissionListener implements FormSubmissionListener {

        @Override
        public void submitted( FormSectionEvent event )
            throws FormProcessException {
            PageState state = event.getPageState();

            if ( m_saveCancelSection.getCancelButton()
                 .isSelected(state) && m_container != null) {
                m_container.onlyShowComponent(
                    state, MultiPartArticleViewSections.SECTION_TABLE+
                    m_container.getTypeIDStr());
                throw new FormProcessException(
                      "Submission cancelled",
                      MPArticleGlobalizationUtil.globalize(
                          "cms.contenttypes.ui.mparticle.submission_cancelled")
                );
            } else if ( m_imageUpload.getDeleteImageButton().isSelected(state) ) {
                BigDecimal id = new BigDecimal(m_selSection
                        .getSelectedKey(state).toString());
            	log.debug("deleting image for MPA section " + id);
                try {
                	ArticleSection section = new ArticleSection(id);
                	section.setImage(null);
                } catch ( DataObjectNotFoundException ex ) {
                    log.error("Section(" + id + ") could not be found");
                }

            }
        }
    }

    /**
     * Called after form has been validated.  Create the new ArticleSection and
     * assign it to the current MultiPartArticle.
     */
    private class SectionProcessListener implements FormProcessListener {

        @Override
        public void process( FormSectionEvent event )
            throws FormProcessException {
            PageState state = event.getPageState();
            FormData data = event.getFormData();

            // retrieve the current MultiPartArticle
            BigDecimal id = new BigDecimal(
                m_selArticle.getSelectedKey(state).toString());
            MultiPartArticle article = null;

            try {
                article = new MultiPartArticle(id);
            } catch ( DataObjectNotFoundException ex ) {
                throw new UncheckedWrapperException(ex);
            }

            // get the selected section to update or create a new one
            ArticleSection section = (ArticleSection)
                m_selSection.getSelectedObject(state);
            if ( section == null ) {
                section = createSection(event, article);
                article.addSection(section);
            }

            section.setTitle((String)data.get(TITLE));

            Object[] pageBreakVal = (Object[])data.get(PAGE_BREAK);
            boolean pageBreak;
            if (pageBreakVal == null ||
                pageBreakVal.length == 0 ||
                !"true".equals(pageBreakVal[0])) {
                pageBreak = false;
            } else {
                pageBreak = true;
            }
            section.setPageBreak(pageBreak);

            // get the image asset
            ReusableImageAsset reusableImageAsset = 
                               m_imageUpload.processImageUpload(event);
            if ( reusableImageAsset != null ) {
                section.setImage(reusableImageAsset);
                m_selImage.setSelectedObject(state, reusableImageAsset);
            }


            // get the text asset
            TextAsset textAsset = (TextAsset)m_selText.getSelectedObject(state);
            if ( textAsset == null ) {
                textAsset = new TextAsset();
                textAsset.setName(section.getName() + " text");
                m_selText.setSelectedObject(state, textAsset);
                section.setText(textAsset);
            }

            String text = (String)data.get(TEXT);
            if ( text == null ) {
                text = "";
            }

            textAsset.setText(text);
            if ( m_container != null) {
                m_container.onlyShowComponent(
                    state,
                    MultiPartArticleViewSections.SECTION_TABLE+
                    m_container.getTypeIDStr());
            }
        }
    }

}
