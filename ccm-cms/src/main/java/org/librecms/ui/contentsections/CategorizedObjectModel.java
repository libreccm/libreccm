/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import org.libreccm.l10n.GlobalizationHelper;

/**
 * Model bean for transferring the data about an object in a folder to the
 * frontend.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategorizedObjectModel {

    /**
     * The ID of the object.
     */
    private long objectId;

    /**
     * The UUID of the object.
     */
    private String objectUuid;

    /**
     * The display name of the object.
     */
    private String displayName;

    /**
     * The title of the object. If available the title is in the {@link GlobalizationHelper#getNegotiatedLocale()
     * } is used. Otherwise the default language is used.
     */
    private String title;

    /**
     * The type of the object.
     */
    private String type;

    /**
     * Is the object the index object of the folder?
     */
    private boolean indexObject;

    /**
     * The order index for the object.
     */
    private long objectOrder;

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(final long objectId) {
        this.objectId = objectId;
    }

    public String getObjectUuid() {
        return objectUuid;
    }

    public void setObjectUuid(final String objectUuid) {
        this.objectUuid = objectUuid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
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

    public boolean isIndexObject() {
        return indexObject;
    }

    public void setIndexObject(final boolean indexObject) {
        this.indexObject = indexObject;
    }

    public long getObjectOrder() {
        return objectOrder;
    }

    public void setObjectOrder(final long objectOrder) {
        this.objectOrder = objectOrder;
    }

}
