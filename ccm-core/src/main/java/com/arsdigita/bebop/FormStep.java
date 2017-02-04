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

import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.parameters.BooleanParameter;
import com.arsdigita.bebop.util.Traversal;
import com.arsdigita.xml.Element;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The FormStep class modifies the behavior of FormSection with respect to
 * listener firing. Instead of firing init listeners the first time the
 * enclosing form is displayed on the page, the FormStep class fires init
 * listeners the first time the FormStep itself is displayed on the page. The
 * process, validate, and submission listeners are then fired on every
 * submission following the one in which the init listeners were fired. This
 * behavior is useful when used in conjunction with {@link MultiStepForm} or its
 * subclasses to provide initialization in later steps of a multi step form that
 * depends on the values entered in earlier steps.
 *
 * updated chris.gilbert@westsussex.gov.uk - support for session based wizards
 * (which enable use of actionlinks in wizard)
 *
 * @see Wizard
 * @see MultiStepForm
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 *
 */
public class FormStep extends FormSection {

    private final static Logger LOGGER = LogManager.getLogger(FormStep.class);

    private Form m_form = null;

    // cg - changed to using a parameter that is stored in pagestate so that if there are links
    // within the form then the init status of the steps is not lost
    // private Hidden m_initialized;
    private BooleanParameter m_initialized;

    /**
     * Constructs a new FormStep with the given name. The name must uniquely
     * identify this FormStep within it's enclosing Form.
     *
     * @param name A name that uniquely identifies this FormStep within it's
     *             enclosing Form.
     *
     */
    public FormStep(String name) {
        addInitialized(name);
    }

    /**
     * Constructs a new FormStep with the given name. The name must uniquely
     * identify this FormStep within it's enclosing Form.
     *
     * @param name  A name that uniquely identifies this FormStep within it's
     *              enclosing Form.
     * @param panel The container used to back this FormStep.
     *
     */
    public FormStep(String name, Container panel) {
        super(panel);
        addInitialized(name);
    }

    protected FormStep(String name, Container panel, FormModel model) {
        super(panel, model);
        addInitialized(name);
    }

    public void register(Page p) {
        super.register(p);
        p.addComponentStateParam(this, m_initialized);
        Traversal trav = new Traversal() {

            protected void act(Component c) {
                if (c instanceof Widget) {
                    ((Widget) c).setValidateInvisible(false);
                }
            }

        };

        trav.preorder(this);
    }

    public void register(Form form, FormModel model) {
        super.register(form, model);
        m_form = form;
    }

    private void addInitialized(String name) {
        // m_initialized = new Hidden(new BooleanParameter(name));
        // add(m_initialized);     
        m_initialized = new BooleanParameter(name);
        m_initialized.setDefaultValue(Boolean.FALSE);
    }

    public boolean isInitialized(PageState ps) {
        // Object init = m_initialized.getValue(ps);
        Boolean init = (Boolean) ps.getValue(m_initialized);
        if (init == null) {
            LOGGER.debug("init for step " + m_initialized.getName()
                             + " is null. returning true");
            // happens if step state is stored in session - 
            // form containing this step clears session
            // info when processed, but fireProcess invoked
            // on this step AFTER form is processed. At that point,
            // the step has been initialised because we are on the 
            // final process at the end of the steps
            // 
            init = Boolean.TRUE;
        }
        return init.booleanValue();
    }

    private void setInitialized(PageState ps) {
        //m_initialized.setValue(ps, Boolean.TRUE);
        ps.setValue(m_initialized, Boolean.TRUE);
    }

    // Turn off forwarding of init events.
    protected FormInitListener createInitListener() {
        return new FormInitListener() {

            public void init(FormSectionEvent evt) {
            }

        };
    }

    protected void fireSubmitted(FormSectionEvent evt)
        throws FormProcessException {
        if (isInitialized(evt.getPageState())) {
            super.fireSubmitted(evt);
        }
    }

    protected void fireValidate(FormSectionEvent evt) {
        if (isInitialized(evt.getPageState())) {
            super.fireValidate(evt);
        }
    }

    protected void fireProcess(FormSectionEvent evt)
        throws FormProcessException {
        LOGGER.debug("fireprocess invoked on Formstep " + m_initialized
            .getName());
        if (isInitialized(evt.getPageState())) {
            super.fireProcess(evt);

        }
    }

    public void generateXML(PageState ps, Element parent) {
        if (!isInitialized(ps)) {
            FormData fd = m_form.getFormData(ps);
            try {
                fireInit(new FormSectionEvent(this, ps, fd));
                setInitialized(ps);
            } catch (FormProcessException ex) {
                LOGGER.debug("initialization aborted", ex);
                fd.addError("Initialization Aborted: " + ex.getMessages());
            }
        }

        super.generateXML(ps, parent);
    }

}
