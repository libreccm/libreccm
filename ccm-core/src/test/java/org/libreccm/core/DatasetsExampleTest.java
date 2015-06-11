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

import org.dbunit.DatabaseUnitException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.libreccm.tests.categories.UnitTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.h2.tools.RunScript;
import org.jboss.arquillian.persistence.core.data.descriptor.Format;
import org.jboss.arquillian.persistence.dbunit.dataset.DataSetBuilder;


/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(UnitTest.class)
public class DatasetsExampleTest {

    public DatasetsExampleTest() {
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

    @Test
    public void verifyBaseDataset() throws ClassNotFoundException,
                                           SQLException,
                                           DatabaseUnitException,
                                           IOException,
                                           URISyntaxException {
        final DataSetBuilder builder = DataSetBuilder.builderFor(Format.JSON);
        final IDataSet dataSet = builder.build(
            "datasets/org/libreccm/core/CcmObjectRepositoryTest/data.json");
        final Path schemaPath = Paths.get(getClass().getResource(
            "/sql/ddl/auto/h2.sql").toURI());

        Class.forName("org.h2.Driver");
        try (Connection connection = DriverManager.getConnection(
            "jdbc:h2:mem:testdatabase", "sa", "")) {
            //Create db schema
            RunScript.execute(connection, Files.newBufferedReader(schemaPath));
            connection.commit();
            
            final IDatabaseConnection dbUnitConn
                                          = new DatabaseConnection(connection);
            DatabaseOperation.CLEAN_INSERT.execute(dbUnitConn, dataSet);
        }
    }

}
