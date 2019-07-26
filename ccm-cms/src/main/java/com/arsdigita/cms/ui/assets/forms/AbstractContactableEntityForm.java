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

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ui.assets.AbstractAssetForm;
import com.arsdigita.cms.ui.assets.AssetPane;
import com.arsdigita.cms.ui.assets.AssetSearchWidget;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.LockableImpl;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.CmsConstants;
import org.librecms.assets.ContactEntryKey;
import org.librecms.assets.ContactableEntity;
import org.librecms.assets.PostalAddress;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TooManyListenersException;

import static org.librecms.CmsConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T>
 */
public abstract class AbstractContactableEntityForm<T extends ContactableEntity>
    extends AbstractAssetForm<T> {

    private static final int COL_CONTACT_ENTRIES_KEY = 0;

    private static final int COL_CONTACT_ENTRIES_VALUE = 1;

    private static final int COL_CONTACT_ENTRIES_REMOVE = 2;

    private SimpleContainer contactEntriesContainer;

    private Table contactEntriesTable;

    private SingleSelect contactEntryKeySelect;

    private TextField contactEntryValueField;

    private Submit addContactEntryLink;

    private AssetSearchWidget postalAddressSearchWidget;

    public AbstractContactableEntityForm(final AssetPane assetPane) {

        super(assetPane);
    }

    @Override
    protected void addWidgets() {

        addPropertyWidgets();

        contactEntriesContainer = new BoxPanel(BoxPanel.VERTICAL) {

            @Override
            public boolean isVisible(final PageState state) {
                return getSelectedAssetId(state) != null;
            }

        };
        add(contactEntriesContainer);

        contactEntriesTable = buildContactEntriesTable();
        contactEntriesContainer.add(contactEntriesTable);

        contactEntryKeySelect = new SingleSelect(new StringParameter(
            "contactentry-key"));
        try {
            contactEntryKeySelect
                .addPrintListener(new ContactEntryKeySelectPrintListener());
        } catch (TooManyListenersException ex) {
            throw new RuntimeException(ex);
        }
        contactEntriesContainer.add(new Label(
            new GlobalizedMessage(
                "cms.ui.authoring.assets.contactable.contactentries.key",
                CMS_BUNDLE))
        );
        contactEntriesContainer.add(contactEntryKeySelect);

        contactEntryValueField = new TextField("contact-entry-value");
        contactEntriesContainer.add(new Label(
            new GlobalizedMessage(
                "cms.ui.authoring.assets.contactable.contactentries.value",
                CMS_BUNDLE))
        );
        contactEntriesContainer.add(contactEntryValueField);

        addContactEntryLink = new Submit(
            new GlobalizedMessage(
                "cms.ui.authoring.assets.contactable.contactentries.add",
                CMS_BUNDLE)
        );
        contactEntriesContainer.add(addContactEntryLink);

        contactEntriesContainer.add(new Label(
            new GlobalizedMessage(
                "cms.ui.authoring.assets.contactable.postaladdress",
                CMS_BUNDLE))
        );
        postalAddressSearchWidget = new AssetSearchWidget(
            "contactable-postaladdress", PostalAddress.class
        );
        contactEntriesContainer.add(postalAddressSearchWidget);

    }

    @Override
    public void initForm(final PageState state,
                         final Map<String, Object> data) {

        super.initForm(state, data);

        final Long selectedAssetId = getSelectedAssetId(state);

        if (selectedAssetId != null) {

            postalAddressSearchWidget.setValue(
                state,
                data.get(AbstractContactableEntityFormController.POSTAL_ADDRESS)
            );
        }
    }

    @Override
    protected Map<String, Object> collectData(
        final FormSectionEvent event) {
        
        final PageState state = event.getPageState();
        
        final Map<String, Object> data = new HashMap<>();
        
        if (postalAddressSearchWidget.getValue(state) != null) {
            
            data.put(AbstractContactableEntityFormController.POSTAL_ADDRESS,
                     postalAddressSearchWidget.getValue(state));
        }
        
        return data;
    }

    @Override
    public void process(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();

        if (addContactEntryLink.isSelected(state)) {

            final Long selectedAssetId = getSelectedAssetId(state);
            if (selectedAssetId == null) {
                throw new FormProcessException(
                    new GlobalizedMessage(
                        "cms.ui.assets.none_selected", CMS_BUNDLE)
                );
            }

            @SuppressWarnings("unchecked")
            final AbstractContactableEntityFormController<ContactableEntity> controller
                                                                             = (AbstractContactableEntityFormController<ContactableEntity>) getController();

            final String key = (String) contactEntryKeySelect
                .getValue(state);
            final String value = (String) contactEntryValueField.getValue(state);

            controller.addContactEntry(key, value, selectedAssetId);
            
            contactEntryKeySelect.setValue(state, null);
            contactEntryValueField.setValue(state, null);
        } else {
            super.process(event);
        }
    }

    protected abstract void addPropertyWidgets();

    private Table buildContactEntriesTable() {

        final Table table = new Table();
//        {

//            @Override
//            public boolean isVisible(final PageState state) {
//                return getSelectedAsset(state).isPresent();
//            }
//        };
        final TableColumnModel columnModel = table.getColumnModel();
        columnModel.add(new TableColumn(
            COL_CONTACT_ENTRIES_KEY,
            new Label(
                new GlobalizedMessage(
                    "cms.ui.authoring.assets.contactable.contactentries.key",
                    CmsConstants.CMS_BUNDLE
                )
            )
        ));
        columnModel.add(new TableColumn(
            COL_CONTACT_ENTRIES_VALUE,
            new Label(
                new GlobalizedMessage(
                    "cms.ui.authoring.assets.contactable.contactentries.value",
                    CmsConstants.CMS_BUNDLE
                )
            )
        ));
        columnModel.add(new TableColumn(
            COL_CONTACT_ENTRIES_REMOVE,
            new Label(
                new GlobalizedMessage(
                    "cms.ui.authoring.assets.contactable.contactentries.remove",
                    CmsConstants.CMS_BUNDLE
                )
            )
        ));

        table.setModelBuilder(new ContactEntriesTableModelBuilder());

        table
            .getColumn(COL_CONTACT_ENTRIES_REMOVE)
            .setCellRenderer(new ContactEntryRemoveCellRenderer());

        table.setEmptyView(
            new Label(
                new GlobalizedMessage(
                    "cms.ui.authoring.assets.contactable.contactentries.none",
                    CmsConstants.CMS_BUNDLE
                )
            )
        );

        table.addTableActionListener(new TableActionListener() {

            @Override
            public void cellSelected(final TableActionEvent event)
                throws FormProcessException {

                final Integer rowKey = (Integer) event.getRowKey();

                @SuppressWarnings("unchecked")
                final AbstractContactableEntityFormController<ContactableEntity> controller
                                                                                 = (AbstractContactableEntityFormController<ContactableEntity>) getController();
                final PageState state = event.getPageState();
                final Long selectedId = getSelectedAssetId(state);
                if (selectedId != null) {
                    controller.removeContactEntry(rowKey, selectedId);
                }
            }

            @Override
            public void headSelected(final TableActionEvent event) {

                // Nothing
            }

        });

        return table;
    }

    private class ContactEntriesTableModelBuilder
        extends LockableImpl implements TableModelBuilder {

        @Override
        public TableModel makeModel(final Table table,
                                    final PageState state) {

            final Long selectedId = getSelectedAssetId(state);
            if (selectedId == null) {
                throw new RuntimeException("No asset selected.");
            }

            @SuppressWarnings("unchecked")
            final AbstractContactableEntityFormController<ContactableEntity> controller
                                                                             = (AbstractContactableEntityFormController<ContactableEntity>) getController();
            final List<String[]> contactEntries = controller
                .getContactEntries(selectedId, getSelectedLocale(state));

            return new ContactEntriesTableModel(contactEntries);
        }

    }

    private class ContactEntriesTableModel implements TableModel {

        private final Iterator<String[]> contactEntries;

        private String[] currentContactEntry;

        public ContactEntriesTableModel(
            final List<String[]> contactEntries) {

            this.contactEntries = contactEntries.iterator();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public boolean nextRow() {

            if (contactEntries.hasNext()) {
                currentContactEntry = contactEntries.next();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Object getElementAt(final int columnIndex) {

            switch (columnIndex) {
                case COL_CONTACT_ENTRIES_KEY:
                    return currentContactEntry[1];
                case COL_CONTACT_ENTRIES_VALUE:
                    return currentContactEntry[2];
                case COL_CONTACT_ENTRIES_REMOVE:
                    return new Label(
                        new GlobalizedMessage(
                            "cms.ui.authoring.assets.contactable"
                                + ".contactentries.remove",
                            CmsConstants.CMS_BUNDLE
                        )
                    );
                default:
                    throw new IllegalArgumentException(String.format(
                        "Illegal column index %d.", columnIndex));

            }
        }

        @Override
        public Object getKeyAt(int columnIndex) {
            return currentContactEntry[0];
        }

    }

    private class ContactEntryRemoveCellRenderer implements TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {

            return new ControlLink((Component) value);
        }

    }

    private class ContactEntryKeySelectPrintListener implements PrintListener {

        @Override
        public void prepare(final PrintEvent event) {

            final SingleSelect target = (SingleSelect) event.getTarget();
            target.clearOptions();

            target.addOption(
                new Option("",
                           new Label(new GlobalizedMessage("cms.ui.select_one",
                                                           CMS_BUNDLE)))
            );

            final AbstractContactableEntityFormController<?> controller = (AbstractContactableEntityFormController<?>) getController();
           
            final PageState state = event.getPageState();
            
            final List<String[]> keys = controller
                .findAvailableContactEntryKeys(getSelectedLocale(state));

            for (final String[] key : keys) {
                target.addOption(new Option(key[0], new Text(key[1])));
            }
        }

    }

}
