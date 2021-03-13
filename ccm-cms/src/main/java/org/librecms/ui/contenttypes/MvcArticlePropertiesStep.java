/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contenttypes;

import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemL10NManager;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.FolderManager;
import org.librecms.contenttypes.Article;
import org.librecms.ui.contentsections.documents.AuthoringStepPathFragment;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.ws.rs.Path;

import org.librecms.ui.contentsections.documents.MvcAuthoringStep;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/")
@AuthoringStepPathFragment(MvcArticlePropertiesStep.PATH_FRAGMENT)
public class MvcArticlePropertiesStep implements MvcAuthoringStep {

    static final String PATH_FRAGMENT = "basicproperties";

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private ContentItemManager itemManager;


    @Inject
    private FolderManager folderManager;

    @Inject
    private GlobalizationHelper globalizationHelper;

    private ContentSection section;

    private Article document;

    @Override
    public Class<? extends ContentItem> supportedDocumenType() {
        return Article.class;
    }

    @Override
    public String getLabel() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("authoringsteps.basicproperties.label");
    }

    @Override
    public String getDescription() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("authoringsteps.basicproperties.description");
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
    public ContentItem getContentItem() {
        return document;
    }

    @Override
    public String getContentItemPath() {
        return itemManager.getItemPath(document);
    }

    @Override
    public String getContentItemTitle() {
        return globalizationHelper
            .getValueFromLocalizedString(document.getTitle());
    }

    @Override
    public void setContentItem(final ContentItem document) {
        if (!(document instanceof Article)) {
            throw new UnexpectedErrorException("Not an article.");
        }
        this.document = (Article) document;
    }

    @Override
    public String showStep() {
        return "org/librecms/ui/contenttypes/article/article-basic-properties.xhtml";

    }

    public String getName() {
        return document.getDisplayName();
    }

    @POST
    @Path("/name/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateName(@FormParam("name") final String name) {
        document.setDisplayName(name);
        itemRepo.save(document);
        return String.format(
            "redirect:/@documents/%s/%s/%s/@edit/%s",
            section.getLabel(),
            folderManager.getFolderPath(
                itemManager.getItemFolder(document).get()
            ),
            name,
            PATH_FRAGMENT
        );
    }

    public Map<String, String> getTitleValues() {
        return document
            .getTitle()
            .getValues()
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    entry -> entry.getKey().toString(),
                    entry -> entry.getValue()
                )
            );
    }

    @POST
    @Path("/title/@add")
    @Transactional(Transactional.TxType.REQUIRED)
    public String addTitle(
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Locale locale = new Locale(localeParam);
        document.getTitle().addValue(locale, value);
        itemRepo.save(document);

        return String.format(
            "redirect:/@documents/%s/%s/@edit/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

    @POST
    @Path("/title/@edit/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String editTitle(
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Locale locale = new Locale(localeParam);
        document.getTitle().addValue(locale, value);
        itemRepo.save(document);

        return String.format(
            "redirect:/@documents/%s/%s/@edit/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

    @POST
    @Path("/title/@remove/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeTitle(
        @PathParam("locale") final String localeParam
    ) {
        final Locale locale = new Locale(localeParam);
        document.getTitle().removeValue(locale);
        itemRepo.save(document);

        return String.format(
            "redirect:/@documents/%s/%s/@edit/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

    public Map<String, String> getDescriptionValues() {
        return document
            .getDescription()
            .getValues()
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    entry -> entry.getKey().toString(),
                    entry -> entry.getValue()
                )
            );
    }

    @POST
    @Path("/title/@add")
    @Transactional(Transactional.TxType.REQUIRED)
    public String addDescription(
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Locale locale = new Locale(localeParam);
        document.getDescription().addValue(locale, value);
        itemRepo.save(document);

        return String.format(
            "redirect:/@documents/%s/%s/@edit/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

    @POST
    @Path("/title/@edit/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String editDescription(
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Locale locale = new Locale(localeParam);
        document.getDescription().addValue(locale, value);
        itemRepo.save(document);

        return String.format(
            "redirect:/@documents/%s/%s/@edit/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

    @POST
    @Path("/title/@remove/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeDescription(
        @PathParam("locale") final String localeParam
    ) {
        final Locale locale = new Locale(localeParam);
        document.getDescription().removeValue(locale);
        itemRepo.save(document);

        return String.format(
            "redirect:/@documents/%s/%s/@edit/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

}
