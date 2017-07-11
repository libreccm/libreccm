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
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.ArticleSection;
import com.arsdigita.cms.contenttypes.MultiPartArticle;
import com.arsdigita.cms.contenttypes.util.MPArticleGlobalizationUtil;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

/**
 * A form to confirm deletion of a single section of a MultiPartArticle.
 *
 * @author <a href="mailto:dturner@arsdigita.com">Dave Turner</a>
 * @version $Id: SectionDeleteForm.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class SectionDeleteForm extends Form
    implements FormInitListener, FormSubmissionListener, FormProcessListener
{
    private final static Logger log = Logger.getLogger(SectionDeleteForm.class.getName());

    protected ItemSelectionModel m_selArticle;
    protected ItemSelectionModel m_selSection;
    protected SaveCancelSection m_saveCancelSection;
    private Label m_sectionNameLabel;


    /**
     * 
     * @param selArticle
     * @param selSection 
     */
    public SectionDeleteForm 
        ( ItemSelectionModel selArticle,
          ItemSelectionModel selSection) {
        super("SectionDeleteForm", new ColumnPanel(2));
        m_selArticle = selArticle;
        m_selSection = selSection;

        ColumnPanel panel = (ColumnPanel)getPanel();
        panel.setBorder(false);
        panel.setPadColor("#FFFFFF");
        panel.setColumnWidth(1, "20%");
        panel.setColumnWidth(2, "80%");
        panel.setWidth("100%");

        m_sectionNameLabel = new Label ("Section Name");
        add(m_sectionNameLabel, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);
        addSaveCancelSection();

        addInitListener(this);
        addSubmissionListener(this);
        addProcessListener(this);
    }

    /**
     * 
     * @return 
     */
        protected SaveCancelSection addSaveCancelSection () {
        m_saveCancelSection = new SaveCancelSection();
        m_saveCancelSection.getSaveButton().setButtonLabel(
                GlobalizationUtil.globalize("cms.ui.delete"));
        add(m_saveCancelSection, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);
        return m_saveCancelSection;
    }

    @Override
    public void init ( FormSectionEvent event ) throws FormProcessException {
        PageState state = event.getPageState();

        ArticleSection section = (ArticleSection)m_selSection.getSelectedObject(state);

        if ( section == null ) {
            log.error("No section selected");
        } else {
            m_sectionNameLabel.setLabel(section.getTitle(),state);
        }
    }

    @Override
    public void submitted ( FormSectionEvent event ) throws FormProcessException {
        PageState state = event.getPageState();

        if ( m_saveCancelSection.getCancelButton().isSelected(state) ) {
            throw new FormProcessException(
                            "Submission cancelled",
                            MPArticleGlobalizationUtil.globalize(
                            "cms.contenttypes.ui.mparticle.submission_cancelled")
            );
        }
    }

    @Override
    public void process ( FormSectionEvent event ) throws FormProcessException {
        PageState state = event.getPageState();

        MultiPartArticle article = (MultiPartArticle)m_selArticle.getSelectedObject(state);
        ArticleSection section = (ArticleSection)m_selSection.getSelectedObject(state);

        Assert.exists(article, MultiPartArticle.class);
        Assert.exists(section, ArticleSection.class);

        article.removeSection(section);

        log.info("section " + m_selSection.getSelectedKey(state) + " delete");
    }
}
