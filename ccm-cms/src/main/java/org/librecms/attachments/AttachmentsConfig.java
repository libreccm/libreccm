/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.librecms.attachments;

import org.libreccm.configuration.Configuration;
import org.libreccm.configuration.Setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Special configuration parameters for attachments.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Configuration
public class AttachmentsConfig {

    /**
     * Possible names for an {@link AttachmentList} from which the authors can
     * choose.
     */
    @Setting
    private List<String> attachmentListNames = Arrays.asList(new String[]{});

    public List<String> getAttachmentListNames() {
        if (attachmentListNames == null) {
            return new ArrayList<>();
        } else {
            return new ArrayList<>(attachmentListNames);
        }
    }

    public void setAttachmentListNames(final List<String> attachmentListNames) {
        this.attachmentListNames = new ArrayList<>(attachmentListNames);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(attachmentListNames);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AttachmentsConfig)) {
            return false;
        }
        final AttachmentsConfig other = (AttachmentsConfig) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        return Objects.equals(getAttachmentListNames(),
                              other.getAttachmentListNames());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof AttachmentsConfig;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "attachmentListNames = { %s }%d"
                                 + " }",
                             Objects.toString(attachmentListNames),
                             super.toString());
    }

}
