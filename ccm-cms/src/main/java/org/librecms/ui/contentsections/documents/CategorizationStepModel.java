/*
 * Copyright (C) 2021 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.librecms.ui.contentsections.documents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsCategorizationStep")
public class CategorizationStepModel {

    private List<CategorizationTree> categorizationTrees;

    /**
     * Provides a tree view of the category system assigned to the current
     * content section in an format which can be processed in MVC templates.
     *
     * The categories assigned to the current item as marked.
     *
     * @return Tree view of the category systems assigned to the current content
     *         section.
     */
    public List<CategorizationTree> getCategorizationTrees() {
        return Collections.unmodifiableList(categorizationTrees);
    }

    protected void setCategorizationTrees(
        final List<CategorizationTree> categorizationTrees
    ) {
        this.categorizationTrees = new ArrayList<>(categorizationTrees);
    }

}
