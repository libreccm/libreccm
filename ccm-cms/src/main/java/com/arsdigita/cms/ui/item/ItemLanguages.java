/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.item;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.StringParameter;

import org.librecms.contentsection.ContentItem;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.cms.ui.authoring.LanguageWidget;

import org.librecms.util.LanguageUtil;

import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.toolbox.ui.Section;
import com.arsdigita.util.Pair;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.URL;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.contentsection.ContentItemL10NManager;

import java.util.List;
import java.util.TooManyListenersException;
import java.util.stream.Collectors;

import org.libreccm.core.UnexpectedErrorException;
import org.librecms.CmsConstants;

/**
 * Displays the "Language instances" pane, with all the language instances in
 * the Bundle.
 *
 * @author Alan Pevec (apevec@redhat.com)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ItemLanguages extends LayoutPanel {

    private final ItemSelectionModel selectionModel;
//    private final SingleSelectionModel<String> selectedLanguage;
    private final LanguageWidget languageWidget;
    private final StringParameter selectedLanguageParam;
    //    private final Submit changeSubmit;
    private final Submit createSubmit;

    /**
     * Constructs a new <code>ItemLanguages</code>.
     *
     * @param selectionModel        the {@link ItemSelectionModel} which will
     *                              supply the current item
     * @param selectedLanguage      {@link SingleSelectionModel} for the
     *                              selected language.
     * @param selectedLanguageParam
     */
    public ItemLanguages(final ItemSelectionModel selectionModel,
                         //                         final SingleSelectionModel<String> selectedLanguage,
                         final StringParameter selectedLanguageParam) {

        this.selectionModel = selectionModel;
//        this.selectedLanguage = selectedLanguage;
        this.selectedLanguageParam = selectedLanguageParam;

        final Section section = new Section(gz("cms.ui.item.languages"));
        setBody(section);

        final ActionGroup group = new ActionGroup();
        section.setBody(group);

        group.setSubject(new ItemLanguagesTable(selectionModel,
                                                //                                                selectedLanguage,
                                                selectedLanguageParam));

        final Form form = new Form("newLanguage", new BoxPanel(
                                   BoxPanel.HORIZONTAL)) {

            @Override
            public boolean isVisible(final PageState state) {
                final ContentItem item = selectionModel.getSelectedItem(state);

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final ContentItemL10NManager l10NManager = cdiUtil.findBean(
                    ContentItemL10NManager.class);
                return !l10NManager.creatableLocales(item).isEmpty();
            }

        };
        group.addAction(form);

        form.setRedirecting(true);
        languageWidget = new LanguageWidget("language_widget") {

            @Override
            protected void setupOptions() {
                // Don't do anything.
            }

        };

        try {
            languageWidget.addPrintListener(new OptionPrinter());
        } catch (TooManyListenersException ex) {
            throw new UnexpectedErrorException(ex);
        }

        form.add(languageWidget);
//        changeSubmit = new Submit("change", gz("cms.ui.item.language.change"));
//        form.add(changeSubmit);
        createSubmit = new Submit("create", gz("cms.ui.item.language.add"));
        form.add(createSubmit);
        form.addProcessListener(new ProcessListener());
    }

    /**
     * Offers only languages not yet present in the bundle.
     */
    private class OptionPrinter implements PrintListener {

        @Override
        public final void prepare(final PrintEvent event) {
            final PageState state = event.getPageState();
            final OptionGroup target = (OptionGroup) event.getTarget();
            target.clearOptions();
            final ContentItem item = selectionModel.getSelectedItem(state);

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final LanguageUtil languageUtil = cdiUtil.findBean(
                LanguageUtil.class);
            final ContentItemL10NManager l10NManager = cdiUtil.findBean(
                ContentItemL10NManager.class);

            final List<String> creatableLangs = l10NManager.creatableLocales(
                item).stream()
                .map(locale -> locale.toString())
                .collect(Collectors.toList());
            final List<Pair> languages = languageUtil
                .convertToG11N(creatableLangs);

            for (final Pair pair : languages) {
                final String langCode = (String) pair.getKey();
                final GlobalizedMessage langName
                                            = (GlobalizedMessage) pair
                        .getValue();
                target.addOption(new Option(langCode, new Label(langName)));
            }
        }

    }

    /**
     * Adds a new language instance to the bundle.
     */
    private class ProcessListener implements FormProcessListener {

        @Override
        public final void process(final FormSectionEvent event)
            throws FormProcessException {

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();

            final PageState state = event.getPageState();
            final String language = (String) languageWidget.getValue(state);
            final ContentItem item = selectionModel.getSelectedItem(state);

            if (createSubmit.isSelected(state)) {

                final ItemLanguagesController controller = cdiUtil
                    .findBean(ItemLanguagesController.class);

                controller.addLanguage(item, language);

                // redirect to ContentItemPage.AUTHORING_TAB of the new instance
                state.setValue(selectedLanguageParam, language);
                final String langParam = String
                    .format("&%s=%s",
                            ContentItemPage.SELECTED_LANGUAGE,
                            language);
                final String target = String.join(
                    "",
                    URL.getDispatcherPath(),
                    controller.getItemEditUrl(item),
                    langParam);

                throw new RedirectSignal(target, true);
            }
        }

    }

    protected static final GlobalizedMessage gz(final String key) {
        return new GlobalizedMessage(key, CmsConstants.CMS_BUNDLE);
    }

    protected static final String lz(final String key) {
        return (String) gz(key).localize();
    }

}
