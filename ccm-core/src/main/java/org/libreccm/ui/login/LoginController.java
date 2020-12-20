/*
 * Copyright (C) 2020 LibreCCM Foundation.
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
package org.libreccm.ui.login;

import com.arsdigita.kernel.KernelConfig;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.security.ChallengeManager;
import org.libreccm.security.User;
import org.libreccm.security.UserRepository;
import org.libreccm.theming.mvc.ThemesMvc;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Controller
@Path("/")
@RequestScoped
public class LoginController {
    
    @Inject
    private ChallengeManager challengeManager;
    
    @Inject
    private ConfigurationManager confManager;
    
    @Inject
    private Models models;
    
    @Inject
    private Subject subject;
    
    @Inject
    private ThemesMvc themesMvc;
    
    @Inject
    private UserRepository userRepository;
    
    @GET
    @Path("/")
    public String getLoginForm(
        @Context final UriInfo uriInfo,
        @QueryParam("return_url") final String redirectUrl
       
    ) {
       models.put(
            "emailIsPrimaryIdentifier", isEmailPrimaryIdentifier()
        );
       models.put("returnUrl", redirectUrl);
        return themesMvc.getMvcTemplate(uriInfo, "login-form");
    }
    
    @POST
    @Path("/")
    public String processLogin(
        @Context final UriInfo uriInfo,
        @FormParam("login") final String login,
        @FormParam("password") final String password,
        @FormParam("rememberMe") final String rememberMeValue,
        @FormParam("redirectUrl") @DefaultValue("") final String redirectUrl
    ) {
        final UsernamePasswordToken token = new UsernamePasswordToken(
            login, password
        );
        token.setRememberMe("on".equals(rememberMeValue));
        try {
            subject.login(token);
        } catch(AuthenticationException ex) {
            models.put("loginFailed", true);
            return getLoginForm(uriInfo, redirectUrl);
        }
        
        return String.format("redirect:%s", redirectUrl);
    }
    
    @GET
    @Path("/recover-password")
    public String getRecoverPasswordForm(@Context final UriInfo uriInfo) {
        return themesMvc.getMvcTemplate(uriInfo, "login-recover-password");
    }
    
    @POST
    @Path("/recover-password")
    public String recoverPassword(
        @Context final UriInfo uriInfo,
        @FormParam("email") final String email
    ) {
        final Optional<User> user = userRepository.findByEmailAddress(email);
        if (user.isPresent()) {
            try {
            challengeManager.sendPasswordRecover(user.get());
            } catch(MessagingException ex) {
                models.put("failedToSendRecoverMessage", true);
                return getRecoverPasswordForm(uriInfo);
            }
        }
        
        return themesMvc.getMvcTemplate(uriInfo, "login-password-recovered");
   }
    
    private boolean isEmailPrimaryIdentifier() {
         final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class
        );
        return kernelConfig.emailIsPrimaryIdentifier();
    }
}
