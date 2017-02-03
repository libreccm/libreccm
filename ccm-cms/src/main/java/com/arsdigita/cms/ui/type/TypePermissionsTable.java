package com.arsdigita.cms.ui.type;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.RowData;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.CMS;

import org.librecms.contentsection.ContentType;

import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ui.ContentSectionRequestLocal;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.ui.admin.GlobalizationUtil;

import org.libreccm.security.Party;
import org.libreccm.security.Role;

import com.arsdigita.util.LockableImpl;

import java.awt.image.Kernel;
import java.util.List;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Permission;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.RoleRepository;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.privileges.AdminPrivileges;
import sun.security.util.SecurityConstants;

import java.util.Iterator;

/**
 *
 * ToDo, maybe not needed anymore...
 *
 * @author Jens Pelzetter
 */
public class TypePermissionsTable extends Table implements TableActionListener {

    private final String TABLE_COL_ROLE = "table_col_role";
    private final String TABLE_COL_CAN_USE = "table_col_can_use";
    private final String TABLE_COL_ACTION = "table_col_action";
    private final ContentTypeRequestLocal type;

    public TypePermissionsTable(final ContentSectionRequestLocal section,
                                final ContentTypeRequestLocal type) {
        super();

        this.type = type;

        setEmptyView(new Label(new GlobalizedMessage(
            "cms.ui.type.permissions.none",
            CmsConstants.CMS_BUNDLE)));

        TableColumnModel columnModel = getColumnModel();

        columnModel.add(new TableColumn(
            0,
            new GlobalizedMessage("cms.ui.type.permissions.role",
                                  CmsConstants.CMS_BUNDLE).localize(),
            TABLE_COL_ROLE));

        columnModel.add(new TableColumn(
            1,
            new GlobalizedMessage("cms.ui.type.permissions_can_use",
                                  CmsConstants.CMS_BUNDLE).localize(),
            TABLE_COL_CAN_USE));

        columnModel.add(new TableColumn(
            2,
            new GlobalizedMessage("cms.ui.type.permission.action",
                                  CmsConstants.CMS_BUNDLE).localize(),
            TABLE_COL_ACTION));

        setModelBuilder(new TypePermissionsTableModelBuilder());

        columnModel.get(0).setCellRenderer(new RoleCellRenderer());
        columnModel.get(1).setCellRenderer(new CanUseCellRenderer());
        columnModel.get(2).setCellRenderer(new ActionCellRenderer());

        addTableActionListener(this);
    }

    @Override
    public void cellSelected(final TableActionEvent event) {
        final PageState state = event.getPageState();

        final TableColumn column = getColumnModel().get(event.getColumn());

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final RoleRepository roleRepo = cdiUtil.findBean(RoleRepository.class);
        final TypePermissionsTableController controller = cdiUtil.findBean(
            TypePermissionsTableController.class);
        
        if (TABLE_COL_ACTION.equals(column.getHeaderKey().toString())) {
            final Role role = roleRepo.findById(Long.parseLong(
                event.getRowKey().toString())).get();
            ContentType contentType = getType().getContentType(state);
            
            controller.toggleTypeUsePermission(contentType, role);
        }
    }

    @Override
    public void headSelected(final TableActionEvent event) {
        //Nothing to do
    }

    private class TypePermissionsTableModelBuilder
        extends LockableImpl
        implements TableModelBuilder {

        public TypePermissionsTableModelBuilder() {
        }

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            return new TypePermissionsTableModel(table, state);
        }

    }

    private class TypePermissionsTableModel implements TableModel {

        private final Iterator<RowData<Long>> iterator;
        private RowData<Long> currentRow;

        public TypePermissionsTableModel(final Table table,
                                         final PageState state) {
            final ContentType contentType = ((TypePermissionsTable) table)
                .getType()
                .getContentType(state);

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final TypePermissionsTableController controller = cdiUtil.findBean(
                TypePermissionsTableController.class);
            final ContentSection section = CMS.getContext().getContentSection();
            final List<RowData<Long>> rows = controller.retrieveTypePermissions(
                contentType, section);
            iterator = rows.iterator();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public boolean nextRow() {
            if (iterator.hasNext()) {
                currentRow = iterator.next();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return currentRow.getColData(0);
                case 1:
                    return currentRow.getColData(1);
                case 2:
                    if ("cms.ui.type.permissions.can_use.yes".equals(currentRow
                        .getColData(1))) {
                        return "cms.ui.type.permissions.actions.revoke";
                    } else {
                        return "cms.ui.type.permissions.can_use.grant";
                    }
                default:
                    throw new IllegalArgumentException("Invalid column index.");
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return currentRow.getRowKey();
        }

    }

    private class RoleCellRenderer
        extends LockableImpl
        implements TableCellRenderer {

        @Override
        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int column) {
            return new Text(value.toString());
        }

    }

    private class CanUseCellRenderer
        extends LockableImpl
        implements TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            return new Label(new GlobalizedMessage(value.toString(),
                                                   CmsConstants.CMS_BUNDLE));
        }

    }

    private class ActionCellRenderer
        extends LockableImpl
        implements TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PermissionChecker permissionChecker = cdiUtil.findBean(
                PermissionChecker.class);

            final ContentSection section = CMS.getContext().getContentSection();

            if (permissionChecker.isPermitted(
                AdminPrivileges.ADMINISTER_CONTENT_TYPES, section)) {

                return new ControlLink(new Label(new GlobalizedMessage(
                    (String) value), CmsConstants.CMS_BUNDLE));
            } else {
                return new Text("");
            }

        }

    }

    private ContentTypeRequestLocal getType() {
        return type;
    }

}
