/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package org.librecms.contentsection.rs;

import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/")
public class ContentSections {

    @Inject
    private ContentSectionRepository sectionRepo;

    @GET
    @Path("/")
    @Produces("text/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Map<String, String>> listContentSections() {

        final List<ContentSection> sections = sectionRepo.findAll();

        return sections
            .stream()
            .map(this::createContentSectionMapEntry)
            .collect(Collectors.toList());
    }

    private Map<String, String> createContentSectionMapEntry(
        final ContentSection section) {

        Objects.requireNonNull(section);
        
        final Map<String, String> result = new HashMap<>();
        
        result.put("objectId", Long.toString(section.getObjectId()));
        result.put("primaryUrl", section.getPrimaryUrl());
        
        return result;
    }
}
