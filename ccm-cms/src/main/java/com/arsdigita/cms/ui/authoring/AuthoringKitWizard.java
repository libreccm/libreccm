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
import com.arsdigita.bebop.FormProcessException;
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
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.arsdigita.cms.CMSConfig;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItem;
import org.librecms.contenttypes.AuthoringKit;
import org.librecms.contenttypes.AuthoringKitInfo;
import org.librecms.contenttypes.AuthoringStepInfo;
import org.librecms.contenttypes.ContentTypeInfo;
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

    public final String SELECTED_LANGUAGE = "selectedLanguage";

    private static Class[] arguments = new Class[]{
        ItemSelectionModel.class,
        AuthoringKitWizard.class,
        StringParameter.class
    };
    private static Class[] userDefinedArgs = new Class[]{
        ItemSelectionModel.class,
        AuthoringKitWizard.class,
        ContentType.class
    };
    private static final java.util.List<AssetStepEntry> ASSETS
                                                            = new ArrayList<AssetStepEntry>();
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
    private final TaskFinishForm m_taskFinishForm;

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

        selectedLanguageParam = new StringParameter(SELECTED_LANGUAGE);
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

        final Section stepSection = new Section(gz("cms.ui.authoring.steps"));
        leftPanel.add(stepSection);

        list = new List();
        stepSection.setBody(list);

        list.setListData(labels);
        list.setCellRenderer(new ListCellRenderer() {

            @Override
            public Component getComponent(List list,
                                          PageState state,
                                          Object value,
                                          String key,
                                          int index,
                                          boolean isSelected) {
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
            final ResourceBundle labelBundle = ResourceBundle.getBundle(step.
                getLabelBundle());
            final ResourceBundle descBundle = ResourceBundle.getBundle(step.
                getDescriptionBundle());
            final String labelKey = step.getLabelKey();
            final String label = labelBundle.getString(labelKey);
            final String descriptionKey = step.getDescriptionKey();
            final String description = descBundle.getString(descriptionKey);

            final Class<? extends Component> componentClass = step.
                getComponent();
            final String compClassName = componentClass.getName();

            if (panel != null) {
                panel.setNextStepKey(step.getClass().getName());
            }
            panel = new StepComponent(compClassName);
            stepsContainer.add(panel);
            final Component comp;

            if (compClassName.equals(SEC_PAGE_EDIT_DYN)
                    || compClassName.equals(PAGE_EDIT_DYN)) {
                comp = instantiateUserDefinedStep(compClassName, typeInfo);
            } else {
                comp = instantiateStep(compClassName);
            }
            panel.add(comp);
            // XXX should be optional
            if (comp instanceof AuthoringStepComponent) {
                ((AuthoringStepComponent) comp).addCompletionListener(
                    new StepCompletionListener());
            }

            final GlobalizedMessage gzLabel;
            if (labelKey != null) {
                if (step.getLabelBundle() == null) {
                    gzLabel = gz(labelKey);
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

        final Class<? extends ContentItem> typeClass = typeInfo
            .getContentItemClass();

        final java.util.List<String> skipSteps = cmsConfig.getSkipAssetSteps();
        if (LOGGER.isDebugEnabled()) {
            for (final String step : skipSteps) {
                LOGGER.debug("skip step \"{}\"...", step);
            }
        }

        for (final AssetStepEntry data : ASSETS) {

            final Class<?> baseObjectType;
            try {
                baseObjectType = Class.forName(data.getBaseDataObjectType());
            } catch (ClassNotFoundException ex) {
                throw new UncheckedWrapperException(ex);
            }
            //Class step = (Class) data[1];
            Class step = data.getStep();
            LOGGER.debug("possibly adding asset step " + step.getName());
            if (!skipSteps.contains(step.getName())) {
                GlobalizedMessage label = data.getLabel();

                if (!typeClass.isAssignableFrom(baseObjectType)) {
                    continue;
                }

                if (panel != null) {
                    panel.setNextStepKey(step);
                }
                panel = new StepComponent(step);
                stepsContainer.add(panel);

                Component comp = instantiateStep(step.getName());
                if (comp instanceof AuthoringStepComponent) {
                    ((AuthoringStepComponent) comp).addCompletionListener(
                        new StepCompletionListener());
                }
                panel.add(comp);

                labels.put(step, label);
            }
        }

        list.addChangeListener(new StepListener());

        m_taskFinishForm = new TaskFinishForm(new TaskSelectionRequestLocal());
        bodyPanel.add(m_taskFinishForm);

        bodyPanel.connect(assignedTaskTable, 2, m_taskFinishForm);
        bodyPanel.connect(m_taskFinishForm);

        m_taskFinishForm.addProcessListener(new FormProcessListener() {

            @Override
            public final void process(final FormSectionEvent event)
                throws FormProcessException {
                final PageState state = event.getPageState();

                assignedTaskTable.getRowSelectionModel().clearSelection(state);
            }

        });
    }

    private final class StepListener implements ChangeListener {

        @Override
        public final void stateChanged(final ChangeEvent event) {
            final PageState state = event.getPageState();
            final String key = list.getSelectedKey(state).toString();

            final Iterator iter = stepsContainer.children();

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
        public final void actionPerformed(final ActionEvent event) {
            final PageState state = event.getPageState();
            if (ContentItemPage.isStreamlinedCreationActive(state)) {
                final String key = list.getSelectedKey(state).toString();

                final Iterator iter = stepsContainer.children();

                while (iter.hasNext()) {
                    final StepComponent step = (StepComponent) iter.next();
                    if (step.getStepKey().toString().equals(key)) {
                        Object nextStep = step.getNextStepKey();
                        if (nextStep != null) {
                            list.getSelectionModel().setSelectedKey(
                                state, nextStep.toString());
                        }
                    }
                }
            }
        }

    }

    @Override
    public final void register(final Page page) {
        super.register(page);

        final Iterator iter = stepsContainer.children();

        while (iter.hasNext()) {
            final StepComponent child = (StepComponent) iter.next();

            page.setVisibleDefault(child, false);
        }

        page.addActionListener(new ActionListener() {

            @Override
            public final void actionPerformed(final ActionEvent event) {
                final PageState state = event.getPageState();

                if (state.isVisibleOnPage(AuthoringKitWizard.this)) {
                    final SingleSelectionModel model = list.
                        getSelectionModel();

                    if (!model.isSelected(state)) {
                        model.setSelectedKey(state, defaultKey);
                    }
                }
            }

        });
    }

    public static void registerAssetStep(final String baseObjectType,
                                         final Class step,
                                         final GlobalizedMessage label,
                                         final GlobalizedMessage description,
                                         final int sortKey) {
        // cg - allow registered steps to be overridden by registering a step with the same label
        // this is a bit of a hack used specifically for creating a specialised version of image
        // step. There is no straightforward way of preventing the original image step from being
        // registered, but I needed the image step to use a different step class if the specialised
        // image step application was loaded. Solution is to ensure initialiser in new project
        // runs after original ccm-ldn-image-step initializer and override the registered step here
        LOGGER.debug("registering asset step - label: \"{}\"; "
                         + "step class: \"%s\"",
                     label.localize(),
                     step.getName());

        for (final AssetStepEntry data : ASSETS) {

            final String thisObjectType = data.getBaseDataObjectType();
            final GlobalizedMessage thisLabel = data.getLabel();

            /**
             * jensp 2011-11-14: The code above was only testing for the same
             * label, but not for the same object type. I don't think that this
             * was indented since this made it impossible to attach the same
             * step to different object types. The orginal line was if
             * (thisLabel.localize().equals(label.localize())) {
             *
             */
            if ((thisObjectType.equals(baseObjectType))
                    && (thisLabel.localize().equals(label.localize()))) {
                LOGGER.debug(
                    "registering authoring step with same label as previously registered step");
                ASSETS.remove(data);
                break;
            }
        }
        ASSETS.add(
            new AssetStepEntry(baseObjectType, step, label, description,
                               sortKey));
        Collections.sort(ASSETS);
    }

    private static class AssetStepEntry implements Comparable<AssetStepEntry> {

        private String baseDataObjectType;
        private Class step;
        private GlobalizedMessage label;
        private GlobalizedMessage description;
        private Integer sortKey;

        public AssetStepEntry() {
            super();
        }

        public AssetStepEntry(final String baseDataObjectType,
                              final Class step,
                              final GlobalizedMessage label,
                              final GlobalizedMessage description,
                              final Integer sortKey) {
            this.baseDataObjectType = baseDataObjectType;
            this.step = step;
            this.label = label;
            this.description = description;
            this.sortKey = sortKey;
        }

        public String getBaseDataObjectType() {
            return baseDataObjectType;
        }

        public void setBaseDataObjectType(final String baseDataObjectType) {
            this.baseDataObjectType = baseDataObjectType;
        }

        public Class getStep() {
            return step;
        }

        public void setStep(final Class step) {
            this.step = step;
        }

        public GlobalizedMessage getLabel() {
            return label;
        }

        public void setLabel(final GlobalizedMessage label) {
            this.label = label;
        }

        public GlobalizedMessage getDescription() {
            return description;
        }

        public void setDescription(final GlobalizedMessage description) {
            this.description = description;
        }

        public Integer getSortKey() {
            return sortKey;
        }

        public void setSortKey(final Integer sortKey) {
            this.sortKey = sortKey;
        }

        @Override
        public int compareTo(final AssetStepEntry other) {
            if ((int) sortKey == (int) other.getSortKey()) {
                return step.getName().compareTo(other.getStep().getName());
            } else {
                return sortKey.compareTo(other.getSortKey());
            }
        }

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
                     arguments);

        Object[] vals;
        try {
            // Get the creation component
            final Class createClass = Class.forName(className);
            final Constructor constr = createClass.getConstructor(arguments);
            final Component component = (Component) constr.newInstance(values);

            return component;
        } catch (ClassNotFoundException
                 | IllegalAccessException
                 | IllegalArgumentException
                 | InstantiationException
                 | InvocationTargetException
                 | NoSuchMethodException
                 | SecurityException ex) {
            throw new UncheckedWrapperException(String.format(
                "Failed to instantiate authoring kit component \"{}\".",
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

        Object[] vals;
        try {
            // Get the creation component
            final Class createClass = Class.forName(className);
            final Constructor constr = createClass.getConstructor(
                userDefinedArgs);
            final Object[] userDefinedVals = new Object[]{selectionModel,
                                                          this,
                                                          originatingType};
            final Component component = (Component) constr.newInstance(
                userDefinedVals);

            return component;
        } catch (ClassNotFoundException | NoSuchMethodException
                 | InstantiationException | IllegalAccessException
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

            return CmsTaskType.valueOf(key);
        }

    }

    protected final static GlobalizedMessage gz(final String key) {
        return new GlobalizedMessage(key, CmsConstants.CMS_BUNDLE);
    }

    protected final static String lz(final String key) {
        return (String) gz(key).localize();
    }

}
