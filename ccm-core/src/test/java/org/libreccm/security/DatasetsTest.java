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
package org.libreccm.security;

import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.libreccm.tests.categories.UnitTest;

import static org.libreccm.testutils.DatasetType.*;

import org.libreccm.testutils.DatasetType;
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
            "/datasets/org/libreccm/security/GroupManagerTest/data.yml",
            "/datasets/org/libreccm/security/GroupManagerTest/after-add.yml",
            "/datasets/org/libreccm/security/GroupManagerTest/after-remove.yml",
            
            "/datasets/org/libreccm/security/GroupRepositoryTest/data.yml",
            "/datasets/org/libreccm/security/GroupRepositoryTest/after-delete.yml",
            "/datasets/org/libreccm/security/GroupRepositoryTest/after-save-changed.yml",
            "/datasets/org/libreccm/security/GroupRepositoryTest/after-save-new.yml",

            "/datasets/org/libreccm/security/PartyRepositoryTest/data.yml",
            "/datasets/org/libreccm/security/PartyRepositoryTest/after-delete.yml",
            "/datasets/org/libreccm/security/PartyRepositoryTest/after-save-changed.yml",
            "/datasets/org/libreccm/security/PartyRepositoryTest/after-save-new.yml",

            "/datasets/org/libreccm/security/PermissionManagerTest/data.yml",
            "/datasets/org/libreccm/security/PermissionManagerTest/data-recursivly.yml",
            "/datasets/org/libreccm/security/PermissionManagerTest/after-grant.yml",
            "/datasets/org/libreccm/security/PermissionManagerTest/after-grant-recursivly.yml",
            "/datasets/org/libreccm/security/PermissionManagerTest/after-grant-inherited.yml",
            "/datasets/org/libreccm/security/PermissionManagerTest/after-revoke.yml",
            "/datasets/org/libreccm/security/PermissionManagerTest/after-revoke-recursivly.yml",
            "/datasets/org/libreccm/security/PermissionManagerTest/after-copy.yml",
            
            "/datasets/org/libreccm/security/RoleManagerTest/data.yml",
            "/datasets/org/libreccm/security/RoleManagerTest/after-add.yml",
            "/datasets/org/libreccm/security/RoleManagerTest/after-remove.yml",
            
            "/datasets/org/libreccm/security/RoleRepositoryTest/data.yml",
            "/datasets/org/libreccm/security/RoleRepositoryTest/after-delete.yml",
            "/datasets/org/libreccm/security/RoleRepositoryTest/after-save-changed.yml",
            "/datasets/org/libreccm/security/RoleRepositoryTest/after-save-new.yml",

            "/datasets/org/libreccm/security/ShiroTest/data.yml",
            
            "/datasets/org/libreccm/security/UserManagerTest/data.yml",
            "/datasets/org/libreccm/security/UserManagerTest/after-create-user.yml",

            "/datasets/org/libreccm/security/UserRepositoryTest/data.yml",
            "/datasets/org/libreccm/security/UserRepositoryTest/data-email-duplicate.yml",
            "/datasets/org/libreccm/security/UserRepositoryTest/after-delete.yml",
            "/datasets/org/libreccm/security/UserRepositoryTest/after-save-changed.yml",
            "/datasets/org/libreccm/security/UserRepositoryTest/after-save-new.yml"
        });
    }

    public DatasetsTest(final String datasetPath) {
        super(datasetPath);
    }

    @Override
    public DatasetType getDatasetType() {
        return YAML;
    }

    @Override
    public String[] getSchemas() {
        return new String[]{"ccm_core"};
    }

}
