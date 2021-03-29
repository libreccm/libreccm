/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections.documents;

import org.librecms.contentsection.AttachmentList;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsAttachmentListDetailsModel")
public class AttachmentListDetailsModel {

    
    private String uuid;
    
    private String name;

    private Map<String, String> titles;

    private Map<String, String> descriptions;

    public String getUuid() {
        return uuid;
    }

    public Map<String, String> getTitles() {
        return Collections.unmodifiableMap(titles);
    }

    public Map<String, String> getDescriptions() {
        return Collections.unmodifiableMap(descriptions);
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
    }

}
