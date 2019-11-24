/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.profilesite.pagemodel;

import com.arsdigita.kernel.KernelConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.CcmObject;
import org.libreccm.pagemodel.ComponentRenderer;
import org.libreccm.pagemodel.RendersComponent;
import org.libreccm.security.PermissionChecker;
import org.librecms.assets.Person;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemL10NManager;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemVersion;
import org.librecms.contentsection.privileges.ItemPrivileges;
import org.librecms.pagemodel.assets.AbstractAssetRenderer;
import org.librecms.pagemodel.assets.AssetRenderers;
import org.librecms.profilesite.ProfileSiteItem;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import static org.librecms.pages.PagesConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@RendersComponent(componentModel = ProfileSiteComponent.class)
public class ProfileSiteComponentRenderer
    implements ComponentRenderer<ProfileSiteComponent> {

    private static final Logger LOGGER = LogManager.getLogger(
        ProfileSiteComponentRenderer.class
    );

    @Inject
    private AssetRenderers assetRenderers;

    @Inject
    private CategoryRepository categoryRepo;

    @Inject
    private CategoryManager categoryManager;

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private ContentItemL10NManager iteml10nManager;

    @Inject
    private Instance<ProfileSiteContentRenderer> contentRenderers;

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private PermissionChecker permissionChecker;

    @Override
    public Map<String, Object> renderComponent(
        final ProfileSiteComponent componentModel,
        final Map<String, Object> parameters
    ) {
        Objects.requireNonNull(componentModel);
        Objects.requireNonNull(parameters);

        if (!parameters.containsKey(PARAMETER_CATEGORY)) {
            throw new IllegalArgumentException(
                String.format(
                    "The parameters map passed to this component does "
                        + "not include the parameter \"%s\"",
                    PARAMETER_CATEGORY
                )
            );
        }

        if (!(parameters.get(PARAMETER_CATEGORY) instanceof Category)) {
            throw new IllegalArgumentException(
                String.format(
                    "The parameters map passed to this ProfileSiteComponent "
                        + "component contains the parameter \"category\", but "
                        + "parameter is not of type \"%s\" but of type \"%s\".",
                    Category.class.getName(),
                    parameters.get(PARAMETER_CATEGORY).getClass().getName()
                )
            );
        }

        final Category category = categoryRepo
            .findById(
                ((CcmObject) parameters.get(PARAMETER_CATEGORY)).getObjectId()
            )
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "No category with ID %d in the database.",
                        ((CcmObject) parameters.get(PARAMETER_CATEGORY))
                            .getObjectId()
                    )
                )
            );

        final Optional<CcmObject> indexObj = categoryManager
            .getIndexObject(category)
            .stream()
            .filter(object -> object instanceof ContentItem)
            .filter(item -> {
                return ((ContentItem) item)
                    .getVersion() == ContentItemVersion.LIVE;
            })
            .findFirst();

        if (indexObj.isPresent()) {

            if (!(indexObj.get() instanceof ProfileSiteItem)) {
                LOGGER.debug(
                    "The index item of the category {} is not an item of "
                        + "the class",
                    ProfileSiteItem.class.getName()
                );
                return Collections.emptyMap();
            }

            final ProfileSiteItem profileSiteItem = (ProfileSiteItem) indexObj
                .get();

            if (Boolean.TRUE.equals(parameters.get("showDraft"))) {
                final ProfileSiteItem draftItem = itemManager
                    .getDraftVersion(profileSiteItem, profileSiteItem.getClass());

                if (permissionChecker.isPermitted(
                    ItemPrivileges.PREVIEW, draftItem
                )) {
                    final Map<String, Object> result = generateItem(
                        componentModel, parameters, draftItem
                    );
                    result.put("showDraft", Boolean.TRUE);

                    return result;
                } else {
                    throw new WebApplicationException(
                        "You are not permitted to view the draft version of "
                            + "this profile site.",
                        Response.Status.UNAUTHORIZED
                    );
                }
            } else {
                final ProfileSiteItem liveItem = itemManager
                    .getLiveVersion(profileSiteItem, profileSiteItem.getClass())
                    .orElseThrow(
                        () -> new NotFoundException(
                            "This content item does not have a live version."
                        )
                    );

                if (permissionChecker.isPermitted(
                    ItemPrivileges.VIEW_PUBLISHED, liveItem
                )) {
                    return generateItem(
                        componentModel, parameters, liveItem
                    );
                } else {
                    throw new WebApplicationException(
                        "You are not permitted to view the live version of "
                            + "this profile site.",
                        Response.Status.UNAUTHORIZED);
                }
            }
        } else {
            LOGGER.debug("The category {} does not have a index item.",
                         Objects.toString(category));
            return Collections.emptyMap();
        }
    }

    protected Map<String, Object> generateItem(
        final ProfileSiteComponent componentModel,
        final Map<String, Object> parameters,
        final ProfileSiteItem profileSiteItem
    ) {
        final Category category = (Category) parameters.get(PARAMETER_CATEGORY);
        final String categoryName = category.getName();

        final Optional<ProfileSiteContentRenderer> result = contentRenderers
            .stream()
            .filter(renderer -> categoryName.equals(renderer.getCategoryName()))
            .findAny();

        if (result.isPresent()) {
            return result.get().renderContent(
                componentModel, parameters, profileSiteItem
            );
        } else {
            return renderProfileSiteIndexPage(parameters, profileSiteItem);
        }
    }

    private Map<String, Object> renderProfileSiteIndexPage(
        final Map<String, Object> parameters,
        final ProfileSiteItem profileSiteItem
    ) {
        final Locale language;
        if (parameters.containsKey("language")) {
            language = new Locale((String) parameters
                .get(PARAMETER_LANGUAGE));
        } else {
            final KernelConfig kernelConfig = confManager
                .findConfiguration(KernelConfig.class);
            language = kernelConfig.getDefaultLocale();
        }

        if (iteml10nManager.hasLanguage(profileSiteItem, language)) {

            final Map<String, Object> result = new HashMap<>();

            result.put(
                "owner", renderOwner(profileSiteItem.getOwner(), language)
            );
            result.put(
                "position", profileSiteItem.getPosition().getValue(language)
            );
            result.put(
                "interests", profileSiteItem.getInterests().getValue(language)
            );
            result.put(
                "misc", profileSiteItem.getMisc().getValue(language)
            );

            return result;
        } else {
            throw new NotFoundException(
                "Requested language is not available.");
        }
    }

    private Map<String, Object> renderOwner(
        final Person owner, final Locale language
    ) {
        final AbstractAssetRenderer renderer = assetRenderers.findRenderer(
            owner.getClass()
        );

        return renderer.render(owner, language);
    }

}
