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
package com.arsdigita.cms.ui.authoring.assets.relatedinfo;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.GlobalizedParameterListener;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.StringIsLettersOrDigitsValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.assets.AttachmentListSelectionModel;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.UnexpectedErrorException;
import org.librecms.CmsConstants;
import org.librecms.contentsection.AttachmentList;
import org.librecms.contentsection.AttachmentListL10NManager;
import org.librecms.contentsection.AttachmentListManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TooManyListenersException;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class RelatedInfoListForm
    extends Form
    implements FormInitListener,
               FormProcessListener,
               FormSubmissionListener {

    private final RelatedInfoStep relatedInfoStep;
    private final ItemSelectionModel itemSelectionModel;
    private final AttachmentListSelectionModel listSelectionModel;
    private final StringParameter selectedLanguage;

    private BoxPanel showLocalePanel;
    private SingleSelect showLocaleSelect;
    private Submit showLocaleSubmit;

    private BoxPanel addLocalePanel;
    private SingleSelect addLocaleSelect;
    private Submit addLocaleSubmit;

    private TextField nameField;
    private TextField titleField;
    private TextArea descriptionArea;

    private SaveCancelSection saveCancelSection;

    public RelatedInfoListForm(
        final RelatedInfoStep relatedInfoStep,
        final ItemSelectionModel itemSelectionModel,
        final AttachmentListSelectionModel listSelectionModel,
        final StringParameter selectedLanguageParam) {

        super("relatedinfo-list-form", new BoxPanel(BoxPanel.VERTICAL));

        this.relatedInfoStep = relatedInfoStep;
        this.itemSelectionModel = itemSelectionModel;
        this.listSelectionModel = listSelectionModel;
        this.selectedLanguage = selectedLanguageParam;

        showLocalePanel = new BoxPanel(BoxPanel.HORIZONTAL);
        final Label showLocaleLabel = new Label(event -> {

            final PageState state = event.getPageState();
            final Optional<AttachmentList> selectedList = getSelectedList(state);
            final Label target = (Label) event.getTarget();
            if (selectedList.isPresent()) {
                target.setLabel(new GlobalizedMessage(
                    "cms.ui.assetlist.show_locale",
                    CmsConstants.CMS_BUNDLE));
            } else {
                target.setLabel(new GlobalizedMessage(
                    "cms.ui.assetlist.initial_locale",
                    CmsConstants.CMS_BUNDLE));
            }
        });
        showLocaleSelect = new SingleSelect("selected-locale");
        try {
            showLocaleSelect.addPrintListener(event -> {

                final PageState state = event.getPageState();

                final Optional<AttachmentList> selectedList = getSelectedList(
                    state);
                if (selectedList.isPresent()) {
                    final SingleSelect target = (SingleSelect) event.getTarget();

                    target.clearOptions();;

                    final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                    final AttachmentListL10NManager l10NManager = cdiUtil
                        .findBean(AttachmentListL10NManager.class);
                    final List<Locale> availableLocales = new ArrayList<>(
                        l10NManager.availableLocales(selectedList.get()));
                    availableLocales.sort((locale1, locale2) -> {
                        return locale1.toString().compareTo(locale2.toString());
                    });
                    availableLocales.forEach(locale -> target.addOption(
                        new Option(locale.toString(),
                                   new Text(locale.toString()))));
                } else {
                    final SingleSelect target = (SingleSelect) event
                        .getTarget();

                    target.clearOptions();

                    final List<String> langs = new ArrayList<>(
                        KernelConfig.getConfig().getSupportedLanguages());
                    langs.sort((lang1, lang2) -> lang1.compareTo(lang2));

                    langs.forEach(lang -> {
                        target.addOption(new Option(lang, new Text(lang)));
                    });

                }

            });
        } catch (TooManyListenersException ex) {
            throw new UnexpectedErrorException(ex);
        }
        showLocaleSubmit = new Submit(new GlobalizedMessage(
            "cms.ui.assetlist.show_locale",
            CmsConstants.CMS_BUNDLE)) {

            @Override
            public boolean isVisible(final PageState state) {
                return getSelectedList(state).isPresent();
            }

        };
        showLocalePanel.add(showLocaleLabel);
        showLocalePanel.add(showLocaleSelect);
        showLocalePanel.add(showLocaleSubmit);
        super.add(showLocalePanel);

        addLocalePanel = new BoxPanel(BoxPanel.HORIZONTAL) {

            @Override
            public boolean isVisible(final PageState state) {
                return getSelectedList(state).isPresent();
            }

        };
        final Label addLocaleLabel = new Label(
            new GlobalizedMessage("cms.ui.assetlist.add_locale",
                                  CmsConstants.CMS_BUNDLE));
        addLocaleSelect = new SingleSelect("add-locale-select");
        try {
            addLocaleSelect.addPrintListener(event -> {

                final PageState state = event.getPageState();
                final Optional<AttachmentList> selectedList = getSelectedList(
                    state);
                if (selectedList.isPresent()) {
                    final SingleSelect target = (SingleSelect) event.getTarget();

                    target.clearOptions();

                    final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                    final AttachmentListL10NManager l10nManager = cdiUtil
                        .findBean(AttachmentListL10NManager.class);
                    final List<Locale> creatableLocales = new ArrayList<>(
                        l10nManager.creatableLocales(selectedList.get()));
                    creatableLocales.sort((locale1, locale2) -> {
                        return locale1
                            .toString()
                            .compareTo(locale2.toString());
                    });
                    creatableLocales.forEach(locale -> target.addOption(
                        new Option(locale.toString(),
                                   new Text(locale.toString()))));

                }

            });
        } catch (TooManyListenersException ex) {
            throw new UnexpectedErrorException(ex);
        }
        addLocaleSubmit = new Submit(new GlobalizedMessage(
            "cms.ui.assetlist.add_locale",
            CmsConstants.CMS_BUNDLE));
        addLocalePanel.add(addLocaleLabel);
        addLocalePanel.add(addLocaleSelect);
        addLocalePanel.add(addLocaleSubmit);
        super.add(addLocalePanel);

        super.add(new Label(new GlobalizedMessage("cms.ui.assetlist.name",
                                                  CmsConstants.CMS_BUNDLE)));
        nameField = new TextField("attachmentListName");
        nameField.addValidationListener(new AssetListNameValidator());
        super.add(nameField);

        super.add(new Label(new GlobalizedMessage("cms.ui.assetlist.title",
                                                  CmsConstants.CMS_BUNDLE)));
        titleField = new TextField("attachmentListTitle");
        super.add(titleField);

        super.add(new Label(
            new GlobalizedMessage("cms.ui.assetlist.description",
                                  CmsConstants.CMS_BUNDLE)));
        descriptionArea = new TextArea("attachmentListDesc");
        super.add(descriptionArea);

        saveCancelSection = new SaveCancelSection();
        super.add(saveCancelSection);

        super.addInitListener(this);
        super.addProcessListener(this);
        super.addSubmissionListener(this);
    }

    protected Optional<AttachmentList> getSelectedList(final PageState state) {

        if (listSelectionModel.getSelectedKey(state) == null) {
            return Optional.empty();
        } else {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final AttachmentListManager manager = cdiUtil
                .findBean(AttachmentListManager.class);
            final AttachmentList list = manager
                .getAttachmentList(listSelectionModel.getSelectedKey(state))
                .orElseThrow(() -> new IllegalArgumentException(String
                .format("No AttachmentList with ID %d in the database.",
                        listSelectionModel.getSelectedKey(state))));
            return Optional.of(list);
        }
    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {

        final PageState state = event.getPageState();

        final Optional<AttachmentList> selectedList = getSelectedList(state);

        if (selectedList.isPresent()) {

            nameField.setValue(state, selectedList.get().getName());

            showLocaleSelect.setValue(state,
                                      KernelConfig
                                          .getConfig()
                                          .getDefaultLocale()
                                          .toString());

            titleField.setValue(state,
                                selectedList
                                    .get()
                                    .getTitle()
                                    .getValue(getSelectedLocale(state)));

            descriptionArea.setValue(state,
                                     selectedList
                                         .get()
                                         .getTitle()
                                         .getValue(getSelectedLocale(state)));
        } else {
            showLocaleSelect.setValue(state,
                                      KernelConfig
                                          .getConfig()
                                          .getDefaultLocale()
                                          .toString());
        }
    }

    protected Locale getSelectedLocale(final PageState state) {
        final String selected = (String) showLocaleSelect.getValue(state);
        if (selected == null) {
            return KernelConfig.getConfig().getDefaultLocale();
        } else {
            return new Locale(selected);
        }
    }

    @Override
    public void process(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();

        if (showLocaleSubmit.isSelected(state)) {

            return;
        }

        if (addLocaleSubmit.isSelected(state)) {
            final AttachmentListL10NManager l10nManager = cdiUtil
                .findBean(AttachmentListL10NManager.class);
            final Locale add = new Locale((String) addLocaleSelect
                .getValue(state));
            final Optional<AttachmentList> selectedList = getSelectedList(state);
            l10nManager.addLanguage(selectedList.get(), add);
        }

        if (saveCancelSection.getSaveButton().isSelected(state)) {

            final RelatedInfoStepController controller = cdiUtil
                .findBean(RelatedInfoStepController.class);
            final AttachmentListManager attachmentListManager = cdiUtil
                .findBean(AttachmentListManager.class);

            final Optional<AttachmentList> selectedList = getSelectedList(state);
            final AttachmentList attachmentList;
            if (selectedList.isPresent()) {
                attachmentList = selectedList.get();
            } else {
                attachmentList = attachmentListManager
                    .createAttachmentList(itemSelectionModel
                        .getSelectedItem(state),
                                          (String) nameField.getValue(state));
            }

            attachmentList.setName((String) nameField.getValue(state));
            attachmentList
                .getTitle()
                .addValue(getSelectedLocale(state),
                          (String) titleField.getValue(state));
            attachmentList
                .getDescription()
                .addValue(getSelectedLocale(state),
                          (String) descriptionArea.getValue(state));

            controller.saveAttachmentList(attachmentList);
        }
    }

    @Override
    public void submitted(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();

        if (saveCancelSection.getCancelButton().isSelected(state)) {
            listSelectionModel.clearSelection(state);
            relatedInfoStep.showAttachmentListTable(state);
        }
    }

    private class AssetListNameValidator extends GlobalizedParameterListener {

        public AssetListNameValidator() {
            super.setError(new GlobalizedMessage(
                "cms.ui.assetlist.name_cant_start_with_dot",
                CmsConstants.CMS_BUNDLE));
        }

        @Override
        public void validate(final ParameterEvent event) throws
            FormProcessException {

            final ParameterData data = event.getParameterData();
            final String value = (String) data.getValue();

            if (value.startsWith(".")) {
                data.addError(getError());
            }
        }

    }

}
