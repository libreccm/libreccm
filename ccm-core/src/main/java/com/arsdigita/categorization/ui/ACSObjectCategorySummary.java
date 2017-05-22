/*
 * Copyright (C) 2007 Chris Gilbert
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
package com.arsdigita.categorization.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.logging.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.kernel.KernelConfig;

import org.libreccm.core.CcmObject;
import org.libreccm.categorization.Category;

import com.arsdigita.util.Assert;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;

import org.apache.logging.log4j.LogManager;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.categorization.ObjectNotAssignedToCategoryException;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.UnexpectedErrorException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * abstract class for displaying the categories assigned to an object under one
 * or more root nodes. Subclasses should retrieve the object to be assigned and
 * supply the logic to retrieve root categories.
 *
 * * abstracted from ItemCategorySummary in ccm-cms
 *
 * @author chris.gilbert@westsussex.gov.uk
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 *
 *
 */
public abstract class ACSObjectCategorySummary extends SimpleComponent {

    private static final Logger LOGGER = LogManager
        .getLogger(ACSObjectCategorySummary.class);

    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_ADD = "add";
    public static final String ACTION_ADD_JS = "addJS";

    private final Map<String, ActionListener> listenersLap = new HashMap<>();

    public ACSObjectCategorySummary() {
        registerAction(ACTION_DELETE,
                       new DeleteActionListener());
    }

    public void registerAction(final String name,
                               final ActionListener listener) {
        listenersLap.put(name, listener);
    }

    @Override
    public void respond(final PageState state) throws ServletException {

        super.respond(state);

        Assert.isTrue(canEdit(state), "User can edit object");

        final String name = state.getControlEventName();
        final ActionListener listener = listenersLap.get(name);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Got event {} listener {}",
                         name,
                         listener);
        }

        if (listener != null) {
            listener.actionPerformed(new ActionEvent(this, state));
        }
    }

    /**
     * default behaviour is to check for edit access on the current resource. as
     * defined by getCategorizedObject. This can be overridden by subclasses, if
     * for instance a specific privilege should be checked
     *
     * @param state
     *
     * @return
     */
    protected abstract boolean canEdit(final PageState state);

    protected abstract CcmObject getObject(PageState state);

    protected abstract String getXMLPrefix();

    protected abstract String getXMLNameSpace();

    protected abstract List<Category> getRootCategories(PageState state);

    @Override
    public void generateXML(final PageState state, final Element parent) {

        final boolean canEdit = canEdit(state);

        final Element content = parent
            .newChildElement(String.format("%s:categoryStepSummary",
                                           getXMLPrefix()),
                             getXMLNameSpace());
        exportAttributes(content);

        final Element rootCats = content
            .newChildElement(String.format("%s:categoryRoots",
                                           getXMLPrefix()),
                             getXMLNameSpace());

        final List<Category> roots = getRootCategories(state);
        for (final Category rootCategory : roots) {

            final Element root = rootCats
                .newChildElement(String.format("%s:categoryRoot",
                                               getXMLPrefix()),
                                 getXMLNameSpace());
            root.addAttribute("name", rootCategory.getName());
            root.addAttribute("description",
                              rootCategory
                                  .getDescription()
                                  .getValue(KernelConfig
                                      .getConfig()
                                      .getDefaultLocale()));

            if (canEdit) {
                state.setControlEvent(this,
                                      ACTION_ADD,
                                      Long.toString(rootCategory.getObjectId()));
                try {
                    root.addAttribute("addAction",
                                      XML.format(state.stateAsURL()));
                } catch (IOException ex) {
                    throw new UnexpectedErrorException("cannot generate URL",
                                                       ex);
                }
                state.clearControlEvent();
                state.setControlEvent(this,
                                      ACTION_ADD_JS,
                                      Long.toString(rootCategory.getObjectId()));
                try {
                    root.addAttribute("addJsAction",
                                      XML.format(state.stateAsURL()));
                } catch (IOException ex) {
                    throw new UnexpectedErrorException("cannot generate URL",
                                                       ex);
                }
                state.clearControlEvent();
            }
        }

        final Element itemCats = content
            .newChildElement(String.format("%s:itemCategories",
                                           getXMLPrefix()),
                             getXMLNameSpace());

        final List<Category> categories = getObject(state)
            .getCategories()
            .stream()
            .map(categorization -> categorization.getCategory())
            .collect(Collectors.toList());

        final CategoryManager categoryManager = CdiUtil
            .createCdiUtil()
            .findBean(CategoryManager.class);

        for (final Category category : categories) {

            final String path = categoryManager.getCategoryPath(category);

            final Element categoryElem = itemCats
                .newChildElement(String.format("%s:itemCategory",
                                               getXMLPrefix()),
                                 getXMLNameSpace());
            categoryElem.addAttribute("name", category.getName());
            categoryElem.addAttribute("description",
                                      category
                                          .getDescription()
                                          .getValue(KernelConfig
                                              .getConfig()
                                              .getDefaultLocale()));
            categoryElem.addAttribute("path", XML.format(path));

            if (canEdit) {
                state.setControlEvent(this,
                                      ACTION_DELETE,
                                      Long.toString(category.getObjectId()));
                try {
                    categoryElem.addAttribute("deleteAction",
                                              XML.format(state.stateAsURL()));
                } catch (IOException ex) {
                    throw new UnexpectedErrorException("cannot generate URL",
                                                       ex);
                }
                state.clearControlEvent();
            }
        }

    }

    private class DeleteActionListener implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent event) {

            final PageState state = event.getPageState();
            final String value = state.getControlEventValue();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final CategoryRepository categoryRepo = cdiUtil
                .findBean(CategoryRepository.class);
            final Category category = categoryRepo
                .findById(Long.parseLong(value))
                .orElseThrow(() -> new IllegalArgumentException(
                String.format(
                    "No Category with ID %s in the database. "
                        + "Where did that ID come from?",
                    value)));

            final CcmObject object = getObject(state);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Removing category {} from {}",
                             Objects.toString(category),
                             Objects.toString(object));
            }
            
            final CategoryManager categoryManager = cdiUtil
            .findBean(CategoryManager.class);
            try {
            categoryManager.removeObjectFromCategory(object, category);
            } catch(ObjectNotAssignedToCategoryException ex) {
                throw new UnexpectedErrorException(ex);
            }

            state.clearControlEvent();
            throw new RedirectSignal(state.toURL(), true);
        }

    }

}
