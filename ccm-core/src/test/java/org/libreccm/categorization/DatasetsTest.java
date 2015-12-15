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
package org.libreccm.categorization;

import java.util.Arrays;
import java.util.Collection;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.libreccm.tests.categories.UnitTest;
import org.libreccm.testutils.DatasetType;
import org.libreccm.testutils.DatasetsVerifier;

import static org.libreccm.testutils.DatasetType.*;


/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RunWith(Parameterized.class)
@org.junit.experimental.categories.Category(UnitTest.class)
public class DatasetsTest extends DatasetsVerifier {

    @Parameterized.Parameters(name = "Dataset {0}")
    public static Collection<String> data() {
        return Arrays.asList(new String[]{
            "/datasets/org/libreccm/categorization/CategoryManagerTest/after-add-obj-to-category.yml",
            "/datasets/org/libreccm/categorization/CategoryManagerTest/after-add-subcategory.yml",
            "/datasets/org/libreccm/categorization/CategoryManagerTest/after-remove-obj-from-category.yml",
            "/datasets/org/libreccm/categorization/CategoryManagerTest/after-remove-subcategory.yml",
            "/datasets/org/libreccm/categorization/CategoryManagerTest/data.yml",
            "/datasets/org/libreccm/categorization/CategoryManagerTest/data2.yml",
            "/datasets/org/libreccm/categorization/CategoryRepositoryTest/data.yml",
            "/datasets/org/libreccm/categorization/CategoryRepositoryTest/after-save-new-category.yml"
        });
    }

    public DatasetsTest(final String datasetPath) {
        super(datasetPath);
    }

    @Override
    public String[] getSchemas() {
        return new String[]{"ccm_core"};
    }

    @Override
    public DatasetType getDatasetType() {
        return YAML;
    }
    
}
