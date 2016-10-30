/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.librecms.attachments;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.libreccm.tests.categories.UnitTest;
import org.libreccm.testutils.DatasetType;
import org.libreccm.testutils.DatasetsVerifier;

import java.util.Arrays;
import java.util.Collection;

/**
 * Verify the datasets for the tests in {@code org.librecms.attachments}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RunWith(Parameterized.class)
@Category(UnitTest.class)
public class DatasetsTest extends DatasetsVerifier {

    @Parameterized.Parameters(name = "Dataset {0}")
    public static Collection<String> data() {
        return Arrays.asList(new String[]{
            "/datasets/org/librecms/attachments/AttachmentListManagerTest/data.xml",
            "/datasets/org/librecms/attachments/AttachmentListManagerTest/after-create-after-last.xml",
            "/datasets/org/librecms/attachments/AttachmentListManagerTest/after-create-with-negative-position.xml",
            "/datasets/org/librecms/attachments/AttachmentListManagerTest/after-create.xml",
            "/datasets/org/librecms/attachments/AttachmentListManagerTest/after-move-down.xml",
            "/datasets/org/librecms/attachments/AttachmentListManagerTest/after-move-to-first.xml",
            "/datasets/org/librecms/attachments/AttachmentListManagerTest/after-move-to-last.xml",
            "/datasets/org/librecms/attachments/AttachmentListManagerTest/after-move-to.xml",
            "/datasets/org/librecms/attachments/AttachmentListManagerTest/after-move-up.xml",
            "/datasets/org/librecms/attachments/AttachmentListManagerTest/after-remove.xml"
        });
    }

    public DatasetsTest(final String datasetPath) {
        super(datasetPath);
    }

    @Override
    public DatasetType getDatasetType() {
        return DatasetType.FLAT_XML;
    }

    @Override
    public String[] getSchemas() {
        return new String[]{"ccm_core", "ccm_cms"};
    }

    @Override
    public String[] getDdlFiles() {
        return new String[]{"/datasets/create_ccm_cms_schema.sql"};
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
