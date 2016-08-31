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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.tree.TreeModelBuilder;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

/** 
 * 
 * @version $Id$
 */
public class SelectionPanel extends LayoutPanel implements Resettable {

    private static final Logger s_log = Logger.getLogger(SelectionPanel.class);

    private SingleSelectionModel m_model;
    private Component m_selector;
    private ActionGroup m_group;
    private final ModalPanel m_body;

    private Component m_introPane;
    private Component m_itemPane;

    private ActionLink m_addLink;
    private Form m_addForm;
    private ActionLink m_editLink;
    private Form m_editForm;
    private ActionLink m_deleteLink;
    private Form m_deleteForm;

    protected void build(final Component title,
                       final Component selector,
                       final SingleSelectionModel model) {
        m_model = model;
        m_selector = selector;

        final Section section = new Section();
        setLeft(section);

        section.setHeading(title);

        m_group = new ActionGroup();
        section.setBody(m_group);

        m_group.setSubject(selector);
    }

    protected SelectionPanel() {
        m_body = new ModalPanel();
        setBody(m_body);

        m_introPane = new NullComponent();
        m_body.add(m_introPane);
        m_body.setDefault(m_introPane);

        m_itemPane = new NullComponent();
        m_body.add(m_itemPane);

        m_addLink = null;
        m_addForm = null;
        m_editLink = null;
        m_editForm = null;
        m_deleteLink = null;
        m_deleteForm = null;
    }

    /**
     * @pre selector instanceof Tree || selector instanceof List
     */
    public SelectionPanel(final Component title,
                          final Component selector) {
        this();

        if (Assert.isEnabled()) {
            Assert.exists(title, Component.class);
            Assert.exists(selector, Component.class);
            Assert.isTrue(selector instanceof Tree || selector instanceof List);
        }

        // Making up now for some untoward modeling in Bebop.

        if (selector instanceof List) {
            final List list = (List) selector;

            list.addChangeListener(new SelectionListener());

            build(title, list, list.getSelectionModel());
        } else {
            final Tree tree = (Tree) selector;

            tree.addChangeListener(new SelectionListener());

            build(title, tree, tree.getSelectionModel());
        }
    }

    public SelectionPanel(final Component title,
                          final Component selector,
                          final SingleSelectionModel model) {
        this();

        if (Assert.isEnabled()) {
            Assert.exists(title, Component.class);
            Assert.exists(selector, Component.class);
        }

        build(title, selector, model);
    }

    public SelectionPanel(final GlobalizedMessage title,
                          final Component selector) {
        this(new Label(title), selector);
    }

    public SelectionPanel(final GlobalizedMessage title,
                          final Component selector,
                          final SingleSelectionModel model) {
        this(new Label(title), selector, model);
    }

    public SelectionPanel(final Component title,
                          final TreeModelBuilder builder) {
        this(title, new Tree(builder));
    }

    public SelectionPanel(final GlobalizedMessage title,
                          final TreeModelBuilder builder) {
        this(new Label(title), builder);
    }

    public SelectionPanel(final Component title,
                          final ListModelBuilder builder) {
        this(title, new List(builder));
    }

    public SelectionPanel(final GlobalizedMessage title,
                          final ListModelBuilder builder) {
        this(new Label(title), builder);
    }

    @Override
    public void reset(final PageState state) {
        s_log.debug("Resetting to default initial state");

        if (m_selector instanceof Resettable) {
            ((Resettable) m_selector).reset(state);
        } else {
            m_model.clearSelection(state);
        }

        // The SelectionListener, on hearing the clearSelection event,
        // will reset the components in m_body.
    }

    public final void addAction(final Component action) {
        m_group.addAction(action);
    }

    public final void addAction(final Component action, final String clacc) {
        m_group.addAction(action, clacc);
    }

    public final Component getSelector() {
        return m_selector;
    }

    protected final void setSelector(Component selector) {
        m_selector = selector;
    }

    public final void setSelectionModel(final SingleSelectionModel model) {
        m_model = model;
    }

    public final SingleSelectionModel getSelectionModel() {
        return m_model;
    }

    public final ActionLink getAddLink() {
        return m_addLink;
    }

    public final Form getAddForm() {
        return m_addForm;
    }

    public final void setAdd(final GlobalizedMessage message,
                             final Form form) {
        setAdd(new ActionLink(new Label(message)), form);
    }

    public final void setAdd(final ActionLink addLink,
                             final Form form) {
        Assert.exists(addLink, "ActionLink addLink");
        Assert.exists(form, "Form form");
        Assert.isUnlocked(this);

        m_addForm = form;
        m_body.add(m_addForm);

        m_addLink = addLink;

        m_body.connect(m_addLink, m_addForm);
    }

    public final ActionLink getEditLink() {
        return m_editLink;
    }

    public final Form getEditForm() {
        return m_editForm;
    }

    public final void setEdit(final GlobalizedMessage message,
                              final Form form) {
        setEdit(new ActionLink(new Label(message)), form);
    }

    public final void setEdit(final ActionLink editLink,
                              final Form form) {
        Assert.exists(editLink, "ActionLink editLink");
        Assert.exists(form, "Form form");
        Assert.isUnlocked(this);

        m_editForm = form;
        m_body.add(m_editForm);

        m_editLink = editLink;

        m_body.connect(m_editLink, m_editForm);
        m_body.connect(m_editForm);
    }

    public final ActionLink getDeleteLink() {
        return m_deleteLink;
    }

    public final Form getDeleteForm() {
        return m_deleteForm;
    }

    public final void setDelete(final GlobalizedMessage message,
                                final Form form) {
        setDelete(new ActionLink(new Label(message)), form);
    }

    public final void setDelete(final ActionLink deleteLink,
                                final Form form) {
        Assert.exists(deleteLink, "ActionLink deleteLink");
        Assert.exists(form, "Form form");
        Assert.isUnlocked(this);

        m_deleteForm = form;
        m_body.add(m_deleteForm);

        m_deleteLink = deleteLink;

        m_body.connect(m_deleteLink, m_deleteForm);
    }

    public final ModalPanel getBody() {
        return m_body;
    }

    public final Component getIntroPane() {
        return m_introPane;
    }

    public final void setIntroPane(final Component pane) {
        Assert.exists(pane, Component.class);
        Assert.isUnlocked(this);

        m_introPane = pane;
        m_body.add(m_introPane);
        m_body.setDefault(m_introPane);
    }

    public final Component getItemPane() {
        return m_itemPane;
    }

    public final void setItemPane(final Component pane) {
        Assert.exists(pane, "Component pane");
        Assert.isUnlocked(this);

        m_itemPane = pane;
        m_body.add(m_itemPane);
    }

    public class SelectionListener implements ChangeListener {
        @Override
        public final void stateChanged(final ChangeEvent e) {
            s_log.debug("Selection state changed; I may change " +
                        "the body's visible pane");

            final PageState state = e.getPageState();

            m_body.reset(state);

            if (m_model.isSelected(state)) {
                s_log.debug("The selection model is selected; displaying " +
                            "the item pane");

                m_body.push(state, m_itemPane);
            }
        }
    }
}
