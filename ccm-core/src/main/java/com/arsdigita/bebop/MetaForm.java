/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.bebop;

import com.arsdigita.bebop.util.Traversal;
import com.arsdigita.xml.Element;

/**
 * A form that is instantiated on a per-request basis. This class
 * functions as a placeholder and decorator of the request-specific form
 * in a Bebop {@link Page}.
 *
 * <p> Subclasses only need to override {@link #buildForm buildForm} to
 * return the request-specific form. The meta form takes care of
 * interfacing that form with the normal control flow of serving a Bebop
 * <code>Page</code>. The meta form will fool the request-specific forms
 * into thinking that they are part of a static Bebop <code>Page</code>.
 * The properties of the meta form should be used to initialize the
 * correspoding properties of the request-specific form whenever
 * possible. These properties include <code>name</code>,
 * <code>method</code>, and <code>encType</code>.
 *
 * <p> Listeners can be added directly to the meta form and are run
 * whenever the corresponding listeners would be run on an ordinary
 * form. The source of the <code>FormSectionEvent</code> will be the meta
 * form.
 *
 * @author Stas Freidin 
 * @author David Lutterkort 
 */

public abstract class MetaForm extends Form {

    private RequestLocal m_dynamicForm;

    /**
     * Constructs a new meta form.
     *
     * @param name the name of the form
     */
    public MetaForm(String name) {
        super(name);
        m_dynamicForm = new RequestLocal() {
                protected Object initialValue(PageState s) {
                    Form result = buildForm(s);
                    result.getModel().mergeModel(getModel());
                    // form isn't part of the page, so it is invisible
                    // on the page (vacuously).  We should consider it
                    // visible iff the static container MetaForm is visible.
                    result.setProcessInvisible(
                                               MetaForm.this.getProcessInvisible() ||
                                               s.isVisibleOnPage(MetaForm.this));
                    result.traverse();
                    Traversal t = new Traversal() {
                            public void act(Component c) {
                                c.lock();
                            }
                        };
                    t.preorder(result);
                    return result;
                }
            };
    }

    /**
     * Retrieves the form for the request represented by
     * <code>state</code>. If the form hasn't been built
     * yet, calls {@link #buildForm buildForm} to build the
     * form.
     *
     * @param state describes the current request
     * @return a custom-built form for this request.
     * @pre state != null
     * @post return != null
     */
    protected Form getDynamicForm(PageState state) {
        return (Form) m_dynamicForm.get(state);
    }

    /**
     * Builds the dynamic form. Subclasses should override this method to
     * build the form based on the request represented by <code>state</code>.
     *
     * @param state describes the current request
     * @return the form to be used for this request.
     * @pre state != null
     * @post return != null
     */
    public abstract Form buildForm(PageState state);

    /**
     * Force a rebuilding and updating of the dynamic form. Calls
     * <code>buildForm</code> again and sets the dynamic form to the form
     * returned by it.
     *
     * @param s describes the current request
     */
    public void rebuildForm(PageState s) {
        m_dynamicForm.set(s, m_dynamicForm.initialValue(s));
    }

    /**
     * Returns the form data constructed by {@link #process process} for the
     * request described by <code>state</code>. If the form for this request
     * hasn't been built yet, calls {@link #buildForm buildForm}.
     *
     * @param state describes the current request
     * @return the values extracted from the HTTP request contained
     * in <code>state</code>, or <code>null</code> if the form has not
     * yet been processed.
     * @pre state != null
     */
    public FormData getFormData(PageState state) {
        return getDynamicForm(state).getFormData(state);
    }

    /**
     * Generates the XML representing the form and its widgets, but not
     * the state information, from <code>s</code>. The XML generation is
     * delegated to the request-specific form by calling {@link
     * #generateXMLSansState generateXMLSansState} on it.
     *
     * @param s represents the curent request
     * @return the top-level element for the form.
     */
    protected Element generateXMLSansState(PageState s, Element parent) {
        return getDynamicForm(s).generateXMLSansState(s, parent);
    }

    /**
     * Processes the request-specific form for the request represented by
     * <code>state</code>.
     *
     * @param state describes the current request
     * @return the form data extracted from the current request.
     * @pre state != null
     * @post return != null
     * @see Form#process Form.process(...)
     * @see FormModel#process FormModel.process(...)
     */
    public FormData process(PageState state)
        throws FormProcessException {

        if (state.isVisibleOnPage(this))
            return getDynamicForm(state).process(state);
        return null; // XXX is this ok ?
    }

    /**
     * Do nothing; the dynamic form will take care of the tag.
     */
    protected void addMagicTag() {
        return;
    }

    /**
     * Not implemented because meta forms currently don't support mixing static and
     * dynamic widgets.
     * @throws UnsupportedOperationException
     */
    public void add(Component pc, int constraints) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Not implemented.
     * @throws UnsupportedOperationException
     */
    public Container getPanel() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
