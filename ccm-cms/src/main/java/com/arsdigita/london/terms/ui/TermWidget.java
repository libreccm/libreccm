/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.london.terms.ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;

import org.libreccm.categorization.Category;

import com.arsdigita.cms.CMS;

import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentSection;
import org.libreccm.categorization.Domain;

import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;

import org.arsdigita.cms.CMSConfig;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.l10n.GlobalizationHelper;

import java.util.ArrayList;

/**
 * A Widget for selecting Terms. Based heavily on CategoryWidget.
 *
 * @author mbooth@redhat.com
 * @author Chris Gilbert
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * Chris Gilbert - updated to identify each node uniquely (correct behaviour for
 * polyhierarchical trees) - also, allow ajax update on all branches or just top
 * level branch
 *
 * nb - widget applies to allocation of categories to any ACSObject hence xml
 * prefix should be more generic eg bebop rather than cms. cms retained for
 * compatibility with existing stylesheets
 *
 * Jens Pelzetter: Variable naming etc changed to comply with usual Java
 * conventions. Adapted to CCM NG.
 */
// NON Javadoc comment:
// Copied from c.ad.aplaws.ui in order to make forum-categorised independend from
// a specific ccm-???-aplaws, i.e. a specific integration layer.
public class TermWidget extends Widget {

    private final StringParameter mode;
    private final ACSObjectCategoryPicker picker;

    public TermWidget(final StringParameter mode,
                      final ACSObjectCategoryPicker picker) {

        super(new ArrayParameter(new BigDecimalParameter("category")));

        this.mode = mode;
        this.picker = picker;

    }

    @Override
    protected String getType() {
        return "category";
    }

    @Override
    public boolean isCompound() {
        return false;
    }

    @Override
    protected void generateWidget(final PageState state, final Element parent) {
        final Domain domain = picker.getDomain(state);

        final Element widget = parent.newChildElement("cms:categoryWidget",
                                                      CMS.CMS_XML_NS);
        exportAttributes(widget);

        widget.addAttribute("mode", (String) state.getValue(mode));
        widget.addAttribute("name", getName());

        final Set<Long> selectedCats = new HashSet<>();

        //jensp 2015-03-12: In same cases we need to be able to pass the 
        //selected categories *and* the selected roots for displaying the 
        //categories nicely. To maintain backwards 
        //compatibility we check the type of the value and work either with the 
        //selected categories only or with the selected categories and their
        //roots.
        final Set<Long> selectedAncestors = new HashSet<>();

        final Long[] values;
        final Long[] selAncestorsValues; //selectedAncestors
        final Object valueObj = getValue(state);
        if (valueObj instanceof Long[][]) {
            if (((Long[][]) valueObj).length >= 1) {
                values = ((Long[][]) valueObj)[0];
            } else {
                throw new IllegalArgumentException(
                    "Value of TermWidget is of type BigDecimal[][] but the array is empty.");
            }

            if (((Long[][]) valueObj).length >= 2) {
                selAncestorsValues = ((Long[][]) valueObj)[1];
            } else {
                selAncestorsValues = null;
            }
        } else if (valueObj instanceof Long[]) {
            values = (Long[]) valueObj;
            selAncestorsValues = null;
        } else {
            throw new IllegalArgumentException(
                "Value of TermWidget is not of type BigDecimal[] or BigDecimal[][]");
        }

        //BigDecimal[] values = (BigDecimal[]) getValue(state);
        if (values != null) {
            selectedCats.addAll(Arrays.asList(values));
        }

        if (selAncestorsValues != null) {
            selectedAncestors.addAll(Arrays.asList(selAncestorsValues));
        }

        final Element selEl = widget.newChildElement(
            "cms:selectedCategories", CMS.CMS_XML_NS);
        selEl.addAttribute("name", getName());
        final Iterator<Long> selCats = selectedCats.iterator();
//        while (selCats.hasNext()) {
//            final Element selCat = selEl.newChildElement("cms:category",
//                                                         CMS.CMS_XML_NS);
//            selCat.addAttribute("id", selCats.next().toString());
//        }
        for (Long selectedCat : selectedCats) {
            final Element selectedCatElem = selEl.newChildElement(
                "cms:category", CMS.CMS_XML_NS);
            selectedCatElem.addAttribute("id", selectedCat.toString());
        }

        final Element selAncestorsElem = widget.newChildElement(
            "cms:selectedAncestorCategories", CMS.CMS_XML_NS);
        selAncestorsElem.addAttribute("name", getName());
        for (Long selAncestor : selectedAncestors) {
            final Element selAncestorElem = selAncestorsElem.newChildElement(
                "cms:category", CMS.CMS_XML_NS);
            selAncestorElem.addAttribute("id", selAncestor.toString());
        }

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final CategoryPickerController controller = cdiUtil
            .findBean(CategoryPickerController.class);

        // only root terms at first, the rest is loaded on-demand via AJAX
        final List<Category> roots = controller.getRootCategories(domain);

        final Element element = generateCategory(
            widget,
            controller.getDomainModelCategory(domain),
            selectedCats,
            null);

        if (CMSConfig.getConfig().isCategoryPickerAjaxExpandAll()) {
            // add attribute to the parent node, so that in stylesheet
            // we can look for any ancestor with this attribute (can't
            // add attribute to categoryWidget element as that is not
            // visible when subbranches are transformed)
            element.addAttribute("expand", "all");
        }

        for (final Category category : roots) {

            generateRootTerm(element,
                             category,
                             selectedCats,
                             category.getCategoryOrder());

        }
    }

    public static Element generateCategory(final Element parent,
                                           final Category category,
                                           final Set<Long> selected,
                                           final Long sortKey) {

        final Element element = parent.newChildElement("cms:category",
                                                       CMS.CMS_XML_NS);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final GlobalizationHelper globalizationHelper = cdiUtil
            .findBean(GlobalizationHelper.class);

        element.addAttribute("id", XML.format(category.getObjectId()));
        element.addAttribute("name", category.getName());
        element.addAttribute(
            "description",
            globalizationHelper.getValueFromLocalizedString(category
                .getDescription()));
        if (selected.contains(category.getObjectId())) {
            element.addAttribute("isSelected", "1");
        } else {
            element.addAttribute("isSelected", "0");
        }
        if (category.isAbstractCategory()) {
            element.addAttribute("isAbstract", "1");
        } else {
            element.addAttribute("isAbstract", "0");
        }
        if (category.isEnabled()) {
            element.addAttribute("isEnabled", "1");
        } else {
            element.addAttribute("isEnabled", "0");
        }
        if (sortKey != null) {
            element.addAttribute("sortKey", sortKey.toString());
        }
        // sort order attribute added to every node so that we can 
        // correctly transform xml fragments returned by ajax 
        element.addAttribute("order", "sortKey");
        element.addAttribute("genCat", "true");

        StringBuilder path = new StringBuilder(parent.getAttribute("fullname"));
        if (path.length() > 0) {
            path.append(" > ");

        }
        path.append(category.getName());
        element.addAttribute("fullname", path.toString());

        // need to uniquely identify each node in polyhierarchical trees
        // so that expand/contract is applied to the correct node by
        // javascript getElementByID function
        StringBuilder nodeID = new StringBuilder(parent.getAttribute("node-id"));
        if (nodeID.length() > 0) {
            nodeID.append("-");

        }
        nodeID.append(category.getObjectId());
        element.addAttribute("node-id", nodeID.toString());

        return element;
    }

    public static Element generateTerm(final Element parent,
                                       final Category category,
                                       final Set<Long> selected,
                                       final Long sortKey) {
        final Element element = generateCategory(parent,
                                                 category,
                                                 selected,
                                                 sortKey);

        element.addAttribute("pid", category.getUniqueId());
        return element;
    }

    private static void generateRootTerm(final Element parent,
                                         final Category term,
                                         final Set<Long> selected,
                                         final Long sortKey) {
        final Element element = generateTerm(parent, term, selected, sortKey);
        element.addAttribute("root", "1");
    }

    public static void generateSubtree(final Element parent,
                                       final Category root,
                                       final Set<Long> ids) {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final CategoryPickerController controller = cdiUtil
            .findBean(CategoryPickerController.class);

        final List<Category> terms = controller.getSubCategories(root);

        final Map<Long, List<Category>> children = new HashMap<>();
        for (final Category term : terms) {
            final Long parentId = controller
                .getParentCategory(term)
                .getObjectId();

            final List<Category> childList;
            if (children.containsKey(parentId)) {
                childList = children.get(parentId);
            } else {
                childList = new ArrayList<>();
                children.put(parentId, childList);
            }

            childList.add(term);
        }

        final Element element = generateCategory(parent, root, ids, null);
        element.addAttribute("fullname", root.getName());
        element.addAttribute("node-id", Long.toString(root.getObjectId()));
        element.addAttribute("order", "sortKey");
        if (CMSConfig.getConfig().isCategoryPickerAjaxExpandAll()) {
            //recognisable attribute has to be in the XML for each snippet that 
            //is transformed, hence add it to the parent
            element.addAttribute("expand", "all");
        }

        if (children.containsKey(root.getObjectId())) {
            final List<Category> roots = children.get(root.getObjectId());
            for (final Category category : roots) {
                generateTermWithChildren(element,
                                         category,
                                         ids,
                                         category.getCategoryOrder(),
                                         children);
            }
        }
    }

    private static void generateTermWithChildren(
        final Element parent,
        final Category category,
        final Set<Long> selected,
        final Long sortKey,
        final Map<Long, List<Category>> children) {

        final Element element = generateCategory(parent,
                                                 category,
                                                 selected,
                                                 sortKey);

        element.addAttribute("pid", category.getUniqueId());

        if (children.containsKey(category.getObjectId())) {
            final List<Category> childs = children.get(category.getObjectId());
            for (final Category child : childs) {
                if (CMSConfig.getConfig().isCategoryPickerAjaxExpandAll()) {
                    generateTerm(element,
                                 child,
                                 selected,
                                 child.getCategoryOrder());
                } else {
                    generateTermWithChildren(element,
                                             child,
                                             selected,
                                             child.getCategoryOrder(),
                                             children);
                }
            }
        }
    }
}
