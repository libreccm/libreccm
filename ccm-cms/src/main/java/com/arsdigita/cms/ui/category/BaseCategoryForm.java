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
package com.arsdigita.cms.ui.category;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.cms.ui.BaseForm;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;

import org.libreccm.categorization.Category;

import java.util.Collection;

/**
 * A form which creates a new category. Extends the edit form for convenience.
 *
 *
 * @author Michael Pih
 * @author Stanislav Freidin &lt;sfreidin@redhat.com&gt;
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 */
class BaseCategoryForm extends BaseForm {

    final CategoryRequestLocal m_parent;
    final TextField m_name;
    final TextArea m_description;
    final RadioGroup m_isAbstract;
    final RadioGroup m_isVisible;
    final RadioGroup m_isEnabled;
    private Label m_script = new Label(new GlobalizedMessage(String.format(
        "<script language=\"javascript\" src=\"%s/javascript/manipulate-input.js\"></script>",
        Web.getWebappContextPath())),
                                       false);
    private final static String NAME = "name";
    private final static String DESCRIPTION = "description";
    private final static String URL = "url";
    private final static String IS_ABSTRACT = "isAbstract";
    private final static String IS_VISIBLE = "isVisible";
    private final static String IS_ENABLED = "isEnabled";

    /**
     * Constructor.
     */
    BaseCategoryForm(final String key,
                     final GlobalizedMessage heading,
                     final CategoryRequestLocal parent) {
        super(key, heading);

        m_parent = parent;

        m_name = new TextField(new TrimmedStringParameter(NAME));
        addField(gz("cms.ui.name"), m_name);

        m_name.setSize(30);
        m_name.setMaxLength(200);
        m_name.addValidationListener(new NotNullValidationListener());
        m_name.setOnFocus("if (this.form." + URL + ".value == '') { "
                          + " defaulting = true; this.form." + URL
                              + ".value = urlize(this.value); }");
        m_name.setOnKeyUp("if (defaulting) { this.form." + URL
                          + ".value = urlize(this.value) }");

        // is abstract?
        m_isAbstract = new RadioGroup(IS_ABSTRACT);
        m_isAbstract.addOption(new Option("no", new Label(gz("cms.ui.no"))));
        m_isAbstract.addOption(new Option("yes", new Label(gz("cms.ui.yes"))));
        addField(gz("cms.ui.category.is_not_abstract"), m_isAbstract);

        // is visible
        m_isVisible = new RadioGroup(IS_VISIBLE);
        m_isVisible.addOption(new Option("no", new Label(gz("cms.ui.no"))));
        m_isVisible.addOption(new Option("yes", new Label(gz("cms.ui.yes"))));
        addField(gz("cms.ui.category.is_visible"), m_isVisible);

        // is enabled?
        m_isEnabled = new RadioGroup(IS_ENABLED);
        m_isEnabled.addOption(new Option("no", new Label(gz("cms.ui.no"))));
        m_isEnabled.addOption(new Option("yes", new Label(gz("cms.ui.yes"))));
        addField(gz("cms.ui.category.is_enabled"), m_isEnabled);

        m_description = new TextArea(new TrimmedStringParameter(DESCRIPTION));
        addField(gz("cms.ui.description"), m_description);

        m_description.setWrap(TextArea.SOFT);
        m_description.setRows(5);
        m_description.setCols(40);

        addAction(new Finish());
        addAction(new Cancel());
    }

    @Override
    public void generateXML(PageState ps, Element parent) {
        m_script.generateXML(ps, parent);
        super.generateXML(ps, parent);
    }

    class NameUniqueListener implements ParameterListener {

        private final CategoryRequestLocal m_category;
        private final Widget m_widget;
        private final int m_type;
        final static int NAME_FIELD = 1;

        NameUniqueListener(final CategoryRequestLocal category) {
            this(category, m_name, NAME_FIELD);
        }

        NameUniqueListener(final CategoryRequestLocal category,
                           Widget widget, int type) {
            m_category = category;
            m_widget = widget;
            m_type = type;
        }

        @Override
        public final void validate(final ParameterEvent e)
            throws FormProcessException {
            final PageState state = e.getPageState();
            final String title = (String) m_widget.getValue(state);

            final Category parent = m_parent.getCategory(state);

            final Collection<Category> children = parent.getSubCategories();

            for (final Category child : children) {
                String compField = child.getName();
                if (compField.equalsIgnoreCase(title)
                        && (m_category == null
                            || !m_category.getCategory(state).equals(child))) {
                    throw new FormProcessException(GlobalizationUtil.globalize(
                        "cms.ui.category.name_not_unique"));
                }
            }
        }

    }

}
