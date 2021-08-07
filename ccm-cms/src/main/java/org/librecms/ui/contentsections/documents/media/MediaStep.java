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
package org.librecms.ui.contentsections.documents.media;

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.PermissionChecker;
import org.libreccm.ui.BaseUrl;
import org.librecms.assets.AssetTypesManager;
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
import org.librecms.ui.contentsections.ContentSectionNotFoundException;
import org.librecms.ui.contentsections.ItemPermissionChecker;
import org.librecms.ui.contentsections.documents.AbstractMvcAuthoringStep;
import org.librecms.ui.contentsections.documents.DefaultAuthoringStepConstants;
import org.librecms.ui.contentsections.documents.DocumentNotFoundException;
import org.librecms.ui.contentsections.documents.DocumentUi;
import org.librecms.ui.contentsections.documents.MvcAuthoringStepDef;
import org.librecms.ui.contentsections.documents.MvcAuthoringSteps;
import org.librecms.ui.contentsections.documents.relatedinfo.ItemAttachmentDto;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path(MvcAuthoringSteps.PATH_PREFIX + "media")
@Controller
@MvcAuthoringStepDef(
    bundle = DefaultAuthoringStepConstants.BUNDLE,
    descriptionKey = "authoringsteps.media.description",
    labelKey = "authoringsteps.media.label",
    supportedDocumentType = ContentItem.class
)
public class MediaStep extends AbstractMvcAuthoringStep {

    /**
     * The path fragment of the step.
     */
    static final String PATH_FRAGMENT = "media";

    protected static final String MEDIA_LIST_PREFIX = ".media-";

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
     * Model for the details view of an {@link AttachmentList} containing media.
     */
    @Inject
    private MediaListDetailsModel listDetailsModel;

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
    private MediaDetailsModel mediaDetailsModel;

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
    private MediaStepModel mediaStepModel;

    @Override
    public Class<MediaStep> getStepClass() {
        return MediaStep.class;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    protected void init() throws ContentSectionNotFoundException,
                                 DocumentNotFoundException {
        super.init();
        mediaStepModel.setMediaLists(
            getDocument()
                .getAttachments()
                .stream()
                .filter(list -> list.getName().startsWith(MEDIA_LIST_PREFIX))
                .map(this::buildMediaListDto)
                .collect(Collectors.toList())
        );
        mediaStepModel.setMediaAssetPickerBaseUrl(baseUrl.getBaseUrl(request));

        mediaStepModel.setSectionName(getContentSection().getLabel());
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
            return "org/librecms/ui/contentsection/documents/media.xhtml";
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Adds a new media list.
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
    @Path("/medialists/@add")
    @Transactional(Transactional.TxType.REQUIRED)
    public String addMediaList(
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
                document, String.join("", MEDIA_LIST_PREFIX, name)
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
     * Shows the details of an media list.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifier of the list.
     *
     * @return The template for the details view.
     */
    @GET
    @Path("/medialists/{mediaListIdentifier}/@details")
    @Transactional(Transactional.TxType.REQUIRED)
    public String showMediaListDetails(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("mediaListIdentifier")
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
            final Optional<AttachmentList> listResult = findMediaList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showMediaListNotFound(listIdentifierParam);
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

            return "org/librecms/ui/contentsection/documents/media-medialist-details.xhtml";
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Updates an media list.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifier of the list to update.
     * @param name                The new name of the list.
     *
     * @return A redirect to the list of media lists.
     */
    @POST
    @Path("/medialists/{mediaListIdentifier}/@update")
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateAttachmentList(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("mediaListIdentifier")
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
            final Optional<AttachmentList> listResult = findMediaList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showMediaListNotFound(listIdentifierParam);
            }

            final AttachmentList list = listResult.get();
            list.setName(name);
            listRepo.save(list);

            return buildRedirectPathForStep(
                String.format("/medialists/%s/@details", list.getName())
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
     * Removes an media list and all media of the list.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifier of the list to remove.
     * @param confirmed           The value of the confirm parameter. Must
     *                            contain {@code true} (as string not as
     *                            boolean), otherwise this method does nothing.
     *
     * @return A redirect to the list of attachment lists.
     */
    @POST
    @Path("/medialists/{mediaListIdentifier}/@remove")
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeAttachmentList(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("mediaListIdentifier")
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
            final Optional<AttachmentList> listResult = findMediaList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showMediaListNotFound(listIdentifierParam);
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
     * Adds a localized title to an media list.
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
    @Path("/medialists/{mediaListIdentifier}/title/@add")
    @Transactional(Transactional.TxType.REQUIRED)
    public String addMediaListTitle(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("mediaListIdentifier")
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
            final Optional<AttachmentList> listResult = findMediaList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showMediaListNotFound(listIdentifierParam);
            }

            final AttachmentList list = listResult.get();
            list.getTitle().addValue(new Locale(localeParam), value);
            listRepo.save(list);

            return buildRedirectPathForStep(
                String.format("/medialists/%s/@details", list.getName())
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
     * Updates a localized title value of an media list.
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
    @Path("/medialists/{mediaListIdentifier}/title/@edit/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateAttachmentListTitle(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("mediaListIdentifier")
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
            final Optional<AttachmentList> listResult = findMediaList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showMediaListNotFound(listIdentifierParam);
            }

            final AttachmentList list = listResult.get();
            list.getTitle().addValue(new Locale(localeParam), value);
            listRepo.save(list);

            return buildRedirectPathForStep(
                String.format("/medialists/%s/@details", list.getName())
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
     * Removes a localized title value of an media list.
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
        "/medialists/{mediaListIdentifier}/title/@remove/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeMediaListTitle(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("mediaListIdentifier")
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
            final Optional<AttachmentList> listResult = findMediaList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showMediaListNotFound(listIdentifierParam);
            }

            final AttachmentList list = listResult.get();
            list.getTitle().removeValue(new Locale(localeParam));
            listRepo.save(list);

            return buildRedirectPathForStep(
                String.format("/medialists/%s/@details", list.getName())
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
     * Adds a localized description to an media list.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifier of the list.
     * @param localeParam         The locale of the new description value.
     * @param value               The value of the new description value.
     *
     * @return A redirect to the details view of the media list.
     */
    @POST
    @Path("/medialists/{mediaListIdentifier}/description/@add")
    @Transactional(Transactional.TxType.REQUIRED)
    public String addMediaListDescription(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("mediaListIdentifier")
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
            final Optional<AttachmentList> listResult = findMediaList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showMediaListNotFound(listIdentifierParam);
            }

            final AttachmentList list = listResult.get();
            list.getDescription().addValue(new Locale(localeParam), value);
            listRepo.save(list);

            return buildRedirectPathForStep(
                String.format("/medialists/%s/@details", list.getName())
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
     * Updates a localized description value of an media list.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifier of the list.
     * @param localeParam         The locale of the description value to update.
     * @param value               The new description value.
     *
     * @return A redirect to the details view of the media list.
     */
    @POST
    @Path(
        "/medialists/{mediaListIdentifier}/description/@edit/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateAttachmentListDescription(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("mediaListIdentifier")
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
            final Optional<AttachmentList> listResult = findMediaList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showMediaListNotFound(listIdentifierParam);
            }

            final AttachmentList list = listResult.get();
            list.getDescription().addValue(new Locale(localeParam), value);
            listRepo.save(list);

            return buildRedirectPathForStep(
                String.format("/medialists/%s/@details", list.getName())
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
     * Removes a localized description value of an media list.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifier of the list.
     * @param localeParam         The locale of the description value to remove.
     *
     * @return A redirect to the details view of the media list.
     */
    @POST
    @Path(
        "/medialists/{mediaListIdentifier}/description/@remove/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeAttachmentListDescription(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("mediaListIdentifier")
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
            final Optional<AttachmentList> listResult = findMediaList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showMediaListNotFound(listIdentifierParam);
            }

            final AttachmentList list = listResult.get();
            list.getDescription().removeValue(new Locale(localeParam));
            listRepo.save(list);

            return buildRedirectPathForStep(
                String.format("/medialists/%s/@details", list.getName())
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
     * @param listIdentifierParam  The identifier of the list to which the
     *                             attachment is added.
     * @param mediaIdentifierParam The identifier of the media asset to use for
     *                             the media attachment.
     *
     * @return A redirect to the list of media lists and mediaa.
     */
    @POST
    @Path("/medialists/{mediaListIdentifier}/media/@create")
    @Transactional(Transactional.TxType.REQUIRED)
    public String linkMedia(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("mediaListIdentifier")
        final String listIdentifierParam,
        @FormParam("mediaIdentifier")
        final String mediaIdentifierParam
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
            final Optional<AttachmentList> listResult = findMediaList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showMediaListNotFound(listIdentifierParam);
            }
            final AttachmentList list = listResult.get();

            final Optional<Asset> assetResult;
            final Identifier assetIdentifier = identifierParser.parseIdentifier(
                mediaIdentifierParam
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
                models.put("assetUuid", mediaIdentifierParam);
                return "org/librecms/ui/contentsection/documents/media-not-found.xhtml";
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
     * Removes a media attachment from an {@link AttachmentList}.The
     * {@link Asset} of the attachment will not be deleted.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifier of the {@link AttachmentList}
     *                            to which the attachment belongs.
     * @param attachmentUuid      The UUID of the attachment to remove.
     * @param confirmed           The value of the {@code confirm} parameter. If
     *                            the value anything other than the string
     *                            {@code true} the method does nothing.
     *
     * @return
     */
    @POST
    @Path(
        "/medialists/{mediaListIdentifier}/attachments/{attachmentUuid}/@remove")
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeAttachment(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("mediaListIdentifier")
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
            final Optional<AttachmentList> listResult = findMediaList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showMediaListNotFound(listIdentifierParam);
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
     * Move an media list one position up.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param listIdentifierParam The identifer of the list to move.
     *
     * @return A redirect to list of attachment lists.
     */
    @POST
    @Path(
        "/medialists/{mediaListListIdentifier}/@moveUp")
    @Transactional(Transactional.TxType.REQUIRED)
    public String moveListUp(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("mediaListIdentifier")
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
            final Optional<AttachmentList> listResult = findMediaList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showMediaListNotFound(listIdentifierParam);
            }
            final AttachmentList list = listResult.get();

            listManager.moveUp(list);

            return buildRedirectPathForStep(
                String.format("/medialists/%s/@details", list.getName())
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
    @Path("/medialists/{mediaListIdentifier}/@moveDown")
    @Transactional(Transactional.TxType.REQUIRED)
    public String moveListDown(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("mediaListIdentifier")
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
            final Optional<AttachmentList> listResult = findMediaList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showMediaListNotFound(listIdentifierParam);
            }
            final AttachmentList list = listResult.get();

            listManager.moveDown(list);

            return buildRedirectPathForStep(
                String.format("/medialists/%s/@details", list.getName())
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
        "/medialists/{mediaListIdentifier}/attachments/{attachmentUuid}/@moveUp")
    @Transactional(Transactional.TxType.REQUIRED)
    public String moveAttachmentUp(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("mediaListIdentifier")
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
            final Optional<AttachmentList> listResult = findMediaList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showMediaListNotFound(listIdentifierParam);
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
                String.format("/medialists/%s/@details", list.getName())
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
        "/medialists/{mediaListIdentifier}/attachments/{attachmentUuid}/@moveDown")
    @Transactional(Transactional.TxType.REQUIRED)
    public String moveAttachmentDown(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("mediaListIdentifier")
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
            final Optional<AttachmentList> listResult = findMediaList(
                listIdentifierParam
            );
            if (!listResult.isPresent()) {
                return showMediaListNotFound(listIdentifierParam);
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
                String.format("/medialists/%s/@details", list.getName())
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
     * A helper function to find a media list.
     *
     * @param mediaListIdentifier The idenfifier of the attachment list.
     *
     * @return An {@link Optional} with the attachment list or an empty optional
     *         if the current content item has no list with the provided
     *         identifier.
     */
    private Optional<AttachmentList> findMediaList(
        final String mediaListIdentifier
    ) {
        final ContentItem document = getDocument();
        final Identifier identifier = identifierParser.parseIdentifier(
            mediaListIdentifier
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
     * Show the "media list not found" error page.
     *
     * @param listIdentifier The identifier of the list that was not found.
     *
     * @return The template for the "attachment list not found" page.
     */
    private String showMediaListNotFound(final String listIdentifier) {
        models.put("contentItem", getDocumentPath());
        models.put("listIdentifier", listIdentifier);
        return "org/librecms/ui/contentsection/documents/medialist-not-found.xhtml";
    }

    private MediaListDto buildMediaListDto(
        final AttachmentList attachmentList
    ) {
        final MediaListDto dto = new MediaListDto();
        dto.setMedia(
            attachmentList
                .getAttachments()
                .stream()
                .map(this::buildMediaDto)
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
        dto.setOrder(attachmentList.getListOrder());
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
     * @param mediaAttachment The {@link ItemAttachment} from which the
     *                       {@link ItemAttachmentDto} is build.
     *
     * @return The {@link MediaDto}.
     */
    private MediaDto buildMediaDto(
        final ItemAttachment<?> mediaAttachment
    ) {
        final MediaDto dto = new MediaDto();
        dto.setAssetType(
            Optional
                .ofNullable(mediaAttachment.getAsset())
                .map(Asset::getClass)
                .map(clazz -> assetTypesManager.getAssetTypeInfo(clazz))
                .map(info -> info.getAssetClass().getName())
                .orElse("")
        );
        dto.setAssetTypeLabel(
            Optional
                .ofNullable(mediaAttachment.getAsset())
                .map(Asset::getClass)
                .map(clazz -> assetTypesManager.getAssetTypeInfo(clazz))
                .map(
                    info -> globalizationHelper.getLocalizedTextsUtil(
                        info.getLabelBundle()).getText(info.getLabelKey())
                ).orElse("")
        );
        dto.setAssetUuid(
            Optional
                .ofNullable(mediaAttachment.getAsset())
                .map(Asset::getUuid)
                .orElse(null)
        );
        dto.setAttachmentId(mediaAttachment.getAttachmentId());
        dto.setSortKey(mediaAttachment.getSortKey());
        dto.setTitle(
            Optional
                .ofNullable(mediaAttachment.getAsset())
                .map(
                    asset -> globalizationHelper.getValueFromLocalizedString(
                        asset.getTitle()
                    )
                )
                .orElse("")
        );
        dto.setUuid(mediaAttachment.getUuid());
        return dto;
    }

}
