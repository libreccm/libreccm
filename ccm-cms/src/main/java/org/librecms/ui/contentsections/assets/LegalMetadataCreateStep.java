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
package org.librecms.ui.contentsections.assets;

import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.AuthorizationRequired;
import org.librecms.assets.LegalMetadata;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsLegalMetadataCreateStep")
public class LegalMetadataCreateStep
    extends AbstractMvcAssetCreateStep<LegalMetadata> {

    private String rightsHolder;

    private String rights;

    private String publisher;

    private String creator;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Override
    public String showCreateStep() {
        return "org/librecms/ui/contentsection/assets/legalmetadata/create-legalmetadata.xhtml";
    }

    @Override
    public String getLabel() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("legalmetadata.create");
    }

    @Override
    public String getDescription() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("legalmetadata.description");
    }

    @Override
    public String getBundle() {
        return MvcAssetStepsConstants.BUNDLE;
    }

    @Override
    protected Class<LegalMetadata> getAssetClass() {
        return LegalMetadata.class;
    }

    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    protected String setAssetProperties(
        final LegalMetadata asset, final Map<String, String[]> formParams
    ) {
        rights = Optional
            .ofNullable(formParams.get("rights"))
            .filter(value -> value.length > 0)
            .map(value -> value[0])
            .orElse(null);
        rightsHolder =  Optional
            .ofNullable(formParams.get("rightsHolder"))
            .filter(value -> value.length > 0)
            .map(value -> value[0])
            .orElse(null);
        publisher =  Optional
            .ofNullable(formParams.get("publisher"))
            .filter(value -> value.length > 0)
            .map(value -> value[0])
            .orElse(null);
         Optional
            .ofNullable(formParams.get("creator"))
            .filter(value -> value.length > 0)
            .map(value -> value[0])
            .orElse(null);
         
         asset.setCreator(creator);
         asset.setPublisher(publisher);
         asset.getRights().addValue(new Locale(getInitialLocale()), rights);
         asset.setRightsHolder(rightsHolder);

        return String.format(
            "redirect:/%s/assets/%s/%s/@legalmetadata-edit",
            getContentSectionLabel(),
            getFolderPath(),
            getName()
        );
    }

    public String getRightsHolder() {
        return rightsHolder;
    }

    public String getRights() {
        return rights;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getCreator() {
        return creator;
    }

    public GlobalizationHelper getGlobalizationHelper() {
        return globalizationHelper;
    }

}
