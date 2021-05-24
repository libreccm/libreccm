/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.ui.contentsections.assets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsLegalMetadataEditStepModel")
public class LegalMetadataEditStepModel {
    
    private String rightsHolder;
    
    private Map<String, String> rights;
    
    private List<String> unusedRightsLocales;
    
    private String publisher;
    
    private String creator;
    
    private List<String> contributors;

    public String getRightsHolder() {
        return rightsHolder;
    }


    protected void setRightsHolder(final String rightsHolder) {
        this.rightsHolder = rightsHolder;
    }
    
        public List<String> getUnusedRightsLocales() {
        return Collections.unmodifiableList(unusedRightsLocales);
    }

    public void setUnusedRightsLocales(final List<String> unusedRightsLocales) {
        this.unusedRightsLocales = new ArrayList<>(unusedRightsLocales);
    }


    public Map<String, String> getRights() {
        return Collections.unmodifiableMap(rights);
    }

    protected void setRights(final Map<String, String> rights) {
        this.rights = new HashMap<>(rights);
    }

    public String getPublisher() {
        return publisher;
    }

    protected void setPublisher(final String publisher) {
        this.publisher = publisher;
    }

    public String getCreator() {
        return creator;
    }

    protected void setCreator(final String creator) {
        this.creator = creator;
    }

    public List<String> getContributors() {
        return Collections.unmodifiableList(contributors);
    }

    protected void setContributors(final List<String> contributors) {
        this.contributors = new ArrayList<>(contributors);
    }
    
    
    
}
