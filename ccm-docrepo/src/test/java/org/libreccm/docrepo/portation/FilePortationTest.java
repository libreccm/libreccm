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
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.libreccm.docrepo.File;
import org.libreccm.docrepo.portation.exporter.FileExporter;
import org.libreccm.docrepo.portation.importer.FileImporter;
import org.libreccm.tests.categories.UnitTest;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version 13/01/2016
 */
@Category(UnitTest.class)
public class FilePortationTest {

    private static final Logger log = Logger.getLogger(FilePortationTest.class);
    private static File file;
    private static String filename =
            "src/test/java/org/libreccm/docrepo/portation/exportTest.csv";

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
        FileExporter fileExporter = new FileExporter();
        fileExporter.setFilename(filename);
        try {
            fileExporter.exportToCSV(Arrays.asList(file));
        } catch (FileNotFoundException e) {
            log.error("Error exporting files.");
        }
    }

    @Test
    public void fileShouldBeCreated() {
        FileImporter fileImporter = new FileImporter();
        fileImporter.setFilename(filename);
        List<File> files;
        try {
            files = fileImporter.importFromCSV();
            log.info(files.toString());
        } catch (FileNotFoundException e) {
            log.error("Error exporting files.");
        }
    }
}
