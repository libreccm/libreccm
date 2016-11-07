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
package com.arsdigita.cms.ui.type;

import com.arsdigita.kernel.KernelConfig;

import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentType;
import org.librecms.contentsection.ContentTypeRepository;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * A controller class for the {@link ContentTypeAdminPane} and its associated
 * classes. For now it only contains methods which require or transaction (which
 * are controlled by the container now).
 *
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ContentTypeAdminPaneController {

    @Inject
    private ContentTypeRepository typeRepo;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private ConfigurationManager confManager;

    @Transactional(Transactional.TxType.REQUIRED)
    public List<String[]> getTypeList(final ContentSection section) {
        final List<ContentType> types = typeRepo.findByContentSection(section);

        return types.stream()
            .map(type -> generateListEntry(type))
            .collect(Collectors.toList());
    }

    private String[] generateListEntry(final ContentType type) {
        final String[] entry = new String[2];

        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class);

        entry[0] = type.getUuid();
        // Enable if admin UI has fields for editing localised labels...
//        if (type.getLabel().hasValue(globalizationHelper.getNegotiatedLocale())) {
//            entry[1] = type.getLabel().getValue(globalizationHelper
//                .getNegotiatedLocale());
//        } else {
            entry[1] = type.getLabel().getValue(kernelConfig.getDefaultLocale());
//        }
        
        return entry;
    }

}
