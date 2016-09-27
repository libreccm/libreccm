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
package org.libreccm.modules;

import org.libreccm.modules.dependencytree.test.valid.TestModuleB;
import org.libreccm.modules.dependencytree.test.valid.TestModuleC;
import org.libreccm.modules.dependencytree.test.valid.TestModuleA;
import org.libreccm.modules.dependencytree.test.valid.TestModuleRoot;

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
import org.libreccm.tests.categories.IntegrationTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.libreccm.testutils.DependenciesHelpers.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
public class DependencyTreeManagerTest {

    private Iterable<CcmModule> modules;

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
        final List<CcmModule> moduleList = new ArrayList<>();
        moduleList.add(new TestModuleRoot());
        moduleList.add(new TestModuleA());
        moduleList.add(new TestModuleB());
        moduleList.add(new TestModuleC());

        modules = moduleList;
    }

    @After
    public void tearDown() {
    }

    @Deployment
    public static WebArchive createDeployment() {
//        final PomEquippedResolveStage pom = Maven
//            .resolver()
//            .loadPomFromFile("pom.xml");
//        final PomEquippedResolveStage dependencies = pom
//            .importCompileAndRuntimeDependencies();
//        final File[] libs = dependencies.resolve().withTransitivity().asFile();
//
//        for (File lib : libs) {
//            System.err.printf("Adding file '%s' to test archive...%n",
//                              lib.getName());
//        }

        return ShrinkWrap
            .create(WebArchive.class,
                    "LibreCCM-org.libreccm.modules.dependencytree.DependencyTreeManagerTest.war")
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addClass(org.libreccm.modules.DependencyTreeManager.class)
            .addClass(org.libreccm.modules.DependencyException.class)
            .addClass(org.libreccm.modules.IntegrationException.class)
            .addClass(org.libreccm.modules.TreeNode.class)
            .addClass(org.libreccm.modules.CcmModule.class)
            .addClass(org.libreccm.modules.ModuleInfo.class)
            .addClass(org.libreccm.modules.ModuleStatus.class)
            .addClass(org.libreccm.modules.Module.class)
            .addClass(org.libreccm.modules.RequiredModule.class)
            .addClass(org.libreccm.modules.InitEvent.class)
            .addClass(org.libreccm.modules.InstallEvent.class)
            .addClass(org.libreccm.modules.ShutdownEvent.class)
            .addClass(org.libreccm.modules.UnInstallEvent.class)
            .addClass(
                org.libreccm.modules.dependencytree.test.valid.TestModuleRoot.class)
            .addClass(
                org.libreccm.modules.dependencytree.test.valid.TestModuleA.class)
            .addClass(
                org.libreccm.modules.dependencytree.test.valid.TestModuleB.class)
            .addClass(
                org.libreccm.modules.dependencytree.test.valid.TestModuleC.class)
            .addClass(org.libreccm.web.ApplicationType.class)
            .addAsLibraries(getModuleDependencies())
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsResource(
                "module-info/dependency-tree-manager-test/module-root.properties",
                "module-info/org.libreccm.modules.dependencytree.test.valid.TestModuleRoot.properties")
            .addAsResource(
                "module-info/dependency-tree-manager-test/module-a.properties",
                "module-info/org.libreccm.modules.dependencytree.test.valid.TestModuleA.properties")
            .addAsResource(
                "module-info/dependency-tree-manager-test/module-b.properties",
                "module-info/org.libreccm.modules.dependencytree.test.valid.TestModuleB.properties")
            .addAsResource(
                "module-info/dependency-tree-manager-test/module-c.properties",
                "module-info/org.libreccm.modules.dependencytree.test.valid.TestModuleC.properties");
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

    @Test
    public void verifyModuleOrder() throws DependencyException {
        final DependencyTreeManager treeManager = new DependencyTreeManager();

        final List<TreeNode> tree = treeManager.generateTree(modules);
        final List<TreeNode> ordered = treeManager.orderModules(tree);

        final List<String> modulesInOrder = new ArrayList<>();
        for (final TreeNode node : ordered) {
            modulesInOrder.add(node.getModuleInfo().getModuleName());
        }

        assertThat(modulesInOrder,
                   contains("test-module-root",
                            "test-module-a",
                            "test-module-b",
                            "test-module-c"));
    }

}
