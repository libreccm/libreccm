package com.arsdigita.cms.ui.authoring.news;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SelectedLanguageUtil;
import com.arsdigita.cms.ui.authoring.TextBody;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.kernel.KernelConfig;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contenttypes.News;

import java.util.Locale;

import static com.arsdigita.cms.ui.authoring.TextBody.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class NewsTextBody extends TextBody {

    private final ItemSelectionModel itemSelectionModel;
    private final StringParameter selectedLanguageParam;

    public NewsTextBody(final ItemSelectionModel itemSelectionModel,
                        final AuthoringKitWizard authoringKitWizard,
                        final StringParameter selectedLanguageParam) {

        super(itemSelectionModel, selectedLanguageParam);

        this.itemSelectionModel = itemSelectionModel;
        this.selectedLanguageParam = selectedLanguageParam;

        // Rest the component when it is hidden
        authoringKitWizard
            .getList()
            .addActionListener(event -> reset(event.getPageState()));

        // Set the right component access on the forms
        final Component uploadComponent = getComponent(FILE_UPLOAD);
        if (uploadComponent != null) {
            setComponentAccess(FILE_UPLOAD,
                               new WorkflowLockedComponentAccess(
                                   uploadComponent, itemSelectionModel));
        }
        final Component textEntryComponent = getComponent(TEXT_ENTRY);
        setComponentAccess(TEXT_ENTRY,
                           new WorkflowLockedComponentAccess(
                               textEntryComponent, itemSelectionModel));
    }

    /**
     * Adds the options for the mime type select widget of
     * <code>GenericArticleForm</code> and sets the default mime type.
     *
     * @param mimeSelect
     */
    @Override
    protected void setMimeTypeOptions(final SingleSelect mimeSelect) {
        mimeSelect.addOption(new Option("text/html", "HTML Text"));
        mimeSelect.setOptionSelected("text/html");
    }

    protected News getSelectedNews(final PageState state) {

        return (News) itemSelectionModel.getSelectedItem(state);
    }

    @Override
    protected String getTextPropertyName() {
        return "text";
    }

    @Override
    public String getText(final PageState state) {

        final News news = getSelectedNews(state);

        return news
            .getText()
            .getValue(SelectedLanguageUtil
                .selectedLocale(state, selectedLanguageParam));
    }

    @Override
    protected void updateText(final PageState state, final String text) {

        final News news = getSelectedNews(state);

        final Locale selectedLocale = SelectedLanguageUtil
            .selectedLocale(state, selectedLanguageParam);

        news.getText().addValue(selectedLocale, text);
        final ContentItemRepository itemRepo = CdiUtil
            .createCdiUtil()
            .findBean(ContentItemRepository.class);
        itemRepo.save(news);
    }

}
