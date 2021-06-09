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
package org.librecms.ui.contentsections;


/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ContactEntryKeysTableRowModel {
    
    private long keyId;
    
    private String entryKey;
    
    private String label;

    public long getKeyId() {
        return keyId;
    }

    protected void setKeyId(final long keyId) {
        this.keyId = keyId;
    }

    public String getEntryKey() {
        return entryKey;
    }

    protected void setEntryKey(final String entryKey) {
        this.entryKey = entryKey;
    }

    public String getLabel() {
        return label;
    }

    protected void setLabel(final String label) {
        this.label = label;
    }
    
    
    
}
