/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.ui.contentsections.documents.relatedinfo;

import org.librecms.ui.contentsections.ContentSectionNotFoundException;
import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedTextsUtil;
import org.libreccm.security.PermissionChecker;
import org.libreccm.ui.BaseUrl;
import org.librecms.assets.AssetTypesManager;
import org.librecms.assets.Bookmark;
import org.librecms.assets.RelatedLink;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetManager;
import org.librecms.contentsection.AssetRepository;
import org.librecms.contentsection.AttachmentList;
import org.librecms.contentsection.AttachmentListManager;
import org.librecms.contentsection.AttachmentListRepository;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ItemAttachment;
import org.librecms.contentsection.ItemAttachmentManager;
import org.librecms.contentsection.privileges.ItemPrivileges;
import org.librecms.ui.contentsections.ItemPermissionChecker;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.librecms.ui.contentsections.documents.AbstractMvcAuthoringStep;
import org.librecms.ui.contentsections.documents.DefaultAuthoringStepConstants;
import org.librecms.ui.contentsections.documents.DocumentNotFoundException;
import org.librecms.ui.contentsections.documents.DocumentUi;
import org.librecms.ui.contentsections.documents.MvcAuthoringStepDef;
import org.librecms.ui.contentsections.documents.MvcAuthoringSteps;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

/**
 * Authoring step for managing the {@link AttachmentList} and
 * {@link ItemAttachment}s assigned to a {@link ContentItem}.
 *
 * This class acts as controller for several views as well as named bean that
 * provides data for these views. Some of the views of the step use JavaScript
 * enhanced widgets. Therefore, some of the paths/endpoints provided by this
 * class return JSON data.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path(MvcAuthoringSteps.PATH_PREFIX + "relatedinfo")
@Controller
@MvcAuthoringStepDef(
    bundle = DefaultAuthoringStepConstants.BUNDLE,
    descriptionKey = "authoringsteps.relatedinfo.description",
    labelKey = "authoringsteps.relatedinfo.label",
    supportedDocumentType = ContentItem.class
)
public class RelatedInfoStep extends AbstractMvcAuthoringStep {

    /**
     * The path fragment of the step.
     */
    static final String PATH_FRAGMENT = "relatedinfo";

    /**
     * {@link AssetManager} instance of managing {@link Asset}s.
     */
    @Inject
    private AssetManager assetManager;

    /**
     * Used to retrieve and save {@link Asset}s.
     */
    @Inject
    private AssetRepository assetRepo;

    /**
     * Provides access to the available asset types.
     */
    @Inject
    private AssetTypesManager assetTypesManager;

    @Inject
    private BaseUrl baseUrl;

    /**
     * Model for the details view of an {@link AttachmentList}.
     */
    @Inject
    private AttachmentListDetailsModel listDetailsModel;

    /**
     * Manager for {@link AttachmentList}s.
     */
    @Inject
    private AttachmentListManager listManager;

    /**
     * Used to retrieve and save {@link AttachmentList}s.
     */
    @Inject
    private AttachmentListRepository listRepo;

    @Inject
    private DocumentUi documentUi;

    @Context
    private HttpServletRequest request;

    @Inject
    private FileDetailsModel fileDetailsModel;

    /**
     * Model for the details view of an internal {@link RelatedLink}.
     */
    @Inject
    private LinkDetailsModel linkDetailsModel;

    /**
     * Used to retrieve the current content item.
     */
    @Inject
    private ContentItemRepository itemRepo;

    /**
     * Used for globalization stuff.
     */
    @Inject
    private GlobalizationHelper globalizationHelper;

    /**
     * Used to parse identifiers.
     */
    @Inject
    private IdentifierParser identifierParser;

    /**
     * Manages {@link ItemAttachment}.
     */
    @Inject
    private ItemAttachmentManager attachmentManager;

    @Inject
    private ItemPermissionChecker itemPermissionChecker;

    /**
     * Used to provide data for the views without a named bean.
     */
    @Inject
    private Models models;

    @Inject
    private PermissionChecker permissionChecker;

    @Inject
    private RelatedInfoStepModel relatedInfoStepModel;

    @Override
    public Class<RelatedInfoStep> getStepClass() {
        return RelatedInfoStep.class;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    protected void init() throws ContentSectionNotFoundException,
                                 DocumentNotFoundException {
        super.init();
        relatedInfoStepModel.setAttachmentLists(
            getDocument()
                .getAttachments()
                .stream()
                .filter(list -> !list.getName().startsWith("."))
                .map(this::buildAttachmentListDto)
                .collect(Collectors.toList())
        );
        relatedInfoStepModel.setFileAssetPickerBaseUrl(
            baseUrl.getBaseUrl(request)
        );

        relatedInfoStepModel.setSectionName(getContentSection().getLabel());
    }

    @GET
    @Path("/")
    @Transactional(Transactional.TxType.REQUIRED)
    public String showStep(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (permissionChecker.isPermitted(
            ItemPrivileges.EDIT, getDocument()
        )) {
            return "org/librecms/ui/contentsection/documents/relatedinfo.xhtml";
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Adds a new attachment list.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param name              The name of the list.
     * @param title             The title of the list for the language returned
     *                          by {@link GlobalizationHelper#getNegotiatedLocale()
     *                          } .
     * @param description       The description of the list of the default
     *                          locale {@link GlobalizationHelper#getNegotiatedLocale().
     *
     * @return A redirect to the list of attachment lists.
     */
    @POST
    @Path("/attachmentlists/@add")
    @Transactional(Transactional.TxType.REQUIRED)
    public String addAttachmentList(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @FormParam("listName")
        final String name,
        @FormParam("listTitle")
        final String title,
        @FormParam("listDescription")
        final String description
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (permissionChecker.isPermitted(
            ItemPrivileges.EDIT, getDocument()
        )) {
            final ContentItem document = getDocument();
            final AttachmentList list = listManager.createAttachmentList(
                document, name
            );
            list.getTitle().addValue(
                globalizationHelper.getNegotiatedLocale(), title
            );
            list.getDescription().addValue(
                globalizationHelper.getNegotiatedLocale(), description
            );
            listRepo.save(list);
            return buildRedirectPathForStep();
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Shows the details of an attachment list.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifier of the list.
     *
     * @return The template for the details view.
     */
    @GET
    @Path("/attachmentlists/{attachmentListIdentifier}/@details")
    @Transactional(Transactional.TxType.REQUIRED)
    public String showAttachmentListDetails(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (permissionChecker.isPermitted(
            ItemPrivileges.EDIT, getDocument()
        )) {
            final Optional<AttachmentList> listResult = findAttachmentList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showAttachmentListNotFound(listIdentifierParam);
            }

            final AttachmentList list = listResult.get();

            listDetailsModel.setUuid(list.getUuid());
            listDetailsModel.setName(list.getName());
            listDetailsModel.setTitles(
                list
                    .getTitle()
                    .getValues()
                    .entrySet()
                    .stream()
                    .collect(
                        Collectors.toMap(
                            entry -> entry.getKey().toString(),
                            entry -> entry.getValue()
                        )
                    )
            );
            listDetailsModel.setDescriptions(
                list
                    .getDescription()
                    .getValues()
                    .entrySet()
                    .stream()
                    .collect(
                        Collectors.toMap(
                            entry -> entry.getKey().toString(),
                            entry -> entry.getValue()
                        )
                    )
            );

            final Set<Locale> titleLocales = list
                .getTitle()
                .getAvailableLocales();
            listDetailsModel.setUnusedTitleLocales(globalizationHelper
                .getAvailableLocales()
                .stream()
                .filter(locale -> !titleLocales.contains(locale))
                .map(Locale::toString)
                .collect(Collectors.toList())
            );

            final Set<Locale> descriptionLocales = list
                .getDescription()
                .getAvailableLocales();
            listDetailsModel.setUnusedDescriptionLocales(
                globalizationHelper
                    .getAvailableLocales()
                    .stream()
                    .filter(locale -> !descriptionLocales.contains(locale))
                    .map(Locale::toString)
                    .collect(Collectors.toList())
            );

            listDetailsModel.setCanEdit(
                itemPermissionChecker.canEditItem(getDocument())
            );

            return "org/librecms/ui/contentsection/documents/relatedinfo-attachmentlist-details.xhtml";
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Updates an attachment list.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifier of the list to update.
     * @param name                The new name of the list.
     *
     * @return A redirect to the list of attachment lists.
     */
    @POST
    @Path("/attachmentlists/{attachmentListIdentifier}/@update")
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateAttachmentList(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @FormParam("listName")
        final String name
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (permissionChecker.isPermitted(
            ItemPrivileges.EDIT, getDocument()
        )) {
            final Optional<AttachmentList> listResult = findAttachmentList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showAttachmentListNotFound(listIdentifierParam);
            }

            final AttachmentList list = listResult.get();
            list.setName(name);
            listRepo.save(list);

            return buildRedirectPathForStep(
                String.format("/attachmentlists/%s/@details", list.getName())
            );
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Removes an attachment list and all item attachment of the list.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifier of the list to remove.
     * @param confirmed             The value of the confirm parameter. Must
     *                            contain {@code true} (as string not as
     *                            boolean), otherwise this method does nothing.
     *
     * @return A redirect to the list of attachment lists.
     */
    @POST
    @Path("/attachmentlists/{attachmentListIdentifier}/@remove")
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeAttachmentList(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @FormParam("confirmed")
        final String confirmed
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (permissionChecker.isPermitted(
            ItemPrivileges.EDIT, getDocument()
        )) {
            final Optional<AttachmentList> listResult = findAttachmentList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showAttachmentListNotFound(listIdentifierParam);
            }

            if ("true".equalsIgnoreCase(confirmed)) {
                listManager.removeAttachmentList(listResult.get());
            }

            return buildRedirectPathForStep();
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Adds a localized title to an attachment list.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifier of the list.
     * @param localeParam         The locale of the new title value.
     * @param value               The value of the new title value.
     *
     * @return A redirect to the details view of the attachment list.
     */
    @POST
    @Path("/attachmentlists/{attachmentListIdentifier}/title/@add")
    @Transactional(Transactional.TxType.REQUIRED)
    public String addAttachmentListTitle(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @FormParam("locale")
        final String localeParam,
        @FormParam("value")
        final String value
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (permissionChecker.isPermitted(
            ItemPrivileges.EDIT, getDocument()
        )) {
            final Optional<AttachmentList> listResult = findAttachmentList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showAttachmentListNotFound(listIdentifierParam);
            }

            final AttachmentList list = listResult.get();
            list.getTitle().addValue(new Locale(localeParam), value);
            listRepo.save(list);

            return buildRedirectPathForStep(
                String.format("/attachmentlists/%s/@details", list.getName())
            );
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Updates a localized title value of an attachment list.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifier of the list.
     * @param localeParam         The locale of the title value to update.
     * @param value               The new title value.
     *
     * @return A redirect to the details view of the attachment list.
     */
    @POST
    @Path("/attachmentlists/{attachmentListIdentifier}/title/@edit/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateAttachmentListTitle(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @PathParam("locale")
        final String localeParam,
        @FormParam("value")
        final String value
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (permissionChecker.isPermitted(
            ItemPrivileges.EDIT, getDocument()
        )) {
            final Optional<AttachmentList> listResult = findAttachmentList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showAttachmentListNotFound(listIdentifierParam);
            }

            final AttachmentList list = listResult.get();
            list.getTitle().addValue(new Locale(localeParam), value);
            listRepo.save(list);

            return buildRedirectPathForStep(
                String.format("/attachmentlists/%s/@details", list.getName())
            );
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Removes a localized title value of an attachment list.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifier of the list.
     * @param localeParam         The locale of the title value to remove.
     *
     * @return A redirect to the details view of the attachment list.
     */
    @POST
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/title/@remove/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeAttachmentListTitle(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @PathParam("locale")
        final String localeParam
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (permissionChecker.isPermitted(
            ItemPrivileges.EDIT, getDocument()
        )) {
            final Optional<AttachmentList> listResult = findAttachmentList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showAttachmentListNotFound(listIdentifierParam);
            }

            final AttachmentList list = listResult.get();
            list.getTitle().removeValue(new Locale(localeParam));
            listRepo.save(list);

            return buildRedirectPathForStep(
                String.format("/attachmentlists/%s/@details", list.getName())
            );
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Adds a localized description to an attachment list.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifier of the list.
     * @param localeParam         The locale of the new description value.
     * @param value               The value of the new description value.
     *
     * @return A redirect to the details view of the attachment list.
     */
    @POST
    @Path("/attachmentlists/{attachmentListIdentifier}/description/@add")
    @Transactional(Transactional.TxType.REQUIRED)
    public String addAttachmentListDescription(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @FormParam("locale")
        final String localeParam,
        @FormParam("value")
        final String value
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (permissionChecker.isPermitted(
            ItemPrivileges.EDIT, getDocument()
        )) {
            final Optional<AttachmentList> listResult = findAttachmentList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showAttachmentListNotFound(listIdentifierParam);
            }

            final AttachmentList list = listResult.get();
            list.getDescription().addValue(new Locale(localeParam), value);
            listRepo.save(list);

            return buildRedirectPathForStep(
                String.format("/attachmentlists/%s/@details", list.getName())
            );
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Updates a localized description value of an attachment list.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifier of the list.
     * @param localeParam         The locale of the description value to update.
     * @param value               The new description value.
     *
     * @return A redirect to the details view of the attachment list.
     */
    @POST
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/description/@edit/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateAttachmentListDescription(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @PathParam("locale")
        final String localeParam,
        @FormParam("value")
        final String value
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (permissionChecker.isPermitted(
            ItemPrivileges.EDIT, getDocument()
        )) {
            final Optional<AttachmentList> listResult = findAttachmentList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showAttachmentListNotFound(listIdentifierParam);
            }

            final AttachmentList list = listResult.get();
            list.getDescription().addValue(new Locale(localeParam), value);
            listRepo.save(list);

            return buildRedirectPathForStep(
                String.format("/attachmentlists/%s/@details", list.getName())
            );
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Removes a localized description value of an attachment list.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifier of the list.
     * @param localeParam         The locale of the description value to remove.
     *
     * @return A redirect to the details view of the attachment list.
     */
    @POST
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/description/@remove/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeAttachmentListDescription(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @PathParam("locale")
        final String localeParam
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (permissionChecker.isPermitted(
            ItemPrivileges.EDIT, getDocument()
        )) {
            final Optional<AttachmentList> listResult = findAttachmentList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showAttachmentListNotFound(listIdentifierParam);
            }

            final AttachmentList list = listResult.get();
            list.getDescription().removeValue(new Locale(localeParam));
            listRepo.save(list);

            return buildRedirectPathForStep(
                String.format("/attachmentlists/%s/@details", list.getName())
            );
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Create new attachment.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifier of the list to which the
     *                            attachment is added.
     * @param assetIdentifier     The identifier of the asset to use for the
     *                            attachment.
     *
     * @return A redirect to the list of attachment lists and attachments.
     */
    @POST
    @Path("/attachmentlists/{attachmentListIdentifier}/attachments/@create")
    @Transactional(Transactional.TxType.REQUIRED)
    public String createAttachment(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @FormParam("assetIdentifier")
        final String assetIdentifierParam
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (permissionChecker.isPermitted(
            ItemPrivileges.EDIT, getDocument()
        )) {
            final Optional<AttachmentList> listResult = findAttachmentList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showAttachmentListNotFound(listIdentifierParam);
            }
            final AttachmentList list = listResult.get();

            final Optional<Asset> assetResult;
            final Identifier assetIdentifier = identifierParser.parseIdentifier(
                assetIdentifierParam
            );
            switch (assetIdentifier.getType()) {
                case ID:
                    assetResult = assetRepo.findById(
                        Long.parseLong(
                            assetIdentifier.getIdentifier()
                        )
                    );
                    break;
                case UUID:
                    assetResult = assetRepo.findByUuid(
                        assetIdentifier.getIdentifier()
                    );
                    break;
                default:
                    assetResult = assetRepo.findByPath(
                        getContentSection(),
                        assetIdentifier.getIdentifier()
                    );
                    break;
            }

            if (!assetResult.isPresent()) {
                models
                    .put("section", getContentSection().getLabel());
                models.put("assetUuid", assetIdentifierParam);
                return "org/librecms/ui/contentsection/documents/asset-not-found.xhtml";
            }

            final Asset asset = assetResult.get();

            attachmentManager.attachAsset(asset, list);

            return buildRedirectPathForStep();
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Shows the form for creating a new internal link.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifier of the list to which the
     *                            attachment is added.
     *
     * @return The template for the form for creating a new internal link.
     */
    @GET
    @Path("/attachmentlists/{attachmentListIdentifier}/links/@create")
    @Transactional(Transactional.TxType.REQUIRED)
    public String createLink(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (permissionChecker.isPermitted(
            ItemPrivileges.EDIT, getDocument()
        )) {
            final Optional<AttachmentList> listResult = findAttachmentList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showAttachmentListNotFound(listIdentifierParam);
            }
            final AttachmentList list = listResult.get();
            models.put("attachmentList", list.getName());
            models.put("messages", Collections.emptyMap());
            models.put("selectedType", "");

            return "org/librecms/ui/contentsection/documents/relatedinfo-create-link.xhtml";
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Create a new internal link.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifier of the list to which the
     *                            attachment is added.
     * @param locale
     * @param title               The title of the new internal link for the
     *                            language return by {@link GlobalizationHelper#getNegotiatedLocale()
     *                            }.
     *
     * @return A redirect to the list of attachment lists and attachments.
     */
    @POST
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/links/@create")
    @Transactional(Transactional.TxType.REQUIRED)
    public String createLink(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        //        @FormParam("name")
        //        final String name,
        @FormParam("locale")
        final String locale,
        @FormParam("title")
        final String title
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (permissionChecker.isPermitted(
            ItemPrivileges.EDIT, getDocument()
        )) {
            final Optional<AttachmentList> listResult = findAttachmentList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showAttachmentListNotFound(listIdentifierParam);
            }
            final AttachmentList list = listResult.get();

//            final Optional<ContentItem> itemResult = itemRepo.findByUuid(
//                targetItemUuid
//            );
//            if (!itemResult.isPresent()) {
//                models.put("targetItemUuid", targetItemUuid);
//                return "org/librecms/ui/contentsection/documents/target-item-not-found.xhtml";
//            }
            final RelatedLink relatedLink = new RelatedLink();
            relatedLink.getTitle().addValue(
                globalizationHelper.getNegotiatedLocale(), title
            );
            relatedLink.setDisplayName(
                title
                    .toLowerCase(Locale.ROOT)
                    .replaceAll("\\s*", "-"));

            attachmentManager.attachAsset(relatedLink, list);
            return buildRedirectPathForStep(
                String.format(
                    "/attachmentlists/%s/links/%s/@details",
                    list.getName(),
                    relatedLink.getUuid()
                )
            );
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Show the details of a link..
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifier of the {@link AttachmentList}
     *                            to which the link belongs.
     * @param linkUuid            The UUID of the link.
     *
     * @return The template for the details view of the link, or the template
     *         for the link not found message if the link iwth the provided UUID
     *         is found in the provided attachment list.
     */
    @GET
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/links/{linkUuid}/@details"
    )
    @Transactional(Transactional.TxType.REQUIRED)
    public String showLinkDetails(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @PathParam("linkUuid")
        final String linkUuid
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (permissionChecker.isPermitted(
            ItemPrivileges.EDIT, getDocument()
        )) {
            final Optional<AttachmentList> listResult = findAttachmentList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showAttachmentListNotFound(listIdentifierParam);
            }
            final AttachmentList list = listResult.get();

            final Optional<RelatedLink> linkResult = list
                .getAttachments()
                .stream()
                .map(ItemAttachment::getAsset)
                .filter(asset -> asset instanceof RelatedLink)
                .map(asset -> (RelatedLink) asset)
                .filter(link -> link.getUuid().equals(linkUuid))
                .findAny();

            if (!linkResult.isPresent()) {
                models.put("contentItem", getDocumentPath());
                models.put("listIdentifier", listIdentifierParam);
                models.put("linkUuid", linkUuid);
                return "org/librecms/ui/contentsection/documents/link-asset-not-found.xhtml";
            }

            final RelatedLink link = linkResult.get();
            linkDetailsModel.setListIdentifier(list.getName());

            if (link.getBookmark() != null) {
                linkDetailsModel.setLinkType("external");
                linkDetailsModel.setBookmarkName(
                    link.getBookmark().getDisplayName()
                );
                linkDetailsModel.setBookmarkUuid(link.getBookmark().getUuid());
            } else if (link.getTargetItem() != null) {
                linkDetailsModel.setLinkType("internal");
                linkDetailsModel.setTargetItemName(
                    link.getTargetItem().getDisplayName()
                );
                linkDetailsModel.setTargetItemTitle(
                    globalizationHelper.getValueFromLocalizedString(
                        link.getTargetItem().getTitle()
                    )
                );
                linkDetailsModel.setTargetItemUuid(
                    link.getTargetItem().getUuid()
                );
            } else {
                linkDetailsModel.setLinkType("--none--");
            }

            linkDetailsModel.setBaseUrl(baseUrl.getBaseUrl(request));

            final LocalizedTextsUtil textsUtil = globalizationHelper
                .getLocalizedTextsUtil(DefaultAuthoringStepConstants.BUNDLE);
            final Map<String, String> linkTypes = new LinkedHashMap<>();
            linkTypes.put(
                "--none--",
                textsUtil.getText("relatedinfo.links.type.none_selected")
            );
            linkTypes.put(
                "external",
                textsUtil.getText("relatedinfo.links.type.external")
            );
            linkTypes.put(
                "internal",
                textsUtil.getText("relatedinfo.links.type.internal")
            );
            linkDetailsModel.setLinkTypes(linkTypes);

            linkDetailsModel.setTitle(
                link
                    .getTitle()
                    .getValues()
                    .entrySet()
                    .stream()
                    .collect(
                        Collectors.toMap(
                            entry -> entry.getKey().toString(),
                            entry -> entry.getValue()
                        )
                    )
            );
            final Set<Locale> titleLocales = link.getTitle()
                .getAvailableLocales();
            linkDetailsModel.setUnusedTitleLocales(
                globalizationHelper
                    .getAvailableLocales()
                    .stream()
                    .filter(locale -> !titleLocales.contains(locale))
                    .map(Locale::toString)
                    .collect(Collectors.toList())
            );
            linkDetailsModel.setUuid(link.getUuid());
            linkDetailsModel.setSectionName(getContentSection().getLabel());

            return "org/librecms/ui/contentsection/documents/relatedinfo-link-details.xhtml";
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Updates to target of a related link and sets it to the provided target
     * item, making the link an internal link.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam  The identifier of the {@link AttachmentList}
     *                             to which the link belongs.
     * @param linkUuid             The UUID of the link.
     * @param targetItemIdentifier The UUID of the new target item.
     *
     * @return A redirect to the details page of the link.
     */
    @POST
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/links/{linkUuid}/details/@set-target-item")
    @Transactional(Transactional.TxType.REQUIRED)
    public String setTargetItem(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @PathParam("linkUuid")
        final String linkUuid,
        @FormParam("targetItemIdentifier")
        final String targetItemIdentifier
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (permissionChecker.isPermitted(
            ItemPrivileges.EDIT, getDocument()
        )) {
            final Optional<AttachmentList> listResult = findAttachmentList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showAttachmentListNotFound(listIdentifierParam);
            }
            final AttachmentList list = listResult.get();

            final Optional<ContentItem> itemResult;
            final Identifier itemIdentifier = identifierParser.parseIdentifier(
                targetItemIdentifier
            );
            switch (itemIdentifier.getType()) {
                case ID:
                    itemResult = itemRepo.findById(
                        Long.parseLong(itemIdentifier.getIdentifier())
                    );
                    break;
                case UUID:
                    itemResult = itemRepo.findByUuid(
                        itemIdentifier.getIdentifier()
                    );
                    break;
                default:
                    itemResult = itemRepo.findByPath(
                        getContentSection(),
                        itemIdentifier.getIdentifier()
                    );
                    break;

            }

            if (!itemResult.isPresent()) {
                models.put("targetItemIdentifier", targetItemIdentifier);
                return "org/librecms/ui/contentsection/documents/target-item-not-found.xhtml";
            }
            final Optional<RelatedLink> linkResult = list
                .getAttachments()
                .stream()
                .map(ItemAttachment::getAsset)
                .filter(asset -> asset instanceof RelatedLink)
                .map(asset -> (RelatedLink) asset)
                .filter(link -> link.getUuid().equals(linkUuid))
                .findAny();

            if (!linkResult.isPresent()) {
                models.put("contentItem", getDocumentPath());
                models.put("listIdentifier", listIdentifierParam);
                models.put("linkUuid", linkUuid);
                return "org/librecms/ui/contentsection/documents/internal-link-asset-not-found.xhtml";
            }

            final RelatedLink link = linkResult.get();
            link.setBookmark(null);
            link.setTargetItem(itemResult.get());
            assetRepo.save(link);

            return buildRedirectPathForStep(
                String.format(
                    "/attachmentlists/%s/links/%s/@details",
                    list.getName(),
                    link.getUuid()
                )
            );

        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Updates to target of a related link and sets it to the provided bookmark,
     * making the link an external link.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam      The identifier of the
     *                                 {@link AttachmentList} to which the link
     *                                 belongs.
     * @param linkUuid                 The UUID of the link.
     * @param targetBookmarkIdentifier The UUID of the new target bookmark.
     *
     * @return A redirect to the details page of the link.
     */
    @POST
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/links/{linkUuid}/details/@set-target-bookmark")
    @Transactional(Transactional.TxType.REQUIRED)
    public String setTargetBookmark(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @PathParam("linkUuid")
        final String linkUuid,
        @FormParam("targetBookmarkIdentifier")
        final String targetBookmarkIdentifier
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (permissionChecker.isPermitted(
            ItemPrivileges.EDIT, getDocument()
        )) {
            final Optional<AttachmentList> listResult = findAttachmentList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showAttachmentListNotFound(listIdentifierParam);
            }
            final AttachmentList list = listResult.get();

            final Optional<Bookmark> bookmarkResult;
            final Identifier bookmarkIdentifer = identifierParser
                .parseIdentifier(targetBookmarkIdentifier
                );
            switch (bookmarkIdentifer.getType()) {
                case ID:
                    bookmarkResult = assetRepo
                        .findById(
                            Long.parseLong(
                                bookmarkIdentifer.getIdentifier()
                            ),
                            Bookmark.class
                        );
                    break;
                case UUID:
                    bookmarkResult = assetRepo
                        .findByUuidAndType(
                            bookmarkIdentifer.getIdentifier(),
                            Bookmark.class
                        );
                    break;
                default:
                    bookmarkResult = assetRepo
                        .findByPath(
                            getContentSection(),
                            bookmarkIdentifer.getIdentifier()
                        )
                        .filter(asset -> asset instanceof Bookmark)
                        .map(asset -> (Bookmark) asset);
            }

            if (!bookmarkResult.isPresent()) {
                models.put("targetBookmarkIdentifier", targetBookmarkIdentifier);
                return "org/librecms/ui/contentsection/documents/target-bookmark-not-found.xhtml";
            }

            final Optional<RelatedLink> linkResult = list
                .getAttachments()
                .stream()
                .map(ItemAttachment::getAsset)
                .filter(asset -> asset instanceof RelatedLink)
                .map(asset -> (RelatedLink) asset)
                .filter(link -> link.getUuid().equals(linkUuid))
                .findAny();

            if (!linkResult.isPresent()) {
                models.put("contentItem", getDocumentPath());
                models.put("listIdentifier", listIdentifierParam);
                models.put("linkUuid", linkUuid);
                return "org/librecms/ui/contentsection/documents/link-asset-not-found.xhtml";
            }

            final RelatedLink link = linkResult.get();
            link.setTargetItem(null);
            link.setBookmark(bookmarkResult.get());
            assetRepo.save(link);

            return buildRedirectPathForStep(
                String.format(
                    "/attachmentlists/%s/links/%s/@details",
                    list.getName(),
                    link.getUuid()
                )
            );
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Add a localized title value to a link..
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifier of the {@link AttachmentList}
     *                            to which the link belongs.
     * @param linkUuid            The UUID of the link.
     * @param localeParam         The locale of the new title value.
     * @param value               The localized value.
     *
     * @return A redirect to the details view of the link.
     */
    @POST
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/links/{linkUuid}/title/@add")
    @Transactional(Transactional.TxType.REQUIRED)
    public String addLinkTitle(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @PathParam("linkUuid")
        final String linkUuid,
        @FormParam("locale")
        final String localeParam,
        @FormParam("value")
        final String value
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (permissionChecker.isPermitted(
            ItemPrivileges.EDIT, getDocument()
        )) {
            final Optional<AttachmentList> listResult = findAttachmentList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showAttachmentListNotFound(listIdentifierParam);
            }
            final AttachmentList list = listResult.get();

            final Optional<RelatedLink> linkResult = list
                .getAttachments()
                .stream()
                .map(ItemAttachment::getAsset)
                .filter(asset -> asset instanceof RelatedLink)
                .map(asset -> (RelatedLink) asset)
                .filter(link -> link.getUuid().equals(linkUuid))
                .findAny();

            if (!linkResult.isPresent()) {
                models.put("contentItem", getDocumentPath());
                models.put("listIdentifierParam", listIdentifierParam);
                models.put("linkUuid", linkUuid);
                return "org/librecms/ui/contentsection/documents/internal-link-asset-not-found.xhtml";
            }

            final RelatedLink link = linkResult.get();
            final Locale locale = new Locale(localeParam);
            link.getTitle().addValue(locale, value);
            assetRepo.save(link);

            return buildRedirectPathForStep(
                String.format(
                    "/attachmentlists/%s/links/%s/@details",
                    list.getName(),
                    link.getUuid()
                )
            );
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Updates a localized title value of a link..
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifier of the {@link AttachmentList}
     *                            to which the link belongs.
     * @param linkUuid            The UUID of the link.
     * @param localeParam         The locale of the title value to update.
     * @param value               The localized value.
     *
     * @return A redirect to the details view of the link.
     */
    @POST
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/links/{linkUuid}/title/@edit/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateLinkTitle(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @PathParam("linkUuid")
        final String linkUuid,
        @PathParam("locale")
        final String localeParam,
        @FormParam("value")
        final String value
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (permissionChecker.isPermitted(
            ItemPrivileges.EDIT, getDocument()
        )) {
            final Optional<AttachmentList> listResult = findAttachmentList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showAttachmentListNotFound(listIdentifierParam);
            }
            final AttachmentList list = listResult.get();

            final Optional<RelatedLink> linkResult = list
                .getAttachments()
                .stream()
                .map(ItemAttachment::getAsset)
                .filter(asset -> asset instanceof RelatedLink)
                .map(asset -> (RelatedLink) asset)
                .filter(link -> link.getUuid().equals(linkUuid))
                .findAny();

            if (!linkResult.isPresent()) {
                models.put("contentItem", getDocumentPath());
                models.put("listIdentifierParam", listIdentifierParam);
                models.put("linkUuid", linkUuid);
                return "org/librecms/ui/contentsection/documents/internal-link-asset-not-found.xhtml";
            }

            final RelatedLink link = linkResult.get();
            final Locale locale = new Locale(localeParam);
            link.getTitle().addValue(locale, value);
            assetRepo.save(link);

            return buildRedirectPathForStep(
                String.format(
                    "/attachmentlists/%s/links/%s/@details",
                    list.getName(),
                    link.getUuid()
                )
            );
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Removes a localized title value from a link..
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifier of the {@link AttachmentList}
     *                            to which the link belongs.
     * @param linkUuid            The UUID of the link.
     * @param localeParam         The locale of the value to remove.
     *
     * @return A redirect to the details view of the link.
     */
    @POST
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/links/{linkUuid}/title/@remove/{locale}"
    )
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeLinkTitle(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @PathParam("linkUuid")
        final String linkUuid,
        @PathParam("locale")
        final String localeParam
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (permissionChecker.isPermitted(
            ItemPrivileges.EDIT, getDocument()
        )) {
            final Optional<AttachmentList> listResult = findAttachmentList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showAttachmentListNotFound(listIdentifierParam);
            }
            final AttachmentList list = listResult.get();

            final Optional<RelatedLink> linkResult = list
                .getAttachments()
                .stream()
                .map(ItemAttachment::getAsset)
                .filter(asset -> asset instanceof RelatedLink)
                .map(asset -> (RelatedLink) asset)
                .filter(link -> link.getUuid().equals(linkUuid))
                .findAny();

            if (!linkResult.isPresent()) {
                models.put("contentItem", getDocumentPath());
                models.put("listIdentifierParam", listIdentifierParam);
                models.put("linkUuid", linkUuid);
                return "org/librecms/ui/contentsection/documents/internal-link-asset-not-found.xhtml";
            }

            final RelatedLink link = linkResult.get();
            final Locale locale = new Locale(localeParam);
            link.getTitle().removeValue(locale);
            assetRepo.save(link);

            return buildRedirectPathForStep(
                String.format(
                    "/attachmentlists/%s/links/%s/@details",
                    list.getName(),
                    link.getUuid()
                )
            );
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Removes an attachment from an {@link AttachmentList}.The {@link Asset} of
     * the attachment will not be deleted unless it is a related link.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifier of the {@link AttachmentList}
     *                            to which the attachment belongs.
     * @param attachmentUuid      The UUID of the attachment to remove.
     * @param confirmed             The value of the {@code confirm} parameter. If
     *                            the value anything other than the string
     *                            {@code true} the method does nothing.
     *
     * @return
     */
    @POST
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/attachments/{attachmentUuid}/@remove")
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeAttachment(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @PathParam("attachmentUuid")
        final String attachmentUuid,
        @FormParam("confirmed")
        final String confirmed
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (permissionChecker.isPermitted(
            ItemPrivileges.EDIT, getDocument()
        )) {
            final Optional<AttachmentList> listResult = findAttachmentList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showAttachmentListNotFound(listIdentifierParam);
            }
            final AttachmentList list = listResult.get();

            final Optional<ItemAttachment<?>> result = list
                .getAttachments()
                .stream()
                .filter(attachment -> attachment.getUuid()
                .equals(attachmentUuid))
                .findFirst();

            if (result.isPresent() && "true".equalsIgnoreCase(confirmed)) {
                final Asset asset = result.get().getAsset();
                attachmentManager.unattachAsset(asset, list);
//                if (asset instanceof RelatedLink) {
//                    assetRepo.delete(asset);
//                }
            }

            return buildRedirectPathForStep();
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Move an attachment list one position up.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifer of the list to move.
     *
     * @return A redirect to list of attachment lists.
     */
    @POST
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/@moveUp")
    @Transactional(Transactional.TxType.REQUIRED)
    public String moveListUp(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (permissionChecker.isPermitted(
            ItemPrivileges.EDIT, getDocument()
        )) {
            final Optional<AttachmentList> listResult = findAttachmentList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showAttachmentListNotFound(listIdentifierParam);
            }
            final AttachmentList list = listResult.get();

            listManager.moveUp(list);

            return buildRedirectPathForStep(
                String.format("/attachmentlists/%s/@details", list.getName())
            );
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Move an attachment list one position down.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifer of the list to move.
     *
     * @return A redirect to list of attachment lists.
     */
    @POST
    @Path("/attachmentlists/{attachmentListIdentifier}/@moveDown")
    @Transactional(Transactional.TxType.REQUIRED)
    public String moveListDown(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (permissionChecker.isPermitted(
            ItemPrivileges.EDIT, getDocument()
        )) {
            final Optional<AttachmentList> listResult = findAttachmentList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showAttachmentListNotFound(listIdentifierParam);
            }
            final AttachmentList list = listResult.get();

            listManager.moveDown(list);

            return buildRedirectPathForStep(
                String.format("/attachmentlists/%s/@details", list.getName())
            );
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Move an attachment one position up.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifer to which the attachment belongs.
     * @param attachmentUuid      The UUID of the attachment ot move.
     *
     * @return A redirect to list of attachment lists and attachments.
     */
    @POST
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/attachments/{attachmentUuid}/@moveUp")
    @Transactional(Transactional.TxType.REQUIRED)
    public String moveAttachmentUp(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @PathParam("attachmentUuid")
        final String attachmentUuid
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (permissionChecker.isPermitted(
            ItemPrivileges.EDIT, getDocument()
        )) {
            final Optional<AttachmentList> listResult = findAttachmentList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showAttachmentListNotFound(listIdentifierParam);
            }
            final AttachmentList list = listResult.get();

            final Optional<ItemAttachment<?>> result = list
                .getAttachments()
                .stream()
                .filter(attachment -> attachment.getUuid()
                .equals(attachmentUuid))
                .findFirst();

            if (result.isPresent()) {
                final ItemAttachment<?> attachment = result.get();
                final Asset asset = attachment.getAsset();
                attachmentManager.moveUp(asset, list);
            }

            return buildRedirectPathForStep(
                String.format("/attachmentlists/%s/@details", list.getName())
            );
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Move an attachment one position down.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifer to which the attachment belongs.
     * @param attachmentUuid      The UUID of the attachment ot move.
     *
     * @return A redirect to list of attachment lists and attachements.
     */
    @POST
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/attachments/{attachmentUuid}/@moveDown")
    @Transactional(Transactional.TxType.REQUIRED)
    public String moveAttachmentDown(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @PathParam("attachmentUuid")
        final String attachmentUuid
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (permissionChecker.isPermitted(
            ItemPrivileges.EDIT, getDocument()
        )) {
            final Optional<AttachmentList> listResult = findAttachmentList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showAttachmentListNotFound(listIdentifierParam);
            }
            final AttachmentList list = listResult.get();

            final Optional<ItemAttachment<?>> result = list
                .getAttachments()
                .stream()
                .filter(attachment -> attachment.getUuid()
                .equals(attachmentUuid))
                .findFirst();

            if (result.isPresent()) {
                final ItemAttachment<?> attachment = result.get();
                final Asset asset = attachment.getAsset();
                attachmentManager.moveDown(asset, list);
            }

            return buildRedirectPathForStep(
                String.format("/attachmentlists/%s/@details", list.getName())
            );
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * A helper function to find an attachment list.
     *
     * @param attachmentListIdentifier The idenfifier of the attachment list.
     *
     * @return An {@link Optional} with the attachment list or an empty optional
     *         if the current content item has no list with the provided
     *         identifier.
     */
    private Optional<AttachmentList> findAttachmentList(
        final String attachmentListIdentifier
    ) {
        final ContentItem document = getDocument();
        final Identifier identifier = identifierParser.parseIdentifier(
            attachmentListIdentifier
        );
        final Optional<AttachmentList> listResult;
        switch (identifier.getType()) {
            case ID:
                listResult = listRepo
                    .findForItemAndId(
                        document, Long.parseLong(identifier.getIdentifier())
                    );
                break;
            case UUID:
                listResult = listRepo
                    .findForItemAndUuid(
                        document, identifier.getIdentifier()
                    );
                break;
            default:
                listResult = listRepo
                    .findForItemAndName(
                        document, identifier.getIdentifier()
                    );
                break;
        }

        return listResult;
    }

    /**
     * Show the "attachment list not found" error page.
     *
     * @param listIdentifier The identifier of the list that was not found.
     *
     * @return The template for the "attachment list not found" page.
     */
    private String showAttachmentListNotFound(final String listIdentifier) {
        models.put("contentItem", getDocumentPath());
        models.put("listIdentifier", listIdentifier);
        return "org/librecms/ui/contentsection/documents/attachmentlist-not-found.xhtml";
    }

    private AttachmentListDto buildAttachmentListDto(
        final AttachmentList attachmentList
    ) {
        final AttachmentListDto dto = new AttachmentListDto();
        dto.setAttachments(
            attachmentList
                .getAttachments()
                .stream()
                .map(this::buildItemAttachmentDto)
                .collect(Collectors.toList())
        );
        dto.setDescription(
            globalizationHelper
                .getValueFromLocalizedString(
                    attachmentList.getDescription()
                )
        );
        dto.setListId(attachmentList.getListId());
        dto.setName(attachmentList.getName());
        dto.setOrder(attachmentList.getOrder());
        dto.setTitle(
            globalizationHelper
                .getValueFromLocalizedString(
                    attachmentList.getTitle()
                )
        );
        dto.setUuid(attachmentList.getUuid());
        return dto;
    }

    /**
     * Helper function for building a {@link ItemAttachmentDto} for an
     * {@link ItemAttachment}.
     *
     * @param itemAttachment The {@link ItemAttachment} from which the
     *                       {@link ItemAttachmentDto} is build.
     *
     * @return The {@link ItemAttachmentDto}.
     */
    private ItemAttachmentDto buildItemAttachmentDto(
        final ItemAttachment<?> itemAttachment
    ) {
        final ItemAttachmentDto dto = new ItemAttachmentDto();
        dto.setAssetType(
            Optional
                .ofNullable(itemAttachment.getAsset())
                .map(Asset::getClass)
                .map(clazz -> assetTypesManager.getAssetTypeInfo(clazz))
                .map(info -> info.getAssetClass().getName())
                .orElse("")
        );
        dto.setAssetTypeLabel(
            Optional
                .ofNullable(itemAttachment.getAsset())
                .map(Asset::getClass)
                .map(clazz -> assetTypesManager.getAssetTypeInfo(clazz))
                .map(
                    info -> globalizationHelper.getLocalizedTextsUtil(
                        info.getLabelBundle()).getText(info.getLabelKey())
                ).orElse("")
        );
        dto.setAssetUuid(
            Optional
                .ofNullable(itemAttachment.getAsset())
                .map(Asset::getUuid)
                .orElse(null)
        );
        dto.setAttachmentId(itemAttachment.getAttachmentId());
        dto.setInternalLink(
            itemAttachment.getAsset() != null
                && itemAttachment.getAsset() instanceof RelatedLink
                && ((RelatedLink) itemAttachment.getAsset()).getTargetItem()
                       != null
        );
        dto.setSortKey(itemAttachment.getSortKey());
        dto.setTitle(
            Optional
                .ofNullable(itemAttachment.getAsset())
                .map(
                    asset -> globalizationHelper.getValueFromLocalizedString(
                        asset.getTitle()
                    )
                )
                .orElse("")
        );
        dto.setUuid(itemAttachment.getUuid());
        return dto;
    }

}
