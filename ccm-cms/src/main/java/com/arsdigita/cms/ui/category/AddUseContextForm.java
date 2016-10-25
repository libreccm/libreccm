/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.category;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.parameters.GlobalizedParameterListener;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ui.BaseForm;
import com.arsdigita.kernel.KernelConfig;
import org.apache.log4j.Logger;
import org.libreccm.categorization.Category;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.LocalizedString;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.privileges.AdminPrivileges;


/**
 * TODO Needs a description.
 *
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 * @author Scott Seago
 * @version $Id: AddUseContextForm.java 2090 2010-04-17 08:04:14Z pboy $
 */
class AddUseContextForm extends BaseForm {

    private static final Logger s_log = Logger.getLogger(AddUseContextForm.class);

    private final SingleSelectionModel m_model;

    private final Name m_useContext;
    private final Name m_rootName;
    private final Description m_rootDescription;

    AddUseContextForm(final SingleSelectionModel model) {
        super("useContext", gz("cms.ui.category.add_use_context"));

        m_model = model;
        m_useContext = new Name("useContext", 200, true);
        addField(gz("cms.ui.category.use_context"), m_useContext);
        m_rootName = new Name("rootName", 200, true);
        addField(gz("cms.ui.name"), m_rootName);
        m_rootDescription = new Description("rootName", 200, true);
        addField(gz("cms.ui.description"), m_rootDescription);

        addAction(new Finish());
        addAction(new Cancel());

        addSecurityListener(AdminPrivileges.ADMINISTER_CATEGORIES);
        m_useContext.addValidationListener(new ValidationListener());

        addProcessListener(new ProcessListener());
    }

    private class ValidationListener extends GlobalizedParameterListener {
        public ValidationListener() {
            super();
            setError(GlobalizationUtil.globalize("cms.ui.category.use_context_must_be_unique"));
        }

        public final void validate(final ParameterEvent e)
                throws FormProcessException {

            ParameterData data = e.getParameterData();

            final String name = (String) m_useContext.getValue(e.getPageState());
            if (true) {//Category.getRootForObject(CMS.getContext().getContentSection(),
                                          //name) != null) {
                data.addError(getError());
            }
        }
    }
    private class ProcessListener implements FormProcessListener {
        public final void process(final FormSectionEvent e)
                throws FormProcessException {
            final PageState state = e.getPageState();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ConfigurationManager manager = cdiUtil.findBean(ConfigurationManager.class);
            final KernelConfig config = manager.findConfiguration(KernelConfig.class);

            final String useContext = (String) m_useContext.getValue(state);

            final String rootName = (String) m_rootName.getValue(state);
            final LocalizedString rootDescription = new LocalizedString();
            rootDescription.addValue(config.getDefaultLocale(), (String) m_rootDescription.getValue(state));

            Category root = new Category();
            root.setName(rootName);
            root.setDescription(rootDescription);
            root.setAbstractCategory(true);
            
            final ContentSection section =
                CMS.getContext().getContentSection();

            /*
            Category.setRootForObject(section, root, useContext);
            PermissionService.setContext(root, section);
            Category defaultRoot = Category.getRootForObject(section);
            if (defaultRoot != null) {
                ObjectPermissionCollection coll = 
                    PermissionService.getDirectGrantedPermissions(defaultRoot.getOID());
                while (coll.next()) {
                    PermissionService.grantPermission(new PermissionDescriptor(coll.getPrivilege(), 
                                                                               root.getOID(), 
                                                                               coll.getGranteeOID()));
                }

            }*/
//            m_model.setSelectedKey(state, useContext == null ? 
//                                   CategoryUseContextModelBuilder.DEFAULT_USE_CONTEXT :
//                                   useContext);
        }
    }
}
