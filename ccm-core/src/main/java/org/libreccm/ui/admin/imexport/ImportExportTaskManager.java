/*
 * Copyright (C) 2020 LibreCCM Foundation.
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
package org.libreccm.ui.admin.imexport;

import org.libreccm.imexport.Exportable;
import org.libreccm.imexport.ImportExport;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Schedule;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationScoped
@Named("ImportExportTaskManager")
public class ImportExportTaskManager {

    @Inject
    private EntityManager entityManager;

    @Inject
    private ImExportTasks imExportTasks;

    private SortedSet<ImExportTask> exportTasks;

    private SortedSet<ImExportTask> importTasks;

    public ImportExportTaskManager() {
        exportTasks = new TreeSet<>(
            Comparator.comparing(
                ImExportTask::getStarted)
                .thenComparing(ImExportTask::getName)
        );
        importTasks = new TreeSet<>(
            Comparator.comparing(
                ImExportTask::getStarted)
                .thenComparing(ImExportTask::getName)
        );
    }

    public SortedSet<ImExportTask> getExportTasks() {
        return Collections.unmodifiableSortedSet(exportTasks);
    }

    public SortedSet<ImExportTask> getImportTasks() {
        return Collections.unmodifiableSortedSet(importTasks);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void exportEntities(
        final Set<Class<? extends Exportable>> exportTypes,
        final String exportName
    ) {
        final Set<Exportable> entities = new HashSet<>();
        for (final Class<? extends Exportable> type : exportTypes) {
            @SuppressWarnings("unchecked")
            final Set<? extends Exportable> entitiesOfType = collectEntities(
                (Class<Exportable>) type
            );
            entities.addAll(entitiesOfType);
        }

        final ImExportTask task = new ImExportTask();
        task.setName(exportName);
        task.setStarted(LocalDateTime.now());
        final Future<?> status = imExportTasks.startExport(
            entities, exportName
        );
        task.setStatus(status);
        exportTasks.add(task);
    }

//    public void exportEntities(
//        final Collection<Exportable> entities, final String exportName
//    ) {
//        final ImExportTask task = new ImExportTask();
//        task.setName(exportName);
//        task.setStarted(LocalDate.now());
//        final Future<?> status = startExport(entities, exportName);
//        task.setStatus(status);
//        exportTasks.add(task);
//    }
    public void importEntities(final String importName) {
        final ImExportTask task = new ImExportTask();
        task.setName(importName);
        task.setStarted(LocalDateTime.now());
        final Future<?> status = imExportTasks.startImport(importName);
        task.setStatus(status);
        importTasks.add(task);
    }

    @Schedule(hour = "*", minute = "*/5", persistent = false)
    protected void removeFinishedTasks() {
        exportTasks.removeIf(ImExportTask::isDone);
        importTasks.removeIf(ImExportTask::isDone);
    }

    public void cancelTask(final ImExportTask task) {
        task.cancel();
    }

    private Set<? extends Exportable> collectEntities(
        final Class<Exportable> ofType
    ) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Exportable> query = builder.createQuery(ofType);
        final Root<Exportable> from = query.from(ofType);

        return new HashSet<>(
            entityManager.createQuery(
                query.select(from)
            ).getResultList()
        );
    }

}
