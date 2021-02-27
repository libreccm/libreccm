/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import org.libreccm.security.AuthorizationRequired;

import javax.enterprise.context.RequestScoped;
import javax.mvc.Controller;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/{sectionIdentifier}/configuration/workflows")
public class ConfigurationWorkflowController {

    @GET
    @Path("/")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String listWorkflowDefinitions(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @GET
    @Path("/{workflowIdentifier}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showWorkflowDefinition(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path("/@add")
    @AuthorizationRequired
    public String addWorkflowDefinition(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @FormParam("label") final String label
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path("/{workflowIdentifier}/@delete")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String deleteWorkflowDefinition(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path("/{workflowIdentifier}/label/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addWorkflowDefinitionLabel(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path("/{workflowIdentifier}/label/@edit/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editWorkflowDefinitionLabel(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path("/{workflowIdentifier}/label/@remove/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeWorkflowDefinitionLabel(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path("/{workflowIdentifier}/description/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addWorkflowDefinitionDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path("/{workflowIdentifier}/description/@edit/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editWorkflowDefinitionDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path("/{workflowIdentifier}/description/@remove/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeWorkflowDefinitionDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path("/{workflowIdentifier}/tasks/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addTask(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @FormParam("label") final String label
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path("/{workflowIdentifier}/tasks/{taskIdentifier}/@remove")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeTask(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @PathParam("taskIdentifier") final String taskIdentifierParam
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path("/{workflowIdentifier}/tasks/{taskIdentifier}/label/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addTaskLabel(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @PathParam("taskIdentifier") final String taskIdentifierParam,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path("/{workflowIdentifier}/tasks/{taskIdentifier}/label/@edit/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editTaskLabel(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @PathParam("taskIdentifier") final String taskIdentifierParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path(
        "/{workflowIdentifier}/tasks/{taskIdentifier}/label/@remove/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeTaskLabel(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @PathParam("taskIdentifier") final String taskIdentifierParam,
        @PathParam("locale") final String localeParam
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path("/{workflowIdentifier}/tasks/{taskIdentifier}/description/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addTaskDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @PathParam("taskIdentifier") final String taskIdentifierParam,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path(
        "/{workflowIdentifier}/tasks/{taskIdentifier}/description/@edit/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editTaskDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @PathParam("taskIdentifier") final String taskIdentifierParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path(
        "/{workflowIdentifier}/tasks/{taskIdentifier}/description/@remove/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeTaskDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @PathParam("taskIdentifier") final String taskIdentifierParam,
        @PathParam("locale") final String localeParam
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path(
        "/{workflowIdentifier}/tasks/{taskIdentifier}/blockingTasks/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addBlockingTask(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @PathParam("taskIdentifier") final String taskIdentifierParam,
        @FormParam("blockingTask") final String blockingTaskParam
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path(
        "/{workflowIdentifier}/tasks/{taskIdentifier}/blockingTasks/{blockingTaskIdentifier}/@remove")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeBlockingTask(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("workflowIdentifier") final String workflowIdentiferParam,
        @PathParam("taskIdentifier") final String taskIdentifierParam,
        @PathParam("blockingTaskIdentifier") final String blockingTaskParam
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

}
