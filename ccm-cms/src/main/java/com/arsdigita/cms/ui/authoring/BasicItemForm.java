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
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
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
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;

import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemRepository;

/**
 * A form for editing subclasses of ContentItem. This is just a convenience
 * class.
 *
 * @author <a href="mailto:stas@arsdigita.com">Stanislav Freidin</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class BasicItemForm extends FormSection
    implements FormInitListener,
               FormProcessListener,
               FormValidationListener {

    private final ItemSelectionModel m_itemModel;
    private SaveCancelSection m_saveCancelSection;
    private final FormSection m_widgetSection;
    public static final String CONTENT_ITEM_ID = "ContentItemId";
    public static final String NAME = "ContentItemName";
    public static final String TITLE = "ContentPageTitle";
    public static final String LANGUAGE = "ContentItemLanguage";

    /**
     * Construct a new BasicItemForm with 2 ColumnPanels and add basic content.
     * The left Panel is used for Labels, the right Panel for values.
     *
     * @param formName  the name of this form
     * @param itemModel The {@link ItemSelectionModel} which will be responsible
     *                  for loading the current item
     */
    public BasicItemForm(String formName, ItemSelectionModel itemModel) {

        super(new ColumnPanel(2));

        m_widgetSection = new FormSection(new ColumnPanel(2, true));

        super.add(m_widgetSection, ColumnPanel.INSERT);
        m_itemModel = itemModel;

        /* Prepare Panel design                                               */
        ColumnPanel panel = (ColumnPanel) getPanel();
        panel.setBorder(false);
        panel.setPadColor("#FFFFFF");
        panel.setColumnWidth(1, "20%");
        panel.setColumnWidth(2, "80%");
        panel.setWidth("100%");

        /* Add basic contents */
        addWidgets();

        m_saveCancelSection = new SaveCancelSection();
        super.add(m_saveCancelSection,
                  ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);

        addInitListener(this);
        addProcessListener(this);
        addValidationListener(this);
    }

    /**
     * Construct a new BasicItemForm with a specified number of ColumnPanels and
     * without any content.
     *
     * @param formName    the name of this form
     * @param columnPanel the columnpanel of the form
     * @param itemModel   The {@link ItemSelectionModel} which will be
     *                    responsible for loading the current item
     */
    public BasicItemForm(String formName,
                         ColumnPanel columnPanel,
                         ItemSelectionModel itemModel) {
        super(columnPanel);

        m_widgetSection = new FormSection(new ColumnPanel(columnPanel.
            getNumCols(),
                                                          true));
        super.add(m_widgetSection, ColumnPanel.INSERT);
        m_itemModel = itemModel;
    }

    /**
     * instanciate and add the save/cancel section for this form
     */
    public void addSaveCancelSection() {
        m_saveCancelSection = new SaveCancelSection();
        super.add(m_saveCancelSection,
                  ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);
    }

    /**
     * Currently, to insert javascript code the Label Widget is "abused".
     */
    private final Label m_script = new Label(String.format(
        "<script language=\"javascript\" src=\"%s/javascript/manipulate-input.js\"></script>",
        Web.getWebappContextPath()),
                                             false);

    /**
     * Add basic widgets to the form.
     *
     * Widgets added are 'title' and 'name (url)' which are part of any content
     * item. Child classes will override this method to perform all their
     * widget-adding needs but are supposed to use super() to add the basic
     * widgets.
     */
    protected void addWidgets() {
        //add(new FormErrorDisplay(this), ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);

        final Hidden id = new Hidden(CONTENT_ITEM_ID);
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

        // For some content types it may be useful to change the label of 
        // the name (or URL) field to something different than 'name (url)'.
        //  This can now be accomplished by overwriting the getNameLabel() method.
        // (jensp 2011-01-28)
//      add(new Label(getNameLabel()));
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
    public void generateXML(PageState ps, Element parent) {
        m_script.generateXML(ps, parent);
        super.generateXML(ps, parent);
    }

    /**
     * @return the item selection model used in this form
     */
    public ItemSelectionModel getItemSelectionModel() {
        return m_itemModel;
    }

    /**
     * @return the save/cancel section for this form
     */
    public SaveCancelSection getSaveCancelSection() {
        return m_saveCancelSection;
    }

    /**
     * Perform form initialisation. Children should override this this method to
     * pre-fill the widgets with data, instantiate the content item, etc.
     *
     * @param e
     *
     * @throws FormProcessException
     */
    @Override
    public abstract void init(FormSectionEvent e) throws FormProcessException;

    /**
     * Process the form. Children have to override this method to save the
     * user's changes to the database.
     *
     * @param e
     *
     * @throws FormProcessException
     */
    @Override
    public abstract void process(FormSectionEvent e) throws FormProcessException;

    /**
     * Validate the form. Children have to override this method to provide
     * context form validation, specifically name (url) uniqueness in a folder!
     *
     * @param e
     *
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void validate(FormSectionEvent e) throws FormProcessException {
        // do nothing
    }

    /**
     * Adds a component to this container.
     *
     * @param pc the component to add to this BasicPageForm
     *
     */
    @Override
    public void add(Component pc) {
        m_widgetSection.add(pc);
    }

    /**
     * Adds a component with the specified layout constraints to this container.
     * Layout constraints are defined in each layout container as static ints.
     * Use a bitwise OR to specify multiple constraints.
     *
     * @param pc          the component to add to this container
     * @param constraints layout constraints (a bitwise OR of static ints in the
     *                    particular layout)
     */
    @Override
    public void add(Component pc, int constraints) {
        m_widgetSection.add(pc, constraints);
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

    // //////////////////////////////////////////////////////////////////////
    //
    // VALIDATION helper methods
    //
    // //////////////////////////////////////////////////////////////////////
    /**
     * Ensure that the name of an item is unique within a folder. A "New item"
     * form should call this method in the validation listener.
     *
     * @param parent the folder in which to check
     * @param event  the {@link FormSectionEvent} which was passed to the
     *               validation listener
     */
    public void validateNameUniqueness(final Category parent,
                                       final FormSectionEvent event) {

        final FormData data = event.getFormData();
        final String newName = (String) data.get(NAME);

        validateNameUniqueness(parent, event, newName);
    }

    /**
     *
     * @param parent
     * @param event
     * @param newName
     */
    public void validateNameUniqueness(final Category parent,
                                       final FormSectionEvent event,
                                       final String newName) {

        final String ERR_MSG = "cms.ui.authoring.an_item_with_this_name_exists";

        if (newName != null) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ContentItemRepository itemRepo = cdiUtil
                .findBean(ContentItemRepository.class);

            final long result = itemRepo.countByNameInFolder(parent, newName);

            if (result > 0) {
                // Try to get a currently selected content item
                final ContentItem item;
                if (getItemSelectionModel() == null) {
                    item = null;
                } else {
                    item = getItemSelectionModel()
                        .getSelectedObject(event.getPageState());
                }
                if (item == null) {  // The content item being null
                    // means it is a creation form.
                    // Therefore finding any item of the same name is a fault.
                    event.getFormData()
                        .addError(new GlobalizedMessage(ERR_MSG,
                                                        CmsConstants.CMS_BUNDLE));
                } else {
                    // means we are in a edit form.
                    // We need to add all of the items that are different
                    // versions of this item to the list so that we do not mark
                    // an error if those are the only problems.
                    event.getFormData()
                        .addError(new GlobalizedMessage(
                            ERR_MSG,
                            CmsConstants.CMS_BUNDLE,
                            new Object[]{item.getUuid()}));
                }
            }
        }
    }

}
