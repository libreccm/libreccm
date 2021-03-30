/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections.documents;

import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.assets.RelatedLink;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsInternalLinkDetailsModel")
public class InternalLinkDetailsModel {

    @Inject
    private GlobalizationHelper globalizationHelper;

    private String listIdentifier;

    private String uuid;

    private String label;
    
    private Map<String, String> title;

    private List<String> unusedTitleLocales;

    private String targetItemUuid;

    private String targetItemName;

    private String targetItemTitle;

    public String getListIdentifier() {
        return listIdentifier;
    }

    protected void setListIdentifier(final String listIdentifier) {
        this.listIdentifier = listIdentifier;
    }

    public String getUuid() {
        return uuid;
    }

    public String getLabel() {
        return label;
    }
    
    public Map<String, String> getTitle() {
        return Collections.unmodifiableMap(title);
    }

    public List<String> getUnusedTitleLocales() {
        return Collections.unmodifiableList(unusedTitleLocales);
    }

    public String getTargetItemUuid() {
        return targetItemUuid;
    }

    public String getTargetItemName() {
        return targetItemName;
    }

    public String getTargetItemTitle() {
        return targetItemTitle;
    }

    protected void setInternalLink(final RelatedLink link) {
        Objects.requireNonNull(link);

        uuid = link.getUuid();
        label = globalizationHelper.getValueFromLocalizedString(
            link.getTitle()
        );
        title = link
            .getTitle()
            .getValues()
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    entry -> entry.getKey().toString(),
                    entry -> entry.getValue()
                )
            );
        final Set<Locale> titleLocales = link.getTitle().getAvailableLocales();
        unusedTitleLocales = globalizationHelper
            .getAvailableLocales()
            .stream()
            .filter(locale -> !titleLocales.contains(locale))
            .map(Locale::toString)
            .collect(Collectors.toList());
        targetItemUuid = link.getTargetItem().getItemUuid();
        targetItemName = link.getTargetItem().getDisplayName();
        targetItemTitle = globalizationHelper.getValueFromLocalizedString(
            link.getTargetItem().getTitle()
        );
    }

}
