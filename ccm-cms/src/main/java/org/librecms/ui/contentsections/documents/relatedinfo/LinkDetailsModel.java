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
package org.librecms.ui.contentsections.documents.relatedinfo;

import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.assets.Bookmark;
import org.librecms.assets.RelatedLink;
import org.librecms.contentsection.AttachmentList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * Model proving the properties of an internal {@link RelatedLink} for the
 * internal link edit view of the {@link RelatedInfoStep}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsLinkDetailsModel")
public class LinkDetailsModel {

    private String baseUrl;

    /**
     * The identifier of the {@link AttachmentList} of the link.
     */
    private String listIdentifier;

    /**
     * The UUID of the link.
     */
    private String uuid;

    /**
     * The localized titles of the link.
     */
    private Map<String, String> title;

    /**
     * The locales for which no title has been specified.
     */
    private List<String> unusedTitleLocales;

    private String linkType;

    private Map<String, String> linkTypes;

    /**
     * The UUID of the target item of the link.
     */
    private String targetItemUuid;

    /**
     * The name of the target item of the link
     */
    private String targetItemName;

    private String bookmarkUuid;

    private String bookmarkName;

    private String sectionName;

    /**
     * The title of the target item of the link. This value is determined from
     * the title of the target item using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }.
     */
    private String targetItemTitle;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(final String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getListIdentifier() {
        return listIdentifier;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(final String linkType) {
        this.linkType = linkType;
    }

    public String getBookmarkUuid() {
        return bookmarkUuid;
    }

    public void setBookmarkUuid(final String bookmarkUuid) {
        this.bookmarkUuid = bookmarkUuid;
    }

    public String getBookmarkName() {
        return bookmarkName;
    }

    public void setBookmarkName(final String bookmarkName) {
        this.bookmarkName = bookmarkName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    protected void setListIdentifier(final String listIdentifier) {
        this.listIdentifier = listIdentifier;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public Map<String, String> getTitle() {
        return Collections.unmodifiableMap(title);
    }

    public void setTitle(final Map<String, String> title) {
        this.title = new HashMap<>(title);
    }

    public List<String> getUnusedTitleLocales() {
        return Collections.unmodifiableList(unusedTitleLocales);
    }

    public void setUnusedTitleLocales(final List<String> unusedTitleLocales) {
        this.unusedTitleLocales = new ArrayList<>(unusedTitleLocales);
    }

    public String getTargetItemUuid() {
        return targetItemUuid;
    }

    public void setTargetItemUuid(final String targetItemUuid) {
        this.targetItemUuid = targetItemUuid;
    }

    public String getTargetItemName() {
        return targetItemName;
    }

    public void setTargetItemName(final String targetItemName) {
        this.targetItemName = targetItemName;
    }

    public String getTargetItemTitle() {
        return targetItemTitle;
    }

    public String getBookmarkType() {
        return Bookmark.class.getName();
    }

//    /**
//     * Sets the properties of this model based on the properties on the provided
//     * link.
//     *
//     * @param link The link to use.
//     */
//    protected void setInternalLink(final RelatedLink link) {
//        Objects.requireNonNull(link);
//
//        uuid = link.getUuid();
//        label = globalizationHelper.getValueFromLocalizedString(
//            link.getTitle()
//        );
//        title = link
//            .getTitle()
//            .getValues()
//            .entrySet()
//            .stream()
//            .collect(
//                Collectors.toMap(
//                    entry -> entry.getKey().toString(),
//                    entry -> entry.getValue()
//                )
//            );
//        final Set<Locale> titleLocales = link.getTitle().getAvailableLocales();
//        unusedTitleLocales = globalizationHelper
//            .getAvailableLocales()
//            .stream()
//            .filter(locale -> !titleLocales.contains(locale))
//            .map(Locale::toString)
//            .collect(Collectors.toList());
//        targetItemUuid = link.getTargetItem().getItemUuid();
//        targetItemName = link.getTargetItem().getDisplayName();
//        targetItemTitle = globalizationHelper.getValueFromLocalizedString(
//            link.getTargetItem().getTitle()
//        );
//    }
    public void setTargetItemTitle(String targetItemTitle) {
        this.targetItemTitle = targetItemTitle;
    }

    public Map<String, String> getLinkTypes() {
        return Collections.unmodifiableMap(linkTypes);
    }

    public void setLinkTypes(final Map<String, String> linkTypes) {
        this.linkTypes = new HashMap<>(linkTypes);
    }

}
