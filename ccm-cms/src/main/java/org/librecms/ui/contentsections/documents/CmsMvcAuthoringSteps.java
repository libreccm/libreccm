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

import org.librecms.ui.contentsections.documents.media.MediaStep;
import org.librecms.ui.contentsections.documents.media.MediaStepService;
import org.librecms.ui.contentsections.documents.relatedinfo.RelatedInfoStep;
import org.librecms.ui.contentsections.documents.relatedinfo.RelatedInfoStepService;
import org.librecms.ui.contenttypes.MvcArticlePropertiesStep;
import org.librecms.ui.contenttypes.MvcArticleTextBodyStep;
import org.librecms.ui.contenttypes.MvcArticleTextBodyStepResources;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationScoped
public class CmsMvcAuthoringSteps implements MvcAuthoringSteps {

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(
            ExampleAuthoringStep.class,
            CategorizationStep.class,
            MediaStep.class,
            RelatedInfoStep.class,
            MvcArticlePropertiesStep.class,
            MvcArticleTextBodyStep.class
        );
    }

    @Override
    public Set<Class<?>> getResourceClasses() {
        return Set.of(
            MediaStepService.class,
            MvcArticleTextBodyStepResources.class,
            RelatedInfoStepService.class
        );
    }

}
