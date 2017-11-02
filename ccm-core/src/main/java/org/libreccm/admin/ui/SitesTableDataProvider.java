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
import com.vaadin.data.provider.AbstractDataProvider;
import com.vaadin.data.provider.Query;
import org.libreccm.sites.Site;
import org.libreccm.sites.SiteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
public class SitesTableDataProvider
    //    extends AbstractBackEndDataProvider<SitesTableRowData, String> {
    extends AbstractDataProvider<SitesTableRowData, String> {

    private static final long serialVersionUID = 2696603483924152498L;

    @Inject
    private EntityManager entityManager;

    @Inject
    private SiteRepository siteRepository;

//    @Override
//    @Transactional(Transactional.TxType.REQUIRED)
//    public Stream<SitesTableRowData> fetchFromBackEnd(
//        final Query<SitesTableRowData, String> query) {
//
//        final CriteriaBuilder criteriaBuilder = entityManager
//            .getCriteriaBuilder();
//        final CriteriaQuery<Site> criteriaQuery = criteriaBuilder
//            .createQuery(Site.class);
//        final Root<Site> from = criteriaQuery.from(Site.class);
//
//        criteriaQuery.orderBy(criteriaBuilder.asc(from.get("domainOfSite")));
//
//        return entityManager
//            .createQuery(criteriaQuery)
//            .setFirstResult(query.getOffset())
//            .setMaxResults(query.getLimit())
//            .getResultList()
//            .stream()
//            .map(this::buildRow);
//    }
//
//    @Transactional(Transactional.TxType.REQUIRED)
//    @Override
//    public int sizeInBackEnd(final Query<SitesTableRowData, String> query) {
//
//        return siteRepository.findAll().size();
//    }
    private SitesTableRowData buildRow(final Site site) {

        final SitesTableRowData row = new SitesTableRowData();

        row.setSiteId(site.getObjectId());
        row.setDomainOfSite(site.getDomainOfSite());
        row.setDefaultSite(site.isDefaultSite());
        row.setDefaultTheme(site.getDefaultTheme());
        row.setDeletable(site.getApplications().isEmpty());

        final List<String> applications = site
            .getApplications()
            .stream()
            .map(application -> application.getPrimaryUrl())
            .collect(Collectors.toList());

        row.setApplications(applications);
        return row;
    }

    @Override
    public boolean isInMemory() {
        return false;
    }

    @Override
    public int size(final Query<SitesTableRowData, String> query) {
        return siteRepository.findAll().size();
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public Stream<SitesTableRowData> fetch(
        final Query<SitesTableRowData, String> query) {

        final CriteriaBuilder criteriaBuilder = entityManager
            .getCriteriaBuilder();
        final CriteriaQuery<Site> criteriaQuery = criteriaBuilder
            .createQuery(Site.class);
        final Root<Site> from = criteriaQuery.from(Site.class);

        criteriaQuery.orderBy(criteriaBuilder.asc(from.get("domainOfSite")));

        final List<Site> sites = entityManager
            .createQuery(criteriaQuery)
            .setFirstResult(query.getOffset())
            .setMaxResults(query.getLimit())
            .getResultList();
        final List<SitesTableRowData> rows = new ArrayList<>();
        for (final Site site : sites) {
            final SitesTableRowData row = buildRow(site);
            rows.add(row);
        }

        return rows.stream();

//        return entityManager
//            .createQuery(criteriaQuery)
//            .setFirstResult(query.getOffset())
//            .setMaxResults(query.getLimit())
//            .getResultList()
//            .stream()
//            .map(this::buildRow);
    }

}
