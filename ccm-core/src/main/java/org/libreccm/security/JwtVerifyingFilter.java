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
package org.libreccm.security;

import com.arsdigita.kernel.KernelConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.filter.AccessControlFilter;

import java.security.Key;
import java.util.Base64;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class JwtVerifyingFilter extends AccessControlFilter {

    @Override
    protected boolean isAccessAllowed(
        final ServletRequest request,
        final ServletResponse response,
        final Object mappedValue) throws Exception {

        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final String jwt = httpRequest.getHeader("Authorization");
        if (jwt == null || jwt.startsWith("Bearer ")) {
            return false;
        }

        final SignatureAlgorithm signAlgo = SignatureAlgorithm.HS512;
        final Key key = new SecretKeySpec(
            Base64.getDecoder().decode(
                KernelConfig.getConfig().getJwtSecret()),
            signAlgo.getJcaName());

        final String jwtClaims = jwt.substring(jwt.indexOf((" ")));
        final Claims claims = Jwts
            .parser()
            .setSigningKey(key)
            .parseClaimsJws(jwtClaims)
            .getBody();

        final String userName = claims.getSubject();
        final Subject subject = SecurityUtils.getSubject();
        
        return userName.equals(subject.getPrincipal());
    }

    @Override
    protected boolean onAccessDenied(
        final ServletRequest request,
        final ServletResponse response) throws Exception {

        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return false;
    }

}
