/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.libreccm.core;

import com.arsdigita.ui.login.LoginApplicationCreator;
import com.arsdigita.ui.login.LoginServlet;
import org.libreccm.modules.CcmModule;
import org.libreccm.modules.InitEvent;
import org.libreccm.modules.InstallEvent;
import org.libreccm.modules.Module;
import org.libreccm.modules.ShutdownEvent;
import org.libreccm.modules.UnInstallEvent;
import org.libreccm.security.SystemUsersSetup;
import org.libreccm.security.User;

import javax.persistence.EntityManager;
import org.libreccm.web.ApplicationType;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Module(applicationTypes = {@ApplicationType(name = "Login", 
                                             description = "Login Application",
                                             singleton = true,
                                             creator = LoginApplicationCreator.class,
                                             servlet = LoginServlet.class)},
        entities = {org.libreccm.auditing.CcmRevision.class,
                    org.libreccm.categorization.Categorization.class,
                    org.libreccm.categorization.Category.class,
                    org.libreccm.categorization.Domain.class,
                    org.libreccm.categorization.DomainOwnership.class,
                    org.libreccm.core.CcmObject.class,
                    org.libreccm.core.Resource.class,
                    org.libreccm.core.ResourceType.class,
                    org.libreccm.modules.InstalledModule.class,
                    org.libreccm.formbuilder.Component.class,
                    org.libreccm.formbuilder.DataDrivenSelect.class,
                    org.libreccm.formbuilder.FormSection.class,
                    org.libreccm.formbuilder.Listener.class,
                    org.libreccm.formbuilder.MetaObject.class,
                    org.libreccm.formbuilder.ObjectType.class,
                    org.libreccm.formbuilder.Option.class,
                    org.libreccm.formbuilder.PersistentDataQuery.class,
                    org.libreccm.formbuilder.ProcessListener.class,
                    org.libreccm.formbuilder.Widget.class,
                    org.libreccm.formbuilder.WidgetLabel.class,
                    org.libreccm.formbuilder.actions.ConfirmEmailListener.class,
                    org.libreccm.formbuilder.actions.ConfirmRedirectListener.class,
                    org.libreccm.formbuilder.actions.RemoteServerPostListener.class,
                    org.libreccm.formbuilder.actions.SimpleEmailListener.class,
                    org.libreccm.formbuilder.actions.TemplateEmailListener.class,
                    org.libreccm.formbuilder.actions.XmlEmailListener.class,
                    org.libreccm.messaging.Attachment.class,
                    org.libreccm.messaging.Message.class,
                    org.libreccm.messaging.MessageThread.class,
                    org.libreccm.notification.Digest.class,
                    org.libreccm.notification.Notification.class,
                    org.libreccm.notification.QueueItem.class,
                    org.libreccm.portal.Portal.class,
                    org.libreccm.portal.Portlet.class,
                    org.libreccm.runtime.Initalizer.class,
                    org.libreccm.search.lucene.Document.class,
                    org.libreccm.search.lucene.Index.class,
                    org.libreccm.web.CcmApplication.class,
                    org.libreccm.web.Host.class,
                    org.libreccm.workflow.Task.class,
                    org.libreccm.workflow.UserTask.class,
                    org.libreccm.workflow.Workflow.class})
public class CcmCore implements CcmModule {

    @Override
    public void install(final InstallEvent event) {
        final EntityManager entityManager = event.getEntityManager();

        final SystemUsersSetup systemUsersSetup = new SystemUsersSetup(entityManager);
        systemUsersSetup.setupSystemUsers();
    }

    @Override
    public void init(final InitEvent event) {
        //Nothing
    }

    @Override
    public void shutdown(final ShutdownEvent event) {
        //Nothing
    }

    @Override
    public void uninstall(final UnInstallEvent event) {
        //Nothing
    }

}
