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

import static com.arsdigita.bebop.Component.*;

import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.EventListenerList;
import com.arsdigita.bebop.event.TreeExpansionEvent;
import com.arsdigita.bebop.event.TreeExpansionListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.tree.DefaultTreeCellRenderer;
import com.arsdigita.bebop.tree.TreeCellRenderer;
import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.bebop.tree.TreeModelBuilder;
import com.arsdigita.bebop.tree.TreeNode;
import com.arsdigita.util.Assert;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.xml.Element;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;

/**
 * Used to print a tree structure. Nodes can be in expanded or collapsed state.
 *  <code>Tree</code> uses the getChildren() and getRoot() methods from
 * TreeModel and traverses the iterator to get to all the nodes.
 *
 * This class keeps track of which nodes are expanded and collapsed and the
 * hierarchy of nodes, and displays the tree correspondingly.
 *
 * @author David Lutterkort
 * @author Stanislav Freidin
 * @author Tri Tran
 * @version $Id$
 */
public class Tree extends SimpleComponent implements Resettable {

    private static final Logger LOGGER = LogManager.getLogger(Tree.class);

    private static final boolean s_selectAttributeEnabled = BebopConfig
        .getConfig().isTreeSelectEnabled();

    // Any node id in the currentState is equivalent
    // to that node being expanded.  If node id is
    // NOT in the currentState, then it's collapsed.
    private static final String CURRENT_STATE = "state";
    private static final String EXPAND_EVENT = "expand";
    private static final String COLLAPSE_EVENT = "collapse";
    private static final String SELECT = "sel";
    private static final String SELECT_EVENT = "s";

    private static final boolean EXPANDED = true;
    private static final boolean NOT_EXPANDED = false; // Collapsed
    private static final boolean LEAF = true;
    private static final boolean NOT_LEAF = false;

    protected StringParameter m_currentState;

    protected TreeModelBuilder m_builder;
    private RequestLocal m_model;
    private TreeModel m_tree;

    private EventListenerList m_listeners;

    private SingleSelectionModel m_selection;

    private ChangeListener m_changeListener;

    private Element treeElement;

    private TreeCellRenderer m_renderer;

    /**
     * Constructs a new <code>Tree</code> using the specified
     * {@link TreeModelBuilder}. The {@link TreeModelBuilder} will instantiate a
     * {@link TreeModel} during each request.
     *
     * @param b the {@link TreeModelBuilder}
     */
    public Tree(TreeModelBuilder b) {
        super();
        m_currentState = new StringParameter(CURRENT_STATE);
        m_builder = b;
        m_renderer = new DefaultTreeCellRenderer();
        m_selection = new ParameterSingleSelectionModel(new StringParameter(
            SELECT));
        m_listeners = new EventListenerList();

        m_model = new RequestLocal() {

            protected Object initialValue(PageState s) {
                return getModelBuilder().makeModel(Tree.this, s);
            }

        };

        m_tree = null;
    }

    /**
     * Deprecated constructor that takes a default {@link TreeModel} and wraps
     * it in a dummy TreeModelBuilder.
     *
     * @param t the TreeModel
     *
     * @deprecated This constructor has been deprecated in favor of
     * <code>Tree(TreeModelBuilder b)</code>. It is not practical to hardwire
     * the <code>TreeModel</code> into the <code>Tree</code>, since the model
     * may change during each request. It is possible to write the
     * model-instantiation code in {@link TreeModel#getRoot(PageState)}, but the
     * {@link TreeModelBuilder} fits better into the pattern which has already
     * been established by {@link List} and {@link Table}
     */
    public Tree(TreeModel t) {
        this(new WrapperModelBuilder());
        m_tree = t;
    }

    /**
     * Registers the two parameters to the page.
     */
    public void register(Page p) {
        Assert.isUnlocked(this);

        p.addComponent(this);
        p.addComponentStateParam(this, m_currentState);
        p.addComponentStateParam(this, getSelectionModel().getStateParameter());
    }

    /**
     * Clears the request state of the tree.
     */
    public void reset(final PageState state) {
        clearSelection(state);
        clearExpansionState(state);
    }

    /**
     * Returns the tree model used for this tree.
     *
     * @return a <code>TreeModel</code>.
     *
     * @see #setTreeModel setTreeModel
     * @see TreeModel
     * @deprecated Use {@link #getTreeModel(PageState)} instead
     */
    public final TreeModel getTreeModel() {
        return m_tree;
    }

    /**
     * Returns the {@link TreeModel} used by the tree for the current request.
     *
     * @param s the page state
     */
    public TreeModel getTreeModel(PageState s) {
        return (TreeModel) m_model.get(s);
    }

    /**
     * @return the {@link TreeModelBuilder} used to build the tree model for
     *         this tree.
     */
    public final TreeModelBuilder getModelBuilder() {
        return m_builder;
    }

    /**
     * @param b the new {@link TreeModelBuilder} for the tree
     */
    public void setModelBuilder(TreeModelBuilder b) {
        Assert.isUnlocked(this);
        m_builder = b;
    }

    /**
     * Sets the tree model used for this tree.
     *
     * @return a <code>TreeModel</code>.
     *
     * @see #setTreeModel setTreeModel
     * @see TreeModel
     */
    public void setTreeModel(TreeModel m) {
        Assert.isUnlocked(this);
        m_tree = m;
    }

    /**
     * Sets the selection model, which keeps track of which node is currently
     * selected. It can be used to manipulate the selection programmatically.
     *
     * @param m the new selection model
     */
    public void setSelectionModel(SingleSelectionModel m) {
        Assert.isUnlocked(this);
        m_selection = m;
        LOGGER.debug("New model: " + m);
    }

    /**
     * Gets the selection model, which keeps track of which node is currently
     * selected. It can be used to manipulate the selection programmatically.
     *
     * @return the model used by the tree to keep track of the selected node.
     */
    public final SingleSelectionModel getSelectionModel() {
        return m_selection;
    }

    /**
     * Gets the key for the selected node. This will only be a valid key if
     * {@link #isSelected isSelected} is <code>true</code>.
     *
     * @param state represents the state of the current request
     *
     * @return the key for the selected node.
     *
     * @pre isSelected(state)
     */
    public Object getSelectedKey(PageState state) {
        return m_selection.getSelectedKey(state);
    }

    /**
     * Sets the selection to the one with the specified key. If <code>key</code>
     * was not selected already, fires the {@link
     * ChangeEvent}.
     *
     * @param state represents the state of the current request
     * @param key   the key for the selected node
     *
     * @see #fireStateChanged fireStateChanged
     */
    public void setSelectedKey(PageState state, Object key) {
        m_selection.setSelectedKey(state, key);
    }

    /**
     * Returns <code>true</code> if one of the nodes is currently selected.
     *
     * @param state represents the state of the current request
     *
     * @return <code>true</code> if one of the nodes is selected;
     *         <code>false</code> otherwise.
     */
    public boolean isSelected(PageState state) {
        return m_selection.isSelected(state);
    }

    /**
     * Clears the selection in the request represented by <code>state</code>.
     *
     * @param state represents the state of the current request
     *
     * @post ! isSelected(state)
     */
    public void clearSelection(PageState state) {
        m_selection.clearSelection(state);
    }

    /**
     * Tells whether the tree has state on the request for tree node expansion.
     */
    public final boolean hasExpansionState(final PageState state) {
        return state.getValue(m_currentState) != null;
    }

    /**
     * Clears any tree node expansion state on the request.
     */
    public final void clearExpansionState(final PageState state) {
        state.setValue(m_currentState, null);
    }

    /**
     * Creates the change listener used for forwarding change events fired by
     * the selection model to change listeners registered with the tree. The
     * returned change listener refires the event with the tree, rather than the
     * selection model, as source.
     *
     * @return the change listener used internally by the tree.
     */
    protected ChangeListener createChangeListener() {
        return new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                fireStateChanged(e.getPageState());
            }

        };
    }

    /**
     * Adds a change listener. A change event is fired whenever the selected
     * tree node changes during the processing of a request. The change event
     * that listeners receive names the tree as the source.
     *
     * @param l the change listener to run when the selected item changes in a
     *          request
     *
     * @pre ! isLocked()
     */
    public void addChangeListener(ChangeListener l) {
        Assert.isUnlocked(this);
        if (m_changeListener == null) {
            m_changeListener = createChangeListener();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Adding listener " + l + " to " + this);
            }

            m_selection.addChangeListener(m_changeListener);
        }
        m_listeners.add(ChangeListener.class, l);
    }

    /**
     * Removes a change listener. The listener should have been previously added
     * with {@link #addChangeListener addChangeListener}, although no error is
     * signalled if the change listener is not found among the tree's listeners.
     *
     * @param l the change listener to remove from the tree
     */
    public void removeChangeListener(ChangeListener l) {
        Assert.isUnlocked(this);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Removing listener " + l + " from " + this);
        }

        m_listeners.remove(ChangeListener.class, l);
    }

    /**
     * Fires a change event to signal that the selected list item has changed in
     * the request represented by <code>state</code>. The source of the event is
     * the tree.
     *
     * @param state represents the state of the current request
     */
    protected void fireStateChanged(PageState state) {
        Iterator i = m_listeners.getListenerIterator(ChangeListener.class);
        ChangeEvent e = null;

        while (i.hasNext()) {
            if (e == null) {
                e = new ChangeEvent(this, state);
            }
            ((ChangeListener) i.next()).stateChanged(e);
        }
    }

    /**
     * Adds a listener that is notified whenever a user clicks on any part of
     * the tree, either to expand or collapse a node, or to select a node. The
     * listener is run whenever {@link #respond respond} is called.
     *
     * @pre l != null
     * @pre ! isLocked()
     */
    public void addActionListener(ActionListener l) {
        Assert.isUnlocked(this);
        m_listeners.add(ActionListener.class, l);
    }

    /**
     * Removes a previously added <code>ActionListener</code>.
     *
     * @see #addActionListener addActionListener
     */
    public void removeActionListener(ActionListener l) {
        Assert.isUnlocked(this);
        m_listeners.remove(ActionListener.class, l);
    }

    /**
     * Notifies listeners that some part of the tree was clicked by the user.
     * The source of the event is the tree.
     *
     * @pre data != null
     * @see #respond respond
     */
    protected void fireActionEvent(PageState data) {
        Iterator i = m_listeners.getListenerIterator(ActionListener.class);
        ActionEvent e = null;

        while (i.hasNext()) {
            if (e == null) {
                e = new ActionEvent(this, data);
            }
            ((ActionListener) i.next()).actionPerformed(e);
        }
    }

    /**
     * Adds a listener that is notified whenever a tree node is expanded or
     * collpased, either by a user's click or by explicit calls to {@link
     * #expand expand} or {@link #collapse collapse}.
     *
     * @pre l != null
     * @pre ! isLocked()
     */
    public void addTreeExpansionListener(TreeExpansionListener l) {
        Assert.isUnlocked(this);
        m_listeners.add(TreeExpansionListener.class, l);
    }

    /**
     * Removes a previously added <code>TreeExpansionListener</code>.
     *
     * @pre ! isLocked()
     * @see #addTreeExpansionListener addTreeExpansionListener
     */
    public void removeTreeExpansionListener(TreeExpansionListener l) {
        Assert.isUnlocked(this);
        m_listeners.remove(TreeExpansionListener.class, l);
    }

    /**
     * Notifies all registered {@link
     * com.arsdigita.bebop.event.TreeExpansionListener
     * TreeExpansionListeners} that a node in the tree has been expanded.
     *
     * @pre state != null
     * @pre nodeKey != null
     */
    protected void fireTreeExpanded(PageState state, Object nodeKey) {
        Iterator i = m_listeners
            .getListenerIterator(TreeExpansionListener.class);
        TreeExpansionEvent e = null;

        while (i.hasNext()) {
            if (e == null) {
                e = new TreeExpansionEvent(this, state, nodeKey);
            }
            ((TreeExpansionListener) i.next()).treeExpanded(e);
        }
    }

    /**
     * Notifies all registered {@link
     * com.arsdigita.bebop.event.TreeExpansionListener
     * TreeExpansionListeners} that a node in the tree has been collapsed.
     *
     * @pre state != null
     * @pre nodeKey != null
     */
    protected void fireTreeCollapsed(PageState state, Object nodeKey) {
        Iterator i = m_listeners
            .getListenerIterator(TreeExpansionListener.class);
        TreeExpansionEvent e = null;

        while (i.hasNext()) {
            if (e == null) {
                e = new TreeExpansionEvent(this, state, nodeKey);
            }
            ((TreeExpansionListener) i.next()).treeCollapsed(e);
        }
    }

    /**
     * Notifies the <code>Tree</code> that a node has been selected. Changes the
     * currently selected tree component.
     */
    public void respond(PageState data) throws javax.servlet.ServletException {
        String action = data.getControlEventName();
        String node = data.getControlEventValue();

        if (EXPAND_EVENT.equals(action)) {
            expand(node, data);
        } else if (COLLAPSE_EVENT.equals(action)) {
            collapse(node, data);
        } else if (SELECT_EVENT.equals(action)) {
            setSelectedKey(data, data.getControlEventValue());
        } else {
            throw new javax.servlet.ServletException("Unknown event '" + action
                                                     + "'");
        }
        fireActionEvent(data);
    }

    //////////////////////////////
    // MANAGE TREE'S NODE STATE //
    //////////////////////////////
    /**
     * Determines whether the node at the specified display row is collapsed.
     *
     * @return <code>true</code> if the node at the specified display row is
     *         collapsed; <code>false</code> otherwise.
     */
    public boolean isCollapsed(String nodeKey, PageState data) {
        String stateString = (String) data.getValue(m_currentState);
        String spaceId = " " + nodeKey + " ";
        int idIndex;

        if (stateString == null) {
            return true;
        } else {
            idIndex = stateString.indexOf(spaceId);
        }

        // == -1 means it's not found in current state, thus it's collapsed
        return (idIndex == -1);
    }

    /**
     * Collapses a node in the tree and makes its children visible.
     *
     * @param nodeKey the key that the tree model uses to identify the node
     * @param data    represents the current request
     *
     * @pre nodeKey != null
     * @pre data != null
     */
    public void collapse(String nodeKey, PageState data) {
        Assert.exists(nodeKey);
        Assert.exists(data);

        StringBuffer newCurrentState = new StringBuffer("");
        String stateString = (String) data.getValue(m_currentState);
        int idIndex;
        String spaceId = " " + nodeKey + " ";
        int idLength = spaceId.length();

        if (stateString != null) {
            idIndex = stateString.indexOf(spaceId);
            // Found it; it should currently be expanded, so collapse it
            if (idIndex != -1) {
                newCurrentState
                    .append(stateString.substring(0, idIndex))
                    .append(" ");
                if (stateString.length() > (idIndex + idLength)) {
                    newCurrentState.append(stateString.substring(idIndex
                                                                 + idLength));
                }
                data.setValue(m_currentState, newCurrentState.toString());
                fireTreeCollapsed(data, nodeKey);
            }
        }
    }

    /**
     * Expands a node in the tree and makes its children visible.
     *
     * @param nodeKey the key that the tree model uses to identify the node
     * @param data    represents the current request
     *
     * @pre nodeKey != null
     * @pre data != null
     */
    public void expand(String nodeKey, PageState data) {
        Assert.exists(nodeKey);
        Assert.exists(data);

        String stateString = (String) data.getValue(m_currentState);
        String spaceId = " " + nodeKey + " ";
        StringBuffer newCurrentState = new StringBuffer("");

        if (stateString != null) {
            // Can't find it; it should currently be collapsed, so expand it
            if ((stateString.indexOf(spaceId)) == -1) {
                // Start with existing stateString...
                newCurrentState.append(stateString);
                // Add to newCurrentState string the new node Id
                newCurrentState.append(spaceId);
                // Set the value of the current state
                data.setValue(m_currentState, newCurrentState.toString());
                fireTreeExpanded(data, nodeKey);
            }
        } else {
            // Add to newCurrentState string the new node Id
            newCurrentState.append(spaceId);
            // Set the value of the current state
            data.setValue(m_currentState, newCurrentState.toString());
            fireTreeExpanded(data, nodeKey);
        }
    }

    /////////////////////////////////////////////
    // PRINT THE TREE DIRECTLY OR GENERATE DOM //
    /////////////////////////////////////////////
    /**
     * Returns the renderer currently used to render tree nodes.
     *
     * @return the current tree node renderer.
     */
    public final TreeCellRenderer getCellRenderer() {
        return m_renderer;
    }

    /**
     * Sets the cell renderer to be used when generating output with
     * {@link #generateXML generateXML}.
     *
     * @param r a <code>TreeCellRenderer</code> value
     */
    public void setCellRenderer(TreeCellRenderer r) {
        Assert.isUnlocked(this);
        m_renderer = r;
    }

    private boolean hasSelectedChild(TreeModel tree, TreeNode node,
                                     PageState data, Object selKey) {
        String nodeKey = (String) node.getKey();
        if ((selKey != null) && (selKey.equals(nodeKey) || selKey.toString()
                                 .equals(nodeKey))) {
            return true;
        }
        Iterator i = tree.getChildren(node, data);
        while (i.hasNext()) {
            TreeNode child = (TreeNode) i.next();
            if (hasSelectedChild(tree, child, data, selKey)) {
                // At this point we should close the opened DataQuery pointed to by Iterator (i).
                // Since the data query is wrapped within DataQueryIterator, we don't have
                // access to it directly, so this looks like the only viable option ...
                while (i.hasNext()) {
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Builds a DOM representing the tree.
     *
     */
    protected void generateTree(PageState data, Element parent, TreeNode node,
                                TreeModel tree) {

        Element t_node = parent.newChildElement("bebop:t_node", BEBOP_XML_NS);

        String nodeKey = node.getKey().toString();
        Object selKey = getSelectedKey(data);
        boolean isSelected = (selKey != null)
                                 && (selKey.equals(nodeKey) || selKey.toString()
                                     .equals(nodeKey));

        boolean hasChildren = tree.hasChildren(node, data);
        if (s_selectAttributeEnabled) {
            boolean hasSelectedChild = false;
            if (!isSelected && hasChildren) {
                hasSelectedChild = hasSelectedChild(tree, node, data, selKey);
            }
            t_node.addAttribute("isSelected", String.valueOf(isSelected
                                                             | hasSelectedChild));
        }

        if (hasChildren) {
            boolean collapsed = isCollapsed(nodeKey, data);
            data
                .setControlEvent(this, collapsed ? EXPAND_EVENT : COLLAPSE_EVENT,
                                 nodeKey);
            try {
                t_node.addAttribute("href", data.stateAsURL());
            } catch (java.io.IOException ioe) {
                // TODO: stateAsURL failed
            }
            data.clearControlEvent();
            if (collapsed) {
                // Collapsed
                t_node.addAttribute("collapsed", "t");
                data.setControlEvent(this, SELECT_EVENT, nodeKey);
                Component c = getCellRenderer().getComponent(this, data,
                                                             node.getElement(),
                                                             isSelected,
                                                             NOT_EXPANDED,
                                                             NOT_LEAF,
                                                             nodeKey);
                c.generateXML(data, t_node);
            } else {
                // Expanded
                t_node.addAttribute("expanded", "t");
                data.setControlEvent(this, SELECT_EVENT, nodeKey);
                Component c = getCellRenderer().getComponent(this, data,
                                                             node.getElement(),
                                                             isSelected,
                                                             EXPANDED, NOT_LEAF,
                                                             nodeKey);
                c.generateXML(data, t_node);
                t_node.addAttribute("indentStart", "t");
                for (Iterator i = tree.getChildren(node, data); i.hasNext();) {
                    generateTree(data, t_node, (TreeNode) i.next(), tree);
                }
                t_node.addAttribute("indentClose", "t");
            }
        } else {
            // No children, no need for link...
            t_node.addAttribute("childless", "t");
            data.setControlEvent(this, SELECT_EVENT, nodeKey);
            Component c = getCellRenderer().getComponent(this, data,
                                                         node.getElement(),
                                                         isSelected,
                                                         NOT_EXPANDED, LEAF,
                                                         nodeKey);
            c.generateXML(data, t_node);
        }
    }

    /**
     * Services the request by building a DOM tree with the nodes first and then
     * the included page.
     */
    public void generateXML(PageState data, Element parent) {

        TreeModel tree = getTreeModel(data);

        if (!isVisible(data)) {
            return;
        }

        treeElement = parent.newChildElement("bebop:tree", BEBOP_XML_NS);
        exportAttributes(treeElement);

        TreeNode _rootNode = tree.getRoot(data);
        generateTree(data, treeElement, _rootNode, tree);

    }

    /**
     * Manage the selected item by manipulating the state parameter.
     *
     * @deprecated The {@link ParameterSingleSelectionModel} contains all the
     * functionality of this class
     */
    public static class TreeSingleSelectionModel
        extends ParameterSingleSelectionModel {

        public TreeSingleSelectionModel(ParameterModel m) {
            super(m);
        }

    }

    /**
     * Locks the <code>Tree</code> and prohibits further modifications.
     */
    public void lock() {
        getModelBuilder().lock();
        super.lock();
    }

    /**
     * Returns the tree model of the tree. A wrapper class to make deprecated
     * constructor work.
     */
    private static class WrapperModelBuilder extends LockableImpl
        implements TreeModelBuilder {

        public WrapperModelBuilder() {
            super();
        }

        public TreeModel makeModel(Tree t, PageState s) {
            return t.getTreeModel();
        }

    }

}
