/*
 * Copyright (C) 2019 LibreCCM Foundation.
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
package com.arsdigita.cms.ui.assets.forms;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Date;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ui.assets.AssetPane;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.LockableImpl;

import org.librecms.assets.Person;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.librecms.CmsConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class PersonForm extends AbstractContactableEntityForm<Person> {

    private TextField surnameField;

    private TextField givenNameField;

    private TextField prefixField;

    private TextField suffixField;

    private Submit addPersonNameButton;

    private Date birthdateField;

    public PersonForm(final AssetPane assetPane) {
        super(assetPane);
    }

    @Override
    protected void addPropertyWidgets() {

        final Label surnameLabel = new Label(new GlobalizedMessage(
            "cms.ui.authoring.assets.person.surname",
            CMS_BUNDLE));
        surnameField = new TextField("surname");
        add(surnameLabel);
        add(surnameField);

        final Label givenNameLabel = new Label(new GlobalizedMessage(
            "cms.ui.authoring.assets.person.given_name",
            CMS_BUNDLE));
        givenNameField = new TextField("givenName");
        add(givenNameLabel);
        add(givenNameField);

        final Label prefixLabel = new Label(new GlobalizedMessage(
            "cms.ui.authoring.assets.person.prefix",
            CMS_BUNDLE
        ));
        prefixField = new TextField("prefix");
        add(prefixLabel);
        add(prefixField);

        final Label suffixLabel = new Label(new GlobalizedMessage(
            "cms.ui.authoring.assets.person.suffix",
            CMS_BUNDLE
        ));
        suffixField = new TextField("suffix");
        add(suffixLabel);
        add(suffixField);

        add(buildPersonNamesTable());

        addPersonNameButton = new Submit(new GlobalizedMessage(
            "cms.ui.authoring.assets.person.add_name",
            CMS_BUNDLE));
        add(addPersonNameButton);

        final Label birthdateLabel = new Label(new GlobalizedMessage(
            "cms.ui.authoring.assets.person.birthdate",
            CMS_BUNDLE));
        add(birthdateLabel);
        birthdateField = new Date("birthdate");
        final LocalDate today = LocalDate.now(ZoneId.systemDefault());
        birthdateField.setYearRange(1930, today.getYear());
        add(birthdateField);
    }

    @Override
    protected Class<Person> getAssetClass() {
        return Person.class;
    }

    @Override
    protected void showLocale(final PageState state) {

        // Nothing
    }

    @Override
    protected Map<String, Object> collectData(final FormSectionEvent event) {

        final PageState state = event.getPageState();

        final Map<String, Object> data = new HashMap<>();

        data.put(PersonFormController.SURNAME, surnameField.getValue(state));
        data.put(PersonFormController.GIVENNAME,
                 givenNameField.getValue(state));
        data.put(PersonFormController.PREFIX, prefixField.getValue(state));
        data.put(PersonFormController.SUFFIX, suffixField.getValue(state));

        data.put(PersonFormController.BIRTHDATE,
                 birthdateField.getValue(state));

        return data;
    }

    @Override
    public void initForm(final PageState state,
                         final Map<String, Object> data) {

        super.initForm(state, data);

        if (data.containsKey(PersonFormController.SURNAME)) {
            surnameField.setValue(state,
                                  data.get(PersonFormController.SURNAME));
        }

        if (data.containsKey(PersonFormController.GIVENNAME)) {
            givenNameField.setValue(state,
                                    data.get(PersonFormController.GIVENNAME));
        }

        if (data.containsKey(PersonFormController.PREFIX)) {
            prefixField.setValue(state, data.get(PersonFormController.PREFIX));
        }

        if (data.containsKey(PersonFormController.SUFFIX)) {
            suffixField.setValue(state, data.get(PersonFormController.SUFFIX));
        }

        if (data.containsKey(PersonFormController.BIRTHDATE)) {
            birthdateField.setValue(state,
                                    data.get(PersonFormController.BIRTHDATE));
        }
    }

    @Override
    public void process(final FormSectionEvent event) throws
        FormProcessException {

        if (addPersonNameButton.equals(event.getSource())) {

            final PersonFormController controller
                                           = (PersonFormController) getController();
            controller.addPersonName(getSelectedAssetId(event.getPageState()));

        } else {
            super.process(event);
        }
    }

    private Table buildPersonNamesTable() {

        final Table table = new Table() {

            @Override
            public boolean isVisible(final PageState state) {
                return getSelectedAssetId(state) != null;
            }
        };

        final TableColumnModel columnModel = table.getColumnModel();
        columnModel.add(new TableColumn(
            0,
            new Label(
                new GlobalizedMessage(
                    "cms.ui.authoring.assets.person.surname",
                    CMS_BUNDLE
                )
            )
        ));
        columnModel.add(new TableColumn(
            1,
            new Label(
                new GlobalizedMessage(
                    "cms.ui.authoring.assets.person.givenName",
                    CMS_BUNDLE
                )
            )
        ));
        columnModel.add(new TableColumn(
            2,
            new Label(
                new GlobalizedMessage(
                    "cms.ui.authoring.assets.person.prefix",
                    CMS_BUNDLE
                )
            )
        ));
        columnModel.add(new TableColumn(
            3,
            new Label(
                new GlobalizedMessage(
                    "cms.ui.authoring.assets.person.suffix",
                    CMS_BUNDLE
                )
            )
        ));

        table.setModelBuilder(new PersonNamesTableModelBuilder());

        table.setEmptyView(new Label(new GlobalizedMessage(
            "cms.ui.authoring.assets.person.names.none")));

        return table;
    }

    private class PersonNamesTableModelBuilder
        extends LockableImpl
        implements TableModelBuilder {

        @Override
        public TableModel makeModel(final Table table, final PageState state) {

            final Long selectedPersonId = getSelectedAssetId(state);
            if (selectedPersonId == null) {
                throw new RuntimeException("No asset selected.");
            }

            final PersonFormController controller
                                           = (PersonFormController) getController();
            final List<String[]> personNames = controller
                .getPersonNames(selectedPersonId);

            return new PersonNamesTableModel(personNames);
        }

    }

    private class PersonNamesTableModel implements TableModel {

        private final Iterator<String[]> personNames;

        private String[] currentPersonName;

        private int row;

        public PersonNamesTableModel(final List<String[]> personNames) {

            this.personNames = Objects
                .requireNonNull(personNames,
                                "Can't create PersonNamesTableModel without a "
                                    + "list of person names.")
                .iterator();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public boolean nextRow() {

            if (personNames.hasNext()) {
                currentPersonName = personNames.next();
                row++;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Object getElementAt(final int columnIndex) {

            return currentPersonName[columnIndex];

        }

        @Override
        public Object getKeyAt(final int columnIndex) {

            return row;
        }

    }

}
