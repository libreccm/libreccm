/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.librecms.ui.authoring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides meta information about an authoring step which is independent from
 * the type of the content item.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ContentItemAuthoringStep {

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
     * the default bundle will be used. The default bundle is the fully
     * qualified name of the authoring step class with the suffix
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
     * omitted the default bundle will be used. The default bundle is the fully
     * qualified name of the authoring step class with the suffix
     * {@code Bundle}.
     *
     * @return The bundle providing the description for the authoring step.
     */
    String descriptionBundle() default "";

}
