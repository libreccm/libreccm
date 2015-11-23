package com.arsdigita.ui.admin;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.cdi.utils.CdiLookupException;
import org.libreccm.cdi.utils.CdiUtil;
//import org.libreccm.core.Group;
//import org.libreccm.core.GroupRepository;

import static com.arsdigita.ui.admin.AdminConstants.*;

/**
 * Series of screens required for adding existing groups as subgroups - based on
 * existing functionality for adding permissions to a folder in content/admin
 *
 * @version $Id: ExistingGroupAddPane.java,v 1.4 2004/06/21 11:34:03 cgyg9330
 * Exp $
 */
public class ExistingGroupAddPane extends SimpleContainer implements
    AdminConstants {

    private static final Logger s_log = Logger.getLogger(
        ExistingGroupAddPane.class);

    private ParameterModel searchString = new StringParameter(SEARCH_QUERY);

    private GroupSearchForm groupSearchForm;
    private SimpleContainer selectGroupsPanel;
    private SimpleContainer noResultsPanel;
    private Tree groupTree;
    private GroupAdministrationTab parentPage;

    /**
     *
     */
    private RequestLocal parentGroup = new RequestLocal() {

        @Override
        protected Object initialValue(final PageState ps) {
            String key = (String) groupTree.getSelectedKey(ps);
//
//            Group group = null;
//
//            if (key != null) {
//                final Long id = new Long(key);
//
//                final CdiUtil cdiUtil = new CdiUtil();
//                final GroupRepository groupRepository;
//                try {
//                    groupRepository = cdiUtil.findBean(GroupRepository.class);
//                } catch (CdiLookupException ex) {
//                    throw new UncheckedWrapperException(
//                        "Failed to lookup GroupRepository", ex);
//                }
//                
//                group = groupRepository.findById(id);
//            }
//            
//            return group;
            
            return null;
        }

    };

    /**
     * Constructor.
     *
     * @param groupTree
     * @param parentPage
     */
    public ExistingGroupAddPane(Tree groupTree,
                                GroupAdministrationTab parentPage) {
        this.groupTree = groupTree;
        this.parentPage = parentPage;
    }

    /**
     *
     * @param p
     */
    @Override
    public void register(Page p) {
        super.register(p);
        add(getGroupSearchForm());
        add(getSelectGroupsPanel());
        add(getNoSearchResultPanel());

        // set initial visibility of components
        p.setVisibleDefault(getGroupSearchForm(), true);
        p.setVisibleDefault(getSelectGroupsPanel(), false);
        p.setVisibleDefault(getNoSearchResultPanel(), false);

        p.addGlobalStateParam(searchString);

    }

    /**
     *
     * @return
     */
    public GroupSearchForm getGroupSearchForm() {

        if (groupSearchForm == null) {
            groupSearchForm = new GroupSearchForm(this);
        }
        return groupSearchForm;
    }

    /**
     * Returns a panel with a set of checkboxes for groups fulfilling search
     * criteria
     */
    public SimpleContainer getSelectGroupsPanel() {
        if (selectGroupsPanel == null) {
            SelectGroups selectGroups = new SelectGroups(this,
                                                         getGroupSearchForm());
            selectGroupsPanel = selectGroups.getPanel();
        }
        return selectGroupsPanel;
    }

    /**
     * Returns a bebop panel indicating that the user search yielded no results.
     */
    public SimpleContainer getNoSearchResultPanel() {
        if (noResultsPanel == null) {
            Label errorMsg = GROUP_NO_RESULTS;
            errorMsg.setClassAttr("errorBullet");
            BoxPanel bp = new BoxPanel();
            bp.add(errorMsg);
            bp.add(new GroupSearchForm(this));
            noResultsPanel = new SegmentedPanel().addSegment(new Label(" "), bp);
        }
        return noResultsPanel;
    }

    /**
     * Shows panel with no results to user search.
     */
    public void showNoResults(PageState s) {
        getGroupSearchForm().setVisible(s, false);
        getSelectGroupsPanel().setVisible(s, false);
        getNoSearchResultPanel().setVisible(s, true);
    }

    /**
     * Show the select groups to add as subgroups panel
     */
    public void showGroups(PageState s) {
        getGroupSearchForm().setVisible(s, false);
        getSelectGroupsPanel().setVisible(s, true);
        getNoSearchResultPanel().setVisible(s, false);
    }

    /**
     *
     * show the search form
     */
    public void showSearch(PageState s) {
        getGroupSearchForm().setVisible(s, true);
        getSelectGroupsPanel().setVisible(s, false);
        getNoSearchResultPanel().setVisible(s, false);
    }

    /**
     *
     * @return
     */
    public ParameterModel getSearchString() {
        return searchString;
    }

    /**
     *
     * @return
     */
    public GroupAdministrationTab getParentPage() {
        return parentPage;
    }

    /**
     *
     * @param ps
     *
     * @return
     */
//    public Group getParentGroup(PageState ps) {
//        return (Group) parentGroup.get(ps);
//    }

}
