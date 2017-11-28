/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.libreccm.admin.ui;

import com.vaadin.cdi.ViewScoped;
import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.Query;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.PageModelManager;
import org.libreccm.pagemodel.PageModelVersion;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.CcmApplication;

import java.util.stream.Stream;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ViewScoped
class PageModelsTableDataProvider
    extends AbstractBackEndDataProvider<PageModelsTableRow, String> {

    private static final long serialVersionUID = 8052894182508842905L;

    @Inject
    private ApplicationRepository applicationRepo;
    
    @Inject
    private EntityManager entityManager;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private PageModelManager pageModelManager;

    private CcmApplication application;

    public CcmApplication getApplication() {
        return application;
    }

    public void setApplication(final CcmApplication application) {
        this.application = application;
        refreshAll();
    }
    
    @Transactional(Transactional.TxType.REQUIRED)
    public void setApplicationUuid(final String uuid) {
        application = applicationRepo
            .findByUuid(uuid)
            .orElseThrow(() -> new UnexpectedErrorException(String
            .format("No Application with UUID %s in the database.",
                    uuid)));
        refreshAll();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    protected Stream<PageModelsTableRow> fetchFromBackEnd(
        final Query<PageModelsTableRow, String> query) {

        if (application == null) {
            return Stream.empty();
        } else {

            final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            final CriteriaQuery<PageModel> criteriaQuery = builder
                .createQuery(PageModel.class);

            final Root<PageModel> from = criteriaQuery.from(PageModel.class);

            criteriaQuery
                .where(builder
                    .and(builder.equal(from.get("application"), application),
                         builder.equal(from.get("version"),
                                       PageModelVersion.DRAFT)))
                .orderBy(builder.asc(from.get("name")));
//                .orderBy(builder.asc(from.get("title")));

            return entityManager
                .createQuery(criteriaQuery)
                .setFirstResult(query.getOffset())
                .setMaxResults(query.getLimit())
                .getResultList()
                .stream()
                .map(this::buildRow);
        }
    }

    private PageModelsTableRow buildRow(final PageModel model) {

        final PageModelsTableRow row = new PageModelsTableRow();

        row.setPageModelId(model.getPageModelId());
        row.setName(model.getName());
        row.setTitle(globalizationHelper
            .getValueFromLocalizedString(model.getTitle()));
        row.setDescription(globalizationHelper
            .getValueFromLocalizedString(model.getDescription()));
        row.setPublished(pageModelManager.isLive(model));

        return row;

    }

    @Override
    protected int sizeInBackEnd(final Query<PageModelsTableRow, String> query) {

        if (application == null) {
            return 0;
        } else {

            final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            final CriteriaQuery<Long> criteriaQuery = builder
                .createQuery(Long.class);

            final Root<PageModel> from = criteriaQuery.from(PageModel.class);
            criteriaQuery.select(builder.count(from));

            criteriaQuery
                .where(builder
                    .and(builder.equal(from.get("application"), application),
                         builder.equal(from.get("version"),
                                       PageModelVersion.DRAFT)));

            return entityManager
                .createQuery(criteriaQuery)
                .getSingleResult()
                .intValue();
        }
    }

}
