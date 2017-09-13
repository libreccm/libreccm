/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.london.terms.ui;

import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.categorization.ui.ACSObjectCategoryForm;

import org.apache.logging.log4j.LogManager;
import org.libreccm.categorization.Domain;

import org.apache.logging.log4j.Logger;
import org.libreccm.categorization.DomainRepository;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;

import java.util.Optional;

/**
 * abstracted from original version of c.ad.aplaws.ui.ItemCategoryPicker r1297
 *
 * @author Chris Gilbert
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
// NON JavaDoc:
// copied from c.ad.aplaws.ui (module ccm-ldn-aplaws) in order to avoid a
// dependency from the integration layer for forum-categorised. Otherwise you
// have had to specify the specific integration layer (i.e. ccm-???-aplaws) in
// application.xml for compiling, which may be different for each installation.
public abstract class ACSObjectCategoryPicker extends SimpleContainer {

    private static final Logger LOGGER = LogManager
        .getLogger(ACSObjectCategoryPicker.class);

    private final ACSObjectCategoryForm form;
    private final LongParameter rootParam;

    public ACSObjectCategoryPicker(final LongParameter rootParam,
                                   final StringParameter mode) {

        form = getForm(rootParam, mode);
        this.rootParam = rootParam;

        super.add(form);
        form.addCompletionListener(new ItemCategoryFormCompletion());
    }

    protected abstract ACSObjectCategoryForm getForm(LongParameter root,
                                                     StringParameter mode);

    protected abstract CcmObject getObject(PageState state);

//    protected List<Long> getCurrentCategories(final Domain domain,
//                                                  final CcmObject object) {
//
//        LOGGER.debug("Getting terms from {} to {}", domain, object);
//        final List<Category> terms = domain.getRoot().getSubCategories();
//        terms.addEqualsFilter("model.childObjects.id", object.getID());
//        terms.addPath("model.id");
//
//        List<Long> current = new ArrayList<>();
//        while (terms.next()) {
//            if (LOGGER.isDebugEnabled()) {
//                LOGGER.debug("Got term " + terms.get("model.id"));
//            }
//            current.add(terms.get("model.id"));
//        }
//        return current;
//    }
    // TODO move out of UI code
//    public static Collection getCurrentTerms(Domain domain, ACSObject object) {
//        if (LOGGER.isDebugEnabled()) {
//            LOGGER.debug("Getting terms from " + domain + " to " + object);
//        }
//        Collection current = new LinkedList();
//        DomainCollection terms = domain.getTerms();
//        terms.addEqualsFilter("model.childObjects.id", object.getID());
//        terms.addPath("model.id");
//        while (terms.next()) {
//            if (LOGGER.isDebugEnabled()) {
//                LOGGER.debug("Got term " + terms.get("model.id"));
//            }
//            current.add(terms.getDomainObject());
//        }
//        return current;
//    }
//
//    // TODO move out of UI code
//    public static Collection getRelatedTerms(Collection src, Domain domain) {
//        if (LOGGER.isDebugEnabled()) {
//            LOGGER.debug("Getting related terms to " + domain);
//
//        }
//        if (src.isEmpty()) {
//            // this is a hack, it would be better not to use a completion event listener as
//            // this is called even when the form is cancelled...
//            return new LinkedList();
//        }
//        DomainCollection terms = domain.getTerms();
//        // these next two lines build the query
//        terms.addEqualsFilter("model.parents.link.relationType", "related");
//        terms.addFilter("model.parents.id in :ids").set("ids", src);
//
//        Collection related = new LinkedList();
//        while (terms.next()) {
//            if (LOGGER.isDebugEnabled()) {
//                LOGGER.debug("Got term " + terms.getDomainObject());
//            }
//            related.add(terms.getDomainObject());
//        }
//        return related;
//    }
//    protected void clearTerms(Domain domain, ACSObject object) {
//        if (LOGGER.isDebugEnabled()) {
//            LOGGER.debug("Removing terms from " + domain + " to " + object);
//        }
//        Iterator terms = getCurrentTerms(domain, object).iterator();
//        while (terms.hasNext()) {
//            Term term = (Term) terms.next();
//            if (LOGGER.isDebugEnabled()) {
//                LOGGER.debug("Removing term " + term + " from " + object);
//            }
//            term.removeObject(object);
//        }
//    }
//    // TODO move out of UI code
//    public static void assignTerms(Collection terms, ACSObject object) {
//        if (LOGGER.isDebugEnabled()) {
//            LOGGER.debug("Assigning terms to " + object);
//        }
//        Iterator i = terms.iterator();
//        while (i.hasNext()) {
//            Term term = (Term) i.next();
//            if (LOGGER.isDebugEnabled()) {
//                LOGGER.debug("Assigning term " + term + " to " + object);
//            }
//            term.addObject(object);
//        }
//    }
    protected Domain getDomain(final PageState state) {
        LOGGER.debug("Getting domain for {}", state.getValue(rootParam));

        final Long domainId = (Long) state.getValue(rootParam);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final DomainRepository domainRepo = cdiUtil
            .findBean(DomainRepository.class);

        final Optional<Domain> domain = domainRepo
            .findById(domainId);

        if (domain.isPresent()) {
            return domain.get();
        } else {
            LOGGER.warn("No Domain for ID {} found.", domainId);
            return null;
        }
    }

    /**
     *
     */
    private class ItemCategoryFormCompletion implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent event) {

            final PageState state = event.getPageState();
            final Domain domain = getDomain(state);
            final String domainKey = domain.getDomainKey();

            LOGGER.debug("Saving categories in: {}", domainKey);

            fireCompletionEvent(state);
        }

    }

}
