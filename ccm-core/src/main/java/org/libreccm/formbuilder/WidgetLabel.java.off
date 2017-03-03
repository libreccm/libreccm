/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.libreccm.formbuilder;


import static org.libreccm.core.CoreConstants.*;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "FORMBUILDER_WIDGET_LABELS", schema = DB_SCHEMA)
public class WidgetLabel extends Component implements Serializable {

    private static final long serialVersionUID = -2939505715812565159L;
    
    @OneToOne(mappedBy = "label")
    private Widget widget;

    public Widget getWidget() {
        return widget;
    }

    protected void setWidget(final Widget widget) {
        this.widget = widget;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 19 * hash + Objects.hashCode(widget);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (!super.equals(obj)) {
            return false;
        }
        
        if (!(obj instanceof WidgetLabel)) {
            return false;
        }
        final WidgetLabel other = (WidgetLabel) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        
        return Objects.equals(widget, other.getWidget());
    }
    
    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof WidgetLabel;
    }

}
