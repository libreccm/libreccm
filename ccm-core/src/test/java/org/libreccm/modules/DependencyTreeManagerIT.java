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
import org.junit.After;
import org.junit.AfterClass;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.libreccm.testutils.DependenciesHelpers.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RunWith(Arquillian.class)
public class DependencyTreeManagerIT {

    private Iterable<CcmModule> modules;

    public DependencyTreeManagerIT() {
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
            .addPackages(true, "com.arsdigita", "org.libreccm")
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
