/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package com.arsdigita.ui.admin.pagemodels.layout;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.bebop.PropertySheetModel;
import com.arsdigita.bebop.PropertySheetModelBuilder;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.ui.admin.AdminUiConstants;
import com.arsdigita.ui.admin.pagemodels.AbstractComponentModelForm;
import com.arsdigita.ui.admin.pagemodels.PageModelsTab;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.xml.Element;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.pagemodel.ComponentModelRepository;
import org.libreccm.pagemodel.layout.FlexBox;
import org.libreccm.pagemodel.layout.FlexDirection;
import org.libreccm.pagemodel.layout.FlexLayout;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class FlexLayoutComponentForm
    extends AbstractComponentModelForm<FlexLayout> {

    private final ParameterSingleSelectionModel<String> selectedComponentId;

    private SingleSelect directionSelect;
//    private FlexBoxesPanel boxesPanel;

    private List boxesList;

    public FlexLayoutComponentForm(
        final PageModelsTab pageModelsTab,
        final ParameterSingleSelectionModel<String> selectedModelId,
        final ParameterSingleSelectionModel<String> selectedComponentId) {

        super("FlexLayoutComponentForm",
              pageModelsTab,
              selectedModelId,
              selectedComponentId);

        Objects.requireNonNull(pageModelsTab);
        Objects.requireNonNull(selectedModelId);
        Objects.requireNonNull(selectedComponentId);
        
        this.selectedComponentId = selectedComponentId;
    }

    @Override
    protected void addWidgets() {

        directionSelect = new SingleSelect("directionSelect");
        directionSelect.setLabel(new GlobalizedMessage(
            AdminUiConstants.ADMIN_BUNDLE,
            "ui.admin.pagelayout.flexlayout.direction.label"));
        directionSelect
            .addOption(new Option(
                FlexDirection.HORIZONTAL.toString(),
                new Label(new GlobalizedMessage(
                    AdminUiConstants.ADMIN_BUNDLE,
                    "ui.admin.pagelayout.flexlayout.direction"
                        + ".option.horizontal"))));
        directionSelect
            .addOption(new Option(
                FlexDirection.VERTICAL.toString(),
                new Label(new GlobalizedMessage(
                    AdminUiConstants.ADMIN_BUNDLE,
                    "ui.admin.pagelayout.flexlayout.direction"
                        + ".option.vertical"))));
        add(directionSelect);

//        boxesPanel = new FlexBoxesPanel();
//        add(boxesPanel);
        boxesList
        = new BoxesList(new BoxesListModelBuilder(selectedComponentId));
        boxesList.setCellRenderer(new BoxesListCellRenderer());
        boxesList.setEmptyView(new Label(new GlobalizedMessage(
            "ui.admin.pagelayout.flexlayout.no_boxes",
            AdminUiConstants.ADMIN_BUNDLE)));
        add(boxesList);
    }

    @Override
    protected FlexLayout createComponentModel() {
        return new FlexLayout();
    }

    @Override
    protected void updateComponentModel(final FlexLayout componentModel,
                                        final PageState state,
                                        final FormData data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {

        super.init(event);

        final PageState state = event.getPageState();

        final FlexLayout layout = getComponentModel();

        if (layout == null) {
            directionSelect.setValue(state, FlexDirection.VERTICAL.toString());
        } else {
            directionSelect.setValue(state, layout.getDirection().toString());
        }
    }

//    private class FlexBoxesPanel extends SimpleContainer {
//
//        private FlexDirection direction;
//
//        public FlexBoxesPanel() {
//            super("flexLayout", "bebop");
//
//            direction = FlexDirection.VERTICAL;
//        }
//
//        public FlexDirection getDirection() {
//            return direction;
//        }
//
//        public void setFlexDirection(final FlexDirection direction) {
//            this.direction = direction;
//        }
//
//        @Override
//        protected Element generateParent(final Element parent) {
//
//            final Element element = super.generateParent(parent);
//            element.addAttribute("direction", direction
//                                 .toString()
//                                 .toLowerCase());
//            return element;
//        }
//
//    }
    private class FlexBoxPanel extends BoxPanel {

        private final FlexBox box;

        public FlexBoxPanel(final FlexBox box) {

            super(BoxPanel.VERTICAL);

            this.box = box;

            final PropertySheet propertySheet = new PropertySheet(
                new FlexBoxPropertySheetModelBuilder(box));
            super.add(propertySheet);
        }

    }

    private class FlexBoxPropertySheetModelBuilder
        extends LockableImpl
        implements PropertySheetModelBuilder {

        private final FlexBox box;

        public FlexBoxPropertySheetModelBuilder(final FlexBox box) {
            this.box = box;
        }

        @Override
        public PropertySheetModel makeModel(final PropertySheet sheet,
                                            final PageState state) {

            return new FlexBoxPropertySheetModel(box);
        }

    }

    private class FlexBoxPropertySheetModel implements PropertySheetModel {

        private final FlexBox box;
        private final Iterator<FlexBoxProperty> propertyIterator;
        private FlexBoxProperty currentProperty;

        public FlexBoxPropertySheetModel(final FlexBox box) {
            this.box = box;
            propertyIterator = Arrays
                .asList(FlexBoxProperty.values())
                .iterator();
        }

        @Override
        public boolean nextRow() {

            if (box == null) {
                return false;
            }

            if (propertyIterator.hasNext()) {
                currentProperty = propertyIterator.next();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public String getLabel() {
            return currentProperty.toString();
        }

        @Override
        public GlobalizedMessage getGlobalizedLabel() {

            final String key = String
                .format("ui.admin.pagemodels.flexlayout.box.properties.%s",
                        currentProperty.toString().toLowerCase());
            return new GlobalizedMessage(key, AdminUiConstants.ADMIN_BUNDLE);
        }

        @Override
        public String getValue() {

            switch (currentProperty) {
                case ORDER:
                    return Integer.toString(box.getOrder());
                case SIZE:
                    return Integer.toString(box.getSize());
                case COMPONENT:
                    if (box.getComponent() == null) {
                        return "";
                    } else {
                        return box.getComponent().getClass().getName();
                    }
                default:
                    throw new UnexpectedErrorException(String
                        .format("Invalid \"%s\" for property of FlexBox.",
                                currentProperty.toString()));
            }
        }
    }

    private static enum FlexBoxProperty {
        ORDER,
        SIZE,
        COMPONENT
    }

    private class BoxesList extends List {

        private FlexLayout flexLayout;
        
        public BoxesList(final ListModelBuilder listModelBuilder) {
            super(listModelBuilder);
        }
        
        public FlexLayout getFlexLayout() {
            return flexLayout;
        }

        public void setFlexLayout(final FlexLayout flexLayout) {
            this.flexLayout = flexLayout;
        }

        @Override
        protected String getTagName() {
            return "flexLayoutBoxesList";
        }
        
        @Override
        protected void exportLayoutAttribute(final Element list) {

            if (flexLayout.getDirection() == FlexDirection.HORIZONTAL) {
                list.addAttribute("layout", "horizontal");
            } else {
                list.addAttribute("layout", "vertical");
            }
        }

    }

    private class BoxesListModelBuilder
        extends LockableImpl
        implements ListModelBuilder {

        private final ParameterSingleSelectionModel<String> selectedComponentId;

        public BoxesListModelBuilder(
            final ParameterSingleSelectionModel<String> selectedComponentId) {

            super();
            
            Objects.requireNonNull(selectedComponentId);

            this.selectedComponentId = selectedComponentId;
        }

        @Override
        public ListModel makeModel(final List list,
                                   final PageState state) {

            if (selectedComponentId.isSelected(state)) {

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();

                final ComponentModelRepository componentModelRepo = cdiUtil
                    .findBean(ComponentModelRepository.class);

                final String componentModelId = selectedComponentId
                    .getSelectedKey(state);

                final FlexLayout flexLayout = (FlexLayout) componentModelRepo
                    .findById(Long.parseLong(componentModelId))
                    .orElseThrow(() -> new IllegalArgumentException(String
                    .format("No ComponentModel with ID %s in the database",
                            componentModelId)));

                return new BoxesListModel(flexLayout.getBoxes());

            } else {
                return new BoxesListModel(Collections.emptyList());
            }
        }

    }

    private class BoxesListModel implements ListModel {

        private final Iterator<FlexBox> iterator;
        private FlexBox currentBox;

        public BoxesListModel(final java.util.List<FlexBox> boxes) {
            this.iterator = boxes.iterator();
        }

        @Override
        public boolean next() {
            if (iterator.hasNext()) {
                currentBox = iterator.next();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Object getElement() {
            return currentBox;
        }

        @Override
        public String getKey() {
            return Long.toString(currentBox.getBoxId());
        }

    }

    private class BoxesListCellRenderer implements ListCellRenderer {

        @Override
        public Component getComponent(final List list,
                                      final PageState state,
                                      final Object value,
                                      final String key,
                                      final int index,
                                      final boolean isSelected) {

            final FlexBox box = (FlexBox) value;

            return new FlexBoxPanel(box);
        }

    }

}
