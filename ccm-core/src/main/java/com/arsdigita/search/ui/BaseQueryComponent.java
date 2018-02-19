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
package com.arsdigita.search.ui;

import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormModel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.globalization.Globalization;
import com.arsdigita.xml.Element;

import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.search.SearchConstants;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;

/**
 * This is a simple extension of the QueryComponent that provides management of
 * the 'terms' parameter and uses FilterGenerators to populate a query
 * specification
 * <p>
 * Typical use would be as follows:
 * <pre>
 * Form f = new Form("search");
 * BaseQueryComponent q = new BaseQueryComponent();
 * q.add(new ObjectTypeFilterComponent("com.arsdigita.kernel.User");
 * q.add(new PermissionGenerator(PrivilegeDescriptor.READ));
 * q.add(new Submit("Go"));
 * f.add(q);
 * </pre>
 */
public class BaseQueryComponent extends QueryComponent {

    private static final Logger LOGGER = LogManager.getLogger(
            BaseQueryComponent.class);

    private Set filters;
    private Form form;
    private final StringParameter termsParameter = new StringParameter("terms");

    /**
     * Creates a new query component
     */
    public BaseQueryComponent() {
        
        super("query");
        filters = new HashSet();
    }

    @Override
    public void register(final Page page) {
        super.register(page);
    }

    @Override
    public void register(final Form form, final FormModel formModel) {
        LOGGER.debug("Adding {} to form model...", termsParameter.getName());

        termsParameter.setPassIn(true);
        formModel.addFormParam(termsParameter);
        this.form = form;
    }

    /**
     * Gets the current search terms
     *
     * @return
     */
    @Override
    protected String getTerms(final PageState state) {

        final FormData formData = form.getFormData(state);

        
        if (formData == null) {
            return null;
        } else {
            final ParameterData data = formData.getParameter(termsParameter.
                getName());
            LOGGER.debug("Search terms were: {}", data.getValue());

            return (String) data.getValue();
        }
    }

    /**
     *
     * @param state
     * @param parent
     */
    @Override
    public void generateXML(final PageState state, final Element parent) {
        final Element content = generateParent(parent);

        final Element terms = new Element(SearchConstants.XML_PREFIX + "terms",
                                          SearchConstants.XML_NS);
        terms.addAttribute("param", termsParameter.getName());
        terms.addAttribute("value",
                           Globalization.decodeParameter(
                                   state.getRequest(),
                                   termsParameter.getName()));
        generateErrorXML(state, terms);
        content.addContent(terms);

        generateChildrenXML(state, content);
    }

    protected void generateErrorXML(final PageState state,
                                    final Element parent) {
        final FormData formData = form.getFormData(state);
        if (formData == null) {
            return;
        }

        final Iterator iterator = formData.getErrors(termsParameter.getName());
        while (iterator.hasNext()) {
            final Element error = new Element(
                    SearchConstants.XML_PREFIX + "error",
                    SearchConstants.XML_NS);
            error.setText((String) ((GlobalizedMessage) iterator.next()).
                    localize(state.getRequest())
            );
            parent.addContent(error);
        }
    }
  
}
