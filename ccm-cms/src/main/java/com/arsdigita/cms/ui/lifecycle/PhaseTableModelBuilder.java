/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.ui.lifecycle;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.lifecycle.Duration;

import org.librecms.lifecycle.LifecycleDefinition;
import org.librecms.lifecycle.PhaseDefinition;

import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.Assert;
import com.arsdigita.util.LockableImpl;

import org.arsdigita.cms.CMSConfig;
import org.librecms.CmsConstants;

import java.util.List;

class PhaseTableModelBuilder extends LockableImpl
        implements TableModelBuilder {
    private final LifecycleDefinitionRequestLocal cycle;

    public PhaseTableModelBuilder
            (final LifecycleDefinitionRequestLocal cycle) {
        this.cycle = cycle;
    }

    @Override
    public final TableModel makeModel(final Table table,
                                      final PageState state) {
        return new PhaseTableModel(cycle.getLifecycleDefinition(state));
    }

    private static class PhaseTableModel implements TableModel {
        
        private final List<PhaseDefinition> phases;
        private int index = -1;
        private PhaseDefinition phase;

        public PhaseTableModel(final LifecycleDefinition cycle) {
            phases = cycle.getPhaseDefinitions();
        }

        @Override
        public final int getColumnCount() {
            return 6;
        }

        @Override
        public final boolean nextRow() {
            index++;
            if (index < phases.size()) {
                phase = phases.get(index);

                return true;
            } else {
                return false;
            }
        }

        @Override
        public final Object getElementAt(final int column) {
            Assert.exists(phase, "PhaseDefinition m_phase");

            switch (column) {
            case 0:
                return phase.getLabel();
            case 1:
                return phase.getDescription();
            case 2:
                return Duration.formatDuration(phase.getDefaultDelay());
            case 3:
                final Long duration = phase.getDefaultDuration();

                if (duration == null) {
                    return lz("cms.ui.lifecycle.forever");
                } else {
                    return Duration.formatDuration(duration);
                }
            case 4:
                return lz("cms.ui.lifecycle.phase_edit");
            case 5:
                return lz("cms.ui.lifecycle.phase_delete");
            default:
                throw new IllegalStateException();
            }
        }

        @Override
        public Object getKeyAt(int columnIndex) {
            if (phase == null) {
                throw new IllegalStateException();
            } else {
                return phase.getDefinitionId();
            }
        }
    }

    private static GlobalizedMessage gz(final String key) {
        return new GlobalizedMessage(key, CmsConstants.CMS_BUNDLE);
    }

    private static String lz(final String key) {
        return (String) gz(key).localize();
    }
}
