/**
 * Add your license here, for example LGPL
 */
package org.librecms;

import com.arsdigita.cms.ContentCenterAppCreator;
import com.arsdigita.cms.ContentCenterServlet;
import com.arsdigita.cms.ContentCenterSetup;
import com.arsdigita.cms.ui.pagemodel.CategorizedItemComponentForm;
import com.arsdigita.cms.ui.pagemodel.CategoryTreeComponentForm;
import com.arsdigita.cms.ui.pagemodel.FixedContentItemComponentForm;
import com.arsdigita.cms.ui.pagemodel.GreetingItemComponentForm;
import com.arsdigita.cms.ui.pagemodel.ItemListComponentForm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.core.CoreConstants;
import org.libreccm.modules.CcmModule;
import org.libreccm.modules.InitEvent;
import org.libreccm.modules.InstallEvent;
import org.libreccm.modules.Module;
import org.libreccm.modules.RequiredModule;
import org.libreccm.modules.ShutdownEvent;
import org.libreccm.modules.UnInstallEvent;
import org.libreccm.pagemodel.PageModelComponentModel;
import org.libreccm.ui.admin.contentsections.ContentSectionApplicationController;
import org.libreccm.web.ApplicationType;
import org.libreccm.web.CcmApplication;
import org.librecms.assets.AssetTypes;
import org.librecms.assets.AudioAsset;
import org.librecms.assets.Bookmark;
import org.librecms.assets.ExternalAudioAsset;
import org.librecms.assets.ExternalVideoAsset;
import org.librecms.assets.FileAsset;
import org.librecms.assets.Image;
import org.librecms.assets.LegalMetadata;
import org.librecms.assets.Organization;
import org.librecms.assets.Person;
import org.librecms.assets.PostalAddress;
import org.librecms.assets.SideNote;
import org.librecms.assets.VideoAsset;

import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionCreator;
import org.librecms.contentsection.ContentSectionSetup;
import org.librecms.contentsection.ui.admin.ApplicationInstanceForm;
import org.librecms.contentsection.ui.admin.SettingsPane;
import org.librecms.contenttypes.Article;
import org.librecms.contenttypes.ContentTypes;
import org.librecms.contenttypes.Event;
import org.librecms.contenttypes.MultiPartArticle;
import org.librecms.contenttypes.News;
import org.librecms.pagemodel.CategorizedItemComponent;
import org.librecms.pagemodel.CategoryTreeComponent;
import org.librecms.pagemodel.FixedContentItemComponent;
import org.librecms.pagemodel.GreetingItemComponent;
import org.librecms.pagemodel.ItemListComponent;
import org.librecms.pages.Pages;
import org.librecms.pages.PagesCreator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Module(//packageName = "org.librecms.cms",
    requiredModules = {
        @RequiredModule(module = org.libreccm.core.CcmCore.class)
    },
    applicationTypes = {
        @ApplicationType(
            name = CmsConstants.CONTENT_CENTER_APP_TYPE,
            applicationClass = CcmApplication.class,
            descBundle = CmsConstants.CONTENT_CENTER_DESC_BUNDLE,
            creator = ContentCenterAppCreator.class,
            servlet = ContentCenterServlet.class
        ),
        @ApplicationType(
            name = CmsConstants.CONTENT_SECTION_APP_TYPE,
            applicationClass = ContentSection.class,
            instanceForm = ApplicationInstanceForm.class,
            settingsPane = SettingsPane.class,
            descBundle = CmsConstants.CONTENT_SECTION_DESC_BUNDLE,
            creator = ContentSectionCreator.class,
            servletPath = "/templates/servlet/content-section",
            applicationController = ContentSectionApplicationController.class
        ),
        @ApplicationType(
            name = "org.librecms.pages.Pages",
            applicationClass = Pages.class,
            instanceForm = ApplicationInstanceForm.class,
            settingsPane = SettingsPane.class,
            descBundle = CmsConstants.CMS_BUNDLE,
            titleKey = "pages_application.title",
            creator = PagesCreator.class,
            servletPath = "/templates/servlet/pages"
        )
    },
    configurations = {
        org.librecms.CMSConfig.class,
        org.librecms.assets.BinaryAssetConfig.class
    },
    pageModelComponentModels = {
        @PageModelComponentModel(
            modelClass = CategorizedItemComponent.class,
            editor = CategorizedItemComponentForm.class,
            descBundle = CmsConstants.CMS_BUNDLE,
            titleKey
            = "cms.ui.pagemodel.components.categorized_item_component.title",
            descKey
            = "cms.ui.pagemodel.components.categorized_item_component.desc"),
        @PageModelComponentModel(
            modelClass = CategoryTreeComponent.class,
            editor = CategoryTreeComponentForm.class,
            descBundle = CmsConstants.CMS_BUNDLE,
            titleKey
            = "cms.ui.pagemodel.components.category_tree_component.title",
            descKey = "cms.ui.pagemodel.components.category_tree_component.desc"),
        @PageModelComponentModel(
            modelClass = FixedContentItemComponent.class,
            editor = FixedContentItemComponentForm.class,
            descBundle = CmsConstants.CMS_BUNDLE,
            titleKey
            = "cms.ui.pagemodel.components.fixed_contentitem_component.title",
            descKey
            = "cms.ui.pagemodel.components.fixed_contentitem_component.desc"),
        @PageModelComponentModel(
            modelClass = GreetingItemComponent.class,
            editor = GreetingItemComponentForm.class,
            descBundle = CmsConstants.CMS_BUNDLE,
            titleKey
            = "cms.ui.pagemodel.components.greetingitem_component.title",
            descKey = "cms.ui.pagemodel.components.greetingitem_component.desc"),
        @PageModelComponentModel(
            modelClass = ItemListComponent.class,
            editor = ItemListComponentForm.class,
            descBundle = CmsConstants.CMS_BUNDLE,
            titleKey = "cms.ui.pagemodel.components.itemlist_component.title",
            descKey = "cms.ui.pagemodel.components.itemlist_component.desc")
    }
)
@ContentTypes({Article.class,
               Event.class,
               MultiPartArticle.class,
               News.class
})
@AssetTypes({AudioAsset.class,
             VideoAsset.class,
             Bookmark.class,
             ExternalVideoAsset.class,
             ExternalAudioAsset.class,
             FileAsset.class,
             Image.class,
             LegalMetadata.class,
             Organization.class,
             Person.class,
             PostalAddress.class,
             SideNote.class
})
public class Cms implements CcmModule {

    private static final Logger LOGGER = LogManager.getLogger(Cms.class);

    @Override
    public void install(final InstallEvent event) {
        //ToDo Create initial data for the module if neccessary

        // Create initial content section(s)
        // If a list of sections is provided in the integration properties
        // use that list, otherwise create a content section named 'info'.
        LOGGER.info("Trying to load integration properties...");
        final Properties integrationProps = new Properties();
        try (final InputStream inputStream = getClass().getResourceAsStream(
            CoreConstants.INTEGRATION_PROPS)) {

            if (inputStream == null) {
                LOGGER.warn("No integration.properties available.");
            } else {
                integrationProps.load(inputStream);
            }
        } catch (IOException ex) {
            LOGGER.warn(
                "Failed to load integration properties. Using default values.",
                ex);
        }

        LOGGER.info("Setting content center...");
        final ContentCenterSetup contentCenterSetup = new ContentCenterSetup(
            event);
        contentCenterSetup.setup();

        LOGGER.info("Setting up content sections...");
        final ContentSectionSetup contentSectionSetup = new ContentSectionSetup(
            event);
        contentSectionSetup.setup();

        // Map given domains to content section(s)
        // Also create the following roles for each content section and assign
        // the permissions
        // 
        // * Alert Recipient
        // * Author         : Categorize Items, Create New Items, Edit Items, View Published Items, Preview Items
        // * Editor         : Categorize Items, Create New Items, Edit Items, Approve Items, Delete Items, View Published Items, Preview Items
        // * Manager        : Administer Roles, Administer Workflow, Administer Lifecycles, Administer Categories, Administer Content Types, Categorize Items, Create New Items, Edit Items, Approve Items, Publish Items, Delete Items, View Published Items, Preview Items
        // * Publisher      : Categorize Items, Create New Items, Edit Items, Approve Items, Publish Items, Delete Items, View Published Items, Preview Items
        // * [Thrusted User]: Categorize Items, Create New Items, Edit Items, Apply Alternate Workflows, Approve Items, Publish Items, Delete Items, View Published Items, Preview Items
        // * Content Reader: View Published Items
    }

//    private void createContentSection(final String contentSectionName) {
//        final ContentSection section = new ContentSection();
//        section.setLabel(contentSectionName);
//
//        final Category rootFolder = new Category();
//        rootFolder.setName(String.format("%s_root", contentSectionName));
//
//    }
    @Override
    public void init(final InitEvent event) {
        //ToDo Add initialisation logic necessary for your module
    }

    @Override
    public void shutdown(final ShutdownEvent event) {
        //ToDo Add shutdown logic if necessary
    }

    @Override
    public void uninstall(final UnInstallEvent event) {
        //ToDo Remove module data
    }

}
