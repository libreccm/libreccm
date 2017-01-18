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
package com.arsdigita.cms.ui.search;

import com.arsdigita.bebop.form.Submit;

import org.librecms.contentsection.ContentType;

import com.arsdigita.cms.ui.ContentSectionPage;
import com.arsdigita.search.ui.BaseQueryComponent;

/**
 * This class provides a basic query form for CMS admin pages that automatically
 * adds components for the maximal set of filters supported by the current
 * search query engine.
 *
 * @author unknown
 * @author Sören Bernstein <quasi@quasiweb.de>
 * @author Jens Pelzetter (jens@jp-digital.de)
 */
public class ItemQueryComponent extends BaseQueryComponent {

    private String context;

    public ItemQueryComponent(final String context,
                              final boolean limitToContentSection) {
        this(context, limitToContentSection, null);
    }

    public ItemQueryComponent(final String context,
                              final boolean limitToContentSection,
                              final ContentType type) {
        this.context = context;

//ToDo      
//            add(new PermissionFilterComponent(
//                    SecurityManager.CMS_PREVIEW_ITEM));
//
//            add(new SimpleCategoryFilterWidget() {
//
//                @Override
//                protected Category[] getRoots(PageState state) {
//                    Category[] roots;
//                    if (limitToContentSection == true && CMS.getContext().
//                            hasContentSection()) {
//                        ContentSection section = CMS.getContext().
//                                getContentSection();
//                        roots = new Category[]{section.getRootCategory()};
//                    } else {
//                        ContentSectionCollection sections =
//                                                 ContentSection.getAllSections();
//                        List cats = new ArrayList();
//                        while (sections.next()) {
//                            ContentSection section =
//                                           sections.getContentSection();
//                            cats.add(section.getRootCategory());
//                        }
//                        roots =
//                        (Category[]) cats.toArray(new Category[cats.size()]);
//                    }
//                    return roots;
//                }
//            });
//
//            if (type == null) {
//                add(new ContentTypeFilterWidget() {
//
//                    @Override
//                    protected ContentSection getContentSection() {
//                        if (limitToContentSection == true && CMS.getContext().
//                                hasContentSection()) {
//                            return CMS.getContext().getContentSection();
//                        } else {
//                            return super.getContentSection();
//                        }
//                    }
//                });
//            } else {
//                add(new ContentTypeFilterWidget(type) {
//
//                    @Override
//                    protected ContentSection getContentSection() {
//                        if (limitToContentSection == true && CMS.getContext().
//                                hasContentSection()) {
//                            return CMS.getContext().getContentSection();
//                        } else {
//                            return super.getContentSection();
//                        }
//                    }
//                });
//            }
//
//            add(new VersionFilterComponent(context));
//            if (limitToContentSection == true) {
//                add(new ContentSectionFilterComponent());
//            }
//            add(new DateRangeFilterWidget(new LastModifiedDateFilterType(),
//                                          LastModifiedDateFilterType.KEY));
//            add(new DateRangeFilterWidget(new CreationDateFilterType(),
//                                          CreationDateFilterType.KEY));
//            add(new PartyFilterWidget(new CreationUserFilterType(),
//                                      CreationUserFilterType.KEY));
//            add(new PartyFilterWidget(new LastModifiedUserFilterType(),
//                                      LastModifiedUserFilterType.KEY));
        Submit submit = new Submit(context + "_search",
                                   ContentSectionPage.globalize("cms.ui.search"));
        add(submit);
    }

//    private class LaunchDateFilterWidget extends DateRangeFilterWidget {
//
//        public LaunchDateFilterWidget(FilterType type, String name) {
//            super(type, name);
//
//        }
//
//        @Override
//        public boolean isVisible(PageState state) {
//            return !ContentSection.getConfig().getHideLaunchDate()
//                   && super.isVisible(state);
//        }
//    }
}
