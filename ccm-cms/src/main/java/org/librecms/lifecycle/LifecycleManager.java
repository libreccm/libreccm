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
package org.librecms.lifecycle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.librecms.CmsConstants;
import org.librecms.contentsection.privileges.AdminPrivileges;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class LifecycleManager {

    public static final Logger LOGGER = LogManager.getLogger(
        LifecycleManager.class);

    @Inject
    private LifecycleDefinitionRepository lifecycleDefinitionRepo;

    @Inject
    private PhaseDefinititionRepository phaseDefinititionRepo;

    @Inject
    private LifecycleRepository lifecycleRepo;

    @Inject
    private PhaseRepository phaseRepo;

    @Inject
    private BeanManager beanManager;

    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(AdminPrivileges.ADMINISTER_LIFECYLES)
    public void addPhaseDefinition(
        final LifecycleDefinition lifecycleDefinition,
        final PhaseDefinition phaseDefinition) {

        lifecycleDefinition.addPhaseDefinition(phaseDefinition);

        lifecycleDefinitionRepo.save(lifecycleDefinition);
        phaseDefinititionRepo.save(phaseDefinition);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(AdminPrivileges.ADMINISTER_LIFECYLES)
    public void removePhaseDefinition(
        final LifecycleDefinition lifecycleDefinition,
        final PhaseDefinition phaseDefinition) {

        lifecycleDefinition.removePhaseDefinition(phaseDefinition);

        lifecycleDefinitionRepo.save(lifecycleDefinition);
        phaseDefinititionRepo.save(phaseDefinition);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(AdminPrivileges.ADMINISTER_LIFECYLES)
    public Lifecycle createLifecycle(
        final LifecycleDefinition lifecycleDefinition) {

        final Lifecycle lifecycle = new Lifecycle();

        lifecycle.setDefinition(lifecycleDefinition);
        lifecycleDefinition.getPhaseDefinitions().forEach(
            phaseDefinition -> createPhase(lifecycle, phaseDefinition));

        lifecycleRepo.save(lifecycle);

        return lifecycle;
    }

    private void createPhase(final Lifecycle lifecycle,
                             final PhaseDefinition phaseDefinition) {
        final Phase phase = new Phase();
        phase.setDefinition(phaseDefinition);

        lifecycle.addPhase(phase);
        phase.setLifecycle(lifecycle);

        phaseRepo.save(phase);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(AdminPrivileges.ADMINISTER_LIFECYLES)
    public void startLifecycle(final Lifecycle lifecycle) {
        if (!lifecycle.isStarted()) {
            if (lifecycle.isFinished()) {
                lifecycle.setFinished(false);
            }

            lifecycle.getPhases().forEach(phase -> {
                phase.setStarted(false);
                phase.setFinished(false);
                phaseRepo.save(phase);
            });

            lifecycle.getPhases().get(0).setStarted(true);
            phaseRepo.save(lifecycle.getPhases().get(0));

            lifecycleRepo.save(lifecycle);

            if (lifecycle.getListener() != null
                    && !lifecycle.getListener().isEmpty()) {
                final List<?> beans = new ArrayList<>(beanManager.getBeans(
                    lifecycle.getListener()));
                if (!beans.isEmpty()) {
                    ((LifecycleEventListener) beans.get(0)).update(
                        lifecycle, LifecycleEvent.STARTED);
                }
            }

            invokeLifecycleEventListener(lifecycle, LifecycleEvent.STARTED);
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(AdminPrivileges.ADMINISTER_LIFECYLES)
    public void nextPhase(final Lifecycle lifecycle) {
        if (lifecycle.isStarted()) {
            int current = -1;
            for (int i = 0; i < lifecycle.getPhases().size(); i++) {
                if (lifecycle.getPhases().get(i).isStarted()
                        && !lifecycle.getPhases().get(i).isFinished()) {
                    current = i;
                    break;
                }
            }

            if (current == -1) {
                //Lifecycle is already finished, nothing to do.
                return;
            }

            lifecycle.getPhases().get(current).setFinished(true);
            invokePhaseEventListener(lifecycle, 
                                     lifecycle.getPhases().get(current), 
                                     PhaseEvent.FINISHED);
            //Check for last phase, if not set next phase to started
            if (current < lifecycle.getPhases().size() - 1) {
                lifecycle.getPhases().get(current + 1).setStarted(true);
                invokePhaseEventListener(lifecycle,
                                         lifecycle.getPhases().get(current + 1),
                                         PhaseEvent.STARTED);
            }
        } else {
            startLifecycle(lifecycle);
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(AdminPrivileges.ADMINISTER_LIFECYLES)
    public void reset(final Lifecycle lifecycle) {
        lifecycle.setStarted(false);
        lifecycle.setFinished(false);

        lifecycle.getPhases().forEach(phase -> {
            phase.setStarted(false);
            phase.setFinished(false);
            phaseRepo.save(phase);
        });

        invokeLifecycleEventListener(lifecycle, LifecycleEvent.RESET);
        lifecycleRepo.save(lifecycle);
    }

    private void invokeLifecycleEventListener(final Lifecycle lifecycle,
                                              final LifecycleEvent event) {
        final String listenerClassName = lifecycle.getListener();
        final Class<?> listenerClass;
        try {
            listenerClass = Class.forName(listenerClassName);
        } catch (ClassNotFoundException ex) {
            LOGGER.error("Failed to find LifecycleListener class \"{}\". "
                             + "Listener is ignored.",
                         listenerClassName);
            return;
        }

        if (!LifecycleEventListener.class.isAssignableFrom(listenerClass)) {
            LOGGER.warn("Class \"{}\" is not an implementation of the "
                            + "interface \"{}\".",
                        listenerClassName,
                        LifecycleEventListener.class.getName());
            return;
        }

        final Object object;
        try {
            object = listenerClass.newInstance();
        } catch (IllegalAccessException |
                 InstantiationException ex) {
            LOGGER.error("Failed to create instance of LifecycleEventListener "
                             + "of class \"{}\".",
                         listenerClass.getName());
            LOGGER.error("Reason: ", ex);
            return;
        }

        if (object instanceof LifecycleEventListener) {
            final LifecycleEventListener listener
                                             = (LifecycleEventListener) object;
            listener.update(lifecycle, event);
        } else {
            LOGGER.error("Generated object is not an instance of \"{}\". "
                             + "Ignoring listener.",
                         LifecycleEventListener.class.getName());
        }
    }

    private void invokePhaseEventListener(final Lifecycle lifecycle,
                                          final Phase phase,
                                          final PhaseEvent event) {
        final String listenerClassName = phase.getListener();
        final Class<?> listenerClass;
        try {
            listenerClass = Class.forName(listenerClassName);
        } catch (ClassNotFoundException ex) {
            LOGGER.error("Failed to find PhaseListener class \"{}\". "
                             + "Listener is ignored.",
                         listenerClassName);
            return;
        }

        if (!PhaseEventListener.class.isAssignableFrom(listenerClass)) {
            LOGGER.warn("Class \"{}\" is not an implementation of the "
                            + "interface \"{}\".",
                        listenerClassName,
                        PhaseEventListener.class.getName());
            return;
        }

        final Object object;
        try {
            object = listenerClass.newInstance();
        } catch (IllegalAccessException |
                 InstantiationException ex) {
            LOGGER.error("Failed to create instance of PhaseEventListener "
                             + "of class \"{}\".",
                         listenerClass.getName());
            LOGGER.error("Reason: ", ex);
            return;
        }

        if (object instanceof PhaseEventListener) {
            final PhaseEventListener listener = (PhaseEventListener) object;
            listener.update(lifecycle, phase, event);
        } else {
            LOGGER.error("Generated object is not an instance of \"{}\". "
                             + "Ignoring listener.",
                         PhaseEventListener.class.getName());
        }
    }

}
