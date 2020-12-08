/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.contentsection;

import org.libreccm.imexport.AbstractEntityImExporter;
import org.libreccm.imexport.Exportable;
import org.libreccm.imexport.Processes;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Processes(ContentSection.class)
public class ContentSectionImExporter
    extends AbstractEntityImExporter<ContentSection> {

    @Inject
    private ContentSectionRepository sectionRepository;

    @Override
    public Class<ContentSection> getEntityClass() {
        return ContentSection.class;
    }

    @Override
    protected Set<Class<? extends Exportable>> getRequiredEntities() {
        return Collections.emptySet();
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    protected void saveImportedEntity(final ContentSection entity) {
        sectionRepository.save(entity);
    }

    @Override
    protected ContentSection reloadEntity(final ContentSection entity) {
        return sectionRepository
            .findById(Objects.requireNonNull(entity).getObjectId())
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "ContentSection entity %s not found in database.",
                        Objects.toString(entity)
                    )
                )
            );
    }

}
