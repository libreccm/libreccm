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
package com.arsdigita.cms.ui.item;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import com.arsdigita.bebop.PageState;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.CMSDispatcher;
import com.arsdigita.cms.lifecycle.Lifecycle;
import com.arsdigita.cms.ui.CMSContainer;
import com.arsdigita.cms.util.SecurityConstants;
import com.arsdigita.cms.workflow.CMSEngine;
import com.arsdigita.cms.workflow.CMSTask;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainServiceInterfaceExposer;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.Filter;
import com.arsdigita.toolbox.ui.FormatStandards;
import com.arsdigita.util.Assert;
import com.arsdigita.util.GraphSet;
import com.arsdigita.util.Graphs;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.versioning.Transaction;
import com.arsdigita.versioning.TransactionCollection;
import com.arsdigita.versioning.Versions;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.Web;
import com.arsdigita.web.WebContext;
import com.arsdigita.workflow.simple.Engine;
import com.arsdigita.workflow.simple.Task;
import com.arsdigita.workflow.simple.TaskCollection;
import com.arsdigita.workflow.simple.TaskComment;
import com.arsdigita.workflow.simple.Workflow;
import com.arsdigita.workflow.simple.WorkflowTemplate;
import com.arsdigita.xml.Element;
import java.io.UnsupportedEncodingException;

/**
 * <p>
 * This panel displays basic details about a content item such as attributes and associations.</p>
 *
 * <p>
 * Container: {@link com.arsdigita.cms.ui.ContentItemPage}
 *
 * <p>
 * This panel uses an {@link com.arsdigita.cms.dispatcher.XMLGenerator} to convert content items
 * into XML.</p>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Id: Summary.java 1940 2009-05-29 07:15:05Z terry $
 */
public class Summary extends CMSContainer {

    private static final String SUMMARY = "itemAdminSummary";
    private static final String RESTART_WORKFLOW = "restartWorkflow";

    private final ItemSelectionModel m_item;

    public Summary(ItemSelectionModel m) {
        super();

        m_item = m;
    }

    /**
     * Generate XML representation of an item summary.
     *
     * @param state  The page state
     * @param parent The parent DOM element
     *
     * @pre ( state != null )
     * @pre ( parent != null )
     */
    @Override
    public void generateXML(PageState state, Element parent) {
        if (isVisible(state)) {

            // Determine the item's environment
            ContentItem item = getContentItem(state);
            ContentSection section = getContentSection(state);
            User user = Web.getWebContext().getUser();

            // Setup xml element for item's properties
            Element itemElement = new Element("cms:itemSummary", CMS.CMS_XML_NS);

            // Determine item's name / url stub
            itemElement.addAttribute("name", item.getName());

            // obviously  getName() here gets the 'semantically meaningful name' 
            // from database using class DataType. It is not localizable! And 
            // it is not really 'semantically meaningful'
            String objectType = item.getObjectType().getName();

            // Quasimodo: ObjectType for summary
            itemElement.addAttribute("objectType", objectType);

            // NOT USED - CUSTOMIZED SUMMARY
            // Take advantage of caching in the CMS Dispatcher.
            // XMLGenerator xmlGenerator = section.getXMLGenerator();
            // xmlGenerator.generateXML(state, parent, SUMMARY);
            String descriptionAttribute = "";
            if (objectType.equals("NewsItem") || objectType.equals("Article")) {
                descriptionAttribute = "lead";
            } else if (objectType.equals("FileStorageItem") || objectType.equals("Minutes")) {
                descriptionAttribute = "description";
            } else if (objectType.equals("Job")) {
                descriptionAttribute = "jobDescription";
            } else if (objectType.equals("MultiPartArticle") || objectType.equals("Agenda")
                           || objectType.equals("PressRelease") || objectType.equals("Service")) {
                descriptionAttribute = "summary";
            }

            if (!descriptionAttribute.equals("")) {
                itemElement.addAttribute("description", (String) DomainServiceInterfaceExposer.get(
                                         item, descriptionAttribute));
            }

            try {
                ContentPage page = new ContentPage(item.getID());
                itemElement.addAttribute("title", page.getTitle());
            } catch (DataObjectNotFoundException ex) {
                //
            }

            // subject category
            Element subjectsElement = new Element("cms:subjectCategories", CMS.CMS_XML_NS);
            itemElement.addContent(subjectsElement);
            Category itemCategory = null;
            Category subjectCategory = Category.getRootForObject(section, "subject");
            if (subjectCategory != null) {
                CategoryCollection categories = item.getCategoryCollection();
                while (categories.next()) {
                    Category category = categories.getCategory();
                    CategoryCollection parents = category.getDefaultAscendants();
                    parents.addOrder(Category.DEFAULT_ANCESTORS);
                    if (parents.next()) {
                        Category parentCategory = parents.getCategory();
                        if (parentCategory.equals(subjectCategory)) {
                            Element subjectElement = new Element("cms:subjectCategory",
                                                                 CMS.CMS_XML_NS);
                            subjectElement.addAttribute("name", category.getName());
                            subjectElement.setText(category.getPreferredQualifiedName(" -&gt; ",
                                                                                      true));
                            subjectsElement.addContent(subjectElement);
                        }
                        parents.close();
                    }
                }
            }

            // URL
            Element linkElement = new Element("cms:linkSummary", CMS.CMS_XML_NS);
            try {
                linkElement.addAttribute("url",
                                         String.format("%s/redirect?oid=%s",
                                                       Web.getWebappContextPath(),
                                                       URLEncoder.encode(item.getDraftVersion()
                                                           .getOID()
                                                           .toString(), "utf-8")));
            } catch (UnsupportedEncodingException ex) {
                throw new UncheckedWrapperException(ex);
            }

            //"/redirect?oid=" + URLEncoder.encode(item.getDraftVersion().getOID().toString()));
            // WORKFLOW
            Element workflowElement = new Element("cms:workflowSummary", CMS.CMS_XML_NS);
            Workflow workflow = Workflow.getObjectWorkflow(item);

            SecurityManager sm = CMS.getContext().getSecurityManager();
            if (canWorkflowBeExtended(user, item, workflow)) {
                // control event for restarting workflow in edit mode
                try {
                    state.setControlEvent(this, RESTART_WORKFLOW, item.getID().toString());
                    workflowElement.addAttribute("restartWorkflowURL", state.stateAsURL());
                    state.clearControlEvent();
                } catch (java.io.IOException ex) {
                    //
                }
            }

            if (workflow == null) {
                workflowElement.addAttribute("noWorkflow", "1");
            } else {
                workflowElement.addAttribute("name", workflow.getDisplayName());

                TaskCollection tc = workflow.getTaskCollection();
                GraphSet g = new GraphSet();
                while (tc.next()) {
                    Task t = tc.getTask();
                    final TaskCollection deps = t.getRequiredTasks();
                    final StringBuffer buffer = new StringBuffer();
                    while (deps.next()) {
                        Task dep = deps.getTask();
                        g.addEdge(t, dep, null);
                        buffer.append(dep.getLabel() + ", ");
                    }

                    final int len = buffer.length();
                    if (len >= 2) {
                        buffer.setLength(len - 2);
                    } else {
                        g.addNode(t);
                    }
                    deps.close();
                }

                List taskList = new ArrayList();
                outer:
                while (g.nodeCount() > 0) {
                    List l = Graphs.getSinkNodes(g);
                    for (Iterator it = l.iterator(); it.hasNext();) {
                        Task t = (Task) it.next();
                        taskList.add(0, t);
                        g.removeNode(t);
                        continue outer;
                    }
                    // break loop if no nodes removed
                    break;
                }
                Iterator tasks = taskList.iterator();

                while (tasks.hasNext()) {
                    Task task = (Task) tasks.next();
                    Element taskElement = new Element("cms:task", CMS.CMS_XML_NS);
                    taskElement.addAttribute("name", task.getDisplayName());
                    taskElement.addAttribute("state", task.getStateString());
                    Iterator comments = task.getComments();
                    while (comments.hasNext()) {
                        TaskComment comment = (TaskComment) comments.next();
                        Element commentElement = new Element("cms:taskComment", CMS.CMS_XML_NS);
                        User author = comment.getUser();
                        String authorName = "Anonymous";
                        if (author != null) {
                            authorName = author.getDisplayName();
                        }

                        commentElement.addAttribute("author", authorName);
                        commentElement.addAttribute("comment", comment.getComment());
                        commentElement.addAttribute("date", FormatStandards.formatDate(comment
                                                    .getDate()));

                        taskElement.addContent(commentElement);
                    }

                    workflowElement.addContent(taskElement);
                }
            }

            // REVISION HISTORY
            Element transactionElement = new Element("cms:transactionSummary", CMS.CMS_XML_NS);
            transactionElement.addAttribute("creationDate", FormatStandards.formatDate(item
                                            .getCreationDate()));
            transactionElement.addAttribute("lastModifiedDate", FormatStandards.formatDate(item
                                            .getLastModifiedDate()));

            TransactionCollection transactions = Versions.getTaggedTransactions(item.getOID());
            while (transactions.next()) {
                Transaction transaction = transactions.getTransaction();
                Element element = new Element("cms:transaction", CMS.CMS_XML_NS);
                element.addAttribute("date", FormatStandards.formatDate(transaction.getTimestamp()));
                String authorName = "Anonymous";
                User author = transaction.getUser();
                if (author != null) {
                    authorName = author.getDisplayName();
                }
                element.addAttribute("author", authorName);

                String url = section.getItemResolver().generateItemURL(state, item, section,
                                                                       CMSDispatcher.PREVIEW)
                                 + "?transID=" + transaction.getID();
                element.addAttribute("url", url);
                transactionElement.addContent(element);
            }

            transactions.close();

            // CATEGORY
            Element categoryElement = new Element("cms:categorySummary", CMS.CMS_XML_NS);

            CategoryCollection categories = item.getCategoryCollection();
            while (categories.next()) {
                Category category = categories.getCategory();
                Element element = new Element("cms:category", CMS.CMS_XML_NS);
                element.setText(category.getPreferredQualifiedName(" -&gt; ", true));
                categoryElement.addContent(element);

            }
            categories.close();

            // LIFECYCLE
            Element lifecycleElement = new Element("cms:lifecycleSummary", CMS.CMS_XML_NS);

            Lifecycle lifecycle = item.getLifecycle();
            if (lifecycle == null) {
                lifecycleElement.addAttribute("noLifecycle", "1");
            } else {
                lifecycleElement.addAttribute("name", lifecycle.getLabel());
                lifecycleElement.addAttribute("startDate", FormatStandards.formatDate(lifecycle
                                              .getStartDate()));

                java.util.Date endDate = lifecycle.getEndDate();
                if (endDate == null) {
                    lifecycleElement.addAttribute("endDateString", "last forever");
                } else {
                    lifecycleElement.addAttribute("endDateString", "expire on " + FormatStandards
                                                  .formatDate(endDate));
                    lifecycleElement.addAttribute("endDate", FormatStandards.formatDate(endDate));
                }

                lifecycleElement.addAttribute("hasBegun", (new Boolean(lifecycle.hasBegun()))
                                              .toString());
                lifecycleElement.addAttribute("hasEnded", (new Boolean(lifecycle.hasEnded()))
                                              .toString());
            }

            parent.addContent(itemElement);
            parent.addContent(categoryElement);
            parent.addContent(linkElement);
            parent.addContent(lifecycleElement);
            parent.addContent(workflowElement);
            parent.addContent(transactionElement);
        }
    }

    /**
     * Fetch the selected content item.
     *
     * @param state The page state
     *
     * @return The selected item
     *
     * @pre ( state != null )
     */
    protected ContentItem getContentItem(PageState state) {
        ContentItem item = (ContentItem) m_item.getSelectedObject(state);
        Assert.exists(item);
        return item;
    }

    /**
     * Fetch the current content section.
     *
     * @param state The page state
     *
     * @return The content section
     *
     * @pre ( state != null )
     */
    protected ContentSection getContentSection(PageState state) {
        ContentSection section = CMS.getContext().getContentSection();
        return section;
    }

    public void respond(PageState state) throws ServletException {
        String key = state.getControlEventName();
        String value = state.getControlEventValue();
        if (RESTART_WORKFLOW.equals(key)) {
            User user = Web.getWebContext().getUser();
            ContentItem item = getContentItem(state);
            ContentSection section = item.getContentSection();
            Workflow w = Workflow.getObjectWorkflow(item);

            if (canWorkflowBeExtended(user, item, w)) {
                WorkflowTemplate template = w.getWorkflowTemplate();
                if (template != null) {
                    template.extendWorkflow(w);
                    w.save();
                }

                // lock the next task
                Engine engine = Engine.getInstance(CMSEngine.CMS_ENGINE_TYPE);
                Iterator i = engine.getEnabledTasks(user, w.getID()).iterator();
                if (i.hasNext()) {
                    CMSTask task = (CMSTask) i.next();

                    if (!task.isLocked()) {
                        task.lock(user);
                    }
                }
            }

            String redirectURL = Web.getConfig().getDispatcherServletPath() + item
                .getContentSection().getPath() + "/admin/item.jsp?item_id=" + item.getID()
                                     + "&set_tab=1";
            throw new RedirectSignal(redirectURL, true);
        } else {
            throw new ServletException("Unknown control event: " + key);
        }
    }

    /*
     * Checks if workflow can be extended
     */
    protected boolean canWorkflowBeExtended(User user, ContentItem item, Workflow workflow) {
        boolean canBeExtended = true;

        if (workflow == null) {
            canBeExtended = false;
        } else if (!workflow.isFinished()) {
            canBeExtended = false;
        } else if (workflow.getWorkflowTemplate() == null) {
            canBeExtended = false;
        } else {
            TaskCollection templates = item.getContentSection().getWorkflowTemplates();
            Filter f = templates.addInSubqueryFilter("id",
                                                     "com.arsdigita.cms.getWorkflowTemplateUserFilter");
            f.set("userId", Web.getWebContext().getUser().getID());
            templates.addEqualsFilter(ACSObject.ID, workflow.getWorkflowTemplate().getID());

            PrivilegeDescriptor pd = PrivilegeDescriptor.get(SecurityConstants.CMS_WORKFLOW_ADMIN);
            PermissionDescriptor perm = new PermissionDescriptor(pd, item, user);
            if (!(templates.next() || PermissionService.checkPermission(perm))) {
                canBeExtended = false;
            }
            templates.close();

        }

        return canBeExtended;
    }

}
