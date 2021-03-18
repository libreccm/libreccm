/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections.documents;

import org.librecms.contentsection.ContentItem;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Provides the steps for creating and viewing and editing a document (content
 * item). The classes provided for {@link #createStep()} and 
 * {@link #authoringSteps() } provide information about the steps.
 *
 * This annotation can only be used on classes extending the {@link ContentItem}
 * class.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MvcAuthoringKit {

    /**
     * Controller of the create step for a document type.
     *
     * @return Descriptor class for the create step.
     */
    Class<? extends MvcDocumentCreateStep<?>> createStep();

    /**
     * The authoring steps for editing the properties of the document. They are
     * used in the same order as they are provided here.
     *
     * @return The descriptor classes ofr the authoring steps for the annotated
     *         document type.
     */
    Class<? extends MvcAuthoringStep>[] authoringSteps();

}
