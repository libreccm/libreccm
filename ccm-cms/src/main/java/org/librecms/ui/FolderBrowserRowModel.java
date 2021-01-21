/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui;

import java.util.Collections;
import java.util.SortedSet;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class FolderBrowserRowModel {

    private String created;

    private boolean deletable;

    private boolean isFolder;

    private SortedSet<String> languages;

    private String lastEdited;

    private boolean lastEditPublished;

    private String name;

    private boolean noneCmsObject;

    private String title;

    private String type;

    public String getCreated() {
        return created;
    }

    protected void setCreated(final String created) {
        this.created = created;
    }

    public boolean isDeletable() {
        return deletable;
    }

    protected void setDeletable(final boolean deletable) {
        this.deletable = deletable;
    }

    public boolean isIsFolder() {
        return isFolder;
    }

    protected void setIsFolder(final boolean isFolder) {
        this.isFolder = isFolder;
    }

    public SortedSet<String> getLanguages() {
        return Collections.unmodifiableSortedSet(languages);
    }

    protected void setLanguages(final SortedSet<String> languages) {
        this.languages = languages;
    }

    public String getLastEdited() {
        return lastEdited;
    }

    protected void setLastEdited(final String lastEdited) {
        this.lastEdited = lastEdited;
    }

    public boolean isLastEditPublished() {
        return lastEditPublished;
    }

    protected void setLastEditPublished(final boolean lastEditPublished) {
        this.lastEditPublished = lastEditPublished;
    }

    public String getName() {
        return name;
    }

    protected void setName(final String name) {
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

    protected void setTitle(final String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    protected void setType(final String type) {
        this.type = type;
    }

}
