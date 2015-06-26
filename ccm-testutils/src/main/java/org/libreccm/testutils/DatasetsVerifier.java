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
package org.libreccm.testutils;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.h2.tools.RunScript;
import org.jboss.arquillian.persistence.dbunit.dataset.json.JsonDataSet;
import org.junit.runners.Parameterized;

/**
 *
 * For testing the CCM modules the Arquillian Persistence extension including
 * the DBUnit integration is used. Unfortunately there are some issues with
 * exception reporting if there errors in the datasets used for testing.
 * Therefore we provide this utility class which can be used to implement
 * test for the datasets outside of Arquillian. 
 * 
 * For testing an in-memory H2
 * database is used. The datasets are loaded into the database using
 * DBUnit classes. Before loading the dataset this class creates the database
 * schema. The SQL script for generating the schema is loaded via 
 * {@code getClass().getResource(/sql/ddl/auto/h2.sql).toURI()}. Therefore the
 * utility expects to find the SQL for generating the database schema in the 
 * classpath at th path {@code /sql/ddl/auto/h2.sql}. The default 
 * {@code pom.xml} for modules take care of that.
 * 
 * After each dataset the database is scrapped. To use this utility create
 * a JUnit test class using the {@link Parameterized} test runner from JUnit.
 * An example is the
 * <a href="../../../../../ccm-core/xref-test/org/libreccm/core/DatasetsTest.html"><code>DatasetsTest</code></a> 
 * in the {@code org.libreccm.core} package of the ccm-core module.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class DatasetsVerifier {

    private final transient String datasetPath;

    public DatasetsVerifier(final String datasetsPath) {
        this.datasetPath = datasetsPath;
    }

    @Test
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(
        value = "DMI_EMPTY_DB_PASSWORD",
        justification = "H2 in mem database does not need a password")
    public void verifyDataset() throws SQLException,
                                       URISyntaxException,
                                       IOException,
                                       DatabaseUnitException {
        //Create database connection to an in memory h2 database. Placed in
        //try-with-resources block to ensure that the connection is closed.
        try (Connection connection = DriverManager.getConnection(
            "jdbc:h2:mem:testdatabase", "sa", "")) {
            //Create DB schema 
            final Path schemaPath = Paths.get(getClass().getResource(
                "/sql/ddl/auto/h2.sql").toURI());
            RunScript.execute(connection, Files.newBufferedReader(schemaPath));
            connection.commit();

            //Get dataset to test
            final IDataSet dataSet = new JsonDataSet(getClass()
                .getResourceAsStream(datasetPath));

            //Create DBUnit DB connection
            final IDatabaseConnection dbUnitConn
                                          = new DatabaseConnection(connection);

            //Check if dumping works the DB works before loading the dataset.
            System.out.println("Dump before loading dataset...");
            verifyDumping(dbUnitConn);

            //Put dataset into DB
            DatabaseOperation.CLEAN_INSERT.execute(dbUnitConn, dataSet);

            //Check if dumping works after loading the dataset
            System.out.println("Dump after loading dataset...");
            verifyDumping(dbUnitConn);
        }
    }

    private void verifyDumping(final IDatabaseConnection connection)
        throws SQLException, IOException, DataSetException {
        final IDataSet data = connection.createDataSet();
        FlatXmlDataSet.write(data, System.out);
    }

}
