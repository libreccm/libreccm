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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @param <T> Type managed by the {@link SingleSelectionModel} used in instances
 * of this class.
 * 
 * @author unknown
 * @author <a href="jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SelectionPanel<T> extends LayoutPanel implements Resettable {

    private static final Logger LOGGER = LogManager.getLogger(
        SelectionPanel.class);

    private SingleSelectionModel<T> selectionModel;
    private Component selector;
    private ActionGroup actionGroup;
    private final ModalPanel body;

    private Component introPane;
    private Component itemPane;

    private ActionLink addLink;
    private Form addForm;
    private ActionLink editLink;
    private Form editForm;
    private ActionLink deleteLink;
    private Form deleteForm;

    protected void build(final Component title,
                         final Component selector,
                         final SingleSelectionModel<T> model) {
        selectionModel = model;
        this.selector = selector;

        final Section section = new Section();
        setLeft(section);

        section.setHeading(title);

        actionGroup = new ActionGroup();
        section.setBody(actionGroup);

        actionGroup.setSubject(selector);
    }

    protected SelectionPanel() {
        body = new ModalPanel();
        setBody(body);

        introPane = new NullComponent();
        body.add(introPane);
        body.setDefault(introPane);

        itemPane = new NullComponent();
        body.add(itemPane);

        addLink = null;
        addForm = null;
        editLink = null;
        editForm = null;
        deleteLink = null;
        deleteForm = null;
    }

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
                          final SingleSelectionModel<T> model) {
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
                          final SingleSelectionModel<T> model) {
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
        LOGGER.debug("Resetting to default initial state");

        if (selector instanceof Resettable) {
            ((Resettable) selector).reset(state);
        } else {
            selectionModel.clearSelection(state);
        }

        // The SelectionListener, on hearing the clearSelection event,
        // will reset the components in m_body.
    }

    public final void addAction(final Component action) {
        actionGroup.addAction(action);
    }

    public final void addAction(final Component action, final String clacc) {
        actionGroup.addAction(action, clacc);
    }

    public final Component getSelector() {
        return selector;
    }

    protected final void setSelector(final Component selector) {
        this.selector = selector;
    }

    public final void setSelectionModel(final SingleSelectionModel<T> model) {
        selectionModel = model;
    }

    public final SingleSelectionModel<T> getSelectionModel() {
        return selectionModel;
    }

    public final ActionLink getAddLink() {
        return addLink;
    }

    public final Form getAddForm() {
        return addForm;
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

        addForm = form;
        body.add(addForm);

        this.addLink = addLink;

        body.connect(addLink, addForm);
    }

    public final ActionLink getEditLink() {
        return editLink;
    }

    public final Form getEditForm() {
        return editForm;
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

        editForm = form;
        body.add(editForm);

        this.editLink = editLink;

        body.connect(editLink, editForm);
        body.connect(editForm);
    }

    public final ActionLink getDeleteLink() {
        return deleteLink;
    }

    public final Form getDeleteForm() {
        return deleteForm;
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

        deleteForm = form;
        body.add(deleteForm);

        this.deleteLink = deleteLink;

        body.connect(deleteLink, deleteForm);
    }

    public final ModalPanel getBody() {
        return body;
    }

    public final Component getIntroPane() {
        return introPane;
    }

    public final void setIntroPane(final Component pane) {
        Assert.exists(pane, Component.class);
        Assert.isUnlocked(this);

        introPane = pane;
        body.add(introPane);
        body.setDefault(introPane);
    }

    public final Component getItemPane() {
        return itemPane;
    }

    public final void setItemPane(final Component pane) {
        Assert.exists(pane, "Component pane");
        Assert.isUnlocked(this);

        itemPane = pane;
        body.add(itemPane);
    }

    public class SelectionListener implements ChangeListener {

        @Override
        public final void stateChanged(final ChangeEvent e) {
            LOGGER.debug("Selection state changed; I may change "
                             + "the body's visible pane");

            final PageState state = e.getPageState();

            body.reset(state);

            if (selectionModel.isSelected(state)) {
                LOGGER.debug("The selection model is selected; displaying "
                                 + "the item pane");

                body.push(state, itemPane);
            }
        }

    }

}
