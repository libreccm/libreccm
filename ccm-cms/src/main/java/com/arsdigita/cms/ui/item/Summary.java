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
import java.util.List;

import com.arsdigita.bebop.PageState;

import org.libreccm.categorization.Category;

import com.arsdigita.cms.CMS;

import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentSection;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.dispatcher.CMSDispatcher;

import org.librecms.lifecycle.Lifecycle;

import com.arsdigita.cms.ui.CMSContainer;

import org.libreccm.security.User;

import com.arsdigita.toolbox.ui.FormatStandards;
import com.arsdigita.util.Assert;
import com.arsdigita.util.GraphSet;
import com.arsdigita.util.Graphs;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Web;

import org.libreccm.workflow.Task;
import org.libreccm.workflow.TaskComment;
import org.libreccm.workflow.Workflow;

import com.arsdigita.xml.Element;

import org.libreccm.auditing.CcmRevision;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.Shiro;
import org.libreccm.workflow.TaskDependency;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSectionManager;
import org.librecms.dispatcher.ItemResolver;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * This panel displays basic details about a content item such as attributes and
 * associations.
 *
 * Container: {@link com.arsdigita.cms.ui.ContentItemPage}
 *
 * This panel uses an {@link com.arsdigita.cms.dispatcher.XMLGenerator} to
 * convert content items into XML.
 *
 * @author Michael Pih
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class Summary extends CMSContainer {

    private static final String SUMMARY = "itemAdminSummary";
    private static final String RESTART_WORKFLOW = "restartWorkflow";

    private final ItemSelectionModel itemSelectionModel;

    public Summary(final ItemSelectionModel itemSelectionModel) {
        super();

        this.itemSelectionModel = itemSelectionModel;
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

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final GlobalizationHelper globalizationHelper = cdiUtil.findBean(
                GlobalizationHelper.class);
            final Locale language = globalizationHelper.getNegotiatedLocale();

            // Determine the item's environment
            final ContentItem item = getContentItem(state);
            final ContentSection section = getContentSection(state);

            final Shiro shiro = cdiUtil.findBean(Shiro.class);
            final User user = shiro.getUser().get();

            // Setup xml element for item's properties
            final Element itemElement = new Element("cms:itemSummary",
                                                    CMS.CMS_XML_NS);

            // Determine item's name / url stub
            itemElement.addAttribute("name", item.getDisplayName());

            // obviously  getName() here gets the 'semantically meaningful name' 
            // from database using class DataType. It is not localizable! And 
            // it is not really 'semantically meaningful'
            final String objectType = item.getClass().getName();

            // Quasimodo: ObjectType for summary
            itemElement.addAttribute("objectType", objectType);
            itemElement.addAttribute("description",
                                     item.getDescription().getValue(language));

            itemElement.addAttribute("title",
                                     item.getTitle().getValue(language));

            // subject category
            final Element subjectCategoriesElement = new Element(
                "cms:subjectCategories", CMS.CMS_XML_NS);
            itemElement.addContent(subjectCategoriesElement);

            // URL
            final Element linkElement = new Element("cms:linkSummary",
                                                    CMS.CMS_XML_NS);
            try {
                linkElement.addAttribute(
                    "url",
                    String.format("%s/redirect?oid=%s",
                                  Web.getWebappContextPath(),
                                  URLEncoder.encode(item.getUuid(), "UTF-8")));
            } catch (UnsupportedEncodingException ex) {
                throw new UncheckedWrapperException(ex);
            }

            //"/redirect?oid=" + URLEncoder.encode(item.getDraftVersion().getOID().toString()));
            // WORKFLOW
            final Element workflowElement = new Element("cms:workflowSummary",
                                                        CMS.CMS_XML_NS);
            final Workflow workflow = item.getWorkflow();
            if (workflow == null) {
                workflowElement.addAttribute("noWorkflow", "1");
            } else {
                workflowElement.addAttribute("name",
                                             workflow.getName().getValue(
                                                 language));

                final List<Task> tasks = workflow.getTasks();
                final GraphSet graph = new GraphSet();

                for (final Task task : tasks) {
                    final List<TaskDependency> blockingTasks = task
                        .getBlockingTasks();
                    final StringBuilder builder = new StringBuilder();
                    for (final TaskDependency blocking : blockingTasks) {
                        graph.addEdge(task, blocking, null);
                        builder.append(blocking
                            .getBlockingTask()
                            .getLabel()
                            .getValue(language));
                    }

                    final int len = builder.length();
                    if (len >= 2) {
                        builder.setLength(len - 2);
                    } else {
                        graph.addNode(task);
                    }
                }

                final List<Task> taskList = new ArrayList<>();
                outer:
                while (graph.nodeCount() > 0) {
                    @SuppressWarnings("unchecked")
                    final List<Task> list = Graphs.getSinkNodes(graph);
                    for (final Task task : list) {
                        taskList.add(0, task);
                        graph.removeNode(task);
                        continue outer;
                    }
                    // break loop if no nodes removed
                    break;
                }

                for (final Task task : taskList) {
                    Element taskElement = new Element("cms:task",
                                                      CMS.CMS_XML_NS);
                    taskElement.addAttribute("name",
                                             task.getLabel().getValue(language));
                    taskElement.addAttribute("state",
                                             task.getTaskState().toString());
                    for (final TaskComment comment : task.getComments()) {
                        final Element commentElement = new Element(
                            "cms:taskComment", CMS.CMS_XML_NS);
                        final User author = comment.getAuthor();
                        final String authorName;
                        if (author == null) {
                            authorName = "Anonymous";
                        } else {
                            authorName = author.getName();
                        }

                        commentElement.addAttribute("author", authorName);
                        commentElement.addAttribute("comment",
                                                    comment.getComment());
                        taskElement.addContent(commentElement);
                    }

                    workflowElement.addContent(taskElement);
                }
            }

            // Revision History (we are using to "transaction" for XML elememts
            // here because this used by the old API and we don't want to brake
            // the XSL.
            final Element revisionsElement = new Element(
                "cms:transactionSummary",
                CMS.CMS_XML_NS);
            final ContentItemRepository itemRepo = cdiUtil.findBean(
                ContentItemRepository.class);
            final List<CcmRevision> revisions = itemRepo.retrieveRevisions(
                item, item.getObjectId());
            if (revisions != null && !revisions.isEmpty()) {
                revisionsElement.addAttribute(
                    "creationDate",
                    FormatStandards.formatDate(revisions.get(0)
                        .getRevisionDate()));
                revisionsElement.addAttribute(
                    "lastModifiedDate",
                    FormatStandards
                        .formatDate(revisions.get(revisions.size() - 1)
                            .getRevisionDate()));
                final ContentSectionManager sectionManager = cdiUtil.findBean(
                    ContentSectionManager.class);
                final ItemResolver itemResolver = sectionManager
                    .getItemResolver(section);
                for (final CcmRevision revision : revisions) {
                    final Element revisionElement = new Element(
                        "cms:transaction", CMS.CMS_XML_NS);
                    revisionElement.addAttribute(
                        "date",
                        FormatStandards.formatDate(revision.getRevisionDate()));
                    final String authorName;
                    if (revision.getUserName() == null
                            || revision.getUserName().trim().isEmpty()) {
                        authorName = "Anonymous";
                    } else {
                        authorName = revision.getUserName();
                    }
                    revisionElement.addAttribute("author", authorName);

                    final String url = String.format(
                        "%s?revision=%d",
                        itemResolver.generateItemURL(
                            state,
                            item,
                            section,
                            CMSDispatcher.PREVIEW),
                        revision.getId());
                     revisionElement.addAttribute("url", url);
                     revisionsElement.addContent(revisionElement);
                }
            }

            // CATEGORY
            final Element categoriesElement = new Element(
                "cms:categorySummary", CMS.CMS_XML_NS);

            final List<Category> categories = item.getCategories().stream()
                .map(categorization -> categorization.getCategory())
                .collect(Collectors.toList());
            final CategoryManager categoryManager = cdiUtil.findBean(
                CategoryManager.class);
            for (final Category category : categories) {
                final Element categoryElement = new Element("cms:category",
                                                            CMS.CMS_XML_NS);
                categoryElement.setText(categoryManager
                    .getCategoryPath(category));
                categoriesElement.addContent(categoryElement);
            }

            // LIFECYCLE
            final Element lifecycleElement = new Element("cms:lifecycleSummary",
                                                         CMS.CMS_XML_NS);

            final Lifecycle lifecycle = item.getLifecycle();
            if (lifecycle == null) {
                lifecycleElement.addAttribute("noLifecycle", "1");
            } else {
                lifecycleElement.addAttribute(
                    "name",
                    lifecycle.getDefinition().getLabel().getValue(language));
                lifecycleElement.addAttribute(
                    "startDate",
                    FormatStandards.formatDate(lifecycle.getStartDateTime()));

                final Date endDate = lifecycle.getEndDateTime();
                if (endDate == null) {
                    lifecycleElement.addAttribute("endDateString",
                                                  "last forever");
                } else {
                    lifecycleElement.addAttribute(
                        "endDateString",
                        String.format("expire on %s",
                                      FormatStandards.formatDate(endDate)));
                    lifecycleElement.addAttribute(
                        "endDate", FormatStandards.formatDate(endDate));
                }

                lifecycleElement.addAttribute(
                    "hasBegun", Boolean.toString(lifecycle.isStarted()));
                lifecycleElement.addAttribute(
                    "hasEnded", Boolean.toString(lifecycle.isFinished()));
            }

            parent.addContent(itemElement);
            parent.addContent(categoriesElement);
            parent.addContent(linkElement);
            parent.addContent(lifecycleElement);
            parent.addContent(workflowElement);
            parent.addContent(revisionsElement);
        }
    }

    /**
     * Fetch the selected content item.
     *
     * @param state The page state
     *
     * @return The selected item
     */
    protected ContentItem getContentItem(final PageState state) {
        final ContentItem item = itemSelectionModel.getSelectedObject(state);
        Assert.exists(item);
        return item;
    }

    /**
     * Fetch the current content section.
     *
     * @param state The page state
     *
     * @return The content section
     */
    protected ContentSection getContentSection(final PageState state) {
        return CMS.getContext().getContentSection();
    }

}
