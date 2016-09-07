/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.librecms.contenttypes;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;

/**
 * Annotation used inside the {@link AuthoringKit} annotation to describe the
 * authoring steps belonging to an authoring kit.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public @interface AuthoringStep {

    /**
     * Key of the label in the {@link #labelBundle()}. If blank (default) the
     * simple name of the annotated class with the suffix {@code .label} is
     * used.
     *
     * @return The label key of the authoring step.
     */
    String labelKey() default "";

    /**
     * Bundle providing the localised label for the authoring step. If omitted
     * the default bundle for the content type will be used. The default bundle
     * is the fully qualified name of the content type class with the suffix
     * {@code Bundle}.
     *
     * @return The bundle providing the label for the authoring step.
     */
    String labelBundle() default "";

    /**
     * Key of the description in the {@link #descriptionBundle()}. If blank
     * (default) the simple name of the annotated class with the suffix
     * {@code .description} is used.
     *
     * @return The description key of the authoring step.
     */
    String descriptionKey() default "";

    /**
     * Bundle providing the localised description for the authoring step. If
     * omitted the default bundle for the content type will be used. The default
     * bundle is the fully qualified name of the content type class with the
     * suffix {@code Bundle}.
     *
     * @return The bundle providing the description for the authoring step.
     */
    String descriptionBundle() default "";

    /**
     * The position of the authoring step.
     * 
     * @return The position of the authoring step.
     */
    int order();
    
    /**
     * The component (usually a {@link Form} providing the UI for the authoring
     * step.
     *
     * @return The class providing the UI for the authoring step.
     */
    Class<? extends Component> component();

}
