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
package com.arsdigita.kernel.security;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.SpecificClassParameter;
import com.arsdigita.util.parameter.StringArrayParameter;
import com.arsdigita.util.parameter.StringParameter;

import java.util.Arrays;
import java.util.List;

/**
 * A record containing server-session scoped security configuration properties.
 *
 * Accessors of this class may return null. Developers should take care to trap
 * null return values in their code.
 *
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SecurityConfig extends AbstractConfig {

    private static SecurityConfig s_config = null;

    private static String s_systemAdministratorEmailAddress = null;

    /**
     * Size of secret key in bytes. *
     */
    @SuppressWarnings("PublicField")
    public static int SECRET_KEY_BYTES = 16;

    /**
     * The class name of the SecurityHelper implementation. Must implement
     * SecurityHelper interface
     */
    private final Parameter m_securityHelperClass = new SpecificClassParameter(
        "waf.security_helper_class", Parameter.REQUIRED,
        com.arsdigita.kernel.security.DefaultSecurityHelper.class,
        com.arsdigita.kernel.security.SecurityHelper.class);

    /**
     * List of extensions excluded from authentication cookies. Authentication
     * is checked for all requests, but requests with one of these extensions
     * will never cause a new cookie to be set. Include a leading dot for each
     * extension.
     */
    private final Parameter m_excludedExtensions = new StringArrayParameter(
        "waf.excluded_extensions", Parameter.REQUIRED,
        new String[]{".jpg", ".gif", ".png", ".pdf"});

    private final Parameter m_cookieDurationMinutes = new IntegerParameter(
        "waf.pagemap.cookies_duration_minutes", Parameter.OPTIONAL, null);

    private final Parameter m_cookieDomain = new StringParameter(
        "waf.cookie_domain", Parameter.OPTIONAL, null);

    private final Parameter m_adminEmail = new StringParameter(
        "waf.admin.contact_email", Parameter.OPTIONAL, null);

    private final Parameter m_autoRegistrationOn = new BooleanParameter(
        "waf.auto_registration_on", Parameter.REQUIRED, Boolean.TRUE);

    private final Parameter m_userBanOn = new BooleanParameter(
        "waf.user_ban_on",
        Parameter.REQUIRED,
        Boolean.FALSE);

    private final Parameter m_enableQuestion = new BooleanParameter(
        "waf.user_question.enable", Parameter.REQUIRED, Boolean.FALSE);

    /**
     * The default hash algorithm used for new passwords. Default is SHA-512
     * which should sufficient for good security.
     */
    private final Parameter m_hashAlgorithm = new StringParameter(
        "waf.security.hash_algorithm", Parameter.REQUIRED, "SHA-512");

    /**
     * Default length of the salt for new passwords.
     */
    private final Parameter m_saltLength = new IntegerParameter(
        "waf.security.salt_length", Parameter.REQUIRED, 256);

    /**
     * Default number of hash iterations for new passwords.
     */
    private final Parameter m_hashIterations = new IntegerParameter(
            "waf.security.hash_iterations", Parameter.REQUIRED, 50000);
    
    /**
     * Constructs an empty SecurityConfig object
     */
    public SecurityConfig() {

        register(m_securityHelperClass);
        register(m_excludedExtensions);

        register(m_cookieDomain);
        register(m_cookieDurationMinutes);
        register(m_adminEmail);
        register(m_autoRegistrationOn);
        register(m_userBanOn);
        register(m_enableQuestion);

        register(m_hashAlgorithm);
        register(m_saltLength);
        register(m_hashIterations);

        loadInfo();
    }

    /**
     * Returns the singleton configuration record for the runtime environment.
     *
     * @return The <code>RuntimeConfig</code> record; it cannot be null
     */
    public static final synchronized SecurityConfig getConfig() {
        if (s_config == null) {
            s_config = new SecurityConfig();
            s_config.load();
        }

        return s_config;
    }

    /**
     *
     * @return
     */
    public final Class getSecurityHelperClass() {
        return (Class) get(m_securityHelperClass);
    }

//  /**
//   * Obsolete!
//   * @return
//   */
//  public final String getSessionTrackingMethod() {
//      return (String) get(m_sessionTrackingMethod);
//  }
    /**
     *
     * @return
     */
    public final List<String> getExcludedExtensions() {
        return Arrays.asList((String[]) get(m_excludedExtensions));
    }

    public String getCookieDomain() {
        return (String) get(m_cookieDomain);
    }

    Integer getCookieDurationMinutes() {
        return (Integer) get(m_cookieDurationMinutes);
    }

    boolean isUserBanOn() {
        return ((Boolean) get(m_userBanOn)).booleanValue();
    }

    public String getAdminContactEmail() {
        String email = (String) get(m_adminEmail);

        // Return empty string instead of looking up into the database. If no
        // email if configured for the admin we consider that as a configuration
        // issue.
        if (email == null || email.isEmpty()) {
            return "";
        } else {
            return email;
        }
//        if (email == null || email.trim().length() == 0) {
//            email = getSystemAdministratorEmailAddress();
//        }
//        return email;
    }

    public Boolean getEnableQuestion() {
        return (Boolean) get(m_enableQuestion);
    }

//    private static synchronized String getSystemAdministratorEmailAddress() {
//        if (s_systemAdministratorEmailAddress == null) {
//            ObjectPermissionCollection perms = PermissionService.
//                getGrantedUniversalPermissions();
//            perms.addEqualsFilter("granteeIsUser", Boolean.TRUE);
//            perms.clearOrder();
//            perms.addOrder("granteeID");
//            if (perms.next()) {
//                s_systemAdministratorEmailAddress = perms.getGranteeEmail().
//                    toString();
//                perms.close();
//            } else {
//                // Haven't found anything.  We don't want to repeat this query
//                // over and over again.
//                s_systemAdministratorEmailAddress = "";
//            }
//        }
//        return s_systemAdministratorEmailAddress;
//    }
    public final boolean isAutoRegistrationOn() {
        return ((Boolean) get(m_autoRegistrationOn)).booleanValue();
    }

    public String getHashAlgorithm() {
        return (String) get(m_hashAlgorithm);
    }

    public Integer getSaltLength() {
        return (Integer) get(m_saltLength);
    }
    
    public Integer getHashIterations() {
        return (Integer) get(m_hashIterations);
    }

}
