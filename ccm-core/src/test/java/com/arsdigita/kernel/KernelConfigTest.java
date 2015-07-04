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
package com.arsdigita.kernel;

import java.io.File;

import static org.hamcrest.Matchers.*;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.tests.categories.IntegrationTest;
import sun.util.locale.StringTokenIterator;

import java.util.StringTokenizer;

import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RunWith(Arquillian.class)
@Category(IntegrationTest.class)
public class KernelConfigTest {

    public KernelConfigTest() {
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
        final PomEquippedResolveStage pom = Maven
            .resolver()
            .loadPomFromFile("pom.xml");
        final PomEquippedResolveStage dependencies = pom
            .importCompileAndRuntimeDependencies();
        final File[] libs = dependencies.resolve().withTransitivity().asFile();

        for (File lib : libs) {
            System.err.printf("Adding file '%s' to test archive...%n",
                              lib.getName());
        }

        return ShrinkWrap
            .create(WebArchive.class,
                    "LibreCCM-com.arsdigita.kernel.KernelConfigTest.war")
            //.addPackage(CcmObject.class.getPackage())
            .addPackage(com.arsdigita.kernel.KernelConfig.class.getPackage())
            .addPackage(com.arsdigita.runtime.AbstractConfig.class.getPackage())
            .addPackage(com.arsdigita.util.parameter.AbstractParameter.class.
                getPackage())
            .addPackage(com.arsdigita.util.JavaPropertyReader.class.
                getPackage())
            .addPackage(com.arsdigita.web.CCMApplicationContextListener.class
                .getPackage())
            .addPackage(com.arsdigita.xml.XML.class.getPackage())
            .addPackage(com.arsdigita.xml.formatters.DateFormatter.class
                .getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addAsLibraries(libs)
            .addAsResource(
                "configtests/com/arsdigita/kernel/KernelConfigTest/ccm-core.config",
                "ccm-core.config")
            .addAsWebInfResource(
                "configtests/com/arsdigita/kernel/KernelConfigTest/registry.properties",
                "conf/registry/registry.properties")
            .addAsWebInfResource(
                "configtests/com/arsdigita/kernel/KernelConfigTest/kernel.properties",
                "conf/registry/ccm-core/kernel.properties")
            .addAsResource(
                "com/arsdigita/kernel/KernelConfig_parameter.properties",
                "com/arsdigita/kernel/KernelConfig_parameter.properties")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void verifyKernelConfig() {
        final KernelConfig kernelConfig = KernelConfig.getConfig();

        assertThat(kernelConfig.isDebugEnabled(),
                   is(true));
        assertThat(kernelConfig.isWebdevSupportActive(),
                   is(false));
        assertThat(kernelConfig.isDataPermissionCheckEnabled(),
                   is(false));
        assertThat(kernelConfig.getPrimaryUserIdentifier(),
                   is(equalTo("email")));
        assertThat(kernelConfig.screenNameIsPrimaryIdentifier(),
                   is(false));
        assertThat(kernelConfig.emailIsPrimaryIdentifier(),
                   is(true));
        assertThat(kernelConfig.isSSOenabled(),
                   is(false));
        assertThat(kernelConfig.isLoginRemembered(),
                   is(true));
        assertThat(kernelConfig.isSecureLoginRequired(),
                   is(false));
        assertThat(kernelConfig.getSupportedLanguages(),
                   is(equalTo("de,en")));
        assertThat(kernelConfig.languageIndependentItems(),
                   is(true));
        assertThat(kernelConfig.getLanguagesIndependentCode(),
                   is(equalTo("--")));
        assertThat(kernelConfig.hasLanguage("de"),
                   is(true));
        assertThat(kernelConfig.hasLanguage("en"),
                   is(true));
        assertThat(kernelConfig.hasLanguage("es"),
                   is(false));
        assertThat(kernelConfig.getDefaultLanguage(),
                   is(equalTo("de")));
        final StringTokenizer tokenizer = kernelConfig
            .getSupportedLanguagesTokenizer();
        assertThat(tokenizer.countTokens(), is(2));
    }

}
