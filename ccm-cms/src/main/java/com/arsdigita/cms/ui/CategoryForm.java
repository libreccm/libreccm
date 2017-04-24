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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.*;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.bebop.util.SequentialMap;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.globalization.Globalization;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;

import javax.enterprise.inject.spi.CDI;
import java.math.BigDecimal;
import java.util.*;

/**
 * This is an abstract class which displays the category assignment UI.
 *
 * Displays two listboxes for assigning categories to items, with two
 * submit buttons to move categories back and forth. The left
 * listbox displays all available categories which have not been
 * assigned to the current item. The right listbox displays all categories
 * assigned to the current item.
 * <p>
 *
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 */
public abstract class CategoryForm extends Form
        implements FormProcessListener, FormValidationListener {

    private static final Logger LOGGER = LogManager.getLogger(
            CategoryForm.class);
    private static final String SEPARATOR = ">";
    public static final String FREE = "free";
    public static final String ASSIGNED = "assigned";
    public static final String ASSIGN = "assign";
    public static final String REMOVE = "remove";
    public static final int SELECT_WIDTH = 30;
    public static final int SELECT_HEIGHT = 10;
    public static final String FILLER_OPTION = StringUtils.repeat("_", SELECT_WIDTH);

    private final RequestLocal m_assigned;
    private Submit m_assign, m_remove;

    private final Label m_freeLabel;
    private final Label m_assignedLabel;

    /**
     * Construct a new CategoryForm component
     *
     * @param name the name of the form
     */
    public CategoryForm(String name) {
        super(name, new ColumnPanel(3));

        ColumnPanel panel = (ColumnPanel) getPanel();
        panel.setBorder(false);
        panel.setPadColor("#FFFFFF");
        panel.setColumnWidth(1, "0%");
        panel.setColumnWidth(2, "0%");
        panel.setColumnWidth(3, "0");
        panel.setWidth("0%");
        panel.setClassAttr("CMS Admin");

        // Create the request local
        m_assigned = new RequestLocal() {

            @Override
            public Object initialValue(PageState state) {
                CategoryMap m = new CategoryMap();
                initAssignedCategories(state, m);
                return m;
            }
        };

        // Top row
        m_freeLabel = new Label(GlobalizationUtil.globalize("cms.ui.item.categories.available"), false);
        m_freeLabel.setFontWeight(Label.BOLD);
        add(m_freeLabel, ColumnPanel.LEFT);

        //add(new Label("&nbsp;", false));
        add(new Embedded("&nbsp;", false));

        m_assignedLabel = new Label(GlobalizationUtil.globalize("cms.ui.item.categories.assigned"), false);
        m_assignedLabel.setFontWeight(Label.BOLD);
        add(m_assignedLabel, ColumnPanel.LEFT);

        // Middle Row
        SingleSelect freeWidget = new SingleSelect(new BigDecimalParameter(FREE));
        try {
            freeWidget.addPrintListener(new FreePrintListener());
        } catch (TooManyListenersException e) {
            UncheckedWrapperException.throwLoggedException(getClass(), "Too many listeners", e);
        }
        freeWidget.setSize(SELECT_HEIGHT);
        add(freeWidget);

        BoxPanel box = new BoxPanel(BoxPanel.VERTICAL, true);
        box.setWidth("2%");
        addSubmitButtons(box);
        add(box, ColumnPanel.CENTER | ColumnPanel.MIDDLE);

        SingleSelect assignedWidget =
                new SingleSelect(new BigDecimalParameter(ASSIGNED));
        try {
            assignedWidget.addPrintListener(new AssignedPrintListener());
        } catch (TooManyListenersException e) {
            UncheckedWrapperException.throwLoggedException(getClass(), "Too many listeners", e);
        }
        assignedWidget.setSize(SELECT_HEIGHT);
        add(assignedWidget);

        // Add listeners
        addProcessListener(this);
        addValidationListener(this);

        setClassAttr("CategoryForm");
    }

    protected void addSubmitButtons(Container c) {
        addAssignButton(c);
        addRemoveButton(c);
    }

    protected void addAssignButton(Container c) {
        m_assign = new Submit(ASSIGN, ">>");
        m_assign.setSize(10);
        c.add(m_assign);
    }

    protected void addRemoveButton(Container c) {
        m_remove = new Submit(REMOVE, "<<");
        m_remove.setSize(10);
        c.add(m_remove);
    }

    /**
     * Set the caption of the unassigned categories label
     *
     * @param caption the new caption
     */
    public void setUnassignedCaption(String caption) {
        m_freeLabel.setLabel(caption);
    }

    /**
     * Set the caption of the assigned categories label
     *
     * @param caption the new caption
     */
    public void setAssignedCaption(String caption) {
        m_assignedLabel.setLabel(caption);
    }

    /**
     * @param s the page state
     * @return a {@link CategoryMap} of all assigned categories
     */
    public CategoryMap getAssignedCategories(PageState s) {
        return (CategoryMap) m_assigned.get(s);
    }

    // A print listener which populates the listbox with all
    // unassigned categories, apart from result of getExcludedCategory()
    // (if not null), and the root category.
    // Ordering is alphabetical based on qualified path, so entries are
    // ordered like a tree with all nodes expanded.
    // Ideally ordering should be like an expanded tree but based on
    // the sortkey order of the categories. However, I don't know
    // if it would be possible to write a comparison function that
    // could do this efficiently, and I'm not even going to try
    // chris.gilbert@westsussex.gov.uk
    //
    private class FreePrintListener implements PrintListener {

        @Override
        public void prepare(PrintEvent e) {

            OptionGroup target = (OptionGroup) e.getTarget();
            target.clearOptions();
            PageState state = e.getPageState();
//            Category root = getRootCategory(state);
//            if (root == null) {
//                return;
//            }

            // exclude children of the excluded category (as per javadoc on
            // getExcludedCategory() method. This prevents attempts
            // to create circular category graph (which causes
            // exception in Category during addMapping if not checked here
            Category excludedCat = getExcludedCategory(state);
            CategoryMap excluded = new CategoryMap();
            if (excludedCat != null) {
                java.util.List<Category> excludedSubTree = getExcludedCategory(state).getSubCategories();
                excludedSubTree.forEach(excluded::add);
            }
            CategoryMap assigned = getAssignedCategories(state);
            SortedMap sortedCats = new TreeMap();
//            java.util.List<Category> children = root.getSubCategories();
//            children.forEach(x -> sortedCats.put(x.getName(), x.getUniqueId()));

            Iterator it = sortedCats.entrySet().iterator();
            Map.Entry entry;
            String path;
            String id;
            boolean notExcluded;
            boolean notAlreadyAssigned;
//            boolean notRoot;

            while (it.hasNext()) {
                entry = (Map.Entry) it.next();
                path = (String) entry.getKey();
                id = (String) entry.getValue();

                notExcluded = !excluded.containsKey(id);
                notAlreadyAssigned = !assigned.containsKey(id);
//                notRoot = !id.equals(root.getUniqueId());

                if (notExcluded && notAlreadyAssigned) {// && notRoot) {
                    target.addOption(new Option(id, new Text(path)));
                }

            }

            addFillerOption(target);
        }
    }

    /**
     * Populate a {@link CategoryMap} with all categories which are assigned to
     * the item. Child classes should override this method to do the right thing.
     *
     * @param map The sequential map of all categories which are assigned to
     *   the current item. Overridden method should repeatedly
     *   <code>call map.addCategory(someCategory);</code>
     * @param state The page state
     */
    protected abstract void initAssignedCategories(PageState state, CategoryMap map);

    /**
     * Assign a category, moving it from the list on the left
     * to the list on the right
     *
     * @param s the page state
     * @param cat the category to assign
     */
    protected abstract void assignCategory(PageState s, Category cat);

    /**
     * Unassign a category, moving it from the list on the right
     * to the list on the left
     *
     * @param s the page state
     * @param cat the category to unassign
     */
    protected abstract void unassignCategory(PageState s, Category cat);

    /**
     *  This method returns the URL for the givne item to make sure that
     *  the item it is not possible to have two objects in the same category
     *  with the same URL.
     *  @param state The Page State
     */
    protected abstract String getItemURL(PageState state);

    /**
     *  This allows the validation code to validate the properties of the
     *  object
     */
    protected abstract CcmObject getObject(PageState state);

    /**
     * Get the category which will act as the root for the lists
     * of assigned and unassigned categories. The default implementation
     * returns the root category for the content section. Child classes
     * should override this method if they wish to provide an alternate root category.
     *
     * @param state the page state
     * @return the root category which should be used to populate the lists
     *   of assigned and unassigned categories
     */
//    public Category getRootCategory(PageState state) {
//        return null;
//        return CMS.getContext().getContentSection().getRootCategory();
//    }

    /**
     * Return a category which should be excluded from the list of
     * free categories. It is permissible to return null
     *
     * @param s the page state
     * @return a category whose subtree will not be shown in the
     *   category list
     */
    protected Category getExcludedCategory(PageState s) {
        return null;
    }

    // Populates the "assigned categories" widget
    @Deprecated
    private class AssignedPrintListener implements PrintListener {

        @Override
        public void prepare(PrintEvent e) {
            OptionGroup o = (OptionGroup) e.getTarget();
            o.clearOptions();
            PageState state = e.getPageState();
            CategoryMap m = getAssignedCategories(state);

            if (!m.isEmpty()) {
                for (Iterator i = m.values().iterator(); i.hasNext();) {
                    Category c = (Category) i.next();
                    o.addOption(new Option(c.getUniqueId(), new Text(getCategoryPath(c))));
                }
            } else {
                o.addOption(new Option("", new Text("-- none --")));
            }

            addFillerOption(o);
        }
    }

    // Process the form: assign/unassign categories
    @Override
    public void process(FormSectionEvent e) throws FormProcessException {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final CategoryRepository categoryRepository = cdiUtil.findBean(CategoryRepository.class);

        PageState state = e.getPageState();
        FormData data = e.getFormData();
        Long id;

        if (m_assign.isSelected(state)) {
            id = ((BigDecimal) data.get(FREE)).longValue();
            Optional<Category> optional = categoryRepository.findById(id);
            if (optional.isPresent()) {
                Category cat = optional.get();
                assignCategory(state, cat);
                data.put(ASSIGNED, id);
            } else {
                throw new FormProcessException(GlobalizationUtil.globalize(String.format("Can't find category with id %d", id)));
            }
        } else if (m_remove.isSelected(state)) {
            id = ((BigDecimal) data.get(ASSIGNED)).longValue();
            Optional<Category> optional = categoryRepository.findById(id);
            if (optional.isPresent()) {
                Category cat = optional.get();
                unassignCategory(state, cat);
                data.put(FREE, id);
            } else {
                throw new FormProcessException(GlobalizationUtil.globalize(String.format("Can't find category with id %d", id)));
            }
        }
    }

    // Validate the form: make sure that a category is selected
    // for the remove/assign buttons
    @Override
    public void validate(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();
        FormData data = e.getFormData();
        if (m_assign.isSelected(state)) {
            if (data.get(FREE) == null) {
                data.addError(GlobalizationUtil.globalize("cms.ui.category.assign_select_missing"));
            } else {
                // we need to make sure that no other item in this
                // category has the same name (url)
                 Long id = ((BigDecimal) data.get(FREE)).longValue();

                // Assign a new category
//                try {
//                    String url = getItemURL(state);
//
//                    if (url != null) {
//                        DataQuery query = SessionManager.getSession().retrieveQuery("com.arsdigita.categorization.getAllItemURLsForCategory");
//                        query.setParameter("categoryID", id);
//                        query.addEqualsFilter("lower(url)", url.toLowerCase());
//
//                        if (query.size() > 0) {
//                            // we need to make sure that there is not an item
//                            ACSObject item = getObject(state);
//                            Collection list;
//                            if (item instanceof ContentItem) {
//                                list = BasicItemForm.getAllVersionIDs((ContentItem) item);
//                            } else {
//                                list = new ArrayList();
//                                list.add(item.getID());
//                            }
//                            BigDecimal itemID;
//                            while (query.next()) {
//                                itemID = (BigDecimal) query.get("itemID");
//                                if (!list.contains(itemID)) {
//                                    data.addError("There is already an item "
//                                            + "with the url " + url
//                                            + " in the category "
//                                            + cat.getName());
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                } catch (DataObjectNotFoundException ex) {
//                    s_log.error("Error processing category.  Unable to find "
//                            + "category with id " + id);
//                    throw new FormProcessException(ex);
//                }
            }
        } else if (m_remove.isSelected(state)) {
            if (data.get(ASSIGNED) == null) {
                data.addError(GlobalizationUtil.globalize("cms.ui.category.assign_select_missing"));
            }
        }
    }

    // Add a "filler" option to the option group in order to ensure
    // the correct horizontal width
    private static void addFillerOption(OptionGroup o) {
        o.addOption(new Option("", FILLER_OPTION));
    }

    /**
     * @return the full path to a category
     */
    public static String getCategoryPath(Category c) {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final CategoryManager categoryManager = cdiUtil.findBean(CategoryManager.class);
        return categoryManager.getCategoryPath(c);
    }

    /**
     * A convenience method that abstracts SequentialMap
     * to deal with categories
     */
    protected static class CategoryMap extends SequentialMap {

        public CategoryMap() {
            super();
        }

        public void add(Category c) {
            super.put(c.getUniqueId(), c);
        }
    }
}
