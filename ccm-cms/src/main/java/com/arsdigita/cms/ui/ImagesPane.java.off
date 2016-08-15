/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.FormModel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.MapComponentSelectionModel;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SegmentedPanel.Segment;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.toolbox.ui.Section;
import com.arsdigita.util.Assert;
import com.arsdigita.util.LockableImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * A {@link LayoutPanel} to insert into {@link ContentSectionPage}.
 *
 * @author Sören Bernstein <quasi@quasiweb.de>
 */
public class ImagesPane extends LayoutPanel implements Resettable {

    public static final Logger S_LOG = Logger.getLogger(ImagesPane.class);
    private final StringParameter m_imageComponentKey;
    private final MapComponentSelectionModel m_imageComponent;
    private final ImageComponentAdminListener m_adminListener;
    final private SegmentedPanel m_body;
    private HashMap<String, Segment> m_bodySegments = new HashMap();
    private final ResettableParameterSingleSelectionModel m_model;
    private final List m_links;
    private final LinksSection m_modes;

    public ImagesPane() {
        super();

        m_model = new ResettableParameterSingleSelectionModel(new 
                                         StringParameter(List.SELECTED));
        m_model.setDefaultSelection(ImageComponent.LIBRARY);
        m_model.addChangeListener(new ImageAdminSelectionListener());

        m_links = new List(new ImageAdminListModelBuilder());
        m_links.setSelectionModel(m_model);

        final SimpleContainer left = new SimpleContainer();
        setLeft(left);

        m_modes = new LinksSection();
        left.add(m_modes);

        m_body = new SegmentedPanel();
        setBody(m_body);

        m_imageComponentKey = new StringParameter("imageComponent");

        final ParameterSingleSelectionModel componentModel = new 
                       ParameterSingleSelectionModel(m_imageComponentKey);
        m_imageComponent = new MapComponentSelectionModel(componentModel, 
                                                          new HashMap());

        final Map selectors = m_imageComponent.getComponentsMap();
        m_adminListener = new ImageComponentAdminListener(m_imageComponent, this);

        // Image library component
        final ImageLibraryComponent library = new 
                   ImageLibraryComponent(ImageComponent.ADMIN_IMAGES);
        library.getForm().addInitListener(m_adminListener);
        library.getForm().addProcessListener(m_adminListener);
        selectors.put(ImageComponent.LIBRARY, library);
        m_bodySegments.put(ImageComponent.LIBRARY, m_body.addSegment(
                new Label(GlobalizationUtil.globalize(
                          "cms.contentasset.image.ui.image_library")),
                library));

        // Image upload component
        final ImageUploadComponent upload = new 
                   ImageUploadComponent(ImageComponent.ADMIN_IMAGES);
        upload.getForm().addInitListener(m_adminListener);
        upload.getForm().addSubmissionListener(m_adminListener);
        upload.getForm().addProcessListener(m_adminListener);
        selectors.put(ImageComponent.UPLOAD, upload);
        m_bodySegments.put(ImageComponent.UPLOAD, m_body.addSegment(
                new Label(GlobalizationUtil.globalize(
                          "cms.contentasset.image.ui.image_upload")),
                upload));

    }

    @Override
    public final void register(final Page page) {
        super.register(page);

        Iterator<String> keys = m_bodySegments.keySet().iterator();

        while (keys.hasNext()) {
            String key = keys.next();
            page.setVisibleDefault(m_bodySegments.get(key), 
                                   m_model.getDefaultSelection().equals(key));
        }

        page.addComponentStateParam(this, m_imageComponentKey);
    }

    /**
     * Resets this pane and all its resettable components.
     *
     * @param state Page state
     */
    @Override
    public final void reset(final PageState state) {
        super.reset(state);

        m_model.reset(state);
        this.setActiveImageComponent(state, m_model.getDefaultSelection());
    }

    public final void setActiveImageComponent(PageState state, String activeComp) {

        Iterator<String> keys = m_bodySegments.keySet().iterator();
        m_imageComponent.setSelectedKey(state, activeComp);

        while (keys.hasNext()) {

            String key = keys.next();
            final boolean visibility = key.equals(activeComp);
            state.setVisible(m_bodySegments.get(key), visibility);

            for (int index = 0; index < m_bodySegments.get(key).size(); index++) {

                Component component = m_bodySegments.get(key).get(index);

                // Reset all components if they are of type Resettable
                if (component instanceof Resettable) {
                    ((Resettable) component).reset(state);
                }

                // Set visibility
                component.setVisible(state, visibility);
            }
        }
    }

    /**
     * 
     */
    private class ResettableParameterSingleSelectionModel 
                  extends ParameterSingleSelectionModel
                  implements Resettable {

        private String defaultKey;

        public ResettableParameterSingleSelectionModel(ParameterModel m) {
            super(m);
        }

        public void setDefaultSelection(String selKey) {
            this.defaultKey = selKey;
        }

        public String getDefaultSelection() {
            return defaultKey;
        }

        public void reset(PageState state) {

            if (Assert.isEnabled()) {
                final FormModel model = state.getPage().getStateModel();
                Assert.isTrue(model.containsFormParam(getStateParameter()));
            }

            state.setValue(getStateParameter(), this.defaultKey);
        }
    }

    /**
     * 
     */
    private class ImageAdminListModel implements ListModel {

        private ArrayList<String> m_keys;
        private int m_index = -1;

        public ImageAdminListModel(ArrayList keys) {
            m_keys = keys;
        }

        public boolean next() {
            return (m_index++ < m_keys.size() - 1);
        }

        public Object getElement() {
            return GlobalizationUtil.globalize(
                   "cms.contentasset.image.ui.image_" + m_keys.get(m_index)).localize();
        }

        public String getKey() {
            return m_keys.get(m_index);
        }
    }

    private class ImageAdminListModelBuilder extends LockableImpl 
                                             implements ListModelBuilder {

        public ListModel makeModel(final List list, final PageState state) {
            ArrayList<String> keys = new ArrayList(2);
            keys.add(ImageComponent.LIBRARY);
            keys.add(ImageComponent.UPLOAD);
            return new ImageAdminListModel(keys);
        }
    }

    private class ImageAdminSelectionListener implements ChangeListener {

        public final void stateChanged(final ChangeEvent e) {
            S_LOG.debug("Selection state changed; I may change "
                       +"the body's visible pane");

            final PageState state = e.getPageState();

//            ImagesPane.this.reset(state);

            if (m_model.isSelected(state)) {
                S_LOG.debug("The selection model is selected; displaying "
                           +"the item pane");

                ImagesPane.this.setActiveImageComponent(
                                               state, 
                                               state.getControlEventValue());
            }
        }
    }

    private class LinksSection extends Section {

        LinksSection() {
            setHeading(GlobalizationUtil.globalize(
                                         "cms.contentasset.image.ui.images"));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(m_links);
        }
    }
}