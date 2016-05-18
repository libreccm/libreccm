/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package com.arsdigita.ui;


import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.Configuration;
import org.libreccm.configuration.ConfigurationException;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.configuration.Setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;


/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Configuration
public final class UIConfig {
    
    @Setting
    private List<String> defaultLayout = Arrays.asList(new String[]{
        "top:com.arsdigita.ui.UserBanner",
        "bottom:com.arsdigita.ui.SiteBanner",
        "bottom:com.arsdigita.ui.DebugPanel"
    });
    
    @Setting
    private String rootPageUrl = "/register/";
    
    @Setting
    private String userRedirectUrl = "/permissions/";
    
    @Setting
    private String workspaceUrl = "/pvt/";
    
    public static UIConfig getConfig() {
        final ConfigurationManager confManager = CdiUtil.createCdiUtil()
            .findBean(ConfigurationManager.class);
        
        return confManager.findConfiguration(UIConfig.class);
    }
    
    public UIConfig() {
        super();
    }
    
    public List<String> getDefaultLayout() {
        return new ArrayList<>(defaultLayout);
    }
    
    public void setDefaultLayout(final List<String> defaultLayout) {
        this.defaultLayout = defaultLayout;
    }
    
    public SimplePageLayout buildDefaultLayout() {
        final SimplePageLayout layout = new SimplePageLayout();
        
        defaultLayout.forEach(c -> {
            final String[] tokens = c.split(":");
            if (tokens.length != 2) {
                throw new ConfigurationException(
                    "Default layout not provided in the correct format.");
            }
            
            Class<?> clazz;
            try {
                clazz = Class.forName(tokens[1]);
            } catch (ClassNotFoundException ex) {
                throw new ConfigurationException(String.format(
                    "Component \"%s\" not found.", tokens[1]),
                                                 ex);
            }
            
            layout.addComponent(clazz, tokens[0]);
        });
        
        return layout;
    }
    
    public String getRootPageUrl() {
        return rootPageUrl;
    }
    
    public void setRootPageUrl(final String rootPageUrl) {
        this.rootPageUrl = rootPageUrl;
    }
    
    public String getUserRedirectUrl() {
        return userRedirectUrl;
    }
    
    public void setUserRedirectUrl(final String userRedirectUrl) {
        this.userRedirectUrl = userRedirectUrl;
    }
    
    public String getWorkspaceUrl() {
        return workspaceUrl;
    }
    
    public void setWorkspaceUrl(final String workspaceUrl) {
        this.workspaceUrl = workspaceUrl;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(defaultLayout);
        hash = 31 * hash + Objects.hashCode(rootPageUrl);
        hash = 31 * hash + Objects.hashCode(userRedirectUrl);
        hash = 31 * hash + Objects.hashCode(workspaceUrl);
        return hash;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof UIConfig)) {
            return false;
        }
        final UIConfig other = (UIConfig) obj;
        if (!Objects.equals(rootPageUrl, other.getRootPageUrl())) {
            return false;
        }
        if (!Objects.equals(userRedirectUrl, other.getUserRedirectUrl())) {
            return false;
        }
        if (!Objects.equals(workspaceUrl, other.getWorkspaceUrl())) {
            return false;
        }
        return Objects.equals(defaultLayout, other.getDefaultLayout());
    }
    
    @Override
    public String toString() {
//        final StringJoiner joiner = new StringJoiner(", ");
//        if (defaultLayout != null) {
//            defaultLayout.forEach(s -> joiner.add(s));
//        }
        
        return String.format("%s{ "
                                 + "defaultLayout = \"%s\", "
                                 + "rootPageUrl = \"%s\", "
                                 + "userRedirectUrl = \"%s\", "
                                 + "workspaceUrl = \"%s\""
                                 + " }",
                             super.toString(),
                             defaultLayout,
                             rootPageUrl,
                             userRedirectUrl,
                             workspaceUrl);
    }
    
}
