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
package org.libreccm.modules.dependencytree;

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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.modules.Module;
import org.libreccm.tests.categories.IntegrationTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
public class DependencyTreeManagerTest {

    @Inject
    @org.libreccm.modules.annotations.Module
    private transient Instance<Module> modules;

    public DependencyTreeManagerTest() {
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
                    "LibreCCM-org.libreccm.modules.dependencytree.DependencyTreeManager.war")
            .addPackage(DependencyTreeManager.class.getPackage())
            .addPackage(org.libreccm.modules.annotations.Module.class
                .getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addClass(org.libreccm.modules.Module.class)
            .addClass(org.libreccm.modules.ModuleUtil.class)
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void allModulesInjected() {

        final List<Class<Module>> moduleList = new ArrayList<>();
        for (final Module module : modules) {
            moduleList.add((Class<Module>) module.getClass());
        }

        assertThat(moduleList.size(), is(4));
        assertThat(moduleList, containsInAnyOrder(TestModuleRoot.class,
                                                  TestModuleA.class,
                                                  TestModuleB.class,
                                                  TestModuleC.class));

    }

}
