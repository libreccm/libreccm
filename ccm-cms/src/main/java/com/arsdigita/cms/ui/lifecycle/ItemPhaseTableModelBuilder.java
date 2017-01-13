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
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.globalization.GlobalizedMessage;

import org.arsdigita.cms.CMSConfig;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionConfig;
import org.librecms.lifecycle.Phase;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Xixi D'Moon &lt;xdmoon@arsdigita.com&gt;
 * @author Michael Pih
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class ItemPhaseTableModelBuilder extends AbstractTableModelBuilder {

    private final LifecycleRequestLocal lifecycle;

    public ItemPhaseTableModelBuilder(final LifecycleRequestLocal lifecycle) {
        this.lifecycle = lifecycle;
    }

    @Override
    public final TableModel makeModel(final Table table,
                                      final PageState state) {
        return new Model(lifecycle.getLifecycle(state).getPhases());
    }

    private static class Model implements TableModel {

        private final List<Phase> phases;
        private int index = -1;
        private Phase phase;

        public Model(final List<Phase> phases) {
            this.phases = phases;
        }

        @Override
        public final int getColumnCount() {
            return 4;
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
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final GlobalizationHelper globalizationHelper = cdiUtil
                .findBean(GlobalizationHelper.class);
            final Locale locale = globalizationHelper.getNegotiatedLocale();
            final DateFormat format;
            if (CMSConfig.getConfig().isHideTimezone()) {
                format = DateFormat.getDateTimeInstance(
                    DateFormat.FULL, DateFormat.SHORT, locale);
            } else {
                format = DateFormat.getDateTimeInstance(
                    DateFormat.FULL, DateFormat.FULL, locale);
            }

            switch (column) {
                case 0:
                    return phase.getDefinition().getLabel().getValue(locale);
                case 1:
                    return phase.getDefinition().getDescription().getValue(
                        locale);
                case 2:
                    final Date startDate = phase.getStartDateTime();
                    return format.format(startDate);
                case 3:
                    final Date endDate = phase.getEndDateTime();

                    if (endDate == null) {
                        return lz("cms.ui.lifecycle.forever");
                    } else {
                        return format.format(endDate);
                    }
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        public final Object getKeyAt(final int column) {
            return phase.getDefinition().getDefinitionId();
        }

    }

    protected final static String lz(final String key) {
        final GlobalizedMessage message = new GlobalizedMessage(key, 
            CmsConstants.CMS_BUNDLE);
        return (String) message.localize();
    }

}
