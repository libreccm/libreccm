/*
 * Copyright (C) 2008 Sören Bernstein All Rights Reserved.
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
package com.arsdigita.cms.ui.category;

import com.arsdigita.bebop.Embedded;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.cms.ui.BaseForm;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;
import org.apache.logging.log4j.LogManager;
import org.libreccm.categorization.Category;

import java.util.List;
import java.util.TooManyListenersException;

/**
 * Base class for CategoryLocalizationAddForm and CategoryLocalizationEditForm.
 *
 * This class is part of the admin GUI of CCM and extends the standard form in order to present
 * forms for managing the multi-language categories.
 *
 * @author Sören Bernstein <quasi@quasiweb.de>
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 */
public class CategoryLocalizationForm extends BaseForm {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(
            CategoryLocalizationForm.class);

    final CategoryRequestLocal m_category;
    final SingleSelect m_locale;
    final TextField m_title;
    final TextArea m_description;
    //final TextField m_url;
//    final Hidden m_url;
//    final RadioGroup m_isEnabled;
//    private Embedded m_script = new Embedded(String.format(
//        "<script language=\"javascript\" src=\"%s/javascript/manipulate-input.js\">" + "</script>",
//        Web.getWebappContextPath()),
//                                             false);

    private final static String LOCALE = "locale";
    private final static String TITLE = "title";
    private final static String DESCRIPTION = "description";
    private final static String URL = "url";
    private final static String IS_ENABLED = "isEnabled";

    /**
     * Creates a new instance of CategoryLocalizationForm.
     *
     * @param key
     * @param heading
     * @param category
     */
    public CategoryLocalizationForm(final String key,
                                    final GlobalizedMessage heading,
                                    final CategoryRequestLocal category) {

        super(key, heading);

        m_category = category;

        // Parameter-Model for SingleSelect
        ParameterModel localeParam = new StringParameter(LOCALE);
        localeParam.addParameterListener(new StringInRangeValidationListener(0, 2));

        m_locale = new SingleSelect(localeParam);
        m_locale.addValidationListener(e -> {

            // the --select one-- option is not allowed
            ParameterData data = e.getParameterData();
            String code = (String) data.getValue();
            if (code == null || code.length() == 0) {
                data.addError(
                    GlobalizationUtil.globalize(
                        "cms.ui.category.localization_error_locale"));
            }
        });

        addField(gz("cms.ui.category.localization_locale"), m_locale);

        m_title = new TextField(new TrimmedStringParameter(TITLE));
        addField(gz("cms.ui.title"), m_title);

        m_title.setSize(30);
        m_title.setMaxLength(200);
        m_title.addValidationListener(new NotNullValidationListener());
        m_title.setOnFocus("if (this.form." + URL + ".value == '') { "
                              + " defaulting = true; this.form." + URL
                              + ".value = urlize(this.value); }");
        m_title.setOnKeyUp("if (defaulting) { this.form." + URL + ".value = urlize(this.value) }");

        // is enabled?
//        m_isEnabled = new RadioGroup(IS_ENABLED);
//        m_isEnabled.addOption(new Option("no", new Label(gz("cms.ui.no"))));
//        m_isEnabled.addOption(new Option("yes", new Label(gz("cms.ui.yes"))));
//        addField(gz("cms.ui.category.is_enabled"), m_isEnabled);

        m_description = new TextArea(new TrimmedStringParameter(DESCRIPTION));
        addField(gz("cms.ui.description"), m_description);

        m_description.setWrap(TextArea.SOFT);
        m_description.setRows(5);
        m_description.setCols(40);

        // URL
        // JavaScript auto-url generation is off by default.
        // It is turned on under the following circumstances
        //
        // * If the url is null, upon starting edit of the title
        // * If the url is null, upon finishing edit of name
        //
        // The rationale is that, auto-url generation is useful
        // if the url is currently null, but once a name has been
        // created you don't want to subsequently change it since
        // it breaks URLs & potentially overwrites the user's
        // customizations.        
//        m_url = new TextField(new TrimmedStringParameter(URL));
//        m_url.setSize(30);
//        m_url.setMaxLength(200);
//        m_url.addValidationListener(new NotNullValidationListener());
//        m_url.setOnFocus("defaulting = false");
//        m_url.setOnBlur("if (this.value == '') "
//                            + "{ defaulting = true; this.value = urlize(this.form." + TITLE
//                            + ".value) } " + "else { this.value = urlize(this.value); }");
//        addField(gz("cms.ui.category.url"), m_url);
        //jensp 2014-09-16: Localisation of URLs is not useful but causes problems when resolving
        //the URLs. Also, a category is the same resource for every language variant therefore 
        //the URL should be the same.
        //Changed field to Hidden, initalised with URL of category itself.
//        m_url = new Hidden(new TrimmedStringParameter(URL));
//        try {
//            m_url.addPrintListener(new PrintListener() {
//
//                @Override
//                public void prepare(final PrintEvent event) {
//                    final Hidden target = (Hidden) event.getTarget();
//                    final PageState state = event.getPageState();
//
//                    final Category cat = m_category.getCategory(state);
//
//                    target.setValue(state, cat.getName());
//                }
//
//            });
//        } catch (TooManyListenersException | IllegalArgumentException ex) {
//            LOGGER.fatal(ex);
//        }
//        addField(gz("cms.ui.category.url"), m_url);

        addAction(new Finish());
        addAction(new Cancel());

    }

    @Override
    public void generateXML(PageState ps, Element parent) {
//        m_script.generateXML(ps, parent);
        super.generateXML(ps, parent);
    }

    /**
     * Purpose:
     *
     * XXXToDo: Should be extended with the function: Names have to be unambiguous in the selected
     * language
     */
    class NameUniqueListener implements ParameterListener {

        private final CategoryRequestLocal m_category;
        private final Widget m_widget;
        private final int m_type;
        final static int NAME_FIELD = 1;
        public final static int URL_FIELD = 2;

        NameUniqueListener(final CategoryRequestLocal category) {
            this(category, m_title, NAME_FIELD);
        }

        NameUniqueListener(final CategoryRequestLocal category,
                           Widget widget, int type) {
            m_category = category;
            m_widget = widget;
            m_type = type;
        }

        /**
         * Purpose:
         *
         * XXX provisional, has to be adapted
         *
         * @param e
         *
         * @throws com.arsdigita.bebop.FormProcessException
         */
        @Override
        public final void validate(final ParameterEvent e)
            throws FormProcessException {
            final PageState state = e.getPageState();
            final String title = (String) m_widget.getValue(state);

            final Category category = m_category.getCategory(state);

            final List<Category> children = category.getSubCategories();

            for (Category child : children) {
                String compField = child.getName();
                if (compField.equalsIgnoreCase(title)
                            || !m_category.getCategory(state).equals(child)) {
                    throw new FormProcessException(GlobalizationUtil.globalize("cms.ui.category.name_not_unique"));
                }
            }
        }

    }

}
