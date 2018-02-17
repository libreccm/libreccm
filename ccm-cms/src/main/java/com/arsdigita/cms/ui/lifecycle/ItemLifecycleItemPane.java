/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.lifecycle;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.cms.ui.BaseItemPane;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.cms.ui.item.ContentItemRequestLocal;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.mail.Mail;
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.Property;
import com.arsdigita.toolbox.ui.PropertyList;
import com.arsdigita.toolbox.ui.Section;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.URL;
import com.arsdigita.xml.Element;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arsdigita.cms.CMSConfig;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.Shiro;
import org.libreccm.security.User;
import org.libreccm.security.UserRepository;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.privileges.ItemPrivileges;
import org.librecms.lifecycle.Lifecycle;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.mail.MessagingException;

/**
 * This class contains the component which displays the information for a
 * particular lifecycle, with the ability to edit and delete. This information
 * also includes the associated phases for this lifecycle, also with the ability
 * to add, edit, and delete.
 *
 * @author Michael Pih
 * @author Jack Chung
 * @author Xixi D'Moon
 * @author Justin Ross
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class ItemLifecycleItemPane extends BaseItemPane {

    private static final Logger LOGGER = LogManager.getLogger(
        ItemLifecycleItemPane.class);
    private final ContentItemRequestLocal selectedItem;
    private final LifecycleRequestLocal selectedLifecycle;
    private final SimpleContainer detailPane;

    public ItemLifecycleItemPane(final ContentItemRequestLocal selectedItem,
                                 final LifecycleRequestLocal selectedLifecycle) {
        this.selectedItem = selectedItem;
        this.selectedLifecycle = selectedLifecycle;

        detailPane = new SimpleContainer();
        add(detailPane);
        setDefault(detailPane);

        detailPane.add(new SummarySection());

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ContentItemRepository itemRepo = cdiUtil.findBean(
            ContentItemRepository.class);
        final ContentItemManager itemManager = cdiUtil.findBean(
            ContentItemManager.class);
        final GlobalizationHelper globalizationHelper = cdiUtil.findBean(
            GlobalizationHelper.class);

        final Label lastPublishedLabel = new Label();
        lastPublishedLabel.addPrintListener(new PrintListener() {

            @Override
            public void prepare(final PrintEvent event) {
                final PageState state = event.getPageState();
                final Optional<ContentItem> item = itemManager.getLiveVersion(
                    selectedItem.getContentItem(state), ContentItem.class);
                final Label label = (Label) event.getTarget();
                final String dateStr;
                if (item.isPresent()) {
                    final Date lastModifiedDate = itemRepo
                        .retrieveCurrentRevision(item.get(),
                                                 item.get().getObjectId())
                        .getRevisionDate();
                    dateStr = DateFormat.getDateTimeInstance(
                        DateFormat.LONG,
                        DateFormat.SHORT,
                        globalizationHelper.getNegotiatedLocale())
                        .format(lastModifiedDate);
                } else {
                    dateStr = "";
                }

                label.setLabel(new GlobalizedMessage(
                    "cms.ui.lifecycle.details.last_published",
                    CmsConstants.CMS_BUNDLE,
                    new Object[]{dateStr}));
            }

        });
        detailPane.add(lastPublishedLabel);

        detailPane.add(
            new PhaseSection());
    }

    private class SummarySection extends Section {

        public SummarySection() {
            setHeading(new Label(gz("cms.ui.lifecycle.details")));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            if (CMSConfig.getConfig().isUseOldStyleItemLifecycleItemPane()) {
                group.setSubject(new Properties());
                group.addAction(new UnpublishLink());
                group.addAction(new RepublishLink());
                group.addAction(new RepublishAndResetLink());
            } else {
                group.addAction(new ActionForm());
            }

        }

        private class Properties extends PropertyList {

            @Override
            protected final List<Property> properties(final PageState state) {
                final List<Property> props = super.properties(state);
                final Lifecycle cycle = selectedLifecycle.getLifecycle(state);

                final DateFormat format = DateFormat.getDateTimeInstance(
                    DateFormat.FULL,
                    DateFormat.FULL);

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final GlobalizationHelper globalizationHelper = cdiUtil
                    .findBean(GlobalizationHelper.class);
                final Locale language = globalizationHelper
                    .getNegotiatedLocale();

                props.add(new Property(
                    gz("cms.ui.name"),
                    cycle.getDefinition().getLabel().getValue(language)));
                props.add(new Property(
                    gz("cms.ui.item.lifecycle.start_date"),
                    format.format(cycle.getStartDateTime())));

                final java.util.Date endDate = cycle.getEndDateTime();

                if (endDate == null) {
                    props.add(new Property(gz("cms.ui.item.lifecycle.end_date"),
                                           lz("cms.ui.none")));
                } else {
                    props.add(new Property(gz("cms.ui.item.lifecycle.end_date"),
                                           format.format(endDate)));
                }

                return props;
            }

        }

    }

    private class PublishLink extends ActionLink {

        private final RequestLocal canPublishRequestLocal = new RequestLocal();

        PublishLink(final Component component) {
            super(component);
        }

        @Override
        public void generateXML(final PageState state, final Element parent) {
            Boolean canPublish = (Boolean) canPublishRequestLocal.get(state);
            if (null == canPublish) {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final PermissionChecker permissionChecker = cdiUtil.findBean(
                    PermissionChecker.class);
                ContentItem item = selectedItem.getContentItem(state);
                if (permissionChecker.isPermitted(ItemPrivileges.PUBLISH, item)) {
                    canPublish = true;
                } else {
                    canPublish = false;
                }

                canPublishRequestLocal.set(state, canPublish);
            }

            if (canPublish) {
                if (LOGGER.isDebugEnabled()) {
                    final ContentItem item = selectedItem.getContentItem(state);
                    LOGGER.debug("User can publish {}" + item.getUuid());
                }

                super.generateXML(state, parent);
            } else if (LOGGER.isDebugEnabled()) {
                final ContentItem item = selectedItem.getContentItem(state);
                LOGGER.debug("User cannot publish {}", item.getUuid());
            }
        }

    }

    private class UnpublishLink extends PublishLink {

        UnpublishLink() {
            super(new Label(gz("cms.ui.item.lifecycle.unpublish")));

            super.addActionListener(new Listener());
        }

        private class Listener implements ActionListener {

            @Override
            public final void actionPerformed(final ActionEvent event) {
                final PageState state = event.getPageState();
                final ContentItem item = selectedItem.getContentItem(state);
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final ContentItemManager itemManager = cdiUtil.findBean(
                    ContentItemManager.class);

                itemManager.unpublish(item);

                final String target = String.join(
                    "",
                    URL.getDispatcherPath(),
                    ContentItemPage.getItemURL(item,
                                               ContentItemPage.AUTHORING_TAB));

                throw new RedirectSignal(target, true);
            }

        }

    }

    private static void republish(final ContentItem item,
                                  final boolean reset,
                                  final User user) {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ItemLifecycleAdminController controller = cdiUtil
            .findBean(ItemLifecycleAdminController.class);

        controller.repulish(item);
    }

    private class RepublishLink extends PublishLink {

        RepublishLink() {
            super(new Label(gz("cms.ui.item.lifecycle.republish")));

            super.addActionListener(new Listener());
        }

        private class Listener implements ActionListener {

            @Override
            public final void actionPerformed(final ActionEvent event) {
                final PageState state = event.getPageState();
                final ContentItem item = selectedItem.getContentItem(state);
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final Shiro shiro = cdiUtil.findBean(Shiro.class);
                final User user = shiro.getUser().get();

                /*
                 * jensp 2011-12-14: Check is threaded publishing is active. 
                 * If yes, execute publishing in a thread.
                 */
                if (CMSConfig.getConfig().isThreadPublishing()) {
                    final Republisher republisher = new Republisher(item, user);
                    final Thread thread = new Thread(republisher);
                    thread.setUncaughtExceptionHandler(
                        new Thread.UncaughtExceptionHandler() {

                        @Override
                        public void uncaughtException(final Thread thread,
                                                      final Throwable ex) {
                            final StringWriter strWriter = new StringWriter();
                            final PrintWriter writer
                                                  = new PrintWriter(strWriter);
                            ex.printStackTrace(writer);

//                            PublishLock.getInstance().setError(item, strWriter
//                                                               .toString());
                            LOGGER.error(String.format(
                                "An error occurred while "
                                    + "publishing the item '%s': ",
                                item.getUuid()), ex);

                            if ((CMSConfig.getConfig()
                                 .getPublishingFailureSender()
                                 == null)
                                    && (CMSConfig.getConfig()
                                            .getPublishingFailureReceiver()
                                        == null)) {
                                return;
                            }

                            final UserRepository userRepo = cdiUtil.findBean(
                                UserRepository.class);
                            final User receiver = userRepo.findByEmailAddress(
                                CMSConfig.getConfig()
                                    .getPublishingFailureReceiver()).get();
                            final User sender = userRepo.findByEmailAddress(
                                CMSConfig.getConfig()
                                    .getPublishingFailureSender()).get();

                            if ((sender != null) && (receiver != null)) {
                                final Writer traceWriter = new StringWriter();
                                final PrintWriter printWriter = new PrintWriter(
                                    traceWriter);
                                ex.printStackTrace(printWriter);

                                final Mail notification = new Mail(
                                    receiver.getPrimaryEmailAddress()
                                        .getAddress(),
                                    sender.getPrimaryEmailAddress().getAddress(),
                                    String.format(
                                        "Failed to publish item '%s'",
                                        item.getUuid()));
                                notification.setBody(String.format(
                                    "Publishing item '%s' failed "
                                        + "with error message: %s.\n\n"
                                        + "Stacktrace:\n%s",
                                    item.getUuid(),
                                    ex.getMessage(),
                                    traceWriter.toString()));
                                try {
                                    notification.send();
                                } catch (MessagingException msgex) {
                                    throw new UncheckedWrapperException(msgex);
                                }
                            }
                        }

                    });

                    thread.start();

                    throw new RedirectSignal(
                        URL.getDispatcherPath()
                            + ContentItemPage.getItemURL(item,
                                                         ContentItemPage.PUBLISHING_TAB),
                        true);
                    /*
                     * jensp 2011-12-14 end
                     */
                } else {
                    republish(item, false, user);
//                    if (CMSConfig.getConfig().isUseStreamlinedCreation()) {
//                        throw new RedirectSignal(
//                            URL.there(state.getRequest(),
//                                      CmsConstants.CONTENT_CENTER_URL),
//                            true);
//                    }
                }
            }

        }

        /**
         * @author Jens Pelzetter
         */
        private class Republisher implements Runnable {

            private final String itemUuid;
            private final User user;

            private Republisher(final ContentItem item, User user) {
                itemUuid = item.getUuid();
                this.user = user;
            }

            @Override
            public void run() {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final ContentItemRepository itemRepo = cdiUtil.findBean(
                    ContentItemRepository.class);
                final ContentItem item = itemRepo.findByUuid(itemUuid).get();

//                PublishLock.getInstance().lock(item);
                republish(item, false, user);
//                PublishLock.getInstance().unlock(item);
            }

        }

    }

    private class RepublishAndResetLink extends PublishLink {

        RepublishAndResetLink() {
            super(new Label(gz("cms.ui.item.lifecycle.republish_and_reset")));

            super.addActionListener(new Listener());
            // warning gets a bit annoying, and link should be descriptive
            // enough that it is not required
            // setConfirmation("This will reset all your publication dates, are
            // you sure you want to continue?");
        }

        private class Listener implements ActionListener {

            @Override
            public final void actionPerformed(final ActionEvent e) {
                final PageState state = e.getPageState();
                final ContentItem item = selectedItem.getContentItem(state);
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final Shiro shiro = cdiUtil.findBean(Shiro.class);
                final User user = shiro.getUser().get();

                /**
                 * jensp 2011-12-14: Execute is a thread if threaded publishing
                 * is active.
                 */
                if (CMSConfig.getConfig().isThreadPublishing()) {
                    final Republisher republisher = new Republisher(item, user);
                    final Thread thread = new Thread(republisher);
                    thread.setUncaughtExceptionHandler(
                        new Thread.UncaughtExceptionHandler() {

                        @Override
                        public void uncaughtException(final Thread thread,
                                                      final Throwable ex) {
                            final StringWriter strWriter = new StringWriter();
                            final PrintWriter writer
                                                  = new PrintWriter(strWriter);
                            ex.printStackTrace(writer);

//                            PublishLock.getInstance().setError(item, strWriter
//                                                               .toString());
                            LOGGER.error(String.format(
                                "An error occurred while "
                                    + "publishing the item '%s': ",
                                item.getUuid()), ex);

                            if ((CMSConfig.getConfig().
                                 getPublishingFailureSender() == null)
                                    && (CMSConfig.getConfig().
                                            getPublishingFailureReceiver()
                                        == null)) {
                                return;
                            }

                            final UserRepository userRepo = cdiUtil.findBean(
                                UserRepository.class);
                            final User receiver = userRepo.findByEmailAddress(
                                CMSConfig.getConfig()
                                    .getPublishingFailureReceiver()).get();
                            final User sender = userRepo.findByEmailAddress(
                                CMSConfig.getConfig()
                                    .getPublishingFailureSender()).get();

                            if ((sender != null) && (receiver != null)) {
                                final Writer traceWriter = new StringWriter();
                                final PrintWriter printWriter = new PrintWriter(
                                    traceWriter);
                                ex.printStackTrace(printWriter);

                                final Mail notification = new Mail(
                                    receiver.getPrimaryEmailAddress()
                                        .getAddress(),
                                    sender.getPrimaryEmailAddress().getAddress(),
                                    String.format(
                                        "Failed to publish item '%s'",
                                        item.getUuid()));
                                notification.setBody(String.format(
                                    "Publishing item '%s' failed "
                                        + "with error message: %s.\n\n"
                                        + "Stacktrace:\n%s",
                                    item.getUuid(),
                                    ex.getMessage(),
                                    traceWriter.toString()));
                                try {
                                    notification.send();
                                } catch (MessagingException msgex) {
                                    throw new UncheckedWrapperException(msgex);
                                }
                            }
                        }

                    });

                    thread.start();

                    throw new RedirectSignal(
                        URL.getDispatcherPath()
                            + ContentItemPage.getItemURL(item,
                                                         ContentItemPage.PUBLISHING_TAB),
                        true);
                } else {
                    /**
                     * jensp 2011-12-14 end
                     */
                    republish(item, true, user);
//                    if (CMSConfig.getConfig().isUseStreamlinedCreation()) {
//                        throw new RedirectSignal(
//                            URL.there(state.getRequest(),
//                                      CmsConstants.CONTENT_CENTER_URL),
//                            true);
//                    }
                }
            }

        }

        /**
         * @author Jens Pelzetter
         */
        private class Republisher implements Runnable {

            private final String itemUuid;
            private final User user;

            private Republisher(final ContentItem item, User user) {
                itemUuid = item.getUuid();
                this.user = user;
            }

            @Override
            public void run() {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final ContentItemRepository itemRepo = cdiUtil.findBean(
                    ContentItemRepository.class);
                final ContentItem item = itemRepo.findByUuid(itemUuid).get();
//                PublishLock.getInstance().lock(item);
                republish(item, true, user);
//                PublishLock.getInstance().unlock(item);
            }

        }

    }

    private class PhaseSection extends Section {

        PhaseSection() {
            super(gz("cms.ui.lifecycle.phases"));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(new PhaseTable());
        }

    }

    private class PhaseTable extends Table {

        PhaseTable() {
            super(new ItemPhaseTableModelBuilder(selectedLifecycle),
                  new String[]{
                      lz("cms.ui.name"),
                      lz("cms.ui.description"),
                      lz("cms.ui.item.lifecycle.start_date"),
                      lz("cms.ui.item.lifecycle.end_date")
                  });
        }

    }

    /**
     * New style pane. Uses a select box for the action to avoid wrong clicks on
     * unpublish.
     *
     * @author Jens Pelzetter
     */
    private class ActionForm
        extends Form
        implements FormProcessListener,
                   FormInitListener {

        private static final String LIFECYCLE_ACTION
                                        = "itemLifecycleItemPaneActionSelect";
        private static final String REPUBLISH = "republish";
        private static final String UNPUBLISH = "unpublish";
        private static final String REPUBLISH_AND_RESET = "republishAndReset";
        private final Submit submit;
        private final Label notAuthorized;

        public ActionForm() {
            super("itemLifecycleItemPaneActionForm");

            final BoxPanel actionPanel = new BoxPanel(BoxPanel.HORIZONTAL);
            final SingleSelect actionSelect = new SingleSelect(
                LIFECYCLE_ACTION);

            actionSelect.addOption(new Option(
                REPUBLISH,
                new Label(new GlobalizedMessage(
                    "cms.ui.item.lifecycle.republish",
                    CmsConstants.CMS_BUNDLE))));
            if (!CMSConfig.getConfig().isHideResetLifecycleLink()) {
                actionSelect.addOption(new Option(
                    REPUBLISH_AND_RESET,
                    new Label(new GlobalizedMessage(
                        "cms.ui.item.lifecycle.republish_and_reset",
                        CmsConstants.CMS_BUNDLE))));
            }
            actionSelect.addOption(new Option(
                UNPUBLISH,
                new Label(gz("cms.ui.item.lifecycle.unpublish"))));

            submit = new Submit(gz("cms.ui.item.lifecycle.do"));
            notAuthorized = new Label(gz(
                "cms.ui.item.lifecycle.do.not_authorized"));

            actionPanel.add(actionSelect);
            actionPanel.add(submit);
            actionPanel.add(notAuthorized);
            add(actionPanel);

            addInitListener(this);
            addProcessListener(this);
        }

        @Override
        public void init(final FormSectionEvent event)
            throws FormProcessException {

            final PageState state = event.getPageState();
            final ContentItem item = selectedItem.getContentItem(state);

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PermissionChecker permissionChecker = cdiUtil.findBean(
                PermissionChecker.class);

            if (permissionChecker.isPermitted(ItemPrivileges.PUBLISH, item)) {
                submit.setVisible(state, true);
                notAuthorized.setVisible(state, false);
            } else {
                submit.setVisible(state, false);
                notAuthorized.setVisible(state, true);
            }
        }

        @Override
        public void process(final FormSectionEvent event)
            throws FormProcessException {

            final PageState state = event.getPageState();
            final FormData data = event.getFormData();
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final Shiro shiro = cdiUtil.findBean(Shiro.class);
            final User user = shiro.getUser().get();

            String selected = (String) data.get(LIFECYCLE_ACTION);
            final ContentItem item = selectedItem.getContentItem(state);

            /**
             * Republish/Republish and Reset are executed in the thread if
             * threaded publishing is active.
             */
            if (REPUBLISH.equals(selected)) {
                if (CMSConfig.getConfig().isThreadPublishing()) {
                    final RepublishRunner runner = new RepublishRunner(item,
                                                                       user);
                    final Thread thread = new Thread(runner);
                    thread.setUncaughtExceptionHandler(
                        new Thread.UncaughtExceptionHandler() {

                        @Override
                        public void uncaughtException(final Thread thread,
                                                      final Throwable ex) {
                            final StringWriter strWriter = new StringWriter();
                            final PrintWriter writer
                                                  = new PrintWriter(strWriter);
                            ex.printStackTrace(writer);

//                            PublishLock.getInstance().setError(item, strWriter
//                                                               .toString());
                            LOGGER.error(String.format(
                                "An error occurred while "
                                    + "publishing the item '%s': ",
                                item.getUuid()), ex);

                            if ((CMSConfig.getConfig()
                                 .getPublishingFailureSender()
                                 == null)
                                    && (CMSConfig.getConfig()
                                            .getPublishingFailureReceiver()
                                        == null)) {
                                return;
                            }

                            final UserRepository userRepo = cdiUtil.findBean(
                                UserRepository.class);
                            final User receiver = userRepo.findByEmailAddress(
                                CMSConfig.getConfig()
                                    .getPublishingFailureReceiver()).get();
                            final User sender = userRepo.findByEmailAddress(
                                CMSConfig.getConfig()
                                    .getPublishingFailureSender()).get();

                            if ((sender != null) && (receiver != null)) {
                                final Writer traceWriter = new StringWriter();
                                final PrintWriter printWriter = new PrintWriter(
                                    traceWriter);
                                ex.printStackTrace(printWriter);

                                final Mail notification = new Mail(
                                    receiver.getPrimaryEmailAddress()
                                        .getAddress(),
                                    sender.getPrimaryEmailAddress().getAddress(),
                                    String.format(
                                        "Failed to publish item '%s'",
                                        item.getUuid()));
                                notification.setBody(String.format(
                                    "Publishing item '%s' failed "
                                        + "with error message: %s.\n\n"
                                        + "Stacktrace:\n%s",
                                    item.getUuid(),
                                    ex.getMessage(),
                                    traceWriter.toString()));
                                try {
                                    notification.send();
                                } catch (MessagingException msgex) {
                                    throw new UncheckedWrapperException(msgex);
                                }
                            }
                        }

                    });

                    thread.start();

                    throw new RedirectSignal(
                        URL.getDispatcherPath()
                            + ContentItemPage.getItemURL(item,
                                                         ContentItemPage.PUBLISHING_TAB),
                        true);
                } else {
                    republish(item, false, user);

//                    if (CMSConfig.getConfig().isUseStreamlinedCreation()) {
//                        throw new RedirectSignal(
//                            URL.there(state.getRequest(),
//                                      CmsConstants.CONTENT_CENTER_URL), true);
//                    }
                }
            } else if (REPUBLISH_AND_RESET.equals(selected)) {
                if (CMSConfig.getConfig().isThreadPublishing()) {
                    final RepublishAndResetRunner runner
                                                      = new RepublishAndResetRunner(
                            item, user);
                    final Thread thread = new Thread(runner);
                    thread.setUncaughtExceptionHandler(
                        new Thread.UncaughtExceptionHandler() {

                        @Override
                        public void uncaughtException(final Thread thread,
                                                      final Throwable ex) {
                            final StringWriter strWriter = new StringWriter();
                            final PrintWriter writer
                                                  = new PrintWriter(strWriter);
                            ex.printStackTrace(writer);

//                            PublishLock.getInstance().setError(item, strWriter
//                                                               .toString());
                            LOGGER.error(String.format(
                                "An error occurred while "
                                    + "publishing the item '%s': ",
                                item.getUuid()), ex);

                            if ((CMSConfig.getConfig()
                                 .getPublishingFailureSender()
                                 == null)
                                    && (CMSConfig.getConfig()
                                            .getPublishingFailureReceiver()
                                        == null)) {
                                return;
                            }

                            final UserRepository userRepo = cdiUtil.findBean(
                                UserRepository.class);
                            final User receiver = userRepo.findByEmailAddress(
                                CMSConfig.getConfig()
                                    .getPublishingFailureReceiver()).get();
                            final User sender = userRepo.findByEmailAddress(
                                CMSConfig.getConfig()
                                    .getPublishingFailureSender()).get();

                            if ((sender != null) && (receiver != null)) {
                                final Writer traceWriter = new StringWriter();
                                final PrintWriter printWriter = new PrintWriter(
                                    traceWriter);
                                ex.printStackTrace(printWriter);

                                final Mail notification = new Mail(
                                    receiver.getPrimaryEmailAddress()
                                        .getAddress(),
                                    sender.getPrimaryEmailAddress().getAddress(),
                                    String.format(
                                        "Failed to publish item '%s'",
                                        item.getUuid()));
                                notification.setBody(String.format(
                                    "Publishing item '%s' failed "
                                        + "with error message: %s.\n\n"
                                        + "Stacktrace:\n%s",
                                    item.getUuid(),
                                    ex.getMessage(),
                                    traceWriter.toString()));
                                try {
                                    notification.send();
                                } catch (MessagingException msgex) {
                                    throw new UncheckedWrapperException(msgex);
                                }
                            }
                        }

                    });

                    thread.start();

                    throw new RedirectSignal(
                        URL.getDispatcherPath()
                            + ContentItemPage.getItemURL(item,
                                                         ContentItemPage.PUBLISHING_TAB),
                        true);
                } else {
                    republish(item, true, user);

//                    if (CMSConfig.getConfig().isUseStreamlinedCreation()) {
//                        throw new RedirectSignal(
//                            URL.there(state.getRequest(),
//                                      CmsConstants.CONTENT_CENTER_URL), true);
//                    }
                }
            } else if (UNPUBLISH.equals(selected)) {
                final ContentItemManager itemManager = cdiUtil.findBean(
                    ContentItemManager.class);
                itemManager.unpublish(item);
            } else {
                throw new IllegalArgumentException("Illegal selection");
            }
        }

        private class RepublishRunner implements Runnable {

            private final String itemUuid;
            private final User user;

            private RepublishRunner(final ContentItem item,
                                    final User user) {
                itemUuid = item.getUuid();
                this.user = user;
            }

            private void doRepublish() {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final ContentItemRepository itemRepo = cdiUtil.findBean(
                    ContentItemRepository.class);
                final ContentItem item = itemRepo.findByUuid(itemUuid).get();
                republish(item, false, user);
            }

            @Override
            public void run() {
//                PublishLock.getInstance().lock(item);
                doRepublish();
//                PublishLock.getInstance().unlock(item);
            }

        }

        private class RepublishAndResetRunner implements Runnable {

            private final String itemUuid;
            private final User user;

            private RepublishAndResetRunner(final ContentItem item,
                                            final User user) {
                itemUuid = item.getUuid();
                this.user = user;
            }

            private void doRepublishAndReset() {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final ContentItemRepository itemRepo = cdiUtil.findBean(
                    ContentItemRepository.class);
                final ContentItem item = itemRepo.findByUuid(itemUuid).get();
                republish(item, true, user);
            }

            @Override
            public void run() {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final ContentItemRepository itemRepo = cdiUtil.findBean(
                    ContentItemRepository.class);
                final ContentItem item = itemRepo.findByUuid(itemUuid).get();
//                PublishLock.getInstance().lock(item);
                doRepublishAndReset();
//                PublishLock.getInstance().unlock(item);
            }

        }

    }

}
