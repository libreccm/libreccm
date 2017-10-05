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

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Date;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.NumberInRangeValidationListener;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ui.BaseForm;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.cms.ui.item.ContentItemRequestLocal;
import com.arsdigita.cms.ui.item.ItemWorkflowRequestLocal;
import com.arsdigita.cms.ui.workflow.WorkflowRequestLocal;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.mail.Mail;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arsdigita.cms.CMSConfig;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.Shiro;
import org.libreccm.security.User;
import org.libreccm.security.UserRepository;
import org.libreccm.workflow.Task;
import org.libreccm.workflow.TaskManager;
import org.libreccm.workflow.Workflow;
import org.libreccm.workflow.WorkflowManager;
import org.libreccm.workflow.WorkflowRepository;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.privileges.ItemPrivileges;
import org.librecms.lifecycle.Lifecycle;
import org.librecms.lifecycle.LifecycleDefinition;
import org.librecms.lifecycle.LifecycleDefinitionRepository;
import org.librecms.lifecycle.LifecycleManager;
import org.librecms.lifecycle.Phase;
import org.librecms.lifecycle.PhaseDefinition;
import org.librecms.lifecycle.PhaseRepository;
import org.librecms.workflow.CmsTask;
import org.librecms.workflow.CmsTaskManager;
import org.librecms.workflow.CmsTaskType;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.TooManyListenersException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * A form to select and apply a lifecycle to a content item.</p>
 *
 * @author Michael Pih
 * @author Xixi D'moon &lt;xdmoon@redhat.com&gt;
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class ItemLifecycleSelectForm extends BaseForm {

    private static final Logger LOGGER = LogManager.getLogger(
        ItemLifecycleSelectForm.class);

    private final static String LIFECYCLE = "lifecycle";
    private final static String START_DATE = "start_date";
    private final static String END_DATE = "end_date";
    private final static String NOTIFICATION_DAYS = "notifyDays";
    private final static String NOTIFICATION_HOURS = "notifyHours";

    private final ContentItemRequestLocal itemRequestLocal;
    private final WorkflowRequestLocal workflowRequestLocal;

    // Form widgets
    private final SingleSelect cycleSelect;
    private final Date startDateField;
    private final TextField startHourField;
    private final TextField startMinuteField;
    private final SingleSelect startAmpmSelect;
    private final Date endDateField;
    private final TextField endHourField;
    private final TextField endMinuteField;
    private final SingleSelect endAmpmSelect;
    private final TextField notificationDaysField;
    private final TextField notificationHoursField;

    public ItemLifecycleSelectForm(final ContentItemRequestLocal item) {
        super("PublishItem", gz("cms.ui.item.lifecycle.apply"));

        this.itemRequestLocal = item;
        workflowRequestLocal = new ItemWorkflowRequestLocal();

        cycleSelect = new SingleSelect(new BigDecimalParameter(LIFECYCLE));
        try {
            cycleSelect.addPrintListener(new OptionPrinter());
        } catch (TooManyListenersException tmle) {
            throw new UncheckedWrapperException(tmle);
        }
        addField(gz("cms.ui.item.lifecycle"), cycleSelect);

        // Start date
        startDateField = new Date(new DateParameter(START_DATE) {

            @Override
            protected final Calendar getCalendar(final HttpServletRequest sreq) {
                final Calendar cal = super.getCalendar(sreq);

                cal.setLenient(false);

                return cal;
            }

        });
        addField(gz("cms.ui.item.lifecycle.start_date"), startDateField);

        // Start time
        final BoxPanel startTime = new BoxPanel(BoxPanel.HORIZONTAL);
        addField(gz("cms.ui.item.lifecycle.start_time"), startTime);

        // Hour
        startHourField = new TextField(new IntegerParameter("start_hour"));
        startTime.add(startHourField);

        startHourField.setSize(3);
        startHourField.addValidationListener(
            new NumberInRangeValidationListener(1, 12));

        // Minute
        startMinuteField = new TextField(new IntegerParameter("start_minute"));
        startTime.add(startMinuteField);

        startMinuteField.setSize(3);
        startMinuteField.addValidationListener(
            new NumberInRangeValidationListener(
                0, 59));

        // AM/PM
        startAmpmSelect = new SingleSelect(new IntegerParameter("start_ampm"));
        startTime.add(startAmpmSelect);

        startAmpmSelect.addOption(new Option("0", "am"));
        startAmpmSelect.addOption(new Option("1", "pm"));

        // Time zone
        startTime.add(new Label(new TimeZonePrinter()));

        // Expiration date
        endDateField = new Date(new DateParameter(END_DATE) {

            @Override
            protected final Calendar getCalendar(final HttpServletRequest sreq) {
                final Calendar cal = super.getCalendar(sreq);

                cal.setLenient(false);

                return cal;
            }

        });
        addField(gz("cms.ui.item.lifecycle.end_date"), endDateField);

        // End time
        final BoxPanel endTime = new BoxPanel(BoxPanel.HORIZONTAL);
        addField(gz("cms.ui.item.lifecycle.end_time"), endTime);

        // Hour
        endHourField = new TextField(new IntegerParameter("end_hour"));
        endTime.add(endHourField);

        endHourField.setSize(3);
        endHourField.addValidationListener(
            new NumberInRangeValidationListener(1,
                                                12));

        // Minute
        endMinuteField = new TextField(new IntegerParameter("end_minute"));
        endTime.add(endMinuteField);

        endMinuteField.setSize(3);
        endMinuteField.addValidationListener(
            new NumberInRangeValidationListener(0, 59));

        // AM/PM
        endAmpmSelect = new SingleSelect(new IntegerParameter("end_ampm"));
        endTime.add(endAmpmSelect);

        endAmpmSelect.addOption(new Option("0", "am"));
        endAmpmSelect.addOption(new Option("1", "pm"));

        endTime.add(new Label(new TimeZonePrinter()));

        notificationDaysField = new TextField(new IntegerParameter(
            NOTIFICATION_DAYS));
        notificationDaysField.setSize(4);
        notificationHoursField = new TextField(new IntegerParameter(
            NOTIFICATION_HOURS));
        notificationHoursField.setSize(4);
        SimpleContainer cont = new SimpleContainer();
        cont.add(notificationDaysField);
        cont.add(new Label(new GlobalizedMessage("cms.ui.item.days",
                                                 CmsConstants.CMS_BUNDLE),
                           false));
        cont.add(notificationHoursField);
        cont.add(new Label(new GlobalizedMessage("cms.ui.item.hours",
                                                 CmsConstants.CMS_BUNDLE),
                           false));

        addField(gz("cms.ui.item.notification_period"), cont);

        // A hidden field that checks to see if the user wants publish
        // with a start time earlier than current time.
        addAction(new Submit("finish", gz("cms.ui.item.lifecycle.publish")));

        // Form listeners
        super.addValidationListener(new ValidationListener());
        super.addSecurityListener(ItemPrivileges.PUBLISH, item);
        super.addInitListener(new InitListener());
        super.addProcessListener(new ProcessListener());
    }

    private class OptionPrinter implements PrintListener {

        @Override
        public final void prepare(final PrintEvent event) {
            final ContentSection section = CMS.getContext().getContentSection();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ItemLifecycleAdminController controller = cdiUtil
                .findBean(ItemLifecycleAdminController.class);

            final List<LifecycleDefinition> definitions = controller
                .getLifecycleDefinitions(section);

            final SingleSelect target = (SingleSelect) event.getTarget();
            target.clearOptions();

            final GlobalizationHelper globalizationHelper = cdiUtil.findBean(
                GlobalizationHelper.class);
            final Locale locale = globalizationHelper.getNegotiatedLocale();

            for (final LifecycleDefinition definition : definitions) {
                final List<PhaseDefinition> phaseDefinitions = definition
                    .getPhaseDefinitions();

                if (!phaseDefinitions.isEmpty()) {
                    target.addOption(new Option(
                        Long.toString(definition.getDefinitionId()),
                        new Text(definition.getLabel().getValue(locale))));
                }
            }
        }

    }

    private class InitListener implements FormInitListener {

        @Override
        public final void init(final FormSectionEvent event) {
            final PageState state = event.getPageState();

            final ContentItem item = itemRequestLocal.getContentItem(state);

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ContentItemManager itemManager = cdiUtil.findBean(
                ContentItemManager.class);
            final ItemLifecycleAdminController controller = cdiUtil
                .findBean(ItemLifecycleAdminController.class);

            if (itemManager.isLive(item)) {
                // If the item is published, select the currently
                // associated lifecycle.

                final LifecycleDefinition definition = item.getLifecycle()
                    .getDefinition();
                cycleSelect.setValue(state, definition.getDefinitionId());
            } else {
                // Set the default lifecycle (if it exists).
                final LifecycleDefinition definition = controller
                    .getDefaultLifecycle(item);

                if (definition != null) {
                    cycleSelect.setValue(state, definition.getDefinitionId());
                }
            }

            // Set the default start date.
            // XXX Isn't just new Date() sufficient?
            final java.util.Date start = new java.util.Date();
            startDateField.setValue(state, start);

            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(start);

            // If the hour is 12, then Calendar.get(Calendar.HOUR)
            // returns 0 (from the 24 hour time - 12).  We want it to
            // return 12.
            if (calendar.get(Calendar.HOUR) == 0) {
                startHourField.setValue(state, 12);
            } else {
                startHourField.setValue(state, calendar.get(Calendar.HOUR));
            }

            final Integer min = calendar.get(Calendar.MINUTE);

            if (min < 10) {
                startMinuteField.setValue(state, "0" + min.toString());
            } else {
                startMinuteField.setValue(state, min.toString());
            }

            startAmpmSelect.setValue(state, calendar.get(Calendar.AM_PM));

            final int defaultIime = CMSConfig.getConfig()
                .getDefaultNotificationTime();
            final int defaultTimeDays = defaultIime / 24;
            final int defaultTimeHours = defaultIime % 24;

            notificationDaysField.setValue(state, defaultTimeDays);
            notificationHoursField.setValue(state, defaultTimeHours);
        }

    }

    /**
     * jensp 2011-12-14: Some larger changes to the behavior of the process
     * listener. The real action has been moved to the
     * @link{Publisher} class. If threaded publishing is active, the publish
     * process runs in a separate thread (the item is locked before using
     * {@link PublishLock}. If threaded publishing is not active, nothing has
     * changed.
     */
    private class ProcessListener implements FormProcessListener {

        @Override
        public final void process(final FormSectionEvent event)
            throws FormProcessException {
            final PageState state = event.getPageState();
            final ContentItem item = itemRequestLocal.getContentItem(state);
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();

            final Publisher publisher = new Publisher(state);
            if (CMSConfig.getConfig().isThreadPublishing()) {
                final Runnable threadAction = new Runnable() {

                    @Override
                    public void run() {
//                        PublishLock.getInstance().lock(item);
                        publisher.publish();
//                        PublishLock.getInstance().unlock(item);
                    }

                };
                final Thread thread = new Thread(threadAction);
                thread.setUncaughtExceptionHandler(
                    new Thread.UncaughtExceptionHandler() {

                    @Override
                    public void uncaughtException(final Thread thread,
                                                  final Throwable ex) {
                        final StringWriter strWriter = new StringWriter();
                        final PrintWriter writer = new PrintWriter(strWriter);
                        ex.printStackTrace(writer);

//                        PublishLock.getInstance().setError(item, strWriter
//                                                           .toString());
                        LOGGER.error(String.format(
                            "An error occurred while "
                                + "publishing the item '%s': ",
                            item.getUuid()), ex);

                        if ((CMSConfig.getConfig()
                             .getPublishingFailureSender() == null)
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
            } else {
                publisher.publish();
            }

            if (CMSConfig.getConfig().isThreadPublishing()) {
                throw new RedirectSignal(
                    URL.getDispatcherPath()
                        + ContentItemPage.getItemURL(item,
                                                     ContentItemPage.PUBLISHING_TAB),
                    true);
            } else {
                if (CMSConfig.getConfig().isUseStreamlinedCreation()) {
                    throw new RedirectSignal(
                        URL.there(state.getRequest(),
                                  CmsConstants.CONTENT_CENTER_URL), true);
                }
            }

            /*
             * final Integer startHour = (Integer) m_startHour.getValue(state);
             * Integer startMinute = (Integer) m_startMinute.getValue(state);
             *
             * if (startMinute == null) { startMinute = new Integer(0); }
             *
             * final Integer startAmpm = (Integer) m_startAmpm.getValue(state);
             *
             * final Integer endHour = (Integer) m_endHour.getValue(state);
             * Integer endMinute = (Integer) m_endMinute.getValue(state);
             *
             * if (endMinute == null) { endMinute = new Integer(0); }
             *
             * final Integer endAmpm = (Integer) m_endAmpm.getValue(state);
             *
             * // Instantiate the instance of the content type. final
             * ContentItem item = m_item.getContentItem(state);
             *
             * final BigDecimal defID = (BigDecimal)
             * m_cycleSelect.getValue(state); Assert.exists(defID); final
             * LifecycleDefinition cycleDef = new LifecycleDefinition(defID);
             *
             * java.util.Date startDate = (java.util.Date)
             * m_startDate.getValue(state);
             *
             * final Calendar start = Calendar.getInstanceOf();
             * start.setTime(startDate); start.set(Calendar.AM_PM,
             * startAmpm.intValue()); start.set(Calendar.MINUTE,
             * startMinute.intValue()); start.set(Calendar.AM_PM,
             * startAmpm.intValue()); if (startHour.intValue() != 12) {
             * start.set(Calendar.HOUR_OF_DAY, 12 * startAmpm.intValue() +
             * startHour.intValue()); start.set(Calendar.HOUR,
             * startHour.intValue()); } else { if (startAmpm.intValue() == 0) {
             * start.set(Calendar.HOUR_OF_DAY, 0); start.set(Calendar.HOUR, 0);
             * } else { start.set(Calendar.HOUR_OF_DAY, 12);
             * start.set(Calendar.HOUR, 0); } } startDate = start.getTime();
             *
             * java.util.Date endDate = (java.util.Date)
             * m_endDate.getValue(state);
             *
             * if (endDate != null) { final Calendar end =
             * Calendar.getInstanceOf();
             *
             * end.setTime(endDate); end.set(Calendar.AM_PM,
             * endAmpm.intValue()); end.set(Calendar.MINUTE,
             * endMinute.intValue()); end.set(Calendar.AM_PM,
             * endAmpm.intValue());
             *
             * if (endHour.intValue() != 12) { end.set(Calendar.HOUR_OF_DAY, 12
             * * endAmpm.intValue() + endHour.intValue());
             * end.set(Calendar.HOUR, endHour.intValue()); } else { if
             * (endAmpm.intValue() == 0) { end.set(Calendar.HOUR_OF_DAY, 0);
             * end.set(Calendar.HOUR, 0); } else { end.set(Calendar.HOUR_OF_DAY,
             * 12); end.set(Calendar.HOUR, 0); } } endDate = end.getTime(); }
             *
             * // If the item is already published, remove the current
             * lifecycle. // Do not touch the live version. if
             * (item.isPublished()) { item.removeLifecycle(item); item.save(); }
             *
             * // Apply the new lifecycle. ContentItem pending =
             * item.publish(cycleDef, startDate); final Lifecycle lifecycle =
             * pending.getLifecycle();
             *
             * // XXX domlay Whoa. This must be broken for multiphase //
             * lifecycles.
             *
             * if (endDate != null) {
             *
             * // update individual phases final PhaseCollection phases =
             * lifecycle.getPhases();
             *
             * while (phases.next()) { final Phase phase = phases.getPhase();
             * java.util.Date thisEnd = phase.getEndDate(); java.util.Date
             * thisStart = phase.getStartDate(); if
             * (thisStart.compareTo(endDate) > 0) { phase.setStartDate(endDate);
             * phase.save(); }
             *
             * if (thisEnd == null || thisEnd.compareTo(endDate) > 0) {
             * phase.setEndDate(endDate); phase.save(); } } }
             *
             * // endOfCycle may be the original date according to lifecycle
             * phase definitions, or endDate if that was before // natural end
             * of lifecycle java.util.Date endOfCycle = lifecycle.getEndDate();
             * if (endOfCycle != null) {
             *
             * // if advance notification is requested (!= 0) // add another
             * phase at the start of which the user is notified Integer
             * notificationDays = (Integer) m_notificationDays.getValue(state);
             * Integer notificationHours = (Integer)
             * m_notificationHours.getValue(state); java.util.Date
             * notificationDate = null;
             *
             * int notificationPeriod = 0; if (notificationDays != null) {
             * notificationPeriod += notificationDays.intValue() * 24; } if
             * (notificationHours != null) { notificationPeriod +=
             * notificationHours.intValue(); }
             *
             * if (notificationPeriod > 0) { notificationDate =
             * computeNotificationDate(endOfCycle, notificationPeriod);
             * s_log.debug("adding custom phase"); Phase expirationImminentPhase
             * = lifecycle.addCustomPhase("expirationImminent", new
             * Long(notificationDate. getTime()), new
             * Long(endOfCycle.getTime()));
             * expirationImminentPhase.setListenerClassName(
             * "org.librecms.lifecycle.NotifyLifecycleListener");
             * expirationImminentPhase.save(); } }
             *
             * // Force the lifecycle scheduler to run to avoid any //
             * scheduler delay for items that should be published //
             * immediately. pending.getLifecycle().start();
             *
             * item.save();
             *
             * final Workflow workflow = m_workflow.getWorkflow(state); try {
             * finish(workflow, item, Web.getWebContext().getUser()); } catch
             * (TaskException te) { throw new FormProcessException(te); } //
             * redirect to /content-center if streamlined creation mode is
             * active. if
             * (ContentSection.getConfig().getUseStreamlinedCreation()) { throw
             * new RedirectSignal(URL.there(state.getRequest(),
             * Utilities.getWorkspaceURL()), true); }
             */
        }

    }

    /**
     * This class contains the real publish action.
     */
    private class Publisher {

        private final Integer startHour;
        private final Integer startMinute;
        private final Integer startAmpm;
        private final Integer endHour;
        private final Integer endMinute;
        private final Integer endAmpm;
        private final String itemUuid;
        private final Long defID;
        private final java.util.Date startDate;
        private final java.util.Date endDate;
        private final Integer notificationDays;
        private final Integer notificationHours;
        private final String workflowUuid;
        private final User user;

        /**
         * The constructor collects all necessary data and stores them.
         *
         * @param state
         */
        public Publisher(final PageState state) {
            startHour = (Integer) startHourField.getValue(state);
            if (startMinuteField.getValue(state) == null) {
                startMinute = 0;
            } else {
                startMinute = (Integer) startMinuteField.getValue(state);
            }
            startAmpm = (Integer) startAmpmSelect.getValue(state);

            endHour = (Integer) endHourField.getValue(state);
            if (endMinuteField.getValue(state) == null) {
                endMinute = 0;
            } else {
                endMinute = (Integer) endMinuteField.getValue(state);
            }
            endAmpm = (Integer) endAmpmSelect.getValue(state);

            //item = m_item.getContentItem(state);
            itemUuid = itemRequestLocal.getContentItem(state).getItemUuid();

            defID = (Long) cycleSelect.getValue(state);

            final Calendar start = Calendar.getInstance();
            start.setTime((java.util.Date) startDateField.getValue(state));
            start.set(Calendar.AM_PM, startAmpm);
            start.set(Calendar.MINUTE, startMinute);
            start.set(Calendar.AM_PM, startAmpm);
            if (startHour != 12) {
                start.set(Calendar.HOUR_OF_DAY,
                          12 * startAmpm + startHour);
                start.set(Calendar.HOUR, startHour);
            } else {
                if (startAmpm == 0) {
                    start.set(Calendar.HOUR_OF_DAY, 0);
                    start.set(Calendar.HOUR, 0);
                } else {
                    start.set(Calendar.HOUR_OF_DAY, 12);
                    start.set(Calendar.HOUR, 0);
                }
            }
            startDate = start.getTime();

            if (endDateField.getValue(state) == null) {
                endDate = null;
            } else {
                final Calendar end = Calendar.getInstance();

                end.setTime((java.util.Date) endDateField.getValue(state));
                end.set(Calendar.AM_PM, endAmpm);
                end.set(Calendar.MINUTE, endMinute);
                end.set(Calendar.AM_PM, endAmpm);

                if (endHour != 12) {
                    end.set(Calendar.HOUR_OF_DAY,
                            12 * endAmpm + endHour);
                    end.set(Calendar.HOUR, endHour);
                } else {
                    if (endAmpm == 0) {
                        end.set(Calendar.HOUR_OF_DAY, 0);
                        end.set(Calendar.HOUR, 0);
                    } else {
                        end.set(Calendar.HOUR_OF_DAY, 12);
                        end.set(Calendar.HOUR, 0);
                    }
                }
                endDate = end.getTime();
            }

            notificationDays = (Integer) notificationDaysField.getValue(state);
            notificationHours = (Integer) notificationHoursField.getValue(state);

            if (workflowRequestLocal.getWorkflow(state) != null) {
                workflowUuid = workflowRequestLocal.getWorkflow(state).getUuid();
            } else {
                workflowUuid = null;
            }

            user = CdiUtil.createCdiUtil().findBean(Shiro.class).getUser().get();
        }

        /**
         * Published the item
         */
        public void publish() {

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ContentItemRepository itemRepo = cdiUtil.findBean(
                ContentItemRepository.class);
            final ContentItemManager itemManager = cdiUtil.findBean(
                ContentItemManager.class);
            final PhaseRepository phaseRepo = cdiUtil.findBean(
                PhaseRepository.class);
            final LifecycleDefinitionRepository lifecycleDefRepo = cdiUtil
                .findBean(LifecycleDefinitionRepository.class);
            final LifecycleManager lifecycleManager = cdiUtil.findBean(
                LifecycleManager.class);

            final ContentItem item = itemRepo.findByUuid(itemUuid).get();

            // If the item is already published, remove the current lifecycle.
            // Do not touch the live version.
            if (itemManager.isLive(item)) {
                item.setLifecycle(null);
                itemRepo.save(item);
            }

            ContentItem pending;
            final LifecycleDefinition cycleDef;
            final Lifecycle lifecycle;
            // Apply the new lifecycle.
            cycleDef = lifecycleDefRepo.findById(defID).get();
            pending = itemManager.publish(item, cycleDef);
            lifecycle = pending.getLifecycle();

            if (endDate != null) {

                // update individual phases
                final List<Phase> phases = lifecycle.getPhases();

                for (final Phase phase : phases) {
                    final java.util.Date thisEnd = phase.getEndDateTime();
                    final java.util.Date thisStart = phase.getStartDateTime();
                    if (thisStart.compareTo(endDate) > 0) {
                        phase.setStartDateTime(endDate);
                        phaseRepo.save(phase);
                    }
                }
            }

            // endOfCycle may be the original date according to lifecycle phase definitions, or endDate if that was before
            // natural end of lifecycle
            final java.util.Date endOfCycle = lifecycle.getEndDateTime();
            if (endOfCycle != null) {

                // if advance notification is requested (!= 0)
                // add another phase at the start of which the user is notified
                java.util.Date notificationDate;

                int notificationPeriod = 0;
                if (notificationDays != null) {
                    notificationPeriod += notificationDays * 24;
                }
                if (notificationHours != null) {
                    notificationPeriod += notificationHours;
                }
            }

            // Force the lifecycle scheduler to run to avoid any
            // scheduler delay for items that should be published
            // immediately.
            lifecycleManager.startLifecycle(pending.getLifecycle());

            if (workflowUuid != null) {
                final WorkflowRepository workflowRepo = cdiUtil.findBean(
                    WorkflowRepository.class);
                final Workflow workflow = workflowRepo.findByUuid(workflowUuid)
                    .get();
                finish(workflow, item, user);
            }
        }

    }

    static void finish(final Workflow workflow,
                       final ContentItem item,
                       final User user) {
        if ((workflow != null) && (user != null)) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final WorkflowRepository workflowRepo = cdiUtil.findBean(
                WorkflowRepository.class);
            final WorkflowManager workflowManager = cdiUtil.findBean(
                WorkflowManager.class);
            final TaskManager taskManager = cdiUtil.findBean(TaskManager.class);
            final CmsTaskManager cmsTaskManager = cdiUtil.findBean(
                CmsTaskManager.class);

            final List<Task> enabledTasks = workflowManager.findEnabledTasks(
                workflow);
            for (final Task task : enabledTasks) {
                LOGGER.debug("Task is {}.", task.getUuid());
                if (task instanceof CmsTask) {
                    final CmsTask cmsTask = (CmsTask) task;

                    if (cmsTask.getTaskType() == CmsTaskType.DEPLOY) {
                        LOGGER.debug("Found DEPLOY task.");
                        taskManager.finish(cmsTask);
                    }
                }
            }

            if (CMSConfig.getConfig().isDeleteWorkflowAfterPublication()) {
                workflowRepo.delete(workflow);
            } else {
                // restart the workflow by recreating it
                // from the same workflow template
                final Workflow template = workflow.getTemplate();
                workflowRepo.delete(workflow);
                final Workflow restarted = workflowManager.createWorkflow(
                    template, item);
                // Startring the workflow will probably do the wrong thing, because most of the time
                // the current user would be a publisher, not an author 
                workflowRepo.save(restarted);
            }
        }
    }

    private class ValidationListener implements FormValidationListener {

        @Override
        public void validate(final FormSectionEvent event) throws
            FormProcessException {
            final PageState state = event.getPageState();

            final Integer startHour = (Integer) startHourField.getValue(state);
            if (startHour == null) {
                throw new FormProcessException(new GlobalizedMessage(
                    "cms.ui.item.start_time_incomplete",
                    CmsConstants.CMS_BUNDLE));
            }

            final Integer startMinute;
            if (startMinuteField.getValue(state) == null) {
                startMinute = 0;
            } else {
                startMinute = (Integer) startMinuteField.getValue(state);
            }

            final Integer startAmpm = (Integer) startAmpmSelect.getValue(state);

            java.util.Date startDate = (java.util.Date) startDateField.getValue(
                state);
            if (startDate == null) {
                throw new FormProcessException(new GlobalizedMessage(
                    "cms.ui.item.lifecycle.start_date_invalid",
                    CmsConstants.CMS_BUNDLE));
            }

            final java.util.Date nowDate = new java.util.Date();

            final Calendar cStart = Calendar.getInstance();
            final Calendar cNow = Calendar.getInstance();
            cStart.setTime(startDate);
            cNow.setTime(nowDate);

            if (startHour != 12) {
                cStart.set(Calendar.HOUR_OF_DAY,
                           12 * startAmpm + startHour);
                cStart.set(Calendar.HOUR, startHour);
            } else {
                if (startAmpm == 0) {
                    cStart.set(Calendar.HOUR_OF_DAY, 0);
                    cStart.set(Calendar.HOUR, 0);
                } else {
                    cStart.set(Calendar.HOUR_OF_DAY, 12);
                    cStart.set(Calendar.HOUR, 0);
                }
            }

            // Give the user extra 5 minutes before form complains
            // start time's in the past.
            cStart.set(Calendar.MINUTE, startMinute + 5);
            cStart.set(Calendar.AM_PM, startAmpm);
            cStart.set(Calendar.SECOND, cNow.get(Calendar.SECOND));
            cStart.set(Calendar.MILLISECOND, cNow.get(Calendar.MILLISECOND));

            if (cNow.after(cStart)) {
                throw new FormProcessException(new GlobalizedMessage(
                    "cms.ui.item.lifecycle.start_date_in_past",
                    CmsConstants.CMS_BUNDLE));
            }

            final Integer endHour = (Integer) endHourField.getValue(state);
            final Integer endMinuteTmp = (Integer) endMinuteField
                .getValue(state);
            final java.util.Date endDate = (java.util.Date) endDateField
                .getValue(state);

            if (endHour == null && (endMinuteTmp != null || endDate != null)) {
                throw new FormProcessException(new GlobalizedMessage(
                    "cms.ui.item.lifecycle.end_time_incomplete",
                    CmsConstants.CMS_BUNDLE));
            }

            final Integer endMinute;
            if (endMinuteTmp == null && endHour != null) {
                endMinute = 0;
            } else {
                endMinute = endMinuteTmp;
            }

            boolean timeBlank = (endHour == null) && (endMinute == null);

            final Integer endAmpm = (Integer) endAmpmSelect.getValue(state);

            if (endDate == null && !timeBlank) {
                throw new FormProcessException(new GlobalizedMessage(
                    "cms.ui.item.lifecycle.end_date_invalid",
                    CmsConstants.CMS_BUNDLE));
            }

            if (endDate != null) {
                final Calendar cEnd = Calendar.getInstance();
                cEnd.setTime(endDate);

                if (endHour != 12) {
                    cEnd.set(Calendar.HOUR_OF_DAY,
                             12 * endAmpm + endHour);
                    cEnd.set(Calendar.HOUR, endHour);
                } else {
                    if (endAmpm == 0) {
                        cEnd.set(Calendar.HOUR_OF_DAY, 0);
                        cEnd.set(Calendar.HOUR, 0);
                    } else {
                        cEnd.set(Calendar.HOUR_OF_DAY, 12);
                        cEnd.set(Calendar.HOUR, 0);
                    }
                }

                // Give the user extra 5 minutes before form complains
                // end time's in the past.
                cEnd.set(Calendar.MINUTE, endMinute + 5);
                cEnd.set(Calendar.AM_PM, endAmpm);
                cEnd.set(Calendar.SECOND, cNow.get(Calendar.SECOND));
                cEnd.set(Calendar.MILLISECOND, cNow.get(Calendar.MILLISECOND));

                //check if the end date is prior to the start date
                if (cStart.after(cEnd)) {
                    throw new FormProcessException(new GlobalizedMessage(
                        "cms.ui.item.lifecycle.end_date_before_start_date",
                        CmsConstants.CMS_BUNDLE));
                }

                final Integer notificationDays = (Integer) notificationDaysField
                    .getValue(state);
                final Integer notificationHours
                                  = (Integer) notificationHoursField
                        .getValue(state);

                int notificationPeriod = 0;
                if (notificationDays != null) {
                    notificationPeriod += notificationDays * 24;
                }
                if (notificationHours != null) {
                    notificationPeriod += notificationHours;
                }

                if (notificationPeriod > 0) {
                    // point in time for notification == end date - notificationPeriod
                    final java.util.Date notificationDate
                                             = computeNotificationDate(
                            cEnd.getTime(),
                            notificationPeriod);
                    LOGGER.debug("cStart (Date): {}", cStart.getTime());
                    LOGGER.debug("notificationDate: {}", notificationDate);
                    // complain if date for notification is before the start date
                    if (notificationDate.before(cStart.getTime())) {
                        LOGGER.debug("notification date is before start date!");

                        throw new FormProcessException(new GlobalizedMessage(
                            "cms.ui.item.notification_period_before_start",
                            CmsConstants.CMS_BUNDLE));
                    } else {
                        LOGGER.debug(
                            "notification date is after start date, OK");
                    }
                }
            }
        }

    }

    public class TimeZonePrinter implements PrintListener {

        @Override
        public void prepare(final PrintEvent event) {
            final Label target = (Label) event.getTarget();
            if (CMSConfig.getConfig().isHideTimezone()) {
                target.setLabel("");
            } else {
                final PageState state = event.getPageState();
                final Calendar mStart = Calendar.getInstance();
                final java.util.Date startDate = (java.util.Date) startDateField
                    .getValue(state);

                if (startDate != null) {
                    mStart.setTime((java.util.Date) startDateField.getValue(
                        state));
                }

                final String zone = mStart.getTimeZone().getDisplayName(true,
                                                                        TimeZone.SHORT);

                target.setLabel(zone);
            }
        }

    }

    /**
     * Find out at which date a notification (about an item that is about to
     * expire) should be sent, based on the endDate (== date at which the item
     * is unpublished) and the notification period.
     *
     * @param endDate      the endDate of the lifecycle, i.e. the date when the
     *                     item is going to be unpublished
     * @param notification how many hours the users shouls be notified in
     *                     advance
     */
    private java.util.Date computeNotificationDate(final java.util.Date endDate,
                                                   final int notificationPeriod) {
        if (endDate == null) {
            return null;
        }

        return new java.util.Date(
            endDate.getTime() - notificationPeriod * 3600000L);
    }

}
