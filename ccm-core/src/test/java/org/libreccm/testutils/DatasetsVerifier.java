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
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.h2.tools.RunScript;
import org.jboss.arquillian.persistence.core.data.descriptor.Format;
import org.jboss.arquillian.persistence.dbunit.dataset.DataSetBuilder;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class DatasetsVerifier {

    private final String datasetPath;

    public DatasetsVerifier(final String datasetsPath) {
        this.datasetPath = datasetsPath;
    }

    @Test
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
            final DataSetBuilder builder = DataSetBuilder
                .builderFor(Format.JSON);
            final IDataSet dataSet = builder.build(datasetPath);
            
            //Put dataset into DB
            final IDatabaseConnection dbUnitConn
                                          = new DatabaseConnection(connection);
            DatabaseOperation.CLEAN_INSERT.execute(dbUnitConn, dataSet);
        }
    }

}
