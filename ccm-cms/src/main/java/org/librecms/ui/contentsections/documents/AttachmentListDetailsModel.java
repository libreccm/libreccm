/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections.documents;

import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.contentsection.AttachmentList;

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
@Named("CmsAttachmentListDetailsModel")
public class AttachmentListDetailsModel {
    
    @Inject
    private GlobalizationHelper globalizationHelper;

    private String uuid;

    private String name;

    private Map<String, String> titles;

    private Map<String, String> descriptions;

    private List<String> unusedTitleLocales;

    private List<String> unusedDescriptionLocales;

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getTitles() {
        return Collections.unmodifiableMap(titles);
    }

    public Map<String, String> getDescriptions() {
        return Collections.unmodifiableMap(descriptions);
    }

    public List<String> getUnusedTitleLocales() {
        return Collections.unmodifiableList(unusedTitleLocales);
    }

    public List<String> getUnusedDescriptionLocales() {
        return Collections.unmodifiableList(unusedDescriptionLocales);
    }

    protected void setAttachmentList(final AttachmentList list) {
        Objects.requireNonNull(list);
        uuid = list.getUuid();
        name = list.getName();
        titles = list
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
        descriptions = list
            .getDescription()
            .getValues()
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    entry -> entry.getKey().toString(),
                    entry -> entry.getValue()
                )
            );
            
        final Set<Locale> titleLocales = list
            .getTitle()
            .getAvailableLocales();
        unusedTitleLocales = globalizationHelper
            .getAvailableLocales()
            .stream()
            .filter(locale -> !titleLocales.contains(locale))
            .map(Locale::toString)
            .collect(Collectors.toList());
        
        final Set<Locale> descriptionLocales = list
            .getDescription()
            .getAvailableLocales();
        unusedDescriptionLocales = globalizationHelper
            .getAvailableLocales()
            .stream()
            .filter(locale -> !descriptionLocales.contains(locale))
            .map(Locale::toString)
            .collect(Collectors.toList());
    }

}
