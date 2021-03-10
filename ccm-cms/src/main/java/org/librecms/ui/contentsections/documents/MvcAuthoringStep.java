/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections.documents;

import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.contentsection.ContentItem;

import java.util.Set;

/**
 * Describes an authoring step for a document (content item).
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public interface MvcAuthoringStep {

    /**
     * Returns the step identifier.
     *
     * Each authoring step is accessible over the URL {@code /contentsections/documents/{path:+.*}/@authoringsteps/{step}.
     * The method provides the identifier for hte step.
     *
     * @return The path fragment for the authoring step used in URLs.
     */
    String getPathFragment();

    /**
     * Authoring steps only support a specific type, and all subtypes.
     *
     * @return The document type supported by the authoring step.
     */
    Class<? extends ContentItem> supportedDocumenType();

    /**
     * Gets the localized label of the authoring step. The language variant to
     * return should be selected using the locale returned by
     * {@link GlobalizationHelper#getNegotiatedLocale()}.
     *
     * @return The localized label of the authoring step.
     */
    String getLabel();

    /**
     * Gets the localized description of the authoring step. The language
     * variant to return should be selected using the locale returned by
     * {@link GlobalizationHelper#getNegotiatedLocale()}.
     *
     * @return The localized description of the authoring step.
     */
    String getDescription();

    /**
     * Gets the name of the resource bundle providing the localized label and
     * description.
     *
     * @return The resource bundle providing the localized label and description.
     */
    String getBundle();

}
