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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.imexport.Exportable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ejb.Schedule;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
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

    private static final Logger LOGGER = LogManager.getLogger(
        ImportExportTaskManager.class
    );

    @Inject
    private EntityManager entityManager;

    @Inject
    private Event<ExportTask> exportTaskSender;

    @Inject
    private Event<ImportTask> importTaskSender;

//    @Inject
//    private ImExportTasks imExportTasks;

//    private SortedSet<ImExportTaskStatus> exportTasks;
//
//    private SortedSet<ImExportTaskStatus> importTasks;
    private final SortedSet<ExportTaskStatus> exportTasks;

    private final SortedSet<ImportTaskStatus> importTasks;

    public ImportExportTaskManager() {
        exportTasks = new TreeSet<>(
            Comparator.comparing(
                ExportTaskStatus::getStarted)
                .thenComparing(ExportTaskStatus::getName)
        );
        importTasks = new TreeSet<>(
            Comparator.comparing(
                ImportTaskStatus::getStarted)
                .thenComparing(ImportTaskStatus::getName)
        );
    }

    public SortedSet<ExportTaskStatus> getExportTasks() {
        return Collections.unmodifiableSortedSet(exportTasks);
    }

    public SortedSet<ImportTaskStatus> getImportTasks() {
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

        final ExportTaskStatus taskStatus = new ExportTaskStatus();
        taskStatus.setName(exportName);
        taskStatus.setStarted(LocalDateTime.now());
//        final Future<?> status = imExportTasks.startExport(
//            entities, exportName
//        );
        exportTaskSender.fireAsync(
            new ExportTask(exportName, LocalDate.now(), entities, taskStatus)
        ).handle((task , ex) -> handleExportTaskResult(task, ex, taskStatus));

        taskStatus.setStatus(ImExportTaskStatusEnum.RUNNING);
        exportTasks.add(taskStatus);
    }

//    public void exportEntities(
//        final Collection<Exportable> entities, final String exportName
//    ) {
//        final ImExportTaskStatus task = new ImExportTaskStatus();
//        task.setName(exportName);
//        task.setStarted(LocalDate.now());
//        final Future<?> status = startExport(entities, exportName);
//        task.setStatus(status);
//        exportTasks.add(task);
//    }
    public void importEntities(final String importName) {
//        final ImExportTaskStatus task = new ImExportTaskStatus();
//        task.setName(importName);
//        task.setStarted(LocalDateTime.now());
//        final Future<?> status = imExportTasks.startImport(importName);
//        task.setStatus(status);
//        importTasks.add(task);
        throw new UnsupportedOperationException();
    }

    @Schedule(hour = "*", minute = "*/5", persistent = false)
    protected void removeFinishedTasks() {
        exportTasks.removeIf(taskStatus -> taskStatus.getStatus() == ImExportTaskStatusEnum.FINISHED);
//        importTasks.removeIf(taskStatus -> taskStatus.getStatus() == ImExportTaskStatusEnum.FINISHED);
    }

    public void cancelTask(final ImExportTaskStatus task) {
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

    private Object handleExportTaskResult(
        final ExportTask task, final Throwable ex, final ExportTaskStatus status
    ) {
        if (ex == null) {
            status.setStatus(ImExportTaskStatusEnum.FINISHED);
        } else {
            status.setStatus(ImExportTaskStatusEnum.ERROR);
            status.setException(ex);
            LOGGER.error("Export Task {} failed ", task);
            LOGGER.error("with exception:", ex);
        }
        return task;
    }

}
