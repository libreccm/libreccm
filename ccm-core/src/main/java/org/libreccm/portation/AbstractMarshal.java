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
package org.libreccm.portation;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class responsible for ex- and importing entity-objects to several
 * file-formats. Every entity-class (e.g. DocRepo.File) needs to have its own
 * extension of this class to override the abstract methods, making it
 * possible to ex- or import that extending entity-class (e.g. DocRepo
 * .FileMarshal).
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created the 2/10/16
 */
public abstract class AbstractMarshal<T> {

    private static final Logger log = Logger.getLogger(AbstractMarshal.class);

    private File file;

    // CSV specifics
    private CsvMapper mapper;
    private CsvSchema schema;


    public void init(final Format format, String filename) {
        switch (format) {
            case CSV:
                file = new File(filename);
                mapper = new CsvMapper();
                schema = getCsvSchema();
                schema = mapper.schemaFor(getClassT());
                break;
            default:
                break;
        }
    }

    /* CSV Export/Import */

    /**
     * Main export method. Exports a list of same typed objects to CSV
     * strings and writes them into the same file with a name specific to
     * their class.
     *
     * TODO: throw exception for ui
     * TODO: error message not with uuid
     *
     * @param exportObjects List of {@code T}-tpyed objects being exported to
     *                      CSV
     */
    public void exportCSV(final List<T> exportObjects) {
        exportObjects.forEach(t -> {
            try {
                mapper.writer(schema).writeValue(file, t);
            } catch (IOException e) {
                log.error(String.format("Error writing object with UUID " +
                        "%d to CSV-file with the name %s.",
                        1234, file.getName()));
            }
        });
    }

    /**
     * Main import method. Imports a list of string representing object of
     * the same type ({@code T}) from a CSV-file and parses them into the
     * corresponding objects.
     *
     * TODO: throw exception for ui
     * TODO: error message not with uuid
     *
     * @return List of {@code T}-typed objects being imported from CSV
     */
    public List<T> importCSV() {
        List<T> importObjects = new ArrayList<>();
        try {
            mapper.readerFor(getClassT()).with(schema).readValues(file)
                        .forEachRemaining(t -> importObjects.add((T) t));
        } catch (IOException e) {
            log.error(String.format("Error reading object with UUID %d from " +
                    "CSV-file with the name %s.", 1234, file.getName()));
        }
        return importObjects;
    }

    /**
     * Abstract method to get the class of the extending subclass fulfilling
     * the generic type {@code T}.
     *
     * @return Class of the extending subclass.
     */
    protected abstract Class getClassT();

    /**
     * Abstract method to get the CSV schema needed for both ex- and
     * importation specific to every extending subclass.
     *
     * @return The CSV schema specific to the implementing subclass
     */
    protected abstract CsvSchema getCsvSchema();


    /* JSON Export/Import */
}
