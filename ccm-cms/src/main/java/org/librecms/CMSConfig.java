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
package org.librecms;

import com.arsdigita.bebop.form.DHTMLEditor;
import com.arsdigita.cms.ui.authoring.ItemCategoryExtension;
import com.arsdigita.london.terms.ui.ItemCategoryPicker;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.Configuration;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.configuration.Setting;
import org.libreccm.core.UnexpectedErrorException;
import org.librecms.dispatcher.ItemResolver;
import org.librecms.dispatcher.SimpleItemResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Configuration(
    descBundle = "com.arsdigita.cms.CMSConfig",
    descKey = "description",
    titleKey = "title"
)
public class CMSConfig {

    /**
     * Path for the default item template. Path is relative to the Template Root
     * path.
     */
    @Setting(
        descKey = "defaultItemTemplatePath.desc",
        labelKey = "defaultItemTemplatePath.label"
    )
    private String defaultItemTemplatePath = "/default/item.jsp";

    /**
     * Path for the default folder template. Path is relative to the Template
     * Root path.
     */
    @Setting(
        descKey = "defaultFolderTemplatePath.desc",
        labelKey = "defaultFolderTemplatePath.label"
    )
    private String defaultFolderTemplatePath = "/default/folder.jsp";

    /**
     * Path or the root folder for template folders. Path is relative to webapp
     * root. Modify with care! Usually modified by developers only!
     */
    @Setting(
        descKey = "templateRootPath.desc",
        labelKey = "templateRootPath.label"
    )
    private String templateRootPath = "/templates/ccm-cms/content-section/";

    /**
     * Item Adapters File, path to an XML resource containing adapter
     * specifications. Path is relative to webapp root.
     */
    @Setting(
        descKey = "itemAdapters.desc",
        labelKey = "itemAdapters.label"
    )
    private String itemAdapters = "/WEB-INF/resources/cms-item-adapters.xml";

    /**
     * Use streamlined content creation: upon item creation, automatically open
     * authoring steps and forward to the next step
     */
    @Setting(
        descKey = "useStreamlinedCreation.desc",
        labelKey = "useStreamlinedCreation.label"
    )
    private boolean useStreamlinedCreation = true;

    /**
     * DHTML Editor Configuration for use in CMS module, lists the configuration
     * object name and Javascript source location for its definition.
     */
    @Setting(
        descKey = "dhtmlEditorConfig.desc",
        labelKey = "dhtmlEditorConfig.label"
    )
    private List<String> dhtmlEditorConfig = Arrays.asList(new String[]{
        "TinyMCE.Config", "scripts/ccm-cms/tinymce-loader.js"
    });
//        "TinyMCE.Config", "scripts/dist/tinymce/tinymce_cms_config.js"});

    /**
     * Defines which plugins to use, e.g.TableOperations,CSS Format:
     * [string,string,string]
     */
    @Setting(
        descKey = "dhtmlEditorPlugins.desc",
        labelKey = "dhtmlEditorPlugins.label"
    )
    private List<String> dhtmlEditorPlugins = Arrays.asList(new String[]{
        //        "scripts/ccm-cms/tinymce/plugins/insertimage.js"
        "http://localhost/web/ccm-cms-tinymce/insertimage.js"
    });

    /**
     * Prevent undesirable functions from being made available, eg images should
     * only be added through the cms methods.
     */
    @Setting(
        descKey = "dhtmlEditorHiddenButtons.desc",
        labelKey = "dhtmlEditorHiddenButtons.label"
    )
    private List<String> dhtmlEditorHiddenButtons = Collections.emptyList();

    /**
     * Hide section admin tabs from users without administrative rights.
     */
    @Setting(
        descKey = "hideAdminTabs.desc",
        labelKey = "hideAdminTabs.label"
    )
    private boolean hideAdminTabs = true;

    /**
     * Hide Folder Index Checkbox from folder view
     */
    @Setting(
        descKey = "hideFolderIndexCheckbox.desc",
        labelKey = "hideFolderIndexCheckbox.label"
    )
    private boolean hideFolderIndexCheckbox = true;

    /**
     * Hide launch date parameter on all forms and displays where it's used.
     */
    @Setting(
        descKey = "hideLaunchDate.desc",
        labelKey = "hideLaunchDate.label"
    )
    private boolean hideLaunchDate = true;

    /**
     * Require the launch date parameter to be set by the content author.
     */
    @Setting(
        descKey = "requireLaunchDate.desc",
        labelKey = "requireLaunchDate.label"
    )
    private boolean requireLaunchDate = true;

    /**
     * Hide the templates tab on the item admin page.
     */
    @Setting(
        descKey = "hideTemplatesTab.desc",
        labelKey = "hideTemplatesTab.label"
    )
    private boolean hideTemplatesTab = false;

    /**
     * Hide the upload file link in the editing of a text asset.
     */
    @Setting(
        descKey = "hideTextAssetUploadFile.desc",
        labelKey = "hideTextAssetUploadFile.label"
    )
    private boolean hideTextAssetUploadFile = false;

    /**
     * Hide timezone labels (if, for example, all users will be in the same
     * timezone and such information would be unnecessary)
     */
    @Setting(
        descKey = "hideTimezone.desc",
        labelKey = "hideTimezone.label"
    )
    private boolean hideTimezone = false;

    /**
     * Whether the Wysiwyg editor should clear the text of MSWord tags,
     * everytime the user clicks on 'Save'
     */
    @Setting(
        descKey = "saveTextCleansWordTags.desc",
        labelKey = "saveTextCleansWordTags.label"
    )
    private boolean saveTextCleansWordTags = true;

    /**
     * Get the search indexing not to process FileAssets, eg to avoid PDF
     * slowdowns
     */
    @Setting(
        descKey = "disableFileAssetExtraction.desc",
        labelKey = "disableFileAssetExtraction.label"
    )
    private boolean disableFileAssetExtraction = false;

    /**
     * Whether an item's workflow should be deleted, once the item has been
     * (re)published.
     *
     * jensp 2014-11-07: Default changed from true to false. Deleting the
     * assigned workflow means that the authors have to reattach a workflow
     * using the Workflow tab, which is complicated (for some users too
     * complicated). Also deleting the workflow means that the new convenient
     * link to restart a workflow will not work.
     *
     */
    @Setting(
        descKey = "deleteWorkflowAfterPublication.desc",
        labelKey = "deleteWorkflowAfterPublication.label"
    )
    private boolean deleteWorkflowAfterPublication = false;

    /**
     * Defines the number of days ahead that are covered in the 'Soon Expired'
     * tab
     */
    @Setting(
        descKey = "soonExpiredTimespanDays.desc",
        labelKey = "soonExpiredTimespanDays.label"
    )
    private int soonExpiredTimespanDays = 14;

    /**
     * Defines the number of months ahead that are covered in the 'Soon Expired'
     * tab
     */
    @Setting(
        descKey = "soonExpiredTimespanMonths.desc",
        labelKey = "soonExpiredTimespanMonths.label"
    )
    private int soonExpiredTimespanMonths = 1;

    /**
     * Does a redirect to the unpublished item generate not found error?
     */
    @Setting(
        descKey = "unpublishedNotFound.desc",
        labelKey = "unpublishedNotFound.label"
    )
    private boolean unpublishedNotFound = true;

    /**
     * Links created through browse interfaces should only be within the same
     * subsite
     */
    @Setting(
        descKey = "linksOnlyInSameSubsite.desc",
        labelKey = "linksOnlyInSameSubsite.label"
    )
    private boolean linksOnlyInSameSubsite = false;

    /**
     * Link available to reset lifecycle on republish. If false don't display
     * the link otherwise display.
     */
    @Setting(
        descKey = "hideResetLifecycleLink.desc",
        labelKey = "hideResetLifecycleLink.label"
    )
    private boolean hideResetLifecycleLink = true;

    /**
     * Whether to include INPATH operators to contains clause in intermedia
     * search
     */
    @Setting(
        descKey = "scoreTitleAndKeywords.desc",
        labelKey = "scoreTitleAndKeywords.label"
    )
    private boolean scoreTitleAndKeywords = false;

    /**
     * Title Weight, the relative weight given to title element within cms:item
     * when ranking search results (only used by interMedia)
     */
    @Setting(
        descKey = "titleWeight.desc",
        labelKey = "titleWeight.label"
    )
    private int titleWeight = 1;

    /**
     * Keyword Weight, the relative weight given to the dcKeywords element
     * within dublinCore element within cms:item element when ranking search
     * results (only used by interMedia)
     */
    @Setting(
        descKey = "keywordWeight.desc",
        labelKey = "keywordWeight.label"
    )
    private int keywordWeight = 1;

    /**
     * Limit the item search to current content section
     */
    @Setting(
        descKey = "limitItemSearchToContentSection.desc",
        labelKey = "limitItemSearchToContentSection.label"
    )
    private boolean limitItemSearchToContentSection = true;

    /**
     * Asset steps to skip, specify asset steps that are not relevant for
     * specific content types. Each entry in the list is a : separated pair. The
     * first string is the className for the type (refer to classname column in
     * contenttypes table eg com.arsdigita.cms.contenttypes.MultiPartArticle
     * Second string is the name of the bebop step component eg
     * com.arsdigita.cms.contenttypes.ui.ImageStep
     */
    @Setting(
        descKey = "skipAssetSteps.desc",
        labelKey = "skipAssetSteps.label"
    )
    private List<String> skipAssetSteps = Collections.emptyList();

    /**
     * Mandatory Descriptions Content types may refer to this to decide whether
     * to validate against empty descriptions
     */
    @Setting(
        descKey = "mandatoryDescriptions.desc",
        labelKey = "mandatoryDescriptions.label"
    )
    private boolean mandatoryDescriptions = false;

    /**
     * Delete Finished Lifecycles. Decide whether lifecycles and their phases
     * should be deleted from the system when finished.
     */
    @Setting(
        descKey = "deleteLifecycleWhenComplete.desc",
        labelKey = "deleteLifecycleWhenComplete.label"
    )
    private boolean deleteLifecycleWhenComplete = false;

    /**
     * Delete Sent Workflow Notifications. Decide whether successfully sent
     * notifications and messages should be deleted from the system
     */
    @Setting(
        descKey = "deleteWorkflowNotificationWhenSend.desc",
        labelKey = "deleteWorkflowNotificationWhenSend.label"
    )
    private boolean deleteWorkflowNotificationWhenSend = false;

    /**
     * Decide whether successfully sent notifications and messages should be
     * deleted from the system
     */
    @Setting(
        descKey = "deleteExpiryNotificationsWhenSent.desc",
        labelKey = "deleteExpiryNotificationsWhenSent.label"
    )
    private boolean deleteExpiryNotificationsWhenSent = false;

    /**
     * Amount of time (in hours) before the expiration of a content item that
     * users in the Alert Recipient role are alerted via email
     */
    @Setting(
        descKey = "defaultNotificationTime.desc",
        labelKey = "defaultNotificationTime.label"
    )
    private int defaultNotificationTime = 0;

    /**
     * Whether a content item's author should be notified by the item's
     * LifecycleListener; defaults to true
     */
    @Setting(
        descKey = "notifyAuthorOnLifecycle.desc",
        labelKey = "notifyAuthorOnLifecycle.label"
    )
    private boolean notifyAuthorOnLifecycle = false;

    /**
     * XML Mapping of the content center tabs to URLs, see
     * {@link ContentCenterDispatcher}
     */
    @Setting(
        descKey = "contentCenterMap.desc",
        labelKey = "contentCenterMap.label"
    )
    private String contentCenterMap
        = "/WEB-INF/resources/content-center-map.xml";

    @Setting(
        descKey = "defaultItemResolverClassNames.desc",
        labelKey = "defaultItemResolverClassNames.label"
    )
    private List<String> defaultItemResolverClassNames = Arrays.asList(
        new String[]{
            SimpleItemResolver.class.getName()
        });

//    @Setting
//    private List<String> defaultTemplateResolverClassNames = Arrays.asList(
//        new String[]{
//            DefaultTemplateResolver.class.getName(),
//            TemplateResolver.class.getName()
//        });
    @Setting(
        descKey = "itemSearchDefaultTab.desc",
        labelKey = "itemSearchDefaultTab.label"
    )
    private String itemSearchDefaultTab = "flatBrowse";

    @Setting(
        descKey = "itemSearchFlatBrowsePanePageSize.desc",
        labelKey = "itemSearchFlatBrowsePanePageSize.label"
    )
    private int itemSearchFlatBrowsePanePageSize = 20;

    @Setting(
        descKey = "folderBrowseListSize.desc",
        labelKey = "folderBrowseListSize.label"
    )
    private int folderBrowseListSize = 20;

    @Setting(
        descKey = "folderAtoZShowLimit.desc",
        labelKey = "folderAtoZShowLimit.label"
    )
    private int folderAtoZShowLimit = 100;

    @Setting(
        descKey = "useOldStyleItemLifecycleItemPane.desc",
        labelKey = "useOldStyleItemLifecycleItemPane.label"
    )
    private boolean useOldStyleItemLifecycleItemPane = false;

    @Setting(
        descKey = "threadPublishing.desc",
        labelKey = "threadPublishing.label"
    )
    private boolean threadPublishing = false;

    @Setting(
        descKey = "publishingFailureSender.desc",
        labelKey = "publishingFailureSender.label"
    )
    private String publishingFailureSender = "";

    @Setting(
        descKey = "publishingFailureReceiver.desc",
        labelKey = "publishingFailureReceiver.label"
    )
    private String publishingFailureReceiver = "";

    @Setting(
        descKey = "imageBrowserThumbnailMaxWidth.desc",
        labelKey = "imageBrowserThumbnailMaxWidth.label"
    )
    private int imageBrowserThumbnailMaxWidth = 50;

    @Setting(
        descKey = "imageBrowserThumbnailMaxHeight.desc",
        labelKey = "imageBrowserThumbnailMaxHeight.label"
    )
    private int imageBrowserThumbnailMaxHeight = 50;

    @Setting(
        descKey = "imageBrowserCaptionSize.desc",
        labelKey = "imageBrowserCaptionSize.label"
    )
    private int imageBrowserCaptionSize = 50;

    @Setting(
        descKey = "imageBrowserDescriptionSize.desc",
        labelKey = "imageBrowserDescriptionSize.label"
    )
    private int imageBrowserDescriptionSize = 400;

    @Setting(
        descKey = "imageBrowserTitleSize.desc",
        labelKey = "imageBrowserTitleSize.label"
    )
    private int imageBrowserTitleSize = 200;

    @Setting(
        descKey = "imageCacheEnabled.desc",
        labelKey = "imageCacheEnabled.label"
    )
    private boolean imageCacheEnabled = true;

    @Setting(
        descKey = "imageCachePrefetchEnabled.desc",
        labelKey = "imageCachePrefetchEnabled.label"
    )
    private boolean imageCachePrefetchEnabled = false;

    @Setting(
        descKey = "imageCacheMaxSize.desc",
        labelKey = "imageCacheMaxSize.label"
    )
    private int imageCacheMaxSize = 100;

    @Setting(
        descKey = "imageCacheMaxAge.desc",
        labelKey = "imageCacheMaxAge.label"
    )
    private int imageCacheMaxAge = 300;

    @Setting(
        descKey = "attachPersonOrgaUnitsStep.desc",
        labelKey = "attachPersonOrgaUnitsStep.label"
    )
    private boolean attachPersonOrgaUnitsStep = true;

    @Setting(
        descKey = "personOrgaUnitsStepSortKey.desc",
        labelKey = "personOrgaUnitsStepSortKey.label"
    )
    private int personOrgaUnitsStepSortKey = 20;

    @Setting(
        descKey = "enableXmlCache.desc",
        labelKey = "enableXmlCache.label"
    )
    private boolean enableXmlCache = false;

    @Setting(
        descKey = "xmlCacheSize.desc",
        labelKey = "xmlCacheSize.label"
    )
    private int xmlCacheSize = 2500;

    @Setting(
        descKey = "xmlCacheAge.desc",
        labelKey = "xmlCacheAge.label"
    )
    private int xmlCacheAge = 60 * 60 * 24;

    @Setting(
        descKey = "categoryAuthoringAddForm.desc",
        labelKey = "categoryAuthoringAddForm.label"
    )
    private String categoryAuthoringAddForm = ItemCategoryPicker.class.getName();

    @Setting(
        descKey = "categoryAuthoringExtension.desc",
        labelKey = "categoryAuthoringExtension.label"
    )
    private String categoryAuthoringExtension = ItemCategoryExtension.class
        .getName();

    @Setting(
        descKey = "categoryPickerAjaxExpandAll.desc",
        labelKey = "categoryPickerAjaxExpandAll.label"
    )
    private boolean categoryPickerAjaxExpandAll = false;

    /**
     * Max length of the description of a link (in database max length are 4000
     * characters)
     */
    @Setting(
        descKey = "linkDescMaxLength.desc",
        labelKey = "linkDescMaxLength.label"
    )
    private int linkDescMaxLength = 400;

    public static CMSConfig getConfig() {
        final ConfigurationManager confManager = CdiUtil.createCdiUtil()
            .findBean(ConfigurationManager.class);
        return confManager.findConfiguration(CMSConfig.class);
    }

    public CMSConfig() {
        super();
    }

    public String getDefaultItemTemplatePath() {
        return defaultItemTemplatePath;
    }

    public void setDefaultItemTemplatePath(final String defaultItemTemplatePath) {
        this.defaultItemTemplatePath = defaultItemTemplatePath;
    }

    public String getDefaultFolderTemplatePath() {
        return defaultFolderTemplatePath;
    }

    public void setDefaultFolderTemplatePath(
        final String defaultFolderTemplatePath) {
        this.defaultFolderTemplatePath = defaultFolderTemplatePath;
    }

    public String getTemplateRootPath() {
        return templateRootPath;
    }

    public void setTemplateRootPath(final String templateRootPath) {
        this.templateRootPath = templateRootPath;
    }

    public String getItemAdapters() {
        return itemAdapters;
    }

    public void setItemAdapters(final String itemAdapters) {
        this.itemAdapters = itemAdapters;
    }

    public boolean isUseStreamlinedCreation() {
        return useStreamlinedCreation;
    }

    public void setUseStreamlinedCreation(final boolean useStreamlinedCreation) {
        this.useStreamlinedCreation = useStreamlinedCreation;
    }

    public DHTMLEditor.Config getDHTMLEditorConfig() {
        if (dhtmlEditorConfig.size() < 2) {
            return DHTMLEditor.Config.STANDARD;
        } else {
            return new DHTMLEditor.Config(dhtmlEditorConfig.get(0),
                                          dhtmlEditorConfig.get(1));
        }

    }

    public List<String> getDhtmlEditorConfig() {
        return new ArrayList<>(dhtmlEditorConfig);
    }

    public void setDhtmlEditorConfig(final List<String> dhtmlEditorConfig) {
        this.dhtmlEditorConfig = new ArrayList<>(dhtmlEditorConfig);
    }

    public List<String> getDhtmlEditorPlugins() {
        return new ArrayList<>(dhtmlEditorPlugins);
    }

    public void setDhtmlEditorPlugins(final List<String> dhtmlEditorPlugins) {
        this.dhtmlEditorPlugins = new ArrayList<>(dhtmlEditorPlugins);
    }

    public List<String> getDhtmlEditorHiddenButtons() {
        return new ArrayList<>(dhtmlEditorHiddenButtons);
    }

    public void setDhtmlEditorHiddenButtons(
        final List<String> dhtmlEditorHiddenButtons) {
        this.dhtmlEditorHiddenButtons
            = new ArrayList<>(dhtmlEditorHiddenButtons);
    }

    public boolean isHideAdminTabs() {
        return hideAdminTabs;
    }

    public void setHideAdminTabs(final boolean hideAdminTabs) {
        this.hideAdminTabs = hideAdminTabs;
    }

    public boolean isHideFolderIndexCheckbox() {
        return hideFolderIndexCheckbox;
    }

    public void setHideFolderIndexCheckbox(final boolean hideFolderIndexCheckbox) {
        this.hideFolderIndexCheckbox = hideFolderIndexCheckbox;
    }

    public boolean isHideLaunchDate() {
        return hideLaunchDate;
    }

    public void setHideLaunchDate(final boolean hideLaunchDate) {
        this.hideLaunchDate = hideLaunchDate;
    }

    public boolean isRequireLaunchDate() {
        return requireLaunchDate;
    }

    public void setRequireLaunchDate(final boolean requireLaunchDate) {
        this.requireLaunchDate = requireLaunchDate;
    }

    public boolean isHideTemplatesTab() {
        return hideTemplatesTab;
    }

    public void setHideTemplatesTab(final boolean hideTemplatesTab) {
        this.hideTemplatesTab = hideTemplatesTab;
    }

    public boolean isHideTextAssetUploadFile() {
        return hideTextAssetUploadFile;
    }

    public void setHideTextAssetUploadFile(final boolean hideTextAssetUploadFile) {
        this.hideTextAssetUploadFile = hideTextAssetUploadFile;
    }

    public boolean isHideTimezone() {
        return hideTimezone;
    }

    public void setHideTimezone(final boolean hideTimezone) {
        this.hideTimezone = hideTimezone;
    }

    public boolean isSaveTextCleansWordTags() {
        return saveTextCleansWordTags;
    }

    public void setSaveTextCleansWordTags(final boolean saveTextCleansWordTags) {
        this.saveTextCleansWordTags = saveTextCleansWordTags;
    }

    public boolean isDisableFileAssetExtraction() {
        return disableFileAssetExtraction;
    }

    public void setDisableFileAssetExtraction(
        final boolean disableFileAssetExtraction) {
        this.disableFileAssetExtraction = disableFileAssetExtraction;
    }

    public boolean isDeleteWorkflowAfterPublication() {
        return deleteWorkflowAfterPublication;
    }

    public void setDeleteWorkflowAfterPublication(
        boolean deleteWorkflowAfterPublication) {
        this.deleteWorkflowAfterPublication = deleteWorkflowAfterPublication;
    }

    public int getSoonExpiredTimespanDays() {
        return soonExpiredTimespanDays;
    }

    public void setSoonExpiredTimespanDays(final int soonExpiredTimespanDays) {
        this.soonExpiredTimespanDays = soonExpiredTimespanDays;
    }

    public int getSoonExpiredTimespanMonths() {
        return soonExpiredTimespanMonths;
    }

    public void setSoonExpiredTimespanMonths(final int soonExpiredTimespanMonths) {
        this.soonExpiredTimespanMonths = soonExpiredTimespanMonths;
    }

    public boolean isUnpublishedNotFound() {
        return unpublishedNotFound;
    }

    public void setUnpublishedNotFound(final boolean unpublishedNotFound) {
        this.unpublishedNotFound = unpublishedNotFound;
    }

    public boolean isLinksOnlyInSameSubsite() {
        return linksOnlyInSameSubsite;
    }

    public void setLinksOnlyInSameSubsite(final boolean linksOnlyInSameSubsite) {
        this.linksOnlyInSameSubsite = linksOnlyInSameSubsite;
    }

    public boolean isHideResetLifecycleLink() {
        return hideResetLifecycleLink;
    }

    public void setHideResetLifecycleLink(final boolean hideResetLifecycleLink) {
        this.hideResetLifecycleLink = hideResetLifecycleLink;
    }

    public boolean isScoreTitleAndKeywords() {
        return scoreTitleAndKeywords;
    }

    public void setScoreTitleAndKeywords(final boolean scoreTitleAndKeywords) {
        this.scoreTitleAndKeywords = scoreTitleAndKeywords;
    }

    public int getTitleWeight() {
        return titleWeight;
    }

    public void setTitleWeight(final int titleWeight) {
        this.titleWeight = titleWeight;
    }

    public int getKeywordWeight() {
        return keywordWeight;
    }

    public void setKeywordWeight(final int keywordWeight) {
        this.keywordWeight = keywordWeight;
    }

    public boolean isLimitItemSearchToContentSection() {
        return limitItemSearchToContentSection;
    }

    public void setLimitItemSearchToContentSection(
        boolean limitItemSearchToContentSection) {
        this.limitItemSearchToContentSection = limitItemSearchToContentSection;
    }

    public List<String> getSkipAssetSteps() {
        return new ArrayList<>(skipAssetSteps);
    }

    public void setSkipAssetSteps(final List<String> skipAssetSteps) {
        this.skipAssetSteps = new ArrayList<>(skipAssetSteps);
    }

    public boolean isMandatoryDescriptions() {
        return mandatoryDescriptions;
    }

    public void setMandatoryDescriptions(final boolean mandatoryDescriptions) {
        this.mandatoryDescriptions = mandatoryDescriptions;
    }

    public boolean isDeleteLifecycleWhenComplete() {
        return deleteLifecycleWhenComplete;
    }

    public void setDeleteLifecycleWhenComplete(
        boolean deleteLifecycleWhenComplete) {
        this.deleteLifecycleWhenComplete = deleteLifecycleWhenComplete;
    }

    public boolean isDeleteWorkflowNotificationWhenSend() {
        return deleteWorkflowNotificationWhenSend;
    }

    public void setDeleteWorkflowNotificationWhenSend(
        boolean deleteWorkflowNotificationWhenSend) {
        this.deleteWorkflowNotificationWhenSend
            = deleteWorkflowNotificationWhenSend;
    }

    public boolean isDeleteExpiryNotificationsWhenSent() {
        return deleteExpiryNotificationsWhenSent;
    }

    public void setDeleteExpiryNotificationsWhenSent(
        boolean deleteExpiryNotificationsWhenSent) {
        this.deleteExpiryNotificationsWhenSent
            = deleteExpiryNotificationsWhenSent;
    }

    public int getDefaultNotificationTime() {
        return defaultNotificationTime;
    }

    public void setDefaultNotificationTime(final int defaultNotificationTime) {
        this.defaultNotificationTime = defaultNotificationTime;
    }

    public boolean isNotifyAuthorOnLifecycle() {
        return notifyAuthorOnLifecycle;
    }

    public void setNotifyAuthorOnLifecycle(final boolean notifyAuthorOnLifecycle) {
        this.notifyAuthorOnLifecycle = notifyAuthorOnLifecycle;
    }

    public String getContentCenterMap() {
        return contentCenterMap;
    }

    public void setContentCenterMap(final String contentCenterMap) {
        this.contentCenterMap = contentCenterMap;
    }

    public List<String> getDefaultItemResolverClassNames() {
        return new ArrayList<>(defaultItemResolverClassNames);
    }

    public void setDefaultItemResolverClassNames(
        final List<String> defaultItemResolverClassNames) {
        this.defaultItemResolverClassNames
            = new ArrayList<>(defaultItemResolverClassNames);
    }

    @SuppressWarnings("unchecked")
    public List<Class<ItemResolver>> getDefaultItemResolverClasses() {
        final List<Class<ItemResolver>> resolverClasses = new ArrayList<>();
        for (final String className : getDefaultItemResolverClassNames()) {
            try {
                resolverClasses.add((Class<ItemResolver>) Class.forName(
                    className));
            } catch (ClassNotFoundException ex) {
                throw new UnexpectedErrorException(String.format(
                    "ItemResolver class \"%s\" not found.", className), ex);
            }
        }
        return resolverClasses;
    }

//    public List<String> getDefaultTemplateResolverClassNames() {
//        return new ArrayList<>(defaultTemplateResolverClassNames);
//    }
//
//    public void setDefaultTemplateResolverClassNames(
//        List<String> defaultTemplateResolverClassNames) {
//        this.defaultTemplateResolverClassNames = new ArrayList<>(
//            defaultTemplateResolverClassNames);
//    }
//    
//    @SuppressWarnings("unchecked")
//    public List<Class<TemplateResolver>> getDefaultTemplateResolverClasses() {
//        final List<Class<TemplateResolver>> resolverClasses = new ArrayList<>();
//        for (final String className : getDefaultTemplateResolverClassNames()) {
//            try {
//                resolverClasses.add((Class<TemplateResolver>) Class.forName(
//                    className));
//            } catch (ClassNotFoundException ex) {
//                throw new UnexpectedErrorException(String.format(
//                    "ItemResolver class \"%s\" not found.", className), ex);
//            }
//        }
//        return resolverClasses;
//    }
    public String getItemSearchDefaultTab() {
        return itemSearchDefaultTab;
    }

    public void setItemSearchDefaultTab(final String itemSearchDefaultTab) {
        this.itemSearchDefaultTab = itemSearchDefaultTab;
    }

    public int getItemSearchFlatBrowsePanePageSize() {
        return itemSearchFlatBrowsePanePageSize;
    }

    public void setItemSearchFlatBrowsePanePageSize(
        int itemSearchFlatBrowsePanePageSize) {
        this.itemSearchFlatBrowsePanePageSize = itemSearchFlatBrowsePanePageSize;
    }

    public int getFolderBrowseListSize() {
        return folderBrowseListSize;
    }

    public void setFolderBrowseListSize(final int folderBrowseListSize) {
        this.folderBrowseListSize = folderBrowseListSize;
    }

    public int getFolderAtoZShowLimit() {
        return folderAtoZShowLimit;
    }

    public void setFolderAtoZShowLimit(final int folderAtoZShowLimit) {
        this.folderAtoZShowLimit = folderAtoZShowLimit;
    }

    public boolean isUseOldStyleItemLifecycleItemPane() {
        return useOldStyleItemLifecycleItemPane;
    }

    public void setUseOldStyleItemLifecycleItemPane(
        boolean useOldStyleItemLifecycleItemPane) {
        this.useOldStyleItemLifecycleItemPane = useOldStyleItemLifecycleItemPane;
    }

    public boolean isThreadPublishing() {
        return threadPublishing;
    }

    public void setThreadPublishing(final boolean threadPublishing) {
        this.threadPublishing = threadPublishing;
    }

    public String getPublishingFailureSender() {
        return publishingFailureSender;
    }

    public void setPublishingFailureSender(final String publishingFailureSender) {
        this.publishingFailureSender = publishingFailureSender;
    }

    public String getPublishingFailureReceiver() {
        return publishingFailureReceiver;
    }

    public void setPublishingFailureReceiver(
        final String publishingFailureReceiver) {
        this.publishingFailureReceiver = publishingFailureReceiver;
    }

    public int getImageBrowserThumbnailMaxWidth() {
        return imageBrowserThumbnailMaxWidth;
    }

    public void setImageBrowserThumbnailMaxWidth(
        int imageBrowserThumbnailMaxWidth) {
        this.imageBrowserThumbnailMaxWidth = imageBrowserThumbnailMaxWidth;
    }

    public int getImageBrowserThumbnailMaxHeight() {
        return imageBrowserThumbnailMaxHeight;
    }

    public void setImageBrowserThumbnailMaxHeight(
        int imageBrowserThumbnailMaxHeight) {
        this.imageBrowserThumbnailMaxHeight = imageBrowserThumbnailMaxHeight;
    }

    public int getImageBrowserCaptionSize() {
        return imageBrowserCaptionSize;
    }

    public void setImageBrowserCaptionSize(final int imageBrowserCaptionSize) {
        this.imageBrowserCaptionSize = imageBrowserCaptionSize;
    }

    public int getImageBrowserDescriptionSize() {
        return imageBrowserDescriptionSize;
    }

    public void setImageBrowserDescriptionSize(
        final int imageBrowserDescriptionSize) {
        this.imageBrowserDescriptionSize = imageBrowserDescriptionSize;
    }

    public int getImageBrowserTitleSize() {
        return imageBrowserTitleSize;
    }

    public void setImageBrowserTitleSize(final int imageBrowserTitleSize) {
        this.imageBrowserTitleSize = imageBrowserTitleSize;
    }

    public boolean isImageCacheEnabled() {
        return imageCacheEnabled;
    }

    public void setImageCacheEnabled(final boolean imageCacheEnabled) {
        this.imageCacheEnabled = imageCacheEnabled;
    }

    public boolean isImageCachePrefetchEnabled() {
        return imageCachePrefetchEnabled;
    }

    public void setImageCachePrefetchEnabled(
        final boolean imageCachePrefetchEnabled) {
        this.imageCachePrefetchEnabled = imageCachePrefetchEnabled;
    }

    public int getImageCacheMaxSize() {
        return imageCacheMaxSize;
    }

    public void setImageCacheMaxSize(final int imageCacheMaxSize) {
        this.imageCacheMaxSize = imageCacheMaxSize;
    }

    public int getImageCacheMaxAge() {
        return imageCacheMaxAge;
    }

    public void setImageCacheMaxAge(final int imageCacheMaxAge) {
        this.imageCacheMaxAge = imageCacheMaxAge;
    }

    public boolean isAttachPersonOrgaUnitsStep() {
        return attachPersonOrgaUnitsStep;
    }

    public void setAttachPersonOrgaUnitsStep(
        final boolean attachPersonOrgaUnitsStep) {
        this.attachPersonOrgaUnitsStep = attachPersonOrgaUnitsStep;
    }

    public int getPersonOrgaUnitsStepSortKey() {
        return personOrgaUnitsStepSortKey;
    }

    public void setPersonOrgaUnitsStepSortKey(
        final int personOrgaUnitsStepSortKey) {
        this.personOrgaUnitsStepSortKey = personOrgaUnitsStepSortKey;
    }

    public boolean isEnableXmlCache() {
        return enableXmlCache;
    }

    public void setEnableXmlCache(final boolean enableXmlCache) {
        this.enableXmlCache = enableXmlCache;
    }

    public int getXmlCacheSize() {
        return xmlCacheSize;
    }

    public void setXmlCacheSize(final int xmlCacheSize) {
        this.xmlCacheSize = xmlCacheSize;
    }

    public int getXmlCacheAge() {
        return xmlCacheAge;
    }

    public void setXmlCacheAge(final int xmlCacheAge) {
        this.xmlCacheAge = xmlCacheAge;
    }

    public int getLinkDescMaxLength() {
        return linkDescMaxLength;
    }

    public void setLinkDescMaxLength(final int linkDescMaxLength) {
        this.linkDescMaxLength = linkDescMaxLength;
    }

    public String getCategoryAuthoringAddForm() {
        return categoryAuthoringAddForm;
    }

    public void setCategoryAuthoringAddForm(
        final String categoryAuthoringAddForm) {

        this.categoryAuthoringAddForm = categoryAuthoringAddForm;
    }

    public String getCategoryAuthoringExtension() {
        return categoryAuthoringExtension;
    }

    public void setCategoryAuthoringExtension(
        final String categoryAuthoringExtension) {
        this.categoryAuthoringExtension = categoryAuthoringExtension;
    }

    public boolean isCategoryPickerAjaxExpandAll() {
        return categoryPickerAjaxExpandAll;
    }

    public void setCategoryPickerAjaxExpandAll(
        final boolean categoryPickerAjaxExpandAll) {
        this.categoryPickerAjaxExpandAll = categoryPickerAjaxExpandAll;
    }

}
