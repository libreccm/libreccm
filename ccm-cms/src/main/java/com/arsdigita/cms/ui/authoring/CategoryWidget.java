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
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;

import org.libreccm.categorization.Category;

import com.arsdigita.cms.CMS;
import com.arsdigita.kernel.KernelConfig;

import org.librecms.CMSConfig;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.contentsection.ContentSection;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.math.BigDecimal;

import static javax.naming.ldap.SortControl.*;

public class CategoryWidget extends Widget {

    private LongParameter rootParameter;
    private StringParameter modeParameter;

    public CategoryWidget(final String name,
                          final LongParameter rootParameter,
                          final StringParameter modeParameter) {

        super(new ArrayParameter(new BigDecimalParameter(name)));

        this.rootParameter = rootParameter;
        this.modeParameter = modeParameter;
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
    protected void generateWidget(final PageState state,
                                  final Element parent) {

        Element widget = parent.newChildElement("cms:categoryWidget",
                                                CMS.CMS_XML_NS);
        exportAttributes(widget);

        widget.addAttribute("mode", (String) state.getValue(modeParameter));
        widget.addAttribute("name", getName());

        final Set<Long> selectedCategories = new HashSet<>();

        final Long[] values = (Long[]) getValue(state);
        if (values != null) {
            selectedCategories.addAll(Arrays.asList(values));
        }

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final CategoryRepository categoryRepo = cdiUtil
            .findBean(CategoryRepository.class);
        final Category rootCategory = categoryRepo
            .findById((Long) state.getValue(rootParameter))
            .orElseThrow(() -> new IllegalArgumentException(
            String.format(
                "No Category with ID %d in the database. "
                    + "Where did that ID come from?",
                state.getValue(rootParameter))));

        final List<Category> categories = rootCategory.getSubCategories();

        final Map children = new HashMap();
//        ToDo, I don't understand was is done here...
//        while (categories.next()) {
//            final Category cat = categories.getCategory();
//            final BigDecimal parentID = (BigDecimal) categories
//                .get("parents.id");
//
//            List childList = (List) children.get(parentID);
//            if (childList == null) {
//                childList = new ArrayList();
//                children.put(parentID, childList);
//            }
//
//            childList.add(
//                new CategorySortKeyPair(cat, (BigDecimal) categories.get(
//                                        "parents.link.sortKey")));
//        }

        generateCategory(widget, null, rootCategory, null, selectedCategories,
                         children);
    }

    public void generateCategory(final Element parent,
                                 final String path,
                                 final Category category,
                                 final Long sortKey,
                                 final Set selected,
                                 final Map children) {

        final Element element = new Element("cms:category",
                                            CMS.CMS_XML_NS);

        element.addAttribute("id", XML.format(category.getObjectId()));
        element.addAttribute("name", category.getName());
        element.addAttribute("description",
                             category
                                 .getDescription()
                                 .getValue(KernelConfig
                                     .getConfig()
                                     .getDefaultLocale()));
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
        // sort order attribute added to every node in order that same xsl may
        // be used to transform xml fragments returned by ajax in the Aplaws
        // extension
//        element.addAttribute("order",
//                             CMSConfig.getConfig().getCategoryTreeOrder());

        String fullname = path == null ? "/" : path + " > " + category.getName();
        element.addAttribute("fullname", fullname);
        StringBuilder nodeID = new StringBuilder(parent.getAttribute("node-id"));
        if (nodeID.length() > 0) {
            nodeID.append("-");
        }
        nodeID.append(category.getObjectId());
        element.addAttribute("node-id", nodeID.toString());
        parent.addContent(element);

        List c = (List) children.get(category.getObjectId());
        if (c != null) {
            Iterator i = c.iterator();
            while (i.hasNext()) {
                CategorySortKeyPair pair = (CategorySortKeyPair) i.next();
                Category child = pair.getCategory();
                Long childSortKey = pair.getSortKey();
                generateCategory(element, 
                                 fullname, 
                                 child,
                                 childSortKey, 
                                 selected, 
                                 children);
            }
        }
    }

    private class CategorySortKeyPair {

        private Category category;
        private Long sortKey;

        public CategorySortKeyPair(final Category category, 
                                   final Long sortKey) {
            this.category = category;
            this.sortKey = sortKey;
        }

        public Category getCategory() {
            return category;
        }

        public Long getSortKey() {
            return sortKey;
        }

    }

}
