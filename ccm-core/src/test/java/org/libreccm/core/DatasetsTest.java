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
package org.libreccm.core;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.libreccm.tests.categories.UnitTest;
import org.libreccm.testutils.DatasetsVerifier;

import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RunWith(Parameterized.class)
@Category(UnitTest.class)
public class DatasetsTest extends DatasetsVerifier {
    
    @Parameterized.Parameters(name = "Dataset {0}")
    public static Collection<String> data() {
        return Arrays.asList(new String[]{
//            "/datasets/org/libreccm/core/authentication/LoginManagerTest/data.json",
            "/datasets/org/libreccm/core/CcmObjectRepositoryTest/data.json",
            "/datasets/org/libreccm/core/CcmObjectRepositoryTest/after-delete.json",
            "/datasets/org/libreccm/core/CcmObjectRepositoryTest/after-save-changed.json",
            "/datasets/org/libreccm/core/CcmObjectRepositoryTest/after-save-new.json",
//            "/datasets/org/libreccm/core/GroupManagerTest/after-add-to-group.json",
//            "/datasets/org/libreccm/core/GroupManagerTest/after-remove-from-group.json",
//            "/datasets/org/libreccm/core/GroupManagerTest/users-groups.json",
//            "/datasets/org/libreccm/core/GroupRepositoryTest/data.json",
//            "/datasets/org/libreccm/core/GroupRepositoryTest/after-delete.json",
//            "/datasets/org/libreccm/core/GroupRepositoryTest/after-save-changed.json",
//            "/datasets/org/libreccm/core/GroupRepositoryTest/after-save-new.json",
//            "/datasets/org/libreccm/core/PermissionManagerTest/after-grant.json",
//            "/datasets/org/libreccm/core/PermissionManagerTest/after-grant-wildcard.json",
//            "/datasets/org/libreccm/core/PermissionManagerTest/after-revoke.json",
//            "/datasets/org/libreccm/core/PermissionManagerTest/data.json",
//            "/datasets/org/libreccm/core/PermissionRepositoryTest/after-save-changed.json",
//            "/datasets/org/libreccm/core/PermissionRepositoryTest/after-save-new.json",
//            "/datasets/org/libreccm/core/PermissionRepositoryTest/after-delete.json",
//            "/datasets/org/libreccm/core/PermissionRepositoryTest/data.json",
//            "/datasets/org/libreccm/core/PrivilegeRepositoryTest/after-create.json",
//            "/datasets/org/libreccm/core/PrivilegeRepositoryTest/after-delete.json",
//            "/datasets/org/libreccm/core/PrivilegeRepositoryTest/data.json",
//            "/datasets/org/libreccm/core/RoleRepositoryTest/data.json",
//            "/datasets/org/libreccm/core/RoleRepositoryTest/after-delete.json",
//            "/datasets/org/libreccm/core/RoleRepositoryTest/after-save-changed.json",
//            "/datasets/org/libreccm/core/RoleRepositoryTest/after-save-new.json",
//            "/datasets/org/libreccm/core/UserManagerTest/verify-password.json",
//            "/datasets/org/libreccm/core/UserRepositoryTest/data.json",
//            "/datasets/org/libreccm/core/UserRepositoryTest/after-delete.json",
//            "/datasets/org/libreccm/core/UserRepositoryTest/after-save-changed.json",
//            "/datasets/org/libreccm/core/UserRepositoryTest/after-save-new.json"
        });
    }
    
    public DatasetsTest(final String datasetPath) {
        super(datasetPath);
    }
    
    @Override
    public String[] getSchemas() {
        return new String[]{ "ccm_core" };
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
    
}
