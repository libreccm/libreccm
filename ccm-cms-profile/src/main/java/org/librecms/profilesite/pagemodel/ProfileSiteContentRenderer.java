/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.profilesite.pagemodel;

import org.librecms.profilesite.ProfileSiteItem;

import java.util.Map;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public interface ProfileSiteContentRenderer {

    /**
     * Provides the category name for which this renderer is responsible.
     *
     * @return The category name for which this renderer is responsible.
     */
    String getCategoryName();

    /**
     * Renders special content for a profile site depending on the current
     * category.
     *
     * @param componentModel
     * @param parameters
     * @param profileSiteItem
     *
     * @return
     */
    Map<String, Object> renderContent(
        ProfileSiteComponent componentModel,
        Map<String, Object> parameters,
        ProfileSiteItem profileSiteItem
    );

}
