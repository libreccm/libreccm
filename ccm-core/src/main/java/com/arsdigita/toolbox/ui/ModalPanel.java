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
package com.arsdigita.toolbox.ui;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.TableActionAdapter;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ToolboxConstants;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;

import java.util.Iterator;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 */
public class ModalPanel extends ComponentMap {

    private static final Logger LOGGER = LogManager.getLogger(ModalPanel.class);

    private final IndexStack m_stack;
    private Component m_default;

    public ModalPanel() {
        m_stack = new IndexStack();

        m_default = new NullComponent();

        put("__null__", m_default);
    }

    public void register(final Page page) {
        super.register(page);

        page.addComponentStateParam(this, m_stack);

        // All this work is done to keep Bebop's notion of visibility
        // in line with what ModalPanel thinks.
        final Iterator iter = children();

        while (iter.hasNext()) {
            page.setVisibleDefault((Component) iter.next(), false);
        }

        page.addActionListener(new VisibilityListener());
    }

    private class VisibilityListener implements ActionListener {

        public final void actionPerformed(final ActionEvent e) {
            final PageState state = e.getPageState();

            if (state.isVisibleOnPage(ModalPanel.this)) {
                final Iterator iter = children();

                while (iter.hasNext()) {
                    ((Component) iter.next()).setVisible(state, false);
                }
            }
        }

    }

    public final void generateXML(final PageState state,
                                  final Element parent) {
        top(state).setVisible(state, true);

        if (isVisible(state)) {
            top(state).generateXML(state, parent);
        }
    }

    public final void add(final Component component) {
        Assert.isUnlocked(this);
        Assert.exists(component, Component.class);

        put(component, component);
    }

    public void reset(final PageState state) {
        super.reset(state);

        clear(state);
    }

    public final void clear(final PageState state) {
        LOGGER.debug("Clearing the stack");

        m_stack.clear(state);
    }

    public final void push(final PageState state, final Component pushed) {
        if (Assert.isEnabled()) {
            Assert.exists(pushed, Component.class);
            Assert.isTrue(containsKey(pushed),
                          "Component " + pushed + " is not a child "
                              + "of this container");
        }

        if (!pushed.equals(top(state))) {
            m_stack.push(state, state.getPage().stateIndex(pushed));

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Pushed " + top(state) + " visible");
                LOGGER.debug("Stack is " + m_stack.toDebugString(state));
                LOGGER.debug("Here", new Throwable());
            }
        }
    }

    public final void pop(final PageState state) {
        if (m_stack.isEmpty(state)) {
            LOGGER.debug("The stack is empty; nothing was popped");
        } else {
            m_stack.pop(state);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Popped " + top(state) + " visible");
                LOGGER.debug("Stack is " + m_stack.toDebugString(state));
                LOGGER.debug("Here", new Throwable());
            }
        }
    }

    public final Component top(final PageState state) {
        if (m_stack.isEmpty(state)) {
            return getDefault();
        } else {
            return state.getPage().getComponent(m_stack.top(state));
        }
    }

    public final void setDefault(final Component defaalt) {
        if (Assert.isEnabled()) {
            Assert.isUnlocked(this);
            Assert.exists(defaalt, Component.class);
            Assert.isTrue(containsValue(defaalt),
                          defaalt + " is not one of my children");
        }

        m_default = defaalt;

        LOGGER.debug("Default set to " + defaalt);
    }

    public final Component getDefault() {
        return m_default;
    }

    // XXX for thinking about: in a different UI component framework,
    // these connect defs could be reduced to just one or two.
    // XXX does using toggle links in here make more sense from a
    // saner-transition-management pov?
    public final void connect(final ActionLink orig,
                              final Component dest) {
        orig.addActionListener(new NavigationListener(dest));
    }

    public final void connect(final Table orig,
                              final int column,
                              final Component dest) {
        orig.addTableActionListener(new TableNavigationListener(column, dest));
    }

    public final void connect(final List orig,
                              final Component dest) {
        orig.addActionListener(new NavigationListener(dest));
    }

    public final void connect(final Tree orig,
                              final Component dest) {
        orig.addActionListener(new NavigationListener(dest));
    }

    public final void connect(final FormSection orig,
                              final Component dest) {
        orig.addProcessListener(new FormNavigationListener(dest));
    }

    public final void connect(final FormSection origForm,
                              final Widget origWidget,
                              final Object value,
                              final Component dest) {
        origForm.addProcessListener(new WidgetNavigationListener(origWidget,
                                                                 value, dest));
    }

    // Variants to handle forms.
    public final void connect(final ActionLink orig,
                              final FormSection dest) {
        connect(orig, (Component) dest);
        dest.addSubmissionListener(new CancelListener(dest));
    }

    public final void connect(final Table orig,
                              final int column,
                              final FormSection dest) {
        connect(orig, column, (Component) dest);
        dest.addSubmissionListener(new CancelListener(dest, orig
                                                      .getRowSelectionModel()));
    }

    public final void connect(final List orig,
                              final FormSection dest) {
        connect(orig, (Component) dest);
        dest.addSubmissionListener(new CancelListener(dest, orig
                                                      .getSelectionModel()));
    }

    public final void connect(final Tree orig,
                              final FormSection dest) {
        connect(orig, (Component) dest);
        dest.addSubmissionListener(new CancelListener(dest, orig
                                                      .getSelectionModel()));
    }

    public final void connect(final FormSection orig,
                              final FormSection dest) {
        connect(orig, (Component) dest);
        dest.addSubmissionListener(new CancelListener(dest));
    }

    public final void connect(final Form origForm,
                              final Widget origWidget,
                              final Object value,
                              final FormSection dest) {
        connect(origForm, origWidget, value, (Component) dest);
        dest.addSubmissionListener(new CancelListener(dest));
    }

    public final void connect(final FormSection orig) {
        orig.addProcessListener(new FinishListener());
    }

    public final void connect(final FormSection orig,
                              final SingleSelectionModel model) {
        orig.addProcessListener(new FinishListener(model));
    }

    public final void resume(final FormSection orig,
                             final Component dest) {
        orig.addProcessListener(new ResumeListener(dest));
    }

    protected final class NavigationListener implements ActionListener {

        private final Component m_target;

        public NavigationListener(final Component target) {
            Assert.exists(target, Component.class);

            m_target = target;
        }

        public final void actionPerformed(final ActionEvent e) {
            final PageState state = e.getPageState();
            final Object source = e.getSource();

            if (source instanceof Tree && !((Tree) source).isSelected(state)) {
                // Tree fires an action event on expand.  We do not
                // want to do any work in that case.
            } else {
                push(state, m_target);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Navigating to " + m_target);
                }
            }
        }

    }

    protected final class TableNavigationListener extends TableActionAdapter {

        private final int m_column;
        private final Component m_target;

        public TableNavigationListener(final int column,
                                       final Component target) {
            Assert.exists(target, "Component target");

            m_column = column;
            m_target = target;
        }

        public final void cellSelected(final TableActionEvent e) {
            if (e.getColumn().intValue() == m_column) {
                push(e.getPageState(), m_target);
            }
        }

    }

    protected final class FormNavigationListener
        implements FormProcessListener {

        private final Component m_target;

        public FormNavigationListener(final Component target) {
            Assert.exists(target, "Component target");

            m_target = target;
        }

        @Override
        public final void process(final FormSectionEvent e)
            throws FormProcessException {
            push(e.getPageState(), m_target);
        }

    }

    protected final class WidgetNavigationListener
        implements FormProcessListener {

        private final Widget m_widget;
        private final Object m_value;
        private final Component m_target;

        public WidgetNavigationListener(final Widget widget,
                                        final Object value,
                                        final Component target) {
            Assert.exists(widget, "Widget widget");
            Assert.exists(value, "String value");
            Assert.exists(target, "Component target");

            m_widget = widget;
            m_value = value;
            m_target = target;
        }

        @Override
        public final void process(final FormSectionEvent e)
            throws FormProcessException {
            final PageState state = e.getPageState();

            if (m_value.equals(m_widget.getValue(state))) {
                push(state, m_target);
            }
        }

    }

    protected final class CancelListener implements FormSubmissionListener {

        private final Cancellable m_cancellable;
        private SingleSelectionModel m_model;

        public CancelListener(final FormSection form) {
            Assert.exists(form, "FormSection form");

            if (form instanceof Cancellable) {
                m_cancellable = (Cancellable) form;
            } else {
                m_cancellable = null;

                LOGGER.warn("Form " + form + " does not "
                               + "implement Cancellable.");
                // See note above (import statement)!!
                // StackTraces.log("The form was created at", form, s_log, "warn");
            }
        }

        public CancelListener(final FormSection form,
                              final SingleSelectionModel model) {
            this(form);

            Assert.exists(model, "SingleSelectionModel model");

            m_model = model;
        }

        @Override
        public final void submitted(final FormSectionEvent e)
            throws FormProcessException {
            final PageState state = e.getPageState();

            if (m_cancellable != null && m_cancellable.isCancelled(state)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Form processing is cancelled; reverting to "
                                    + "pre-excursion state");
                }

                pop(state);

                // If we got here via a list or table, clear its
                // selection upon cancelling.  If we were strictly
                // correct, we'd revert to the former selection in the
                // model, but we're in no position to support that.
                if (m_model != null) {
                    m_model.clearSelection(state);
                }

                throw new FormProcessException(
                    "cancelled",
                    new GlobalizedMessage("toolbox.ui.cancel_msg",
                                          ToolboxConstants.TOOLBOX_BUNDLE));
            }
        }

    }

    protected final class FinishListener implements FormProcessListener {

        private SingleSelectionModel m_model = null;

        public FinishListener() {
            super();
        }

        public FinishListener(final SingleSelectionModel model) {
            this();
            m_model = model;
        }

        public final void process(final FormSectionEvent e)
            throws FormProcessException {
            final PageState state = e.getPageState();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Form processing went as planned and there is "
                                + "no subsequent step; reverting to "
                                + "pre-excursion state");
            }

            pop(state);
            if (m_model != null) {
                m_model.clearSelection(state);
            }
        }

    }

    protected final class ResumeListener implements FormProcessListener {

        private final Component m_target;

        public ResumeListener(final Component target) {
            m_target = target;
        }

        public final void process(final FormSectionEvent e)
            throws FormProcessException {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Resuming the pre-excursion state");
            }

            final PageState state = e.getPageState();

            while (!top(state).equals(m_target)) {
                pop(state);
            }
        }

    }

    protected final class ResetListener implements ActionListener {

        public ResetListener() {
            super();
        }

        public final void actionPerformed(final ActionEvent e) {
            reset(e.getPageState());
        }

    }

    private class IndexStack extends ArrayParameter {

        IndexStack() {
            super(new IntegerParameter("stack"));
        }

        final boolean isEmpty(final PageState state) {
            final Integer[] stack = (Integer[]) state.getValue(this);

            return stack == null || stack.length == 0;
        }

        final void clear(final PageState state) {
            state.setValue(this, null);
        }

        final int top(final PageState state) {
            final Integer[] stack = (Integer[]) state.getValue(this);

            if (stack == null || stack.length == 0) {
                throw new IllegalStateException("The stack is empty");
            } else {
                return stack[stack.length - 1].intValue();
            }
        }

        final void push(final PageState state, final int index) {
            final Integer[] before = (Integer[]) state.getValue(this);

            if (before == null || before.length == 0) {
                state.setValue(this, new Integer[]{new Integer(index)});
            } else {
                final Integer[] after = new Integer[before.length + 1];

                for (int i = 0; i < before.length; i++) {
                    after[i] = before[i];
                }

                after[before.length] = new Integer(index);

                state.setValue(this, after);
            }
        }

        final void pop(final PageState state) {
            final Integer[] before = (Integer[]) state.getValue(this);

            if (before == null || before.length == 0) {
                throw new IllegalStateException("The stack is empty");
            } else {
                final Integer[] after = new Integer[before.length - 1];

                for (int i = 0; i < after.length; i++) {
                    after[i] = before[i];
                }

                state.setValue(this, after);
            }
        }

        final String toDebugString(final PageState state) {
            final StringBuffer buffer = new StringBuffer();
            final Integer[] stack = (Integer[]) state.getValue(this);

            if (stack == null || stack.length == 0) {
                return "[]";
            } else {
                for (int i = 0; i < stack.length; i++) {
                    buffer.append(",");
                    buffer.append(stack[i]);
                }

                return "[" + buffer.toString().substring(1) + "] <- top";
            }
        }

    }

}
