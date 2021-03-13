/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contenttypes;

import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.workflow.Workflow;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderManager;
import org.librecms.contenttypes.Article;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.librecms.ui.contentsections.documents.MvcDocumentCreateStep;

import java.util.Locale;
import java.util.Optional;

import javax.inject.Inject;
import javax.mvc.Models;
import javax.ws.rs.FormParam;

/**
 * Describes the create step for {@link Article}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named
public class MvcArticleCreateStep implements MvcDocumentCreateStep<Article> {

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private FolderManager folderManager;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private Models models;

    private ContentSection section;

    private Folder folder;

    @FormParam("name")
    private String name;

    @FormParam("title")
    private String title;

    @FormParam("summary")
    private String summary;

    @FormParam("locale")
    private String initialLocale;

    @FormParam("selectedWorkflow")
    private String selectedWorkflow;

    @Override
    public Class<Article> getDocumentType() {
        return Article.class;
    }

    @Override
    public String getDescription() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("createstep.description");
    }

    @Override
    public String getBundle() {
        return ArticleStepsConstants.BUNDLE;
    }

    @Override
    public ContentSection getContentSection() {
        return section;
    }
    
    @Override
    public String getContentSectionLabel() {
        return section.getLabel();
    }
    
    @Override
    public String getContentSectionTitle() {
        return globalizationHelper
            .getValueFromLocalizedString(section.getTitle());
    }
    
    @Override
    public void setContentSection(final ContentSection section) {
        this.section = section;
    }
    
    @Override
    public Folder getFolder() {
        return folder;
    }
    
    @Override
    public String getFolderPath() {
        return folderManager.getFolderPath(folder);
    }

    @Override
    public void setFolder(final Folder folder) {
        this.folder = folder;
    }

    @Override
    public String showCreateForm() {
        return "org/librecms/ui/contenttypes/article/create-article.xhtml";
    }

    @Override
    public String createContentItem() {
        final Optional<Workflow> workflowResult = section
            .getWorkflowTemplates()
            .stream()
            .filter(template -> template.getUuid().equals(selectedWorkflow))
            .findAny();

        if (!workflowResult.isPresent()) {
            models.put("section", section.getLabel());
            models.put("selectedWorkflow", selectedWorkflow);

            return "org/librecms/ui/contentsection/documents/workflow-not-available.xhtml";
        }

        final Locale locale = new Locale(initialLocale);

        final Article article = itemManager.createContentItem(
            name,
            section,
            folder,
            workflowResult.get(),
            Article.class,
            locale
        );

        article.getTitle().addValue(locale, title);
        article.getDescription().addValue(locale, summary);
        itemRepo.save(article);

        return String.format(
            "redirect:/%s/documents/%s/%s/@edit/basicproperties",
            section.getLabel(),
            folderManager.getFolderPath(folder),
            name
        );
    }

}
