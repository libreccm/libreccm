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
package org.librecms.contentsection;

import com.arsdigita.cms.ui.authoring.ItemCategoryStep;
import com.arsdigita.cms.ui.permissions.ItemPermissionsStep;
import com.arsdigita.cms.ui.authoring.assets.relatedinfo.RelatedInfoStep;
import com.arsdigita.cms.ui.authoring.assets.images.ImageStep;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.Configuration;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.configuration.Setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Global settings for content sections. Some of these settings control the
 * initial values for new content sections.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Configuration
public class ContentSectionConfig {

    /**
     * A list of workflow tasks, and the associated events for which alerts have
     * to be sent. Parameter name TASK_ALERTS in the old initialiser system /
     * enterprise.init Specifies when to generate email alerts: by default,
     * generate email alerts on enable, finish, and rollback (happens on
     * rejection) changes. There are four action types for each task type:
     * enable, disable, finish, and rollback. Example: (Note that the values
     * below are based on the task labels, and as such are not globalised.)
     * <pre>
     * taskAlerts = {
     *      { "Authoring",
     *        { "enable", "finish", "rollback" }
     *      },
     *      { "Approval",
     *        { "enable", "finish", "rollback" }
     *      },
     *      { "Deploy",
     *        { "enable", "finish", "rollback" }
     *      }
     *  };
     * </pre>
     *
     * In the new Initialiser system we use a specifically formatted String
     * Array because we have no List parameter. Format: - A string for each task
     * to handle, possible values: Authoring, Approval, Deploy - Each Task
     * String: [taskName]:[alert_1]:...:[alert_n] The specially formatted string
     * is not handled by StringArray parameter, but forwarded untouched to the
     * initialiser which has the duty to process it!
     *
     * Currently there is no way to persist taskAlerts section specific. So all
     * sections have to treated equally. Default values are provided here.
     */
    @Setting
    private List<String> taskAlerts = Arrays.asList(new String[]{
        "Authoring:enable:finish:rollback",
        "Approval:enable:finish:rollback",
        "Deploy:enable:finish:rollback"
    });

    /**
     * Should we send alerts about overdue tasks at all? Send alerts when a task
     * is overdue (has remained in the \"enabled\" state for a long time)
     * Parameter SEND_OVERDUE_ALERTS in the old initialiser system, default
     * false
     */
    @Setting
    private boolean sendOverdueAlerts = false;

    /**
     * The time between when a task is enabled (i.e. it is made available for
     * completion) and when it is considered overdue (in HOURS).
     */
    // XXX Once the Duration of a Task can actually be maintained (in the UI,
    // or initialization parameters), we should use the value in the DB, and
    // get rid of this
    // Parameter name TASK_DURATION in the old initializer system.
    // Description: How long a task can remain \"enabled\" before it is
    // considered overdue (in hours)
    @Setting
    private int taskDuration = 96;

    /**
     * The time to wait between sending successive alerts on the same overdue
     * task (in HOURS). Parameter name OVERDUE_ALERT_INTERVAL in old initialiser
     * system Description: Time to wait between sending overdue notifications on
     * the same task (in hours)
     */
    @Setting
    private int alertInterval = 24;

    /**
     * The maximum number of alerts to send about any one overdue task.
     * Parameter name MAX_ALERTS in old initialiser system. Description: The
     * maximum number of alerts to send that a single task is overdue
     */
    @Setting
    private int maxAlerts = 5;

    /**
     * Assets steps which are added which are present on all content items.
     */
    @Setting
    private List<String> defaultAuthoringSteps = Arrays
        .asList(
            new String[]{
                ItemCategoryStep.class.getName(),
                ImageStep.class.getName(),
                RelatedInfoStep.class.getName(),
//                ItemPermissionsStep.class.getName()
            });

    public static ContentSectionConfig getConfig() {
        final ConfigurationManager confManager = CdiUtil.createCdiUtil()
            .findBean(ConfigurationManager.class);
        return confManager.findConfiguration(ContentSectionConfig.class);
    }

    public ContentSectionConfig() {
        super();
    }

    public List<String> getTaskAlerts() {
        return new ArrayList<>(taskAlerts);
    }

    public void setTaskAlerts(final List<String> taskAlerts) {
        this.taskAlerts = new ArrayList<>(taskAlerts);
    }

    public boolean isSendOverdueAlerts() {
        return sendOverdueAlerts;
    }

    public void setSendOverdueAlerts(final boolean sendOverdueAlerts) {
        this.sendOverdueAlerts = sendOverdueAlerts;
    }

    public int getTaskDuration() {
        return taskDuration;
    }

    public void setTaskDuration(final int taskDuration) {
        this.taskDuration = taskDuration;
    }

    public int getAlertInterval() {
        return alertInterval;
    }

    public void setAlertInterval(final int alertInterval) {
        this.alertInterval = alertInterval;
    }

    public int getMaxAlerts() {
        return maxAlerts;
    }

    public void setMaxAlerts(final int maxAlerts) {
        this.maxAlerts = maxAlerts;
    }

    public List<String> getDefaultAuthoringSteps() {
        return new ArrayList<>(defaultAuthoringSteps);
    }

    public void setDefaultAuthoringSteps(
        final List<String> defaultAuthoringSteps) {
        this.defaultAuthoringSteps = new ArrayList<>(defaultAuthoringSteps);
    }

}
