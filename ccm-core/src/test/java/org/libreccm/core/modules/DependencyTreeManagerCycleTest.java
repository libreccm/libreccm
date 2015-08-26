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
package org.libreccm.core.modules;

import static org.hamcrest.Matchers.*;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.ShouldThrowException;
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
import org.libreccm.core.modules.dependencytree.test.cycle.TestModuleA;
import org.libreccm.core.modules.dependencytree.test.cycle.TestModuleB;
import org.libreccm.core.modules.dependencytree.test.cycle.TestModuleC;
import org.libreccm.core.modules.dependencytree.test.cycle.TestModuleRoot;
import org.libreccm.tests.categories.IntegrationTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;


/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
public class DependencyTreeManagerCycleTest {

    private transient ServiceLoader<CcmModule> modules;

    public DependencyTreeManagerCycleTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        modules = ServiceLoader.load(CcmModule.class);
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
            .addPackage(Module.class
                .getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addPackage(org.libreccm.core.modules.dependencytree.test.cycle.TestModuleRoot.class
                .getPackage())
            .addPackage(CcmModule.class.getPackage())
            .addAsLibraries(libs)
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void allModulesInjected() {
        final List<Class<CcmModule>> moduleList = new ArrayList<>();
        for (final CcmModule module : modules) {
            moduleList.add((Class<CcmModule>) module.getClass());
        }

        assertThat(moduleList.size(), is(4));
        assertThat(moduleList, containsInAnyOrder(TestModuleRoot.class,
                                                  TestModuleA.class,
                                                  TestModuleB.class,
                                                  TestModuleC.class));
    }

    @Test(expected = DependencyException.class)
    @ShouldThrowException(DependencyException.class)
    public void verifyModuleOrder() throws DependencyException {
        final DependencyTreeManager treeManager = new DependencyTreeManager();

        final List<TreeNode> tree = treeManager.generateTree(modules);
        treeManager.orderModules(tree);
    }

}
