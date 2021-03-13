/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections.documents;

import org.librecms.contentsection.ContentItem;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Qualifier annotation for implementation of {@link MvcDocumentCreateStep} 
 * providing the type of the created document/content item. Used to select
 * the requested create step from all available implementations.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target(
    {
        ElementType.METHOD,
        ElementType.FIELD,
        ElementType.PARAMETER,
        ElementType.TYPE
    }
)
public @interface CreatesDocumentOfType {

    Class<? extends ContentItem> value();

}
