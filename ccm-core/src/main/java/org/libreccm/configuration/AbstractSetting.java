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
package org.libreccm.configuration;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.NotBlank;

import static org.libreccm.core.CoreConstants.*;

/**
 * Abstract base class for all settings.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T> The value type of the setting.
 */
@Entity
@Table(name = "SETTINGS",
       schema = DB_SCHEMA,
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"CONFIGURATION_CLASS", "NAME"})
       })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NamedQueries({
    @NamedQuery(
        name = "AbstractSetting.findAllForClass",
        query = "SELECT s FROM AbstractSetting s "
                    + "WHERE s.configurationClass = :class")
    ,
    @NamedQuery(
        name = "AbstractSetting.findByClassAndName",
        query = "SELECT s FROM AbstractSetting s "
                    + "WHERE s.configurationClass = :class "
                    + "AND s.name = :name")
})
public abstract class AbstractSetting<T> implements Serializable {

    private static final long serialVersionUID = 1631163618980178142L;

    /**
     * Database ID of the setting (primary key).
     */
    @Column(name = "SETTING_ID")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long settingId;

    /**
     * This configuration class this setting belongs to.
     */
    @Column(name = "CONFIGURATION_CLASS", nullable = false, length = 512)
    @NotBlank
    @Pattern(regexp = "[\\w_.]*")
    private String configurationClass;

    /**
     * The name of the setting. The string must be a valid URL fragment.
     */
    @Column(name = "NAME", nullable = false, length = 512)
    @NotBlank
    @Pattern(regexp = "[\\w-.]*")
    private String name;

    public long getSettingId() {
        return settingId;
    }

    protected void setSettingId(final long settingId) {
        this.settingId = settingId;
    }

    public String getConfigurationClass() {
        return configurationClass;
    }

    public void setConfigurationClass(final String configurationClass) {
        this.configurationClass = configurationClass;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Getter for the value of the setting.
     *
     * @return The value of the setting.
     */
    public abstract T getValue();

    /**
     * Setter for the value of the setting.
     *
     * @param value The new value of the setting.
     */
    public abstract void setValue(T value);

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(settingId);
        hash = 67 * hash + Objects.hashCode(configurationClass);
        hash = 67 * hash + Objects.hashCode(name);
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
        if (!(obj instanceof AbstractSetting)) {
            return false;
        }
        final AbstractSetting<?> other = (AbstractSetting<?>) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (settingId != other.getSettingId()) {
            return false;
        }

        if (!Objects.equals(configurationClass, other.getConfigurationClass())) {
            return false;
        }
        return Objects.equals(name, other.getName());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof AbstractSetting;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format(
            "%s{ "
                + "settingId = %d, "
                + "configurationClass = \"%s\" "
                + "name = \"%s\" "
                + "%s"
                + " }",
            super.toString(),
            settingId,
            configurationClass,
            name,
            data);
    }

}
