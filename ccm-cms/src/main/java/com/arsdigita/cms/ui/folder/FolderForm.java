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
package com.arsdigita.cms.ui.folder;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.bebop.parameters.URLTokenValidationListener;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.CmsConstants;

/**
 * Class FolderForm implements the basic form for creating or editing folders.
 *
 * Originally folders were ContentItems. For CCM NG we changed that and now use
 * Categories to provide the folder hierarchy. Thus this class originally
 * extended the {@link BasicItemForm} class. Now this class is separate. Some
 * parts have been created by simply coping the relevant parts from
 * {@link BasicItemForm}. Therefore it is possible that comments etc still refer
 * to {@link BasicItemForm}.
 *
 * @author <a href="mailto:jorris@arsdigita.com">Jon Orris</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class FolderForm extends FormSection
        implements FormInitListener,
                   FormProcessListener,
                   FormValidationListener {

    public static final Logger LOGGER = LogManager.getLogger(FolderForm.class);

    public static final String FOLDER_ID = "FolderId";
    public static final String NAME = "FolderName";
    public static final String TITLE = "FolderTitle";
    public static final String DESC = "FolderDesc";

    private final FolderSelectionModel currentFolder;
    private final SaveCancelSection saveCancelSection;
    private final FormSection widgetSection;

    /**
     * Currently, to insert javascript code the Label Widget is "abused".
     */
    private final Label m_script = new Label(String.format(
            "<script language=\"javascript\" src=\"%s/javascript/manipulate-input.js\"></script>",
            Web.getWebappContextPath()),
                                             false);

    /**
     * Create a new folder form.
     *
     * @param name Name of the form
     * @param currentFolder SelectionModel containing the current folder being
     * operated on.
     *
     * @pre name != null && folder != null
     */
    public FolderForm(final String name,
                      final FolderSelectionModel currentFolder) {
        super(new ColumnPanel(2));

        widgetSection = new FormSection(new ColumnPanel(2, true));

        super.add(widgetSection, ColumnPanel.INSERT);

        this.currentFolder = currentFolder;

        final ColumnPanel panel = (ColumnPanel) getPanel();
        panel.setBorder(false);
        panel.setPadColor("#FFFFFF");
        panel.setColumnWidth(1, "20%");
        panel.setColumnWidth(2, "80%");
        panel.setWidth("100%");

        /* Add basic contents */
        addWidgets();

        saveCancelSection = new SaveCancelSection();
        super.add(saveCancelSection,
                  ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);

        addInitListener(this);
        addProcessListener(this);
        addValidationListener(this);
    }

    /**
     * Constructs a new {@code FolderForm} with a specified number of
     * {@link ColumnPanel}s and without content.
     *
     * @param formName
     * @param columnPanel
     * @param currentFolder
     */
    public FolderForm(final String formName,
                      final ColumnPanel columnPanel,
                      final FolderSelectionModel currentFolder) {
        super(columnPanel);

        widgetSection = new FormSection(
                new ColumnPanel(columnPanel.getNumCols()));
        super.add(widgetSection, ColumnPanel.INSERT);
        this.currentFolder = currentFolder;

        saveCancelSection = new SaveCancelSection();
        super.add(saveCancelSection,
                  ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);
    }

    /**
     * Add basic widgets to the form.
     *
     * Widgets added are 'title' and 'name (url)'. Child classes will override
     * this method to perform all their widget-adding needs but are supposed to
     * use super() to add the basic widgets.
     */
    protected void addWidgets() {
        final Hidden id = new Hidden(FOLDER_ID);
        add(id);

        // JavaScript auto-name generation is off by default.
        // It is turned on under the following circumstances
        //
        // * If the name is null, upon starting edit of the title
        // * If the name is null, upon finishing edit of name
        //
        // The rationale is that, auto-name generation is useful
        // if the name is currently null, but once a name has been
        // created you don't want to subsequently change it since
        // it breaks URLs & potentially overwrites the user's
        // customizations.
        final TextField titleWidget = new TextField(new TrimmedStringParameter(
                TITLE));
        titleWidget.setLabel(getTitleLabel());
        titleWidget.setHint(getTitleHint());
        titleWidget.addValidationListener(new NotNullValidationListener());
        titleWidget.setOnFocus("if (this.form." + NAME + ".value == '') { "
                                       + " defaulting = true; this.form." + NAME
                                       + ".value = urlize(this.value); }");
        titleWidget.setOnKeyUp(
                "if (defaulting) { this.form." + NAME
                        + ".value = urlize(this.value) }");
        add(titleWidget);

        final TextField nameWidget = new TextField(new TrimmedStringParameter(
                NAME));
        nameWidget.setLabel(getNameLabel());
        nameWidget.setHint(getNameHint());

        // We just check parameter specific properties here! Additionally, 
        // context properties as uniqueness in folder must be validated 
        // for the form es the whole (using the validate method required by
        // implementing FormValidationListener in this form.
        nameWidget.addValidationListener(new NotNullValidationListener());
        nameWidget.addValidationListener(new URLTokenValidationListener());
        nameWidget.setMaxLength(190);
        nameWidget.setOnFocus("defaulting = false");
        nameWidget.setOnBlur(
                "if (this.value == '') "
                        + "{ defaulting = true; this.value = urlize(this.form."
                        + TITLE
                        + ".value) } "
                        + " else { this.value = urlize(this.value); }");
        add(nameWidget);
    }

    @Override
    public void register(final Page page) {
        super.register(page);
        page.addComponentStateParam(this, currentFolder.getStateParameter());
    }

    @Override
    public void generateXML(final PageState state, final Element parent) {
        m_script.generateXML(state, parent);
        super.generateXML(state, parent);
    }

    /**
     * Perform form initialisation. Children should override this this method to
     * pre-fill the widgets with data, instantiate the content item, etc.
     *
     * @param event
     *
     * @throws FormProcessException
     */
    @Override
    public abstract void init(final FormSectionEvent event)
            throws FormProcessException;

    /**
     * Process the form. Children have to override this method to save the
     * user's changes to the database.
     *
     * @param event
     *
     * @throws FormProcessException
     */
    @Override
    public abstract void process(final FormSectionEvent event)
            throws FormProcessException;

    /**
     * Validates the form. Checks for name uniqueness.
     *
     * @param event
     *
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void validate(final FormSectionEvent event)
            throws FormProcessException {
        Category folder = (Category) currentFolder.getSelectedObject(event
                .getPageState());
        Assert.exists(folder);
//        validateNameUniqueness(folder, event);
    }

    /**
     * Adds a component to this container.
     *
     * @param component the component to add to this FolderForm
     *
     */
    @Override
    public void add(final Component component) {
        widgetSection.add(component);
    }

    /**
     * Adds a component with the specified layout constraints to this container.
     * Layout constraints are defined in each layout container as static ints.
     * Use a bitwise OR to specify multiple constraints.
     *
     * @param component the component to add to this container
     * @param constraints layout constraints (a bitwise OR of static ints in the
     * particular layout)
     */
    @Override
    public void add(final Component component, final int constraints) {
        widgetSection.add(component, constraints);
    }

    /**
     * This method can be overridden to change the label of the title field. To
     * change to label of the title field can be useful for some content types.
     * For example, for an organization the label "Title" for the field may be
     * confusing for the normal user. For such a content type, the label would
     * be changed to something like "Name of the organization".
     *
     * @return (Content for the) Label for the title field as string
     */
    protected GlobalizedMessage getTitleLabel() {
        return new GlobalizedMessage("cms.contenttypes.ui.title",
                                     CmsConstants.CMS_BUNDLE);
    }

    /**
     * Provides the text for the user hint providing some detailed information
     * how to use this widget. This method can be overwritten to adjust the text
     * for some content types. {@link #getTitleLabel()}
     *
     * @return
     */
    protected GlobalizedMessage getTitleHint() {
        return new GlobalizedMessage("cms.contenttypes.ui.title_hint",
                                     CmsConstants.CMS_BUNDLE);
    }

    /**
     * This method does the same as {@link #getTitleLabel() } for the labe l of
     * the name (URL) field.
     *
     * @return (Content for the) Label for the name field as string
     */
    protected GlobalizedMessage getNameLabel() {
        return new GlobalizedMessage("cms.contenttypes.ui.name",
                                     CmsConstants.CMS_BUNDLE);
    }

    /**
     * Provides the text for the user hint providing some detailed information
     * how to use this widget. This method can be overwritten to adjust the text
     * for some content types. {@link #getNameLabel()}
     *
     * @return
     */
    protected GlobalizedMessage getNameHint() {
        return new GlobalizedMessage("cms.contenttypes.ui.name_hint",
                                     CmsConstants.CMS_BUNDLE);
    }

    /**
     * Returns true if the form submission was cancelled.
     *
     * @param state
     *
     * @return
     */
    public boolean isCancelled(final PageState state) {
        return saveCancelSection.getCancelButton().isSelected(state);
    }

    /**
     * Updates a folder with a new parent, name, and label.
     *
     * @param folder The folder to update
     * @param parent The new parent folder. May be null.
     * @param name The new name of the folder
     * @param label The new label for the folder
     */
    final protected void updateFolder(final Category folder,
                                      final Category parent,
                                      final String name,
                                      final String label) {
        folder.setParentCategory(parent);
        updateFolder(folder, name, label);
    }

    /**
     * Updates a folder with a new name and label.
     *
     * @param folder The folder to update
     * @param name The new name of the folder
     * @param label The new label for the folder
     */
    final protected void updateFolder(final Category folder,
                                      final String name,
                                      final String label) {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ConfigurationManager confManager = cdiUtil.findBean(ConfigurationManager.class);
        final KernelConfig kernelConfig = confManager.findConfiguration(
                KernelConfig.class);

        folder.setName(name);
        folder.setDisplayName(name);
        folder.getTitle().addValue(kernelConfig.getDefaultLocale(), label);

        final CategoryRepository categoryRepo = cdiUtil.findBean(
                CategoryRepository.class);
        categoryRepo.save(folder);
    }

    /**
     * Returns the current folder being operated on.
     *
     * @param state
     * @return The current folder
     *
     */
    final protected Category getCurrentFolder(final PageState state) {
        final Category folder = (Category) currentFolder
                .getSelectedObject(state);
        return folder;
    }

    final protected FolderSelectionModel getFolderSelectionModel() {
        return currentFolder;
    }

}
