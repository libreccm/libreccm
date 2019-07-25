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
        add(surnameLabel);

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
    }

    @Override
    protected Class<Person> getAssetClass() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void showLocale(PageState state) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Map<String, Object> collectData(FormSectionEvent event) throws
        FormProcessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {

        super.init(event);

        // ToDo
        throw new UnsupportedOperationException();
    }

    @Override
    public void process(final FormSectionEvent event) throws
        FormProcessException {
        // ToDo
        throw new UnsupportedOperationException();
    }

    private Table buildPersonNamesTable() {

        final Table table = new Table();

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
