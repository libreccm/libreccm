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
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.parameters.StringParameter;

import org.librecms.contentsection.ContentType;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.cms.ui.item.ItemWorkflowRequestLocal;
import com.arsdigita.cms.ui.workflow.AssignedTaskSection;
import com.arsdigita.cms.ui.workflow.AssignedTaskTable;
import com.arsdigita.cms.ui.workflow.TaskFinishForm;
import com.arsdigita.cms.ui.workflow.TaskRequestLocal;
import com.arsdigita.cms.ui.workflow.WorkflowRequestLocal;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.toolbox.ui.ModalPanel;
import com.arsdigita.toolbox.ui.Section;
import com.arsdigita.util.Assert;
import com.arsdigita.util.SequentialMap;
import com.arsdigita.util.UncheckedWrapperException;

import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Objects;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.arsdigita.cms.CMSConfig;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.workflow.Task;
import org.libreccm.workflow.TaskRepository;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItem;
import org.librecms.contenttypes.AuthoringKit;
import org.librecms.contenttypes.AuthoringKitInfo;
import org.librecms.contenttypes.AuthoringStepInfo;
import org.librecms.contenttypes.ContentTypeInfo;
import org.librecms.ui.authoring.ContentItemAuthoringStepInfo;
import org.librecms.ui.authoring.ContentItemAuthoringStepManager;
import org.librecms.workflow.CmsTaskType;

/**
 * This class represents a single authoring kit. The wizard accepts a
 * {@link ContentType} in the constructor; it then extracts the
 * {@link AuthoringKit} for the content type, and creates the components for all
 * the steps in the kit.
 *
 * Note that the individual authoring kit steps must provide the following
 * constructor:
 *
 * <blockquote><pre><code>
 * public TheClass(ItemSelectionModel model, AuthoringKitWizard parent) { ... }
 * </code></pre></blockquote>
 *
 * This constructor will be called when the component is automatically
 * instantiated by the <code>AuthoringKitWizard</code>.
 *
 */
public class AuthoringKitWizard extends LayoutPanel implements Resettable {

    /**
     * Private Logger instance for this class
     */
    private static final Logger LOGGER = LogManager
        .getLogger(AuthoringKitWizard.class);

    private final static Class<?>[] ARGUMENTS = new Class<?>[]{
        ItemSelectionModel.class,
        AuthoringKitWizard.class,
        StringParameter.class
    };
    private static final Class<?>[] USER_DEFINED_ARGS = new Class<?>[]{
        ItemSelectionModel.class,
        AuthoringKitWizard.class,
        ContentType.class
    };

    private final Object[] values;
    private final ContentTypeInfo typeInfo;
    private final AuthoringKitInfo kitInfo;
    private final ItemSelectionModel selectionModel;
    private final WorkflowRequestLocal workflowRequestLocal;
    private final AssignedTaskTable assignedTaskTable;
    private final SequentialMap labels;
    private final List list;
    private String defaultKey;
    private final GridPanel leftPanel;
    private final ModalPanel bodyPanel;
    private final SimpleContainer stepsContainer;
    private final TaskFinishForm taskFinishForm;

    private final StringParameter selectedLanguageParam;

    /**
     * The name of the state parameter that determines whether the wizard is in
     * item creation mode or item editing mode.
     */
    public static final String IS_EDITING = "is_edit";
    /**
     * The key for the item creation step.
     */
    public static final String CREATION = "_creation_";

    private final static String SEC_PAGE_EDIT_DYN
                                    = "com.arsdigita.cms.ui.authoring.SecondaryPageEditDynamic";
    private final static String PAGE_EDIT_DYN
                                    = "com.arsdigita.cms.ui.authoring.PageEditDynamic";

    /**
     * Construct a new AuthoringKitWizard. Add all the steps in the authoring
     * kit to the wizard.
     *
     * @param typeInfo       The content type of the items that this wizard will
     *                       handle
     * @param selectionModel
     */
    public AuthoringKitWizard(final ContentTypeInfo typeInfo,
                              final ItemSelectionModel selectionModel) {
        LOGGER.debug("Authoring kit wizard for type {} undergoing creation...",
                     Objects.toString(typeInfo));

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ConfigurationManager confManager = cdiUtil.findBean(
            ConfigurationManager.class);

        selectedLanguageParam = new StringParameter(
            ContentItemPage.SELECTED_LANGUAGE);
        final GlobalizationHelper globalizationHelper = cdiUtil
            .findBean(GlobalizationHelper.class);
        selectedLanguageParam.setDefaultValue(globalizationHelper
            .getNegotiatedLocale()
            .toString());

        this.typeInfo = typeInfo;
        kitInfo = typeInfo.getAuthoringKit();
        this.selectionModel = selectionModel;
        values = new Object[]{selectionModel, this, selectedLanguageParam};
        workflowRequestLocal = new ItemWorkflowRequestLocal();
        labels = new SequentialMap();

        leftPanel = new GridPanel(1);
        setLeft(leftPanel);

        assignedTaskTable = new AssignedTaskTable(workflowRequestLocal);

        leftPanel.add(new AssignedTaskSection(workflowRequestLocal,
                                              assignedTaskTable));

        final Section stepSection = new Section(
            new GlobalizedMessage("cms.ui.authoring.steps",
                                  CmsConstants.CMS_BUNDLE));
        leftPanel.add(stepSection);

        list = new List();
        stepSection.setBody(list);

        list.setListData(labels);
        list.setCellRenderer(new ListCellRenderer() {

            @Override
            public Component getComponent(final List list,
                                          final PageState state,
                                          final Object value,
                                          final String key,
                                          final int index,
                                          final boolean isSelected) {
                final Label label;
                if (value instanceof GlobalizedMessage) {
                    label = new Label((GlobalizedMessage) value);
                } else {
                    label = new Label((String) value);
                }
                if (isSelected) {
                    label.setFontWeight(Label.BOLD);
                    return label;
                }
                return new ControlLink(label);
            }

        });

        bodyPanel = new ModalPanel();
        setBody(bodyPanel);

        stepsContainer = new SimpleContainer();
        bodyPanel.add(stepsContainer);
        bodyPanel.setDefault(stepsContainer);

        final java.util.List<AuthoringStepInfo> steps = kitInfo.
            getAuthoringSteps();

        if (Assert.isEnabled()) {
            Assert.isTrue(!steps.isEmpty(),
                          String.format("The authoring kit for content type "
                                            + "s\"%s\" has no steps.",
                                        typeInfo.getContentItemClass().getName()));
        }

        final CMSConfig cmsConfig = confManager.findConfiguration(
            CMSConfig.class);

        StepComponent panel = null;
        for (final AuthoringStepInfo step : steps) {
            final String key = step.getComponent().getName();

            if (defaultKey == null) {
                defaultKey = key;
            }

            /**
             * The "label" and "description" are only here for backwards
             * compatibility
             */
            final ResourceBundle labelBundle = ResourceBundle
                .getBundle(step.getLabelBundle());
            final ResourceBundle descBundle = ResourceBundle
                .getBundle(step.getDescriptionBundle());
            final String labelKey = step.getLabelKey();
            final String label = labelBundle.getString(labelKey);
            final String descriptionKey = step.getDescriptionKey();

            final Class<? extends Component> componentClass = step.
                getComponent();
            final String compClassName = componentClass.getName();

            if (panel != null) {
                panel.setNextStepKey(step.getClass().getName());
            }
            panel = new StepComponent(compClassName);
            stepsContainer.add(panel);
            final Component component;

            if (compClassName.equals(SEC_PAGE_EDIT_DYN)
                    || compClassName.equals(PAGE_EDIT_DYN)) {
                component = instantiateUserDefinedStep(compClassName, typeInfo);
            } else {
                component = instantiateStep(compClassName);
            }
            panel.add(component);
            if (component instanceof AuthoringStepComponent) {
                ((AuthoringStepComponent) component).addCompletionListener(
                    new StepCompletionListener());
            }

            final GlobalizedMessage gzLabel;
            if (labelKey != null) {
                if (step.getLabelBundle() == null) {
                    gzLabel = new GlobalizedMessage(labelKey,
                                                    CmsConstants.CMS_BUNDLE);
                } else {
                    gzLabel = new GlobalizedMessage(labelKey,
                                                    step.getLabelBundle());
                }
            } else {
                gzLabel = null;
            }
            if (gzLabel == null) {
                labels.put(key, label);
            } else {
                labels.put(key, gzLabel);
            }
        }

        final java.util.List<String> skipSteps = cmsConfig.getSkipAssetSteps();
        if (LOGGER.isDebugEnabled()) {
            for (final String step : skipSteps) {
                LOGGER.debug("skip step \"{}\"...", step);
            }
        }

        for (final ContentItemAuthoringStepInfo stepInfo
                 : getContentItemAuthoringSteps()) {

            if (panel != null) {
                panel.setNextStepKey(stepInfo.getStep());
            }

            panel = new StepComponent(stepInfo.getStep());
            stepsContainer.add(panel);

            final Component component = instantiateStep(stepInfo
                .getStep().getName());
            if (component instanceof AuthoringStepComponent) {
                ((AuthoringStepComponent) component)
                    .addCompletionListener(new StepCompletionListener());
            }
            panel.add(component);

            labels.put(stepInfo.getStep(),
                       new GlobalizedMessage(stepInfo.getLabelKey(),
                                             stepInfo.getLabelBundle()));
        }

        list.addChangeListener(new StepListener());

        taskFinishForm = new TaskFinishForm(new TaskSelectionRequestLocal());
        bodyPanel.add(taskFinishForm);

        bodyPanel.connect(assignedTaskTable, 2, taskFinishForm);
        bodyPanel.connect(taskFinishForm);

        taskFinishForm
            .addProcessListener(
                event -> assignedTaskTable
                    .getRowSelectionModel()
                    .clearSelection(event.getPageState())
            );
    }

    private final class StepListener implements ChangeListener {

        @Override
        public final void stateChanged(final ChangeEvent event) {
            final PageState state = event.getPageState();
            final String key = list.getSelectedKey(state).toString();

            final Iterator<?> iter = stepsContainer.children();

            while (iter.hasNext()) {
                final StepComponent step = (StepComponent) iter.next();

                if (step.getStepKey().toString().equals(key)) {
                    step.setVisible(state, true);
                } else {
                    step.setVisible(state, false);
                }
            }
        }

    }

    /**
     *
     */
    private final class StepCompletionListener implements ActionListener {

        @Override
        @SuppressWarnings("unchecked")
        public final void actionPerformed(final ActionEvent event) {
            final PageState state = event.getPageState();
            if (ContentItemPage.isStreamlinedCreationActive(state)) {
                final String key = list.getSelectedKey(state).toString();

                final Iterator<?> iter = stepsContainer.children();

                while (iter.hasNext()) {
                    final StepComponent step = (StepComponent) iter.next();
                    if (step.getStepKey().toString().equals(key)) {
                        Object nextStep = step.getNextStepKey();
                        if (nextStep != null) {
                            list
                                .getSelectionModel()
                                .setSelectedKey(state, nextStep.toString());
                        }
                    }
                }
            }
        }

    }

    @Override
    public final void register(final Page page) {
        super.register(page);

        final Iterator<?> iter = stepsContainer.children();

        while (iter.hasNext()) {
            final StepComponent child = (StepComponent) iter.next();

            page.setVisibleDefault(child, false);
        }

        page.addGlobalStateParam(selectedLanguageParam);

        page.addActionListener(new ActionListener() {

            @Override
            public final void actionPerformed(final ActionEvent event) {
                final PageState state = event.getPageState();

                if (state.isVisibleOnPage(AuthoringKitWizard.this)) {
                    @SuppressWarnings("unchecked")
                    final SingleSelectionModel<Object> model = list
                        .getSelectionModel();

                    if (!model.isSelected(state)) {
                        model.setSelectedKey(state, defaultKey);
                    }
                }
            }

        });
    }

    private java.util.List<ContentItemAuthoringStepInfo> getContentItemAuthoringSteps() {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ContentItemAuthoringStepManager manager = cdiUtil
            .findBean(ContentItemAuthoringStepManager.class);

        return manager.getContentItemAuthoringStepInfos();
    }

    /**
     * @return The content type handled by this wizard
     */
    public ContentTypeInfo getContentType() {
        return typeInfo;
    }

    public List getList() {
        return list;
    }

    /**
     * @return The authoring kit which is represented by this wizard
     */
    public AuthoringKitInfo getAuthoringKit() {
        return kitInfo;
    }

    /**
     * @return The ItemSelectionModel used by the steps in this wizard
     */
    public ItemSelectionModel getItemSelectionModel() {
        return selectionModel;
    }

    /**
     * Instantiate the specified authoring kit step. Will throw a
     * RuntimeException on failure.
     *
     * @param className The Java class name of the step
     *
     * @return The instance of the component.
     */
    protected Component instantiateStep(final String className) {

        LOGGER.debug("Instantiating kit wizard \"{}\" with arguments {}...",
                     className,
                     ARGUMENTS);

        try {
            // Get the creation component
            final Class<?> createClass = Class.forName(className);
            final Constructor<?> constr = createClass.getConstructor(ARGUMENTS);
            final Component component = (Component) constr.newInstance(values);

            return component;
        } catch (ClassNotFoundException
                 | IllegalAccessException
                 | IllegalArgumentException
                 | InstantiationException
                 | InvocationTargetException
                 | NoSuchMethodException
                 | SecurityException ex) {
            LOGGER.error(
                "Failed to instantiate authoring kit component \"{}\"...",
                className);
            LOGGER.error("Exception is: ", ex);
            throw new UncheckedWrapperException(String.format(
                "Failed to instantiate authoring kit component \"%s\".",
                className),
                                                ex);
        }
    }

    /**
     * Instantiate the specified authoring kit step for a user defined content
     * type. Will throw a RuntimeException on failure.
     *
     * @param className       The Java class name of the step
     * @param originatingType
     *
     * @return
     */
    protected Component instantiateUserDefinedStep(
        final String className, final ContentTypeInfo originatingType) {

        try {
            // Get the creation component
            final Class<?> createClass = Class.forName(className);
            final Constructor<?> constr = createClass.getConstructor(
                USER_DEFINED_ARGS);
            final Object[] userDefinedVals = new Object[]{selectionModel,
                                                          this,
                                                          originatingType};
            final Component component = (Component) constr.newInstance(
                userDefinedVals);

            return component;
        } catch (ClassNotFoundException
                 | NoSuchMethodException
                 | InstantiationException
                 | IllegalAccessException
                 | InvocationTargetException ex) {
            throw new UncheckedWrapperException(ex);
        }
    }

    /**
     * Reset the state of this wizard
     */
    @Override
    public final void reset(final PageState state) {
        list.setSelectedKey(state, defaultKey);
    }

    private final class StepComponent extends SimpleContainer {

        private final Object key;
        private Object nextKey;

        public StepComponent(final Object key) {
            this.key = key;
        }

        public Object getStepKey() {
            return key;
        }

        public Object getNextStepKey() {
            return nextKey;
        }

        public void setNextStepKey(final Object nextKey) {
            this.nextKey = nextKey;
        }

    }

    private final class TaskSelectionRequestLocal extends TaskRequestLocal {

        @Override
        protected final Object initialValue(final PageState state) {
            final String key = assignedTaskTable
                .getRowSelectionModel()
                .getSelectedKey(state)
                .toString();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final TaskRepository taskRepo = cdiUtil
                .findBean(TaskRepository.class);

            final Task task = taskRepo
                .findById(Long.parseLong(key))
                .orElseThrow(() -> new UnexpectedErrorException(String
                .format("No Task with ID %s in the database.",
                        key)));

            return task;
        }

    }

}
