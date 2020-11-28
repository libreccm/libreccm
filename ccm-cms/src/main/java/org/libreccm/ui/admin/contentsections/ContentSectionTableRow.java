/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libreccm.ui.admin.contentsections;

import java.util.Comparator;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ContentSectionTableRow implements
    Comparable<ContentSectionTableRow> {

    private long sectionId;

    private String uuid;

    private String label;

    public long getSectionId() {
        return sectionId;
    }

    protected void setSectionId(long sectionId) {
        this.sectionId = sectionId;
    }

    public String getUuid() {
        return uuid;
    }

    protected void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getLabel() {
        return label;
    }

    protected void setLabel(final String label) {
        this.label = label;
    }

    @Override
    public int compareTo(final ContentSectionTableRow other) {
        return Comparator
            .nullsFirst(
                Comparator
                    .comparing(ContentSectionTableRow::getLabel)
                    .thenComparing(ContentSectionTableRow::getSectionId)
            ).compare(this, other);
    }

}
