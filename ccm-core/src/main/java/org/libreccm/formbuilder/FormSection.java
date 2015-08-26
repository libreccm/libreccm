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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "formbuilder_formsections", schema = "ccm_core")
public class FormSection extends Component implements Serializable {

    private static final long serialVersionUID = -3195157282292906945L;

    @Column(name = "formsection_action")
    private String action;

    @OneToMany(mappedBy = "formSection")
    private List<ProcessListener> processListeners;

    public FormSection() {
        super();
        processListeners = new ArrayList<>();
    }

    public String getAction() {
        return action;
    }

    public void setAction(final String action) {
        this.action = action;
    }

    public List<ProcessListener> getProcessListeners() {
        return Collections.unmodifiableList(processListeners);
    }

    protected void setProcessListeners(
        final List<ProcessListener> processListeners) {
        this.processListeners = processListeners;
    }

    protected void addProcessListener(final ProcessListener listener) {
        processListeners.add(listener);
    }

    protected void removeListener(final ProcessListener listener) {
        processListeners.remove(listener);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 29 * hash + Objects.hashCode(action);
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
        
        if (!(obj instanceof FormSection)) {
            return false;
        }
        final FormSection other = (FormSection) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        return Objects.equals(action, other.getAction());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof FormSection;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", action = \"%s\"%s",
                                            action,
                                            data));
    }

}
