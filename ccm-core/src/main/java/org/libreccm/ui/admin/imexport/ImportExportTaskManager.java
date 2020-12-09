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
        exportTaskSender.fireAsync(
            new ExportTask(exportName, LocalDate.now(), entities, taskStatus)
        ).handle((task , ex) -> handleExportTaskResult(task, ex, taskStatus));

        taskStatus.setStatus(ImExportTaskStatus.RUNNING);
        exportTasks.add(taskStatus);
    }

    public void importEntities(final String importName) {
        final ImportTaskStatus taskStatus = new ImportTaskStatus();
        taskStatus.setStarted(LocalDateTime.now());
        importTaskSender.fireAsync(
            new ImportTask(importName, LocalDate.now(), taskStatus)
        ).handle((task, ex) -> handleImportTaskResult(task, ex, taskStatus));
        
        taskStatus.setStatus(ImExportTaskStatus.RUNNING);
        importTasks.add(taskStatus);
    }

    @Schedule(hour = "*", minute = "*/5", persistent = false)
    protected void removeFinishedTasks() {
        exportTasks.removeIf(taskStatus -> taskStatus.getStatus() == ImExportTaskStatus.FINISHED);
//        importTasks.removeIf(taskStatus -> taskStatus.getStatus() == ImExportTaskStatus.FINISHED);
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
            status.setStatus(ImExportTaskStatus.FINISHED);
        } else {
            status.setStatus(ImExportTaskStatus.ERROR);
            status.setException(ex);
            LOGGER.error("Export Task {} failed ", task);
            LOGGER.error("with exception:", ex);
        }
        return task;
    }

    private Object handleImportTaskResult(
        final ImportTask task, final Throwable ex, final ImportTaskStatus status
    ) {
        if (ex == null) {
            status.setStatus(ImExportTaskStatus.FINISHED);
        } else {
            status.setStatus(ImExportTaskStatus.ERROR);
            status.setException(ex);
            LOGGER.error("Import Task {} failed", task);
            LOGGER.error("with exception: ", ex);
        }
        return task;
    }
}
