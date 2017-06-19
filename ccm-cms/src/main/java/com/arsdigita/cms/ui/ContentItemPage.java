/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.TabbedPane;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.PageLocations;
import com.arsdigita.cms.dispatcher.CMSDispatcher;
import com.arsdigita.cms.dispatcher.CMSPage;
import com.arsdigita.cms.ui.authoring.WizardSelector;
import com.arsdigita.cms.ui.item.ContentItemRequestLocal;
import com.arsdigita.cms.ui.item.CustomizedPreviewLink;
import com.arsdigita.cms.ui.item.ItemLanguages;
import com.arsdigita.cms.ui.item.Summary;
import com.arsdigita.cms.ui.lifecycle.ItemLifecycleAdminPane;
import com.arsdigita.cms.ui.revision.ItemRevisionAdminPane;
import com.arsdigita.cms.ui.templates.ItemTemplates;
import com.arsdigita.cms.ui.workflow.ItemWorkflowAdminPane;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arsdigita.cms.CMSConfig;
import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentItemVersion;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionManager;
import org.librecms.contentsection.ContentType;
import org.librecms.dispatcher.ItemResolver;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

/**
 * Page for administering a content item.
 *
 * @author Michael Pih
 * @author <a href="mailto:sfreidin@redhat.com">Stanislav Freidin</a>
 * @author Jack Chung
 * @author <a href="mailto:quasi@quasiweb.de">Sören Bernstein</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 */
public class ContentItemPage extends CMSPage implements ActionListener {

    /**
     * Private Logger instance for debugging purpose.
     */
    private static final Logger LOGGER = LogManager.getLogger(
        ContentItemPage.class);
    /**
     * The URL parameter that must be passed in in order to set the current tab.
     * This is a KLUDGE right now because the TabbedDialog's current tab is
     * selected with a local state parameter
     */
    public static final String SET_TAB = "set_tab";
    /**
     * The name of the global state parameter that holds the item id.
     */
    public static final String ITEM_ID = "item_id";
    /**
     * The name of th global state parameter that holds the selected language.
     */
    public static final String SELECTED_LANGUAGE = "selected_language";
    /**
     * The name of the global state parameter which holds the return URL.
     */
    public static final String RETURN_URL = "return_url";
    /**
     * The name of the global state parameter that determines whether or not to
     * use the streamlined authoring process (assuming the option is turned on).
     *
     */
    public static final String STREAMLINED_CREATION = "streamlined_creation";
    public static final String STREAMLINED_CREATION_ACTIVE = "active";
    public static final String STREAMLINED_CREATION_INACTIVE = "active";
    /**
     * Index of the summary tab
     */
    public static final int SUMMARY_TAB = 0;
    /**
     * <p>
     * The name of the state parameter which indicates the content type of the
     * item the user wishes to create. or edit.</p>
     *
     * <p>
     * The parameter must be a BigDecimalParameter which encodes the id of the
     * content type.</p>
     */
    public static final String CONTENT_TYPE = "content_type";
    public static final int AUTHORING_TAB = 1;
    public static final int LANGUAGE_TAB = 2;
    public static final int WORKFLOW_TAB = 3;
    public static final int PUBLISHING_TAB = 4;
    public static final int HISTORY_TAB = 5;
    public static final int TEMPLATES_TAB = 6;

    private final TabbedPane tabbedPane;
    private final StringParameter returnUrlParameter;
    private final ItemSelectionModel itemModel;
    private final SingleSelectionModel<String> selectedLanguageModel;
    private final ACSObjectSelectionModel typeModel;
    private final ContentItemRequestLocal itemRequestLocal;
    private final Summary summaryPane;
    private final ItemWorkflowAdminPane workflowPane;
    private final ItemLifecycleAdminPane lifecyclePane;
    private final WizardSelector wizardPane;
    private final ItemLanguages languagesPane;
    private final ItemRevisionAdminPane revisionsPane;
    private final ItemTemplates templatesPane;
    private final Link m_previewLink;
    private final GlobalNavigation m_globalNavigation;
    private final ContentItemContextBar m_contextBar;

    private final StringParameter selectedLanguageParam;

    private class ItemRequestLocal extends ContentItemRequestLocal {

        @Override
        protected final Object initialValue(final PageState state) {
            return CMS.getContext().getContentItem();
        }

    }

    private class TitlePrinter implements PrintListener {

        @Override
        public final void prepare(final PrintEvent event) {
            final Label label = (Label) event.getTarget();
            final ContentItem item = itemRequestLocal.getContentItem(event.
                getPageState());

            label.setLabel(item.getDisplayName());
        }

    }

    /**
     * Constructs a new ContentItemPage.
     */
    public ContentItemPage() {
        super("", new SimpleContainer());

        itemRequestLocal = new ItemRequestLocal();

        setClassAttr("cms-admin");
        setTitle(new Label(new TitlePrinter()));

        // Add the item id global state parameter
        final LongParameter itemId = new LongParameter(ITEM_ID);
        itemId.addParameterListener(new NotNullValidationListener(ITEM_ID));
        addGlobalStateParam(itemId);
        itemModel = new ItemSelectionModel(itemId);

        // Add the selected item language as parameter
        selectedLanguageParam = new StringParameter(
            SELECTED_LANGUAGE);
        selectedLanguageParam.addParameterListener(
            new NotNullValidationListener(
                SELECTED_LANGUAGE));
        addGlobalStateParam(selectedLanguageParam);
        selectedLanguageModel = new ParameterSingleSelectionModel<>(
            selectedLanguageParam);
        selectedLanguageParam
            .setDefaultValue(KernelConfig.getConfig().getDefaultLanguage());

        // Add the content type global state parameter
        final LongParameter contentType = new LongParameter(CONTENT_TYPE);
        addGlobalStateParam(contentType);

        // Add the streamlined creation global state parameter
        final StringParameter streamlinedCreation = new StringParameter(
            STREAMLINED_CREATION);
        addGlobalStateParam(streamlinedCreation);

        typeModel = new ACSObjectSelectionModel(ContentType.class.getName(),
                                                ContentType.class.getName(),
                                                contentType);

        // Validate the item ID parameter (caches the validation).
        getStateModel().addValidationListener(
            event -> validateItemID(event.getPageState()));

        // Add the return url global state parameter
        returnUrlParameter = new StringParameter(RETURN_URL);
        addGlobalStateParam(returnUrlParameter);

        m_globalNavigation = new GlobalNavigation();
        add(m_globalNavigation);

        m_contextBar = new ContentItemContextBar(itemModel);
        add(m_contextBar);

        // Create panels.
        summaryPane = new Summary(itemModel);
        wizardPane = new WizardSelector(itemModel, typeModel);
        languagesPane = new ItemLanguages(itemModel, selectedLanguageModel);
        workflowPane = new ItemWorkflowAdminPane(itemId); // Make this use m_item XXX
        lifecyclePane = new ItemLifecycleAdminPane(itemRequestLocal);
        revisionsPane = new ItemRevisionAdminPane(itemRequestLocal);
        templatesPane = new ItemTemplates(itemModel);

        // Create tabbed pane.
        tabbedPane = new TabbedPane();
        add(tabbedPane);

        tabbedPane.setIdAttr("page-body");

        tabbedPane.addTab(new Label(gz("cms.ui.item.summary")), summaryPane);
        tabbedPane.
            addTab(new Label(gz("cms.ui.item.authoring")), wizardPane);
        tabbedPane.addTab(new Label(gz("cms.ui.item.languages")),
                          languagesPane);
        tabbedPane.addTab(new Label(gz("cms.ui.item.workflow")),
                          workflowPane);
        tabbedPane.addTab(new Label(gz("cms.ui.item.lifecycles")),
                          lifecyclePane);
        tabbedPane.addTab(new Label(gz("cms.ui.item.history")),
                          revisionsPane);
        tabbedPane.addTab(new Label(gz("cms.ui.item.templates")),
                          templatesPane);

        tabbedPane.addActionListener(new ActionListener() {

            @Override
            public final void actionPerformed(final ActionEvent event) {

                final PageState state = event.getPageState();
                final Component pane = tabbedPane.getCurrentPane(state);

                if (pane instanceof Resettable) {
                    ((Resettable) pane).reset(state);
                }
            }

        });

        // Build the preview link.
        m_previewLink = new Link(new Label(gz("cms.ui.preview")),
                                 new PrintListener() {

                                 @Override
                                 public final void prepare(
                                     final PrintEvent event) {
                                     final Link link = (Link) event.getTarget();
                                     link.setTarget(getPreviewURL(event.
                                         getPageState()));
                                     link.setTargetFrame(Link.NEW_FRAME);
                                 }

                             });
        m_previewLink.setIdAttr("preview_link");
        add(m_previewLink);

        addActionListener(this);

        // Add validation to make sure we are not attempting to edit a live item
        getStateModel().addValidationListener(new FormValidationListener() {

            @Override
            public void validate(final FormSectionEvent event)
                throws FormProcessException {

                PageState s = event.getPageState();
                FormData data = event.getFormData();
                final ContentItem item = itemRequestLocal.getContentItem(s);
                if (item != null
                        && ContentItemVersion.LIVE == item.getVersion()) {
                    LOGGER.error(String.format(
                        "The item %d is live and cannot be edited.", item.
                            getObjectId()));
                    throw new FormProcessException(new GlobalizedMessage(
                        "cms.ui.live_item_not_editable",
                        CmsConstants.CMS_BUNDLE));
                }
            }

        });
    }

    /**
     * Ensures that the item_id parameter references a valid {@link
     * com.arsdigita.cms.ContentItem}.
     *
     * @param state The page state
     *
     * @pre state != null
     * @exception FormProcessException if the item_id is not valid
     */
    protected void validateItemID(final PageState state) throws
        FormProcessException {
        final ContentItem item = itemRequestLocal.getContentItem(state);

        if (item == null) {
            throw new FormProcessException(new GlobalizedMessage(
                "cms.ui.invalid_item_id", CmsConstants.CMS_BUNDLE));
        }
    }

    /**
     * Fetch the request-local content section.
     *
     * @deprecated use com.arsdigita.cms.CMS.getContext().getContentSection()
     * instead
     * @param request The HTTP request
     *
     * @return The current content section
     */
    @Override
    public ContentSection getContentSection(final HttpServletRequest request) {
        // Resets all content sections associations.
        ContentSection section = super.getContentSection(request);
        Assert.exists(section);
        return section;
    }

    /**
     * Overrides CMSPage.getContentItem(PageState state) to get the current
     * content item from the page state.
     *
     * @deprecated Use the ItemSelectionModel
     * @param state The page state
     *
     * @return The current content item, null if there is none
     */
    @Override
    public ContentItem getContentItem(final PageState state) {
        return (ContentItem) itemModel.getSelectedObject(state);
    }

    /**
     * Set the current tab, if necessary
     *
     * @param event
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        final PageState state = event.getPageState();
        final String setTab = state.getRequest().getParameter(SET_TAB);

        // Hide the templates tab, the workflow tab, and the preview
        // link if the current item is a template.
        final ContentItem item = itemRequestLocal.getContentItem(state);

        // ToDo: Reable when Templates have been ported. Not clear yet if 
        // Templates will be ContentItems in LibreCMS...
//        if (item instanceof Template) {
//            tabbedPane.setTabVisible(state, templatesPane, false);
//            tabbedPane.setTabVisible(state, workflowPane, false);
//            tabbedPane.setTabVisible(state, languagesPane, false);
//            m_previewLink.setVisible(state, false);
//        } else {
//            tabbedPane.setTabVisible(state, 
//                                     templatesPane, 
//                                     !ContentSectionConfig.getConfig().getHideTemplatesTab());
//        }
        // Set the current tab based on parameters
        if (setTab != null) {
            Integer tab;

            try {
                tab = Integer.valueOf(setTab);
            } catch (NumberFormatException ex) {
                // Stop processing set_tab parameter.
                LOGGER.warn("Stopping processing of set_tab parameter.", ex);
                return;
            }

            if (tab < tabbedPane.size()) {
                tabbedPane.setSelectedIndex(state, tab);
            }
        }
    }

    /**
     * Construct a URL for displaying a certain item
     *
     * @param nodeURL The URL where this page is mounted
     * @param itemId  The id of the item to display
     * @param tab     The index of the tab to display
     *
     * @return
     */
    public static String getItemURL(final String nodeURL,
                                    final Long itemId,
                                    final int tab) {
        return getItemURL(nodeURL, itemId, tab, false);
    }

    /**
     * Construct a URL for displaying a certain item
     *
     * @param nodeURL             The URL where this page is mounted
     * @param itemId              The id of the item to display
     * @param tab                 The index of the tab to display
     * @param streamlinedCreation Whether to activate Streamlined item authoring
     *
     * @return
     */
    public static String getItemURL(final String nodeURL,
                                    final Long itemId,
                                    final int tab,
                                    final boolean streamlinedCreation) {
        final StringBuilder urlBuilder = new StringBuilder();

        urlBuilder
            .append(nodeURL)
            .append(PageLocations.ITEM_PAGE)
            .append("?")
            .append(ITEM_ID)
            .append("=")
            .append(itemId.toString())
            .append("&")
            .append(SET_TAB)
            .append("=")
            .append(tab);

        if (streamlinedCreation
                && CMSConfig.getConfig().isUseStreamlinedCreation()) {

            urlBuilder
                .append("&")
                .append(STREAMLINED_CREATION)
                .append("=")
                .append(STREAMLINED_CREATION_ACTIVE);
        }

        return urlBuilder.toString();
    }

    /**
     * @param itemId
     * @param tab
     *
     * @return
     *
     * @deprecated Use getItemURL instead
     */
    public static String getRelativeItemURL(final Long itemId, final int tab) {
        final StringBuilder url = new StringBuilder();
        url
            .append(PageLocations.ITEM_PAGE)
            .append("?")
            .append(ITEM_ID)
            .append("=")
            .append(itemId.toString())
            .append("&")
            .append(SET_TAB)
            .append("=")
            .append(tab);

        return url.toString();
    }

    /**
     * Constructs a URL for displaying a certain item.
     *
     * @param item the ContentItem object to display
     * @param tab  The index of the tab to display
     *
     * @return
     */
    public static String getItemURL(final ContentItem item,
                                    final int tab) {
        final ContentSection section = item.getContentType().getContentSection();

        if (section == null) {
            return null;
        } else {
            final String nodeURL = section.getPrimaryUrl() + "/";

            return getItemURL(nodeURL, item.getObjectId(), tab);
        }
    }

    /**
     * Constructs a URL for displaying a certain item.
     *
     * @param itemId the id of the ContentItem object to display
     * @param tab    The index of the tab to display
     *
     * @return
     */
    public static String getItemURL(final long itemId,
                                    final int tab) {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ContentItemRepository itemRepo = cdiUtil.findBean(
            ContentItemRepository.class);

        final Optional<ContentItem> item = itemRepo.findById(itemId);

        if (item.isPresent()) {
            return getItemURL(item.get(), tab);
        } else {
            return null;
        }
    }

    /**
     * Redirect back to wherever the user came from, using the value of the
     * return_url parameter.
     *
     * @param state The current page state
     */
    public void redirectBack(final PageState state) {
        try {
            final String returnUrl = (String) state.getValue(returnUrlParameter);
            state.getResponse().sendRedirect(returnUrl);
        } catch (IOException ex) {
            LOGGER.error("IO Error redirecting back", ex);
            // do nothing
        }
    }

    /**
     * Fetch the preview URL.
     */
    private String getPreviewURL(final PageState state) {
        final ContentItem item = itemRequestLocal.getContentItem(state);

        if (item instanceof CustomizedPreviewLink) {
            final String previewLink = ((CustomizedPreviewLink) item).
                getPreviewUrl(
                    state);
            if ((previewLink == null) || previewLink.isEmpty()) {
                return getDefaultPreviewLink(state, item);
            } else {
                return previewLink;
            }
        } else {
            return getDefaultPreviewLink(state, item);
        }
    }

    /**
     *
     * @param state
     * @param item
     *
     * @return
     */
    private String getDefaultPreviewLink(final PageState state,
                                         final ContentItem item) {
        final ContentSection section = CMS.getContext().getContentSection();
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
//        final ContentSectionManager sectionManager = cdiUtil.findBean(
//            ContentSectionManager.class);
//        final ItemResolver itemResolver = sectionManager
//            .getItemResolver(section);

        // Pass in the "Live" context since we need it for the preview
//        return itemResolver.generateItemURL(state,
//                                            item,
//                                            section,
//                                            CMSDispatcher.PREVIEW);
        final ContentItemPageController controller = cdiUtil
            .findBean(ContentItemPageController.class);

        return controller.getDefaultPreviewLink(section, item, state);
    }

    protected final static GlobalizedMessage gz(final String key) {
        return new GlobalizedMessage(key, CmsConstants.CMS_BUNDLE);
    }

    protected final static String lz(final String key) {
        return (String) gz(key).localize();
    }

    public static boolean isStreamlinedCreationActive(final PageState state) {
        return CMSConfig.getConfig().isUseStreamlinedCreation()
                   && STREAMLINED_CREATION_ACTIVE.equals(state.getRequest().
                getParameter(STREAMLINED_CREATION));
    }

    protected TabbedPane getTabbedPane() {
        return tabbedPane;
    }

    protected WizardSelector getWizardPane() {
        return wizardPane;
    }

    /**
     * Adds the content type to the output.
     *
     * @param state  PageState
     * @param parent Parent document
     *
     * @return page
     */
    @Override
    protected Element generateXMLHelper(final PageState state,
                                        final Document parent) {

        Objects.requireNonNull(itemRequestLocal.getContentItem(state),
                               "No ContentItem in current request.");

        final Element page = super.generateXMLHelper(state, parent);
        final Element contenttype = page.newChildElement("bebop:contentType",
                                                         BEBOP_XML_NS);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();

        final ContentItemPageController controller = cdiUtil
            .findBean(ContentItemPageController.class);
        contenttype
            .setText(controller
                .getContentTypeLabel(itemRequestLocal.getContentItem(state)));

        return page;
    }

}
