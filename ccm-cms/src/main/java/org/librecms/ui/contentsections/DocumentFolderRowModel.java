/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import java.util.Collections;
import java.util.SortedSet;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class DocumentFolderRowModel {

    private String created;

    private boolean deletable;

    private boolean folder;

    private String folderPath;

    private SortedSet<String> languages;

    private String lastEdited;

    private boolean lastEditPublished;

    private String name;

    private boolean noneCmsObject;

    private String title;

    private String type;

    private DocumentPermissionsModel permissions;

    public String getCreated() {
        return created;
    }

    public void setCreated(final String created) {
        this.created = created;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(final boolean deletable) {
        this.deletable = deletable;
    }

    public boolean isFolder() {
        return folder;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(final String folderPath) {
        this.folderPath = folderPath;
    }

    public void setFolder(final boolean folder) {
        this.folder = folder;
    }

    public SortedSet<String> getLanguages() {
        return Collections.unmodifiableSortedSet(languages);
    }

    public String getLanguagesAsString() {
        return String.join(", ", languages);
    }

    public void setLanguages(final SortedSet<String> languages) {
        this.languages = languages;
    }

    public String getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(final String lastEdited) {
        this.lastEdited = lastEdited;
    }

    public boolean isLastEditPublished() {
        return lastEditPublished;
    }

    public void setLastEditPublished(final boolean lastEditPublished) {
        this.lastEditPublished = lastEditPublished;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isNoneCmsObject() {
        return noneCmsObject;
    }

    public void setNoneCmsObject(boolean noneCmsObject) {
        this.noneCmsObject = noneCmsObject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public DocumentPermissionsModel getPermissions() {
        return permissions;
    }

    public void setPermissions(
        final DocumentPermissionsModel permissions
    ) {
        this.permissions = permissions;
    }

}
