/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package com.arsdigita.kernel.security;

import com.arsdigita.util.JavaPropertyReader;
import com.arsdigita.util.parameter.AbstractParameter;
import com.arsdigita.web.CCMApplicationContextListener;
import com.arsdigita.xml.XML;
import com.arsdigita.xml.formatters.DateFormatter;

import static org.hamcrest.Matchers.*;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.AfterClass;

import static org.junit.Assert.*;
import static org.libreccm.testutils.DependenciesHelpers.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.libreccm.categorization.Categorization;
import org.libreccm.core.CcmObject;
import org.libreccm.jpa.EntityManagerProducer;
import org.libreccm.jpa.utils.UriConverter;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.security.Permission;
import org.libreccm.tests.categories.IntegrationTest;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.workflow.Workflow;

import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RunWith(Arquillian.class)
//@Category(IntegrationTest.class)
public class SecurityConfigTest {

    public SecurityConfigTest() {

    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap
            .create(WebArchive.class,
                    "LibreCCM-com.arsdigita.kernel.security.SecurityConfigTest.war")
            .addPackage(CcmObject.class.getPackage())
            .addPackage(Categorization.class.getPackage())
            .addPackage(Permission.class.getPackage())
            .addPackage(LocalizedString.class.getPackage())
            .addPackage(Workflow.class.getPackage())
            .addPackage(UriConverter.class.getPackage())
            .addPackage(ApplicationRepository.class.getPackage())
            .addPackage(EntityManagerProducer.class.getPackage())
            .addPackage(SecurityConfig.class.getPackage())
            .addPackage(AbstractParameter.class.getPackage())
            .addPackage(JavaPropertyReader.class.getPackage())
            .addPackage(CCMApplicationContextListener.class.getPackage())
            .addPackage(XML.class.getPackage())
            .addPackage(DateFormatter.class.getPackage())
            .addPackage(IntegrationTest.class.getPackage())
            .addAsLibraries(getModuleDependencies())
            .addAsResource(
                "configs/com/arsdigita/kernel/security/SecurityConfigTest/ccm-core.config",
                "ccm-core.config")
            .addAsWebInfResource(
                "configs/com/arsdigita/kernel/security/SecurityConfigTest/registry.properties",
                "conf/registry/registry.properties")
            .addAsWebInfResource(
                "configs/com/arsdigita/kernel/security/SecurityConfigTest/kernel.properties",
                "conf/registry/ccm-core/kernel.properties")
            .addAsWebInfResource(
                "configs/com/arsdigita/kernel/security/SecurityConfigTest/security.properties",
                "conf/registry/ccm-core/security.properties")
            .addAsResource(
                "com/arsdigita/kernel/KernelConfig_parameter.properties",
                "com/arsdigita/kernel/KernelConfig_parameter.properties")
            .addAsResource(
                "com/arsdigita/kernel/security/SecurityConfig_parameter.properties",
                "com/arsdigita/kernel/security/SecurityConfig_parameter.properties")
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "WEB-INF/web.xml")
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void verifySecurityConfig() {
        final SecurityConfig securityConfig = SecurityConfig.getConfig();

        final List<String> excludedExtensions = securityConfig
            .getExcludedExtensions();
        assertThat(excludedExtensions.size(), is(4));
        assertThat(excludedExtensions.get(0), is(equalTo(".jpg")));
        assertThat(excludedExtensions.get(1), is(equalTo(".gif")));
        assertThat(excludedExtensions.get(2), is(equalTo(".png")));
        assertThat(excludedExtensions.get(3), is(equalTo(".pdf")));

        assertThat(securityConfig.isAutoRegistrationEnabled(), is(false));

        assertThat(securityConfig.isPasswordRecoveryEnabled(), is(true));

        assertThat(securityConfig.getHashAlgorithm(), is(equalTo("SHA-512")));

        assertThat(securityConfig.getSaltLength(), is(256));
        
        assertThat(securityConfig.getHashIterations(), is(50000));
    }

}
