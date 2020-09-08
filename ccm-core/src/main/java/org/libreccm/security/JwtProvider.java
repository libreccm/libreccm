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

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import io.jsonwebtoken.SignatureAlgorithm;
import org.libreccm.configuration.ConfigurationManager;

import java.io.StringReader;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

import javax.crypto.spec.SecretKeySpec;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.transaction.Transactional;
import javax.ws.rs.POST;

/**
 * JAX-RS endpoint for generating JSON Web Tokens for authenticiation.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/")
public class JwtProvider {

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private Shiro shiro;

    /**
     * Generates a new JSON Web Token
     *
     * @param requestCredentials Credentials for authentication as JSON object
     *                           with the properties {@code username} and
     *                           {@code password}.
     *
     * @return A response with the JSON Web Token for a Forbidden response if
     *         the credentials are incorrect.
     */
    @POST
    @Path("/")
    @Transactional(Transactional.TxType.REQUIRED)
    public Response getJsonWebToken(final String requestCredentials) {

        if (requestCredentials == null) {
            return Response
                .status(Response.Status.BAD_REQUEST)
                .entity("No credentials provided")
                .build();
        }

        final StringReader credentialsReader = new StringReader(
            requestCredentials);
        final JsonReader jsonReader = Json.createReader(credentialsReader);
        final JsonObject credentials = jsonReader.readObject();

        final String userName = credentials.getString("username", null);
        final String password = credentials.getString("password", null);

        if (userName == null
                || userName.isEmpty()
                || userName.matches("\\s*")) {

            return Response
                .status(Response.Status.BAD_REQUEST)
                .entity("No user name was provided")
                .build();
        }

        if (password == null
                || password.isEmpty()
                || password.matches("\\s*")) {

            return Response
                .status(Response.Status.BAD_REQUEST)
                .entity("No password was provided")
                .build();
        }

        final UsernamePasswordToken token = new UsernamePasswordToken(
            userName, password);
        final Subject subject = shiro.getSubject();

        final KernelConfig kernelConfig = confManager
            .findConfiguration(KernelConfig.class);
        if (kernelConfig.getJwtSecret() == null
                || kernelConfig.getJwtSecret().isEmpty()
                || kernelConfig.getJwtSecret().matches("\\s*")) {

            shiro.getSystemUser().execute(this::generateSecret);
        }

        try {

            subject.login(token);

            final SignatureAlgorithm signAlgo = SignatureAlgorithm.HS512;
            final Key key = new SecretKeySpec(
                Base64.getDecoder().decode(kernelConfig.getJwtSecret()),
                signAlgo.getJcaName());
            final JwtBuilder jwtBuilder = Jwts
                .builder()
                .setSubject((String) subject.getPrincipal())
                .signWith(key);

            return Response
                .ok(jwtBuilder.compact())
                .build();
        } catch (AuthenticationException ex) {
            return Response
                .status(Response.Status.FORBIDDEN)
                .build();
        }
    }

    /**
     * Helper method for generating a secret for the JSON Web Tokens. 
     * 
     * Only called if no secret if found in the {@link KernelConfig}. The secret
     * is stored in the {@link KernelConfig}.
     */
    private void generateSecret() {
        final Random random = new SecureRandom();
        final byte[] randomBytes = new byte[64];
        random.nextBytes(randomBytes);

        final String secret = Base64
            .getEncoder()
            .encodeToString(randomBytes);

        final KernelConfig kernelConfig = confManager
            .findConfiguration(KernelConfig.class);

        kernelConfig.setJwtSecret(secret);
        confManager.saveConfiguration(kernelConfig);
    }

}
