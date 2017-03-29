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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to customise the keys and bundles used to
 * retrieve the label and the description of a content type.
 *
 * If the annotation is not present or if one of the values in this annotation
 * is omitted the default values are used.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ContentTypeDescription {

    /**
     * The key for the label for the content type in the label bundle. Default
     * value is {@code label}.
     *
     * @return The key for label of the content type.
     */
    String labelKey() default "";

    /**
     * The bundle which provides the label for the content type. Default is the
     * fully qualified class name of the annotated content item class with the
     * suffix {@code Bundle}. For example the default bundle for the content
     * type {@code org.librecms.contenttypes.Article} is
     * {@code org.librecms.contenttypes.ArticleBundle}.
     *
     *
     * @return The fully qualified name of the bundle providing the label for
     *         the content type.
     */
    String labelBundle() default "";

    /**
     * The key for the description for the content type in the description
     * bundle. Default value is {@code descripion}.
     *
     * @return The key for description of the content type.
     */
    String descriptionKey() default "";

    /**
     * The bundle which provides the description for the content type. Default
     * is the fully qualified class name of the annotated content item class
     * with the suffix {@code Bundle}. For example the default bundle for the
     * content type {@code org.librecms.contenttypes.Article} is
     * {@code org.librecms.contenttypes.ArticleBundle}.
     *
     *
     * @return The fully qualified name of the bundle providing the label for
     *         the content type.
     */
    String descriptionBundle() default "";

}
