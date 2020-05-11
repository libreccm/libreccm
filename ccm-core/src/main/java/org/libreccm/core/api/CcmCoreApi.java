/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libreccm.core.api;

import org.libreccm.api.CorsFilter;
import org.libreccm.api.DefaultResponseHeaders;
import org.libreccm.api.PreflightRequestFilter;
import org.libreccm.security.UsersApi;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationPath("/api/ccm-core")
public class CcmCoreApi extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        
        final Set<Class<?>> classes = new HashSet<>();
        classes.add(CorsFilter.class);
        classes.add(DefaultResponseHeaders.class);
        classes.add(PreflightRequestFilter.class);
        classes.add(UsersApi.class);
        return classes;
    }
    
    
    
}
