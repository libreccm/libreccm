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
package org.libreccm.docrepo.portation;

import org.apache.log4j.Logger;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.CreateSchema;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.core.CcmObjectRepository;
import org.libreccm.docrepo.File;
import org.libreccm.docrepo.ResourceRepository;
import org.libreccm.docrepo.portation.exporter.FileExporter;
import org.libreccm.docrepo.portation.importer.FileImporter;
import org.libreccm.tests.categories.IntegrationTest;
import org.libreccm.tests.categories.UnitTest;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version 13/01/2016
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"create_ccm_docrepo_schema.sql"})
public class FilePortationTest {

    private static final Logger log = Logger.getLogger(FilePortationTest.class);

    @Inject
    private FileExporter fileExporter;

    @Inject
    private FileImporter fileImporter;

    @Inject
    private ResourceRepository ccmObjectRepository;

    private static File file;
    private static String filename =
            "src/test/java/org/libreccm/docrepo/portation/csv/exportTest.csv";

    @BeforeClass
    public static void createResource() {
        file = new File();
        file.setName("testname");
        file.setDescription("this is a text description");
        file.setPath("test/path");
        file.setCreationDate(new Date());
        file.setLastModifiedDate(new Date());
    }

    @Test
    public void csvShouldBeCreated() {
        fileExporter.setFilename(filename);
        fileExporter.exportToCSV(Collections.singletonList(file));
    }

    @Test
    public void fileShouldBeCreated() {
        fileImporter.setFilename(filename);
        List<File> files = fileImporter.importFromCSV();
        log.info(files.toString());
    }
}
