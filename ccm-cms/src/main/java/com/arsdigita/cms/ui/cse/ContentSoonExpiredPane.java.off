/*
 * Copyright (C) 2002-2005 Runtime Collective Ltd. All Rights Reserved.
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
 */
package com.arsdigita.cms.ui.cse;

import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.cms.util.SecurityConstants;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.toolbox.ui.DataQueryBuilder;
import com.arsdigita.toolbox.ui.DataTable;
import com.arsdigita.ui.admin.Admin;
import com.arsdigita.web.Application;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Component;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * A pane that contains details to soon to be expired content.
 *
 * @version $Id: ContentSoonExpiredPane.java 775 2005-09-12 14:54:17Z fabrice $
 */
public class ContentSoonExpiredPane extends SimpleContainer {

    private static final Logger log = Logger.getLogger(ContentSoonExpiredPane.class);

    private DataTable dataTable;

    public ContentSoonExpiredPane() {
        add(getDataTable());
    }

    public final void generateXML(final PageState state, final Element parent) {
        if (!isVisible(state)) {
            return;
        }

        SecurityManager sm = CMS.getContext().getSecurityManager();
        User user = Web.getWebContext().getUser();

        DataTable dt = getDataTable();
        DataQuery dq = dt.getDataQuery(state);
        dq.addFilter(getViewFilter(dq, user));

        dt.generateXML(state, parent);

    }

    private static Filter getViewFilter(DataQuery query, User user) {
        PrivilegeDescriptor privilege = new PrivilegeDescriptor(SecurityConstants.CMS_READ_ITEM);
        FilterFactory ff = query.getFilterFactory();
        OID partyOID = user.getOID();
        return PermissionService.getFilterQuery(ff, "objectId", privilege, partyOID);
    }

    protected DataTable getDataTable() {
        if (dataTable == null) {
            dataTable = new DataTable(new ContentSoonExpiredQueryBuilder());
            dataTable.addColumn(GlobalizationUtil.globalize("cms.ui.cse.authorName").localize()
                .toString(),
                                "authorName", true);
            dataTable.addColumn(GlobalizationUtil.globalize("cms.ui.cse.itemName").localize()
                .toString(),
                                "objectId", true, new ItemTitleCellRender());
            dataTable
                .addColumn(GlobalizationUtil.globalize("cms.ui.cse.view").localize().toString(),
                           "objectId", false, new ItemViewLinkCellRender());
            dataTable
                .addColumn(GlobalizationUtil.globalize("cms.ui.cse.edit").localize().toString(),
                           "objectId", false, new ItemEditLinkCellRender());
            dataTable.addColumn(GlobalizationUtil.globalize("cms.ui.cse.endDateTime").localize()
                .toString(),
                                "endDateTime", true);
            
            dataTable.setEmptyView(new Label(GlobalizationUtil.globalize("cms.ui.cse.none")));
        }
        
        return dataTable;
    }

    private static boolean hasSiteWideAdmin(User user) {
        Application adminApp = Admin.getInstance();
        if (adminApp == null) {
            return false;
        }

        PermissionDescriptor admin = new PermissionDescriptor(PrivilegeDescriptor.ADMIN, adminApp,
                                                              user);
        return PermissionService.checkPermission(admin);
    }

    private static GlobalizedMessage gz(final String key) {
        return GlobalizationUtil.globalize(key);
    }

    private static String lz(final String key) {
        return (String) gz(key).localize();
    }

    private class ContentSoonExpiredQueryBuilder implements DataQueryBuilder {

        public String getKeyColumn() {
            return "objectId";
        }

        public DataQuery makeDataQuery(DataTable t, PageState s) {
            Session ses = SessionManager.getSession();
            DataQuery query = ses.retrieveQuery(
                "com.arsdigita.cms.getContentItemExpiredBeforeInSection");

            int months = ContentSection.getConfig().getSoonExpiredMonths();
            int days = ContentSection.getConfig().getSoonExpiredDays();

            Calendar now = Calendar.getInstance();
            now.add(Calendar.DAY_OF_YEAR, days);
            now.add(Calendar.MONTH, months);
            query.setParameter("endDateTime", now.getTime());

            ContentSection section = CMS.getContext().getContentSection();
            query.setParameter("sectionId", section.getID());

            return query;
        }

        public void lock() {
        }

        public boolean isLocked() {
            return false;
        }

    }

    private class ItemTitleCellRender implements TableCellRenderer {

        private ThreadLocal threadLocal;

        public ItemTitleCellRender() {
            threadLocal = new ThreadLocal() {

                protected Object initialValue() {
                    return new Label("");
                }

            };
        }

        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {
            BigDecimal id = (BigDecimal) key;
            Label l = (Label) threadLocal.get();
            l.setLabel(ContentSoonExpiredPane.getItemFromIdString(id.toString()).getDisplayName());
            return l;
        }

    }

    private class ItemEditLinkCellRender implements TableCellRenderer {

        private ThreadLocal threadLocal;

        public ItemEditLinkCellRender() {
            threadLocal = new ThreadLocal() {

                protected Object initialValue() {
                    return new Link(new Label(), "");
                }

            };
        }

        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {
            boolean canEdit = false;
            BigDecimal id = (BigDecimal) key;
            User user = Web.getWebContext().getUser();
            ContentItem ci = getItemFromIdString(id.toString());
            Iterator permissions = PermissionService
                .getImpliedPrivileges(ci.getOID(), user.getOID());
            while (permissions.hasNext()) {
                PrivilegeDescriptor permission = (PrivilegeDescriptor) permissions.next();
                if (permission.equals(PrivilegeDescriptor.ADMIN) || permission.equals(
                    PrivilegeDescriptor.EDIT)) {
                    canEdit = true;
                    break;
                }
            }

            if (!canEdit) {
                return new Label("");
            }

            Link l = (Link) threadLocal.get();
//            l.setTarget(ContentItemPage.getRelativeItemURL(ContentSoonExpiredPane.getItemDraft(id.toString()), ContentItemPage.AUTHORING_TAB));
            l.setTarget(ContentItemPage.getItemURL(ContentSoonExpiredPane.getItemFromIdString(id
                .toString()), ContentItemPage.AUTHORING_TAB));
            ((Label) l.getChild()).setLabel(GlobalizationUtil.globalize("cms.ui.cse.editLink"));

            return l;
        }

    }

    private class ItemViewLinkCellRender implements TableCellRenderer {

        private ThreadLocal threadLocal;

        public ItemViewLinkCellRender() {
            threadLocal = new ThreadLocal() {

                protected Object initialValue() {
                    return new Link(new Label(), "");
                }

            };
        }

        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {
            BigDecimal id = (BigDecimal) key;
            Link l = (Link) threadLocal.get();

            ContentItem item = getItemFromIdString(id.toString());
            String url = ".jsp";
            while (item.getParent() != null) {
                if (item.getParent() instanceof ContentItem) {
                    item = (ContentItem) item.getParent();
                    if (!"/".equals(item.getName())) {
                        url = "/" + item.getName() + url;
                    } else {
                        break;
                    }
                }
            }
            ContentSection section = CMS.getContext().getContentSection();
            l.setTarget("/" + section.getName() + url);

            ((Label) l.getChild()).setLabel(GlobalizationUtil.globalize("cms.ui.cse.viewLink"));
            return l;
        }

    }

    private static ContentItem getItemFromIdString(String idString) {
        return new ContentItem(new OID(ContentItem.class.getName(), Integer.parseInt(idString)));
    }

    private static BigDecimal getItemDraft(String idString) {
        ContentItem item = getItemFromIdString(idString);
        return item.getDraftVersion().getID();
    }

}
