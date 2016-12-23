/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.search.ui;

import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.globalization.Globalization;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.search.SearchConstants;
import com.arsdigita.util.UncheckedWrapperException;

import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;

import com.arsdigita.web.URL;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.Web;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.Query;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;
import org.libreccm.search.SearchManager;

public class ResultsPane extends SimpleComponent {

    private static final Logger LOGGER = LogManager.getLogger(ResultsPane.class);
    public static final int PAGE_SIZE = 10;

    private final int pageSize = PAGE_SIZE;

    private final QueryGenerator queryGenerator;
    private IntegerParameter pageNumber;
    private boolean relative;
    //jensp 2014-03-04 Allow using classes to set a suitable info messages.
    private GlobalizedMessage searchHelpMsg;
    private GlobalizedMessage noResultsMsg;

    public ResultsPane(final QueryGenerator query) {
        pageNumber = new IntegerParameter("page");
        relative = false;
        this.queryGenerator = query;
    }

    /**
     * Determines whether the links to the search results will be relative or
     * absolute. The default is absolute.
     *
     * @param relative
     */
    public void setRelativeURLs(final boolean relative) {
        this.relative = relative;
    }

    public void setSearchHelpMsg(final GlobalizedMessage msg) {
        searchHelpMsg = msg;
    }

    public void setNoResultsMsg(final GlobalizedMessage msg) {
        noResultsMsg = msg;
    }

    @Override
    public void generateXML(final PageState state, final Element parent) {
        if (!queryGenerator.hasQuery(state)) {

            LOGGER.debug("No query available, skipping XML generation");

            final Element content = new Element(
                    SearchConstants.XML_PREFIX + "results",
                    SearchConstants.XML_NS);
            final Element info = content.newChildElement("info");
            if (searchHelpMsg == null) {
                info.setText(
                        "To search for content items, please enter at least 3 letters into the search field. You can narrow the result by using additional parameters.");
            } else {
                //info.setText(GlobalizationUtil.globalize("cms.ui.search_help").localize().toString());
                info.setText(searchHelpMsg.localize().toString());
            }

            parent.addContent(content);
            return;
        }

        final Query spec = queryGenerator.getQuerySpecification(state);
        final SearchManager searchManager = CdiUtil.createCdiUtil().findBean(
                SearchManager.class);
        final List<CcmObject> results = (List<CcmObject>) searchManager.
                executeQuery(spec);
        LOGGER.debug("Got result list with {} items.", results.size());
        if (results.isEmpty()) {
            final long objectCount = results.size();
            final int pageCount = (int) Math.ceil((double) objectCount
                                                          / (double) pageSize);

            final Integer page = (Integer) pageNumber.transformValue(state.
                    getRequest());
            int pageNum;
            if (page == null) {
                pageNum = 1;
            } else if (page < 1) {
                pageNum = 1;
            } else if (page > pageCount) {
                if (pageCount == 0) {
                    pageNum = 1;
                } else {
                    pageNum = page;
                }
            } else {
                pageNum = page;
            }

            final long begin = ((pageNum - 1) * pageSize);
            final long count = Math.min(pageSize, (objectCount - begin));
            final long end = begin + count;

            final Iterator iterator = results
                    .subList((int) begin, (int) begin + (int) count)
                    .iterator();

            final Element content = new Element(
                    SearchConstants.XML_PREFIX + "results",
                    SearchConstants.XML_NS);
            exportAttributes(content);

            LOGGER.debug("Paginator stats\n"
                                 + "  page number: {}\n"
                                 + "  page count.: {}\n"
                                 + "  page size..: {}\n"
                                 + "  begin......: {}\n"
                                 + "  end........: {}\n"
                                 + "  count      : {}",
                         pageNum,
                         pageCount,
                         pageSize,
                         begin,
                         end,
                         objectCount);

            content.addContent(generatePaginatorXML(state,
                                                    pageNumber.getName(),
                                                    pageNum,
                                                    pageCount,
                                                    pageSize,
                                                    begin,
                                                    end,
                                                    objectCount));
            content.addContent(generateDocumentsXML(state, iterator));

            parent.addContent(content);
        } else {
            // No search result, so we don't need a paginator, but we want
            // to inform the user, that there are no results for this search
            final Element content = new Element(
                    SearchConstants.XML_PREFIX + "results",
                    SearchConstants.XML_NS);
            final Element info = content.newChildElement("info");
//                info.setText(GlobalizationUtil.globalize("cms.ui.search_no_results").localize().toString());
            if (noResultsMsg == null) {
                info.setText("Sorry. Your search returned 0 results.");
            } else {
                info.setText(noResultsMsg.localize().toString());
            }
            parent.addContent(content);
        }
    }

    protected Element generatePaginatorXML(final PageState state,
                                           final String pageParam,
                                           final int pageNumber,
                                           final int pageCount,
                                           final int pageSize,
                                           final long begin,
                                           final long end,
                                           final long objectCount) {
        final Element paginator = new Element(
                SearchConstants.XML_PREFIX + "paginator",
                SearchConstants.XML_NS);
        final URL url = Web.getWebContext().getRequestURL();

        final ParameterMap parameterMap = new ParameterMap();
        final Iterator current = url.getParameterMap().keySet().iterator();
        while (current.hasNext()) {
            final String key = (String) current.next();
            if (key.equals(pageParam)) {
                continue;
            }
            parameterMap.setParameterValues(
                    key, decodeParameters(url.getParameterValues(key), state));
        }

        paginator.addAttribute("pageParam", this.pageNumber.getName());
        paginator.addAttribute("baseURL", URL.there(url.getPathInfo(),
                                                    parameterMap).toString());
        paginator.addAttribute("pageNumber", XML.format(pageNumber));
        paginator.addAttribute("pageCount", XML.format(pageCount));
        paginator.addAttribute("pageSize", XML.format(pageSize));
        paginator.addAttribute("objectBegin", XML.format(begin + 1));
        paginator.addAttribute("objectEnd", XML.format(end));
        paginator.addAttribute("objectCount", XML.format(objectCount));

        return paginator;
    }

    private String[] decodeParameters(final String[] parameters,
                                      final PageState state) {

        final String[] decoded = new String[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            decoded[i] = decodeParameter(parameters[i], state);
        }

        return decoded;
    }

    private String decodeParameter(final String parameter,
                                   final PageState state) {
        String re = state.getRequest().getParameter(
                Globalization.ENCODING_PARAM_NAME);

        if ((re == null) || (re.isEmpty())) {
            re = Globalization.getDefaultCharset();
        }

        if ((parameter == null) || (parameter.isEmpty())) {
            return parameter;
        } else if (Globalization.getDefaultCharset(state.getRequest()).
                equals(re)) {
            return parameter;
        } else {
            try {
                return new String(parameter.getBytes(Globalization.
                        getDefaultCharset(
                                state.getRequest())), re);
            } catch (UnsupportedEncodingException ex) {
                LOGGER.warn("Unsupported encoding.", ex);
                return parameter;
            }
        }
    }

    protected Element generateDocumentsXML(final PageState state,
                                           final Iterator<CcmObject> results) {

        final Element documents = new Element(
                SearchConstants.XML_PREFIX + "documents",
                SearchConstants.XML_NS);

        LOGGER.debug("Outputting documents");

        while (results.hasNext()) {
            final CcmObject doc = results.next();
            LOGGER.debug("Current document {} {}",
                         doc.getObjectId(),
                         doc.getDisplayName());

            documents.addContent(generateDocumentXML(state, doc));
        }

        return documents;
    }

    private Optional<String> getSummary(final CcmObject doc) {
        final BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(doc.getClass());
        } catch (IntrospectionException ex) {
            throw new UncheckedWrapperException(ex);
        }

        final Optional<PropertyDescriptor> propertyDesc
                                           = Arrays.stream(beanInfo.
                        getPropertyDescriptors())
                        .filter(descriptor -> {
                            return "description".equals(descriptor.getName())
                                           || "summary".equals(descriptor.
                                            getName());
                        })
                        .findFirst();

        if (propertyDesc.isPresent()) {
            final Method readMethod = propertyDesc.get().getReadMethod();
            final String summary;
            try {
                summary = (String) readMethod.invoke(doc);
            } catch (IllegalAccessException
                             | IllegalArgumentException
                             | InvocationTargetException ex) {
                throw new UncheckedWrapperException(ex);
            }
            return Optional.of(summary);
        } else {
            return Optional.empty();
        }
    }

    protected Element generateDocumentXML(final PageState state,
                                          final CcmObject doc) {
        final Element entry = new Element(SearchConstants.XML_PREFIX + "object",
                                          SearchConstants.XML_NS);

        final Optional<String> summary = getSummary(doc);

        entry.addAttribute("id", XML.format(doc.getObjectId()));
        entry.addAttribute("uuid", XML.format(doc.getUuid()));
//        entry.addAttribute("url", XML.format(relative ? url.getPath() + "?"
//                                                                + url.getQuery()
//                                             : url.toString()));
        entry.addAttribute("title", XML.format(doc.getDisplayName()));
        if (summary.isPresent()) {
            entry.addAttribute("summary", XML.format(summary));
        }

//        entry.addAttribute("locale", XML.format(doc.getLocale()));

//        Date creationDate = doc.getCreationDate();
//        if (creationDate != null) {
//            entry.addAttribute("creationDate", XML.format(
//                               creationDate.toString()));
//        }
//        Party creationParty = doc.getCreationParty();
//        if (creationParty != null) {
//            entry.addAttribute("creationParty",
//                               XML.format(creationParty.getDisplayName()));
//        }
//
//        Date lastModifiedDate = doc.getLastModifiedDate();
//        if (lastModifiedDate != null) {
//            entry.addAttribute("lastModifiedDate",
//                               XML.format(lastModifiedDate));
//        }
//        Party lastModifiedParty = doc.getLastModifiedParty();
//        if (lastModifiedParty != null) {
//            entry.addAttribute("lastModifiedParty",
//                               XML.format(lastModifiedParty.getDisplayName()));
//        }

//        LOGGER.debug(
//                "about to add the contentSectionName from search index Doc to search result xml");
//        entry.addAttribute("contentSectionName", XML.format(doc.
//                           getContentSection()));

        return entry;
    }

}
