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
@Path("/{sectionIdentifier}/configuration/lifecycles")
public class ConfigurationLifecyclesController {

    @GET
    @Path("/")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String listLifecycleDefinitions(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @GET
    @Path("/{lifecycleIdentifier}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showLifecycleDefinition(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path("/@add")
    @AuthorizationRequired
    public String addLifecycleDefinition(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @FormParam("label") final String label
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path("/{lifecycleIdentifier}/label/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addLifecycleDefinitionLabel(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path("/{lifecycleIdentifier}/label/@edit/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editLifecycleDefinitionLabel(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path("/{lifecycleIdentifier}/label/@remove/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeLifecycleDefinitionLabel(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path("/{lifecycleIdentifier}/description/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addLifecycleDefinitionDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path("/{lifecycleIdentifier}/description/@edit/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editLifecycleDefinitionDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path("/{lifecycleIdentifier}/description/@remove/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeLifecycleDefinitionDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path("/{lifecycleIdentifier}/phases/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addPhase(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @FormParam("label") final String label,
        @FormParam("defaultDelay") final long defaultDelay,
        @FormParam("defaultDuration") final long defaultDuration
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path("/{lifecycleIdentifier}/phases/{phaseIdentifier}/@edit")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editPhase(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @PathParam("phaseIdentifier") final String phaseIdentifierParam,
        @FormParam("defaultDelay") final long defaultDelay,
        @FormParam("defaultDuration") final long defaultDuration
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path("/{lifecycleIdentifier}/phases/{phaseIdentifier}/@remove")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removePhase(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @PathParam("phaseIdentifier") final String phaseIdentifierParam
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path("/{lifecycleIdentifier}/phases/{phaseIdentifier}/label/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addPhaseLabel(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @PathParam("phaseIdentifier") final String phaseIdentifierParam,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path("/{lifecycleIdentifier}/phases/{phaseIdentifier}/label/@edit/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editPhaseLabel(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @PathParam("phaseIdentifier") final String phaseIdentifierParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path(
        "/{lifecycleIdentifier}/phases/{phaseIdentifier}/label/@remove/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removePhaseLabel(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @PathParam("phaseIdentifier") final String phaseIdentifierParam,
        @PathParam("locale") final String localeParam
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path("/{lifecycleIdentifier}/phases/{phaseIdentifier}/description/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addPhaseDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @PathParam("phaseIdentifier") final String phaseIdentifierParam,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path(
        "/{lifecycleIdentifier}/phases/{phaseIdentifier}/description/@edit/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editPhaseDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @PathParam("phaseIdentifier") final String phaseIdentifierParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @POST
    @Path(
        "/{lifecycleIdentifier}/phases/{phaseIdentifier}/description/@remove/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removePhaseDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @PathParam("phaseIdentifier") final String phaseIdentifierParam,
        @PathParam("locale") final String localeParam
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

}
