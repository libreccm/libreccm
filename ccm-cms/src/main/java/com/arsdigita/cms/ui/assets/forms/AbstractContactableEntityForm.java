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

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
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
import org.librecms.assets.ContactEntry;
import org.librecms.assets.ContactEntryKeyByLabelComparator;
import org.librecms.assets.ContactEntryKey;
import org.librecms.assets.ContactEntryKeyRepository;
import org.librecms.assets.ContactableEntity;
import org.librecms.assets.PostalAddress;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.TooManyListenersException;
import java.util.stream.Collectors;

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

    private final AssetPane assetPane;

    private SimpleContainer contactEntriesContainer;

    private Table contactEntriesTable;

    private SingleSelect contactEntryKeySelect;

    private TextField contactEntryValueField;

    private ActionLink addContactEntryLink;

    private AssetSearchWidget postalAddressSearchWidget;

    public AbstractContactableEntityForm(final AssetPane assetPane) {

        super(assetPane);

        this.assetPane = assetPane;
    }

    @Override
    protected void addWidgets() {

        addPropertyWidgets();

        contactEntriesContainer = new SimpleContainer() {

            @Override
            public boolean isVisible(final PageState state) {
                return getSelectedAsset(state) != null;
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

        addContactEntryLink = new ActionLink(
            new GlobalizedMessage(
                "cms.ui.authoring.assets.contactable.contactentries.add",
                CMS_BUNDLE)
        );
        addContactEntryLink
            .addActionListener(new AddContactEntryActionListener());
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
    public void init(final FormSectionEvent event) throws FormProcessException {

        super.init(event);

        final PageState state = event.getPageState();

        final Optional<T> selected = getSelectedAsset(state);

        if (selected.isPresent()) {
            // ToDo
        }
    }

    @Override
    public void register(final Page page) {

        super.register(page);

//        page.addComponent(addContactEntryLink);
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

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final AbstractContactableEntityFormController controller
                                                                  = cdiUtil
                        .findBean(AbstractContactableEntityFormController.class);
                final Optional<T> selected = getSelectedAsset(event
                    .getPageState());
                if (selected.isPresent()) {
                    controller.removeContactEntry(selected.get(), rowKey);
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

            final ContactableEntity selected = getSelectedAsset(state)
                .orElseThrow(
                    () -> new IllegalStateException("No asset selected")
                );
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final AbstractContactableEntityFormController controller = cdiUtil
                .findBean(AbstractContactableEntityFormController.class);
            final List<ContactEntry> contactEntries = controller
                .getContactEntries(selected);
            return new ContactEntriesTableModel(contactEntries);
        }

    }

    private class ContactEntriesTableModel implements TableModel {

        private final Iterator<ContactEntry> contactEntries;

        private ContactEntry currentContactEntry;

        public ContactEntriesTableModel(
            final List<ContactEntry> contactEntries) {

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
                    return currentContactEntry.getKey();
                case COL_CONTACT_ENTRIES_VALUE:
                    return currentContactEntry.getValue();
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
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
                           new Label(new GlobalizedMessage("cms.ui.select_one"),
                                     CMS_BUNDLE))
            );

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ContactEntryKeyRepository keyRepo = cdiUtil
                .findBean(ContactEntryKeyRepository.class);
            final GlobalizationHelper globalizationHelper = cdiUtil
                .findBean(GlobalizationHelper.class);
            final List<ContactEntryKey> keys = keyRepo
                .findAll()
                .stream()
                .sorted(new ContactEntryKeyByLabelComparator(globalizationHelper
                    .getNegotiatedLocale()))
                .collect(Collectors.toList());

            for (final ContactEntryKey key : keys) {

                final Text label = new Text(
                    globalizationHelper
                        .getValueFromLocalizedString(key.getLabel()));

                target.addOption(new Option(key.getEntryKey(), label));
            }
        }

    }

    private class AddContactEntryActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

}
