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

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.mvc.Models;
import javax.ws.rs.FormParam;

/**
 * Describes the create step for {@link Article}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsArticleCreateStep")
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

    private SortedMap<String, String> messages;

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

    public MvcArticleCreateStep() {
        messages = new TreeMap<>();
    }

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
    public Map<String, String> getMessages() {
        return Collections.unmodifiableSortedMap(messages);
    }

    public void addMessage(final String context, final String message) {
        messages.put(context, message);
    }

    public void setMessages(final SortedMap<String, String> messages) {
        this.messages = new TreeMap<>(messages);
    }

//    @GET
//    @Path("/")
    @Override
    public String showCreateForm() {
        return "org/librecms/ui/contenttypes/article/create-article.xhtml";
    }

//    @POST
//    @Path("/")
    @Override
    public String createContentItem() {
        if (name == null || name.isEmpty()) {
            messages.put(
                "danger",
                globalizationHelper.getLocalizedTextsUtil(
                    getBundle()
                ).getText("createstep.name.error.missing")
            );
        }

        if (!name.matches("^([a-zA-Z0-9_-]*)$")) {
            messages.put(
                "danger",
                globalizationHelper.getLocalizedTextsUtil(
                    getBundle()
                ).getText("createstep.name.error.invalid")
            );
        }

        if (title == null || title.isEmpty()) {
            messages.put(
                "danger",
                globalizationHelper.getLocalizedTextsUtil(
                    getBundle()
                ).getText("createstep.title.error.missing")
            );
        }

        if (summary == null || summary.isEmpty()) {
            messages.put(
                "danger",
                globalizationHelper.getLocalizedTextsUtil(
                    getBundle()
                ).getText("createstep.summary.error.missing")
            );
        }

        if (initialLocale == null || initialLocale.isEmpty()) {
            messages.put(
                "danger",
                globalizationHelper.getLocalizedTextsUtil(
                    getBundle()
                ).getText("createstep.initial_locale.error.missing")
            );
        }

        final Optional<Workflow> workflowResult = section
            .getWorkflowTemplates()
            .stream()
            .filter(template -> template.getUuid().equals(selectedWorkflow))
            .findAny();

        if (!workflowResult.isPresent()) {
            messages.put(
                "danger",
                globalizationHelper.getLocalizedTextsUtil(
                    getBundle()
                ).getText("createstep.workflow.error.not_available")
            );
        }

        if (!messages.isEmpty()) {
            final String folderPath = getFolderPath();
            if (folderPath.isEmpty() || "/".equals(folderPath)) {
                return String.format(
                    "/@contentsections/%s/documents/@create/%s",
                    section.getLabel(),
                    getDocumentType().getName()
                );
            } else {
                return String.format(
                    "/@contentsections/%s/documents/%s/@create/%s",
                    section.getLabel(),
                    folderPath,
                    getDocumentType().getName()
                );
            }
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

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getInitialLocale() {
        return initialLocale;
    }

    public String getSelectedWorkflow() {
        if (selectedWorkflow == null || selectedWorkflow.isEmpty()) {
            return section
                .getContentTypes()
                .stream()
                .filter(
                    type -> type.getContentItemClass().equals(
                        Article.class.getName()
                    )
                )
                .findAny()
                .map(type -> type.getDefaultWorkflow())
                .map(
                    workflow -> globalizationHelper.getValueFromLocalizedString(
                        workflow.getName()
                    )
                )
                .orElse("");
        } else {
            return selectedWorkflow;
        }
    }

    public Map<String, String> getAvailableLocales() {
        return globalizationHelper
            .getAvailableLocales()
            .stream()
            .collect(Collectors.toMap(
                Locale::toString,
                locale -> locale.getDisplayLanguage(
                    globalizationHelper.getNegotiatedLocale()
                )
            ));
    }

    public Map<String, String> getAvailableWorkflows() {
        return section
            .getWorkflowTemplates()
            .stream()
            .collect(
                Collectors.toMap(
                    workflow -> workflow.getUuid(),
                    workflow -> globalizationHelper.getValueFromLocalizedString(
                        workflow.getName()
                    )
                )
            );
    }

}
